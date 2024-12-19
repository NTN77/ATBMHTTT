package controller;

import com.google.gson.Gson;
import io.leangen.geantyref.TypeToken;
import logs.EAction;
import logs.ELevel;
import logs.LoggingService;
import model.bean.Order;
import model.bean.OrderDetail;
import model.bean.User;
import model.dao.KeyDAO;
import model.dao.OrderDAO;
import model.dao.ProductDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import model.service.ImageService;
import model.service.JavaMail.MailService;


@WebServlet("/reportKey")
public class ReportKey extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/jsp; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");

        HttpSession sessions = req.getSession();
        User user = (User) sessions.getAttribute("auth");

        String continues = req.getParameter("continueReport");

        HttpSession session = req.getSession();
        if(continues.equals("otp")) {
            System.out.println("Gửi mail");
            String otp = MailService.sendCodeOTPReport(user.getEmail());
            System.out.println(otp);
            session.setAttribute("otp" , otp);
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("sendSuccess");
            return;
        }

        //formatTien
        Locale locale = new Locale("vi", "VN");
        Currency currency = Currency.getInstance(locale);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);


        String title = req.getParameter("publicKey"); //publicKey title
        int idKey = KeyDAO.getIdKey(title);

        String inputDateStr = req.getParameter("inputDisclosureDate");//ngày lộ key
        String reasonable = req.getParameter("inputSituation"); // lý do lộ
        String inputOTP = req.getParameter("inputOTP");
        LocalDateTime expiredDate = LocalDateTime.now();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(inputDateStr, formatter);
        Timestamp inputDate = Timestamp.valueOf(localDateTime);
        Timestamp expiredDateFormat = Timestamp.valueOf(expiredDate);



        if(continues.equals("true")) {
            String otp = (String) session.getAttribute("otp");
            System.out.println(otp);
           if(otp.equals(inputOTP)){
               List<Order> orderList = ProductDAO.AllOrderBeweenDate(inputDate, expiredDateFormat,user.getId(),idKey);
               resp.setContentType("text/html");

               // Tạo đối tượng PrintWriter để viết dữ liệu vào response
               PrintWriter out = resp.getWriter();

               if(orderList.isEmpty()){
                   out.println("<div style='background: #c4e2fd; width: 80%'>");
                   out.println("<div><p class='text-center fw-bold fs-3'>Xác Minh Đơn Hàng Trong Thời Gian Bị Lộ</p></div>");
                   out.println("<div><p class = 'text-center text-danger'>Không có đơn hàng nào được đặt từ thời gian lộ key đến thời gian hiện tại!</p></div>");
                   out.println("<div><p class = 'text-center fst-italic'>(Hãy nhấn nút report key!)</p></div>");
                   out.println("<div class='my-3 d-flex justify-content-center align-items-center'>");
                   out.println("<button id='reportKey' type='submit' style='background: red;color: white ;' class='fw-bold p-2' onclick='ReportKey()'>Report Key</button>");
                   out.println("</div>");
               }else {
                   // Trả về danh sách đơn hàng dưới dạng HTML
                   out.println("<div style='background: #c4e2fd; width: 80%'>");
                   out.println("<div><p class='text-center fw-bold fs-3'>Xác Minh Đơn Hàng Trong Thời Gian Bị Lộ</p></div>");
//<%--Tạo scroll--%>

                   out.println("<div style='height: 75vh; overflow-y: auto;white-space: nowrap ;'>");
                   int count = 0;
                   for (Order o : orderList) {
                       count++;//đếm số order
                       out.println("<div style = 'border: 1px solid rgb(128,128,128); border-radius: 10px ; background: honeydew;' class='mx-3 mb-3' >");
                       out.println("<p class='text-center fw-bold fs-5 text-danger' > Đơn Hàng #" + o.getId() + " </p >");
                       out.println("<div>");
                       out.println("<table >");
                       out.println("<thead >");
                       out.println("<tr class='text-center border-top' >");
                       out.println("<th scope = 'col' class='col-3 ' > Sản Phẩm </th >");
                       out.println("<th scope = 'col' class='col-1 ' > Thời Gian Đặt</th >");
                       out.println("<th scope = 'col' class='col-2' > Địa Chỉ </th >");
                       out.println("<th scope = 'col' class='col-1' > Tổng Tiền </th >");
                       out.println("<th scope = 'col' class='col-1' > Trạng Thái </th >");
                       out.println("<th scope = 'col' class='col-2' > Xác Minh </th >");
                       out.println("</tr >");
                       out.println("</thead >");
                       out.println("<tbody >");
                       out.println("<tr >");
                       out.println("<th scope = 'col' style = 'border-top: 1px solid black;' >");
                       out.println("<div style = 'height: 30vh; overflow-y: auto;white-space: nowrap;' >");
                       List<OrderDetail> orderDetailList = ProductDAO.ListOrderDetain(o.getId());
                       for (OrderDetail od : orderDetailList) {
                           out.println("<div class='d-flex m-2' >");

                           out.println("<div ><img src ='" + req.getContextPath() + "/" + ImageService.pathImageOnly(od.getProductId()) + "' alt = '' width = '100px' ></div >");

                           out.println("<div style = 'word-wrap: break-word; word-break: break-word; white-space: normal; font-size: 15px; font-style: italic; color: #f30404' class='d-flex align-items-center justify-content-center ms-2 '>" + ProductDAO.NameProduct(od.getProductId()) + "(x" + od.getQuantity() + ")</div >");

                           out.println("</div >");
                       }
                       out.println("</div >");
                       out.println("</th >");
                       out.println("<th scope = 'col' class='text-center' style = 'border: 1px solid black' >" + o.getOrderDate() + "</th >");
                       out.println("<th scope = 'col' class='text-center' style = 'border: 1px solid black' >" + o.getAddress() + "</th >");
                       out.println("<th scope = 'col' class='text-center' style = 'border: 1px solid black ;color: red' >" + numberFormat.format(o.getTotalPrice()) + "</th >");
                       String re = null;
                       if (o.getStatus() == 0) {
                           re = "Cần xác nhận";
                       } else if (o.getStatus() == 1) {
                           re = "Đang đóng gói";
                       } else if (o.getStatus() == 2) {
                           re = "Đang giao";
                       } else if (o.getStatus() == 4) {
                           re = "Đã hủy";
                       } else if (o.getStatus() == 3) {
                           re = "Thành công";
                       }
                       out.println("<th scope = 'col' class='text-center' style = 'border: 1px solid black' >" + re + "</th >");
                       out.println("<th scope = 'col' class='text-center' style = 'border: 1px solid black' >");
                       out.println("<div class='mb-2' >");
                       out.println("<button  onclick='showTick(" + o.getId() + ",this)' id='verifyTrue" + o.getId() + "' class='buttonCheck w-75 p-2' > Là Tôi Đặt</button >");
                       out.println("</div >");
                       out.println("<div >");
                       out.println("<button  onclick='showTick(" + o.getId() + ",this)' id='verifyFalse" + o.getId() + "' class='buttonCheck w-75 p-2' > Không Phải Tôi ! </button >");
                       out.println("</div >");
                       out.println("<div class='icon'  id='tickIcon" + o.getId() + "' style='display: none'><i class='fa-solid fa-check fs-1' style='color: red'></i></div>");
                       out.println("</th >");
                       out.println("</tr >");
                       out.println("</tbody >");
                       out.println("</table >");
                       out.println("</div >");
                       out.println("</div >");
                   }
                   out.println(" <div style='display: none' id='countOrder'>" + count + "</div>");
                   out.println("<div class='my-3 d-flex justify-content-center align-items-center'>");
                   out.println("<button id='reportKey' type='submit' class='fw-bold p-2' disabled onclick='ReportKey()'>Report Key</button>");
                   out.println("</div>");
                   out.println("</div>");
                   out.println("</div>");
               }

               }else{
               resp.setContentType("text/plain");
               resp.setCharacterEncoding("UTF-8");
               resp.getWriter().write("incorrect");
           }

        }else if(continues.equals("report")){
            String idOrderCancel = req.getParameter("idOrderCancel");

            if(!idOrderCancel.isEmpty()){
                String[] orderIds = idOrderCancel.split(",");
                for(int i = 0 ; i < orderIds.length ; i++) {
                    OrderDAO.setStatusOrder(Integer.parseInt(orderIds[i]));
                    Order order = OrderDAO.getOrderById(orderIds[i]);
                    assert order != null;
                    MailService.sendNotifyCanceledOrder(user.getEmail(),order,"Không phải người dùng đặt trong thời gian lộ key!");
                    System.out.println("Hoàn thành setStatus:" + orderIds[i]);
                }
            }

            KeyDAO.updateKey(idKey,expiredDateFormat);
            MailService.sendNotifyReportKey(user.getEmail(),idKey,reasonable,title);

            LoggingService.getInstance().log(ELevel.ALERT, EAction.UPDATE, req.getRemoteAddr(), user.getEmail() + " đã report key: " + title + "\n" + "Thời Gian Lộ Key: " + inputDate + "\n" + "Thời Gian report: " + expiredDateFormat );

            String redirectUrl = req.getContextPath() + "/views/ReportPKey/ReportKey.jsp";

            // Trả về URL dưới dạng JSON
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print("{\"redirect\": \"" + redirectUrl + "\"}");
            out.flush();
        }

    }
}
