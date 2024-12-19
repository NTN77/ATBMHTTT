package controller.ajax_handle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import controller.Validation;
import logs.EAction;
import logs.ELevel;
import logs.LoggingService;
import model.bean.User;
import model.service.ImageService;
import model.service.UserService;
import utils.HashPassword;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/user-ajax-handle")
public class UserAjaxHandle extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = (User) req.getSession().getAttribute("auth");
        System.out.println("CÓ ĐẾN ƯỢC ĐOẠN LÀY KHÔNG1?");
        String action = req.getParameter("action");
        if (sessionUser != null && (sessionUser.isOwn() || sessionUser.isAdmin())) {
            if (action != null) {
                String userId = req.getParameter("userId");

                //Xử lý xác thực mật khẩu người dùng.
                if(action.equals("verifyPwd")) {

                    if (action.equals("verifyPwd")) {
                        String userIdMess = req.getParameter("userId");
                        String inputPwd = req.getParameter("password");
                        System.out.println("Giá trị: " + userIdMess + inputPwd);

                        // Kiểm tra mật khẩu người dùng
                        boolean isVerifyPwd = UserService.getInstance().verifyPwd(userIdMess, inputPwd);
                        System.out.println("Mật khẩu người dùng có id " + userIdMess + " là " + isVerifyPwd);

                        // Trả về kết quả true/false mà không cần JSON phức tạp
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        resp.getWriter().write(String.valueOf(isVerifyPwd)); // Gửi true hoặc false
                    }
//
                }

                else if (userId != null && action.equals("lockOrUnLock")) {
                    //lock or unlock
                    User u = UserService.getInstance().getUserById(userId);
                    if (u.getStatus().trim().startsWith("Bình Thường")) {
                        UserService.getInstance().lockUser(userId);
                        String message = sessionUser.nameAsRole() + " " + sessionUser.getName() + " đã khóa tài khoản " + u.getEmail();
                        LoggingService.getInstance().log(ELevel.INFORM, EAction.UPDATE, req.getRemoteAddr(), message);
                    } else if (u.getStatus().trim().equalsIgnoreCase("Bị Khóa")) {
                        UserService.getInstance().unlockUser(userId);
                        String message = sessionUser.nameAsRole() + " " + sessionUser.getName() + " đã bỏ khóa tài khoản " + u.getEmail();
                        LoggingService.getInstance().log(ELevel.INFORM, EAction.UPDATE, req.getRemoteAddr(), message);
                    }
                } else if (action.equals("addUser")) {
                    String uname = req.getParameter("name");
                    String utel = req.getParameter("phoneNumber");
                    String uemail = req.getParameter("email");
                    String upass = req.getParameter("password");
                    String urepass = req.getParameter("rePassword");
                    String role = req.getParameter("roleId");
                    String validateResult = "Thêm tài khoản thành công";
                    if (uname != null && !uname.isEmpty()
                            && utel != null && !utel.isEmpty()
                            && uemail != null && !uemail.isEmpty()
                            && upass != null && !upass.isEmpty()
                            && urepass != null && !urepass.isEmpty()
                            && role != null && !role.isEmpty()) {
                        System.out.println("okok");
                        if (!Validation.checkEmail(uemail)) {
                            validateResult = "Email không hợp lệ";
                        } else if (UserService.getInstance().checkEmail(uemail) != null) {
                            validateResult = "Email đã tồn tại!";
                        } else if (!Validation.checkPassword(upass)) {
                            validateResult = "Mật khẩu không hợp lệ";
                        } else if (!upass.equals(urepass)) {
                            validateResult = "Mật khẩu không trùng khớp";
                        } else if (!(role.equals("1") || role.equals("0"))) {
                            validateResult = "Lỗi phân quyền hạn";
                        } else if (!Validation.checkPhoneNumber(utel)) {
                            validateResult = "Mật khẩu không hợp lệ";
                        } else if (UserService.getInstance().isPhoneNumberExist(utel)) {
                            validateResult = "Số điện thoại đã tồn tại";
                        } else {
                            User newUser = UserService.getInstance().addUser(uname, utel, uemail, HashPassword.toSHA1(upass), Integer.parseInt(role));
                            System.out.println(newUser);
                        }
                    } else {
                        validateResult = "Thiếu dữ liệu";
                    }
                    Gson gson = new Gson();
                    System.out.println("CÓ ĐẾN ƯỢC ĐOẠN LÀY KHÔNG?");
                    String jsonRatesResponse = gson.toJson(validateResult);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    System.out.println(jsonRatesResponse);
                    resp.getWriter().write(jsonRatesResponse);
                }
            }
        }
    }
}
