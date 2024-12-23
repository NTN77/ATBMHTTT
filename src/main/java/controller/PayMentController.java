package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logs.EAction;
import logs.ELevel;
import logs.LoggingService;
import model.bean.*;
import model.dao.OrderDAO;
import model.service.KeyService;
import model.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@WebServlet(name = "PaymentController", value = "/payment")
public class PayMentController extends HttpServlet {

    private static final int TIMEOUT_MINUTES = 15;
    private static final int TIMEOUT_SECONDS = TIMEOUT_MINUTES * 60;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession sessions = req.getSession();
        User user = (User) sessions.getAttribute("auth");
        Cart cart = (Cart) sessions.getAttribute("cart");
        Integer accessCount = (Integer) sessions.getAttribute("accessCount");
        if (accessCount == null) {
            accessCount = 1;
            System.out.println("accessCount " + accessCount);
            sessions.setAttribute("accessCount", accessCount);
        } else {
            accessCount += 1;
            sessions.setAttribute("accessCount", accessCount);
            System.out.println("accessCount " + accessCount);
            if (accessCount >= 5 && user != null) {
                LoggingService.getInstance().log(ELevel.DANGER, EAction.CREATE, req.getRemoteAddr(), user.getEmail() + " đã truy cập thanh toán nhiều lần (" + accessCount + ")");
            }
        }
        //Format gia tien
        Locale locale = new Locale("vi", "VN");
        Currency currency = Currency.getInstance(locale);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);


        // Lay cac khoa cong khai cua nguo dung.
        List<KeyOrderDTO> publicKeys = KeyService.getInstance().getKeyOrderDTOByUserId(user.getId());
        System.out.println("publicKeys TRONG CONTROLLER:  " + publicKeys);
        req.setAttribute("publicKeys", publicKeys);


        String backToCart = req.getParameter("backToCart");
        if (backToCart != null && backToCart.equals("true")) {
            increaseStockCheckout(cart);
            resp.sendRedirect(req.getContextPath() + "/views/CartPage/view/cart.jsp");
            return;
        }
//        Validation (isSale, discount & stock.
        boolean isValidCart = true;
        List<String> errorMessage = new ArrayList<>();


        for (Map.Entry<Integer, Item> entry : cart.getItems().entrySet()) {
            int productId = entry.getKey();
            Item item = entry.getValue();
            Product p = ProductService.getInstance().getProductById(productId);

            double price = ProductService.getInstance().productPriceIncludeDiscount(p);

            if (p.getIsSale() != 1) {
                isValidCart = false;
                errorMessage.add("Sản phẩm \"" + p.getName() + "\" hiện đã ngừng kinh doanh.");
            }

            if (item.getQuantity() > p.getStock()) {
                isValidCart = false;
                errorMessage.add("Sản phẩm \"" + p.getName() + "\" hiện nay chỉ còn " + p.getStock() + " sản phẩm.");
            }

            if (item.getPrice() != price) {
                isValidCart = false;
                errorMessage.add("Giá của sản phẩm \"" + p.getName() + "\" đã được cập nhật thành " + numberFormat.format(price));
            }

        }

        if (user == null) {
            resp.sendRedirect("views/Login/view_login/login.jsp");
        } else {
            if (isValidCart) {
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{ \"isValid\": true }"); // gui fan hoi json voi isValid = true
                //                doPost(req, resp);
                /// Khởi tạo thời gian cho việc get payment.
                long paymentStartTime = System.currentTimeMillis();
                Instant instant = Instant.ofEpochMilli(paymentStartTime);

                // Chuyển đổi từ Instant sang LocalDateTime
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                System.out.println("Thời gian bắt đầu : " + LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));

                //         giảm so luong stock + 5 phut bat dau
                sessions.setAttribute("paymentStartTime", paymentStartTime);
                sessions.setAttribute("timeCounter", TIMEOUT_MINUTES);
                reduceStockCheckout(cart);

            } else {
                sendErrorResponse(resp, errorMessage);
            }

        }
    }

    //
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession sessions = req.getSession();
        User user = (User) sessions.getAttribute("auth");
        Cart cart = (Cart) sessions.getAttribute("cart");
        OrderDAO orderZ = new OrderDAO();

        req.setCharacterEncoding("UTF-8");
        String namePay = req.getParameter("namePay");
        String phonePay = req.getParameter("phonePay");
        String address = req.getParameter("formattedAddress");
        String shippingFee = req.getParameter("shippingFee");
        String totalAmount = req.getParameter("totalAmount");
        String publicKeyId = req.getParameter("publicKeyId");
        String signature = req.getParameter("signature");

//check validation
        Map<String, String> errors = new HashMap<>();

        validateRequireField("namePay", namePay, "Tên hiển thị", errors);
        validateRequireField("phonePay", phonePay, "Số điện thoại", errors);
        validateRequireField("formattedAddress", address, "Địa chỉ", errors);
        validateRequireField("publicKeyId", publicKeyId, "Khoá công khai", errors);
        validateRequireField("signature", signature, "Chữ ký", errors);


        if (!errors.isEmpty() || shippingFee == null || totalAmount == null || publicKeyId == null || signature == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Trả về lỗi 400 nếu dữ liệu không hợp lệ
            resp.getWriter().print("{\"success\": false, \"message\": \"Validation errors occurred.\"}");
            return;
        }
//             KIEM TRA THOI GIAN GIAO DỊCH

        Long paymentStartTime = (Long) sessions.getAttribute("paymentStartTime");
        Instant instant = Instant.ofEpochMilli(paymentStartTime);

        // Chuyển đổi từ Instant sang LocalDateTime
        LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        System.out.println("Thời gian kết thúc: " + LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));

        if (paymentStartTime == null || System.currentTimeMillis() - paymentStartTime > TIMEOUT_SECONDS * 1000) {
            increaseStockCheckout(cart);
//                resp.sendRedirect(req.getContextPath() + "/views/CartPage/view/cart.jsp");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);// Trả về lỗi 400 nếu hết thời gian thanh toán
            resp.getWriter().print("{\"success\": false, \"message\": \"Thời gian đặt hàng đã hết.\"}");
            return;
        }
        Order order = new Order();
        order.setConsigneeName(namePay);
        order.setConsigneePhoneNumber(phonePay);
        order.setAddress(address);
        order.setShippingFee(Integer.parseInt(shippingFee));
        order.setTotalPrice(Integer.parseInt(totalAmount));
        order.setPublicKeyId(Integer.parseInt(publicKeyId));
        order.setSignature(signature);
        Integer accessCount = (Integer) sessions.getAttribute("accessCount");
        if (accessCount != null) {
            accessCount = 0;
            sessions.setAttribute("accessCount", accessCount);
        }
//      Xoá session giỏ hàng.
        orderZ.addOrder(order, cart, user);

        cart.clear();
        req.getSession().setAttribute("cart", cart);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print("{\"success\": true, \"message\": \"Đơn hàng đã được tạo.\"}");
    }

    public static void validateRequireField(String fieldName, String value, String viewName, Map<String, String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.put(fieldName, viewName + " không được bỏ trống");
        }
    }

    // Xử lý tăng - giảm lại giỏ hàng.
    private void reduceStockCheckout(Cart cart) {
        for (Map.Entry<Integer, Item> entry : cart.getItems().entrySet()) {
            int productId = entry.getKey();
            Item item = entry.getValue();
            int quantity = item.getQuantity();
            ProductService.getInstance().reduceStock(productId, quantity);
        }
    }

    private void increaseStockCheckout(Cart cart) {
        for (Map.Entry<Integer, Item> entry : cart.getItems().entrySet()) {
            int productId = entry.getKey();
            Item item = entry.getValue();
            int quantity = item.getQuantity();
            ProductService.getInstance().increaseStock(productId, quantity);
        }
    }

    // Phương thức để gửi phản hồi lỗi về cho client
    private void sendErrorResponse(HttpServletResponse response, List<String> errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(errorMessage));
    }


//    Chữ ký điện tử.

    private String generateSHA256Hash (HashInput hashInput) throws NoSuchAlgorithmException {
        //Chuyển đổi HashInput => JSON bằng GSON.
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonString = gson.toJson(hashInput);
        System.out.println("-------------------------");
        System.out.println("File string"+ jsonString);

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


    private boolean isValidOrder(String publicKeyID, String hashOrder, String signature) {
             // Lấy public key.
            String publicKeyPem = KeyService.getInstance().getPublicKeyByKeyID(Integer.parseInt(publicKeyID));

        try {



            RSAPublicKey publicKey = getRSAPublicKeyFromPEM(publicKeyPem);

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