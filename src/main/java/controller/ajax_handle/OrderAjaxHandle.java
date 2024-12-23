package controller.ajax_handle;

import com.google.gson.Gson;
import logs.EAction;
import logs.ELevel;
import logs.LoggingService;
import model.bean.*;
import model.service.*;
import model.service.JavaMail.MailService;
import org.eclipse.tags.shaded.org.apache.xpath.operations.Or;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@WebServlet("/order-ajax-handle")
public class OrderAjaxHandle extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String orderId = req.getParameter("orderId");
        System.out.println("init: " + action);
        User sessionUser = (User) req.getSession().getAttribute("auth");

        if (action != null && orderId != null) {
            Order order = OrderService.getInstance().getOrderById(orderId);
            if (sessionUser != null && (sessionUser.isOwn() || sessionUser.isAdmin())) {
                if (action.equals("deliveredOrder")) {
                    OrderService.getInstance().deliveredOrder(orderId);

                    String message = sessionUser.nameAsRole() + " " + sessionUser.getName() + " đã xác nhận đã giao đơn hàng có mã " + orderId;
                    LoggingService.getInstance().log(ELevel.INFORM, EAction.UPDATE, req.getRemoteAddr(), message);

                    resp.getWriter().write("(" + OrderService.getInstance().waitConfirmOrdersNumber() + ")");
                } else if (action.equals("confirmOrder")) {
                    /*
                        LOGIC XỬ LÝ XÁC THỰC ĐƠN HÀNG.
                        1. Dựa vào orderId, lấy file json của đơn hàng = method có trong OrderService.
                        2. Hash nội dung file json này để tạo lại thành mã hash đơn hàng.
                        3. Từ orderId, chúng ta lấy được publicKeyID và signature của đơn hàng.
                        3.1. Kiểm tra publicKeyID. Kiểm tra trạng thái publicKeyID.
                        + Nếu publicKeyID đã bị xoá/thu hồi => Nghiễm nhiên đơn hàng sẽ bị huỷ và gửi email: "Khoá mà bạn sử dụng blalba".
                        + Nếu publicKeyID  trạng thái sẵn sàng -> Lấy ra nội dung của public key.
                        => Ghi lock lại khi họ sử dụng publickeyID bị thu hồi hoặc xoá
                        4. Có được nội dung public key, cũng như mã hash ở (2), tiến hành verify chữ ký
                        + Nếu verify thành công, nghiễm nhiên chuyển sang trạng thái "Đang đóng gói" cho đơn hàng nhờ vào orderID.
                        + Nếu verify thất bại, cũng sẽ huỷ đơn hàng và gửi email: "Đơn hàng của bạn có vẻ đã bị thay đổi thông tin, blabla".
                     */

                    //1. Lấy toàn bộ thông tin đơn hàng (có publicKeyID và signatureID)
                    Order mainOrder = OrderService.getInstance().getOrderById(orderId);
                    if(mainOrder == null) {
                        resp.getWriter().write("Không tìm thấy id đơn hàng.");
                        return;
                    }

                    int getPublicKeyId = mainOrder.getPublicKeyId();
                    String signature = mainOrder.getSignature();

                    try {
                        // key ở dạng pem
                        String publicKeyPem = KeyService.getInstance().getPublicKeyByKeyID(getPublicKeyId);
                        System.out.println("PublicKeyPem in OrderAjaxHandle: " + publicKeyPem);
                        // Khoá công khai chính thức.
                        RSAPublicKey publicKey = getRSAPublicKeyFromPEM(publicKeyPem);


                        //File json của đơn hàng.
                        String jsonHashOrder = OrderService.getInstance().getOrderJsonById(orderId);

                        String hashCode = generateSHA256Hash(jsonHashOrder);
                        System.out.println("Hash code by OrderAjaxHandle: " + hashCode);
                        System.out.println("Signature in OrderAjaxHandle: " + signature);

                        boolean isSignatureValid = isValidOrder(publicKey, hashCode, signature);
                        if(isSignatureValid) {
                            //Chữ ký hợp lệ.
                            OrderService.getInstance().confirmOrder(orderId);
                            String message = sessionUser.nameAsRole() + " " + sessionUser.getName() + " đã xác nhận đơn hàng hợp lệ có mã " + orderId;
                            LoggingService.getInstance().log(ELevel.INFORM, EAction.UPDATE, req.getRemoteAddr(), message);
                            resp.getWriter().write("(" + OrderService.getInstance().waitConfirmOrdersNumber() + ")");

                        } else {
                            OrderService.getInstance().cancelOrder(orderId);
                            String message = "Đơn hàng của bạn có vẻ đã bị thay đổi thông tin, và đã bị huỷ.";
                            MailService.sendNotifyCanceledOrder(UserService.getInstance().getUserById(order.getUserId() + "").getEmail(), order, message);
                            resp.getWriter().write("Đơn hàng đã bị huỷ vì không xác thực được chữ ký.");
                        }




                    } catch (IllegalStateException e) {
                        // Public Key không hợp lệ/ không tồn tại
                        OrderService.getInstance().cancelOrder(orderId);
                        String message = "Khoá mà bạn sử dụng để thực hiện ký đơn hàng " + orderId + "đã không còn hoạt động nữa. Đơn hàng của bạn đã bị huỷ!";
                        MailService.sendNotifyCanceledOrder(UserService.getInstance().getUserById(order.getUserId() + "").getEmail(), order, message);
                        resp.getWriter().write("Đơn hàng bị huỷ vì khoá công khai không còn hoạt động.");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        resp.getWriter().write("Lỗi trong quá trình hash đơn hàng.");
                    } catch (Exception e) {
                        resp.getWriter().write("Đã xảy ra lỗi không mong muốn.");
                    }
                } else if (action.equals("preparingOder")) {
                    OrderService.getInstance().preparingOder(orderId);
                    String message = sessionUser.nameAsRole() + " " + sessionUser.getName() + " đã xác nhận đang giao đơn hàng có mã " + orderId;
                    LoggingService.getInstance().log(ELevel.INFORM, EAction.UPDATE, req.getRemoteAddr(), message);
                } else if (action.equals("cancelOrder")) {
                    String cancelReason = req.getParameter("cancelReason");
                    MailService.sendNotifyCanceledOrder(UserService.getInstance().getUserById(order.getUserId() + "").getEmail(), order, cancelReason == null ? "" : cancelReason);
                    OrderService.getInstance().cancelOrder(orderId);
                    String message = sessionUser.nameAsRole() + " " + sessionUser.getName() + " đã hủy bỏ đơn hàng có mã " + orderId;
                    LoggingService.getInstance().log(ELevel.INFORM, EAction.UPDATE, req.getRemoteAddr(), message);
                    resp.getWriter().write("(" + OrderService.getInstance().waitConfirmOrdersNumber() + ")");
                }
            }
//            showOrder, cancel Order from customer,
            if (action.equals("showOrder")) {
                Order o = OrderService.getInstance().getOrderById(orderId);
                User u = UserService.getInstance().getUserById(o.getUserId() + "");
                List<OrderDetailMapping> orderDetailMappings = new ArrayList<>();
                Product product;
                String image;
                for (OrderDetail od : OrderService.getInstance().getOrderDetailsByOrderId(orderId)) {
                    product = ProductService.getInstance().getProductById(od.getProductId());
                    image = ImageService.pathImageOnly(product.getId());
                    orderDetailMappings.add(new OrderDetailMapping(od, product.getName(), image));
                }
                OrderDetailsResponse orderDetailsResponse = new OrderDetailsResponse(o, u, orderDetailMappings);
                Gson gson = new Gson();
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                String jsonResponse = gson.toJson(orderDetailsResponse);
                resp.getWriter().write(jsonResponse);
            } else if (action.equals("customerCancelOrder")) {
                if (OrderService.getInstance().getOrderById(orderId).getUserId() == sessionUser.getId()) {
                    String cancelReason = req.getParameter("cancelReason");
                    OrderService.getInstance().cancelOrder(orderId);


                    String message = sessionUser.isUser() ? "Khách hàng " + sessionUser.getEmail() + " đã hủy bỏ đơn hàng có mã " + orderId : sessionUser.getName() + " đã hủy bỏ đơn hàng có mã " + orderId;
                    LoggingService.getInstance().log(ELevel.ALERT, EAction.UPDATE, req.getRemoteAddr(), message);

                    int cancelOrderNumber = OrderService.getInstance().getCancelOrderNumber(sessionUser.getId());
                    if (cancelOrderNumber >= 3) {
                        LoggingService.getInstance().log(ELevel.WARNING, EAction.UPDATE, req.getRemoteAddr(), "Tài khoản " + sessionUser.getEmail() + " có dấu hiệu hủy bỏ quá nhiều đơn hàng: Tổng số đơn đã hủy là " + cancelOrderNumber);
                    }

                    MailService.sendNotifyCanceledOrder(UserService.getInstance().getUserById(order.getUserId() + "").getEmail(), order, cancelReason == null ? "" : cancelReason);
                    resp.getWriter().write(String.valueOf(OrderService.getInstance().getOrderById(orderId).getStatus()));
                }
            }
        } else {
            if (action.equals("moreOrder")) {
                String userId = req.getParameter("userId");
                String limit = req.getParameter("limit");
                String offset = req.getParameter("offset");
                String statusNumber = req.getParameter("statusNumber");
                try {
                    List<Order> moreOrders = (statusNumber != null && (
                            statusNumber.equals("0")
                                    || statusNumber.equals("1")
                                    || statusNumber.equals("2")
                                    || statusNumber.equals("3")
                                    || statusNumber.equals("4"))) ?
                            OrderService.getInstance().getOrderByCustomerId(userId, Integer.parseInt(limit), Integer.parseInt(offset), Integer.parseInt(statusNumber)) :
                            OrderService.getInstance().getOrderByCustomerId(userId, Integer.parseInt(limit), Integer.parseInt(offset));
                    List<OrderResponse> orderResponses = new ArrayList<>();
                    HashMap map;
                    Product p;
                    String imagePath;
                    OrderResponse orderResponse;
                    for (Order o : moreOrders) {
                        map = new HashMap();
                        for (OrderDetail orderDetail : OrderService.getInstance().getOrderDetailsByOrderId(o.getId()
                                + "")) {
                            p = ProductService.getInstance().getProductById(orderDetail.getProductId() + "");
                            imagePath = ImageService.pathImageOnly(p.getId());
                            map.put(p.getName(), imagePath);
                        }
                        orderResponse = new OrderResponse(o, map);
                        orderResponses.add(orderResponse);
                    }

                    Gson gson = new Gson();
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    String jsonResponse = gson.toJson(orderResponses);
                    resp.getWriter().write(jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (action.equals("getNumberFilter")) {
                String userId = req.getParameter("userId");
                String statusNumber = req.getParameter("statusNumber");
                if (userId != null) {
                    try {
                        int number = (statusNumber != null) ? OrderService.getInstance().ordersNumber(userId, Integer.parseInt(statusNumber)) : OrderService.getInstance().ordersNumber(userId);
                        resp.getWriter().write(String.valueOf(number));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private String generateSHA256Hash (String jsonString) throws NoSuchAlgorithmException {

        System.out.println("-------------------------");
        System.out.println("File string: " + jsonString);


        //Tạo mã SHA-256.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(jsonString.getBytes(StandardCharsets.UTF_8));

        //chuyển đổi byte[] thành string.
        StringBuilder hexString = new StringBuilder();
        for(byte b: hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private boolean isValidOrder(RSAPublicKey publicKey, String hashOrder, String signature) {


        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);

            sig.update(hashOrder.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = Base64.getDecoder().decode(signature);

            return sig.verify(signatureBytes);


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Phương thức chuyển đổi PEM thành RSAPublicKey
    private RSAPublicKey getRSAPublicKeyFromPEM(String publicKeyPem) throws Exception {
        String publicKeyPEM = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
}
