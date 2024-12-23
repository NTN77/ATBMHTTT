package controller.security;

import model.service.KeyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "KeyController", value = "/add-public-key")
public class KeyController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Lấy dữ liệu từ Request.
        String title = req.getParameter("title");
        String publicKey = req.getParameter("publicKey");
        String userIdParam = req.getParameter("userId");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            //Validation dữ liệu đầu vào.
            validateKeyInput(title, publicKey);

            int userId = Integer.parseInt(userIdParam);

            if (KeyService.getInstance().isExistPublicKey(userId, title)) {
                resp.getWriter().write("{\"success\": false, \"message\": \"Tên khóa đã tồn tại. Vui lòng sử dụng tên khóa khác!\"}");
            } else {
                KeyService.getInstance().insertPublicKey(title, publicKey, userId);
                resp.getWriter().write("{\"success\": true, \"message\": \"Thêm khoá thành công!\"}");
            }
        } catch (IllegalArgumentException e) {
            resp.getWriter().write(String.format("{\"success\": false, \"message\": \"%s\"}", e.getMessage()));

        } catch (Exception e) {
            resp.getWriter().write("{\"success\": false, \"message\": \"Lỗi khi thêm khoá!\"}");
        }

    }


    private void validateKeyInput(String title, String publicKey) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khoá không được để trống");
        }
        if (title.length() > 50) {
            throw new IllegalArgumentException("Tên khoá key không được phép dài quá 50 ký tự");
        }
        if (title.contains(" ")) {  // Kiểm tra nếu title chứa khoảng trắng
            throw new IllegalArgumentException("Tên khoá không được chứa khoảng trắng");
        }

        if (publicKey == null || publicKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Trường Public Key không được để trống.");
        }
        if (!isValidPem(publicKey)) {
            throw new IllegalArgumentException("Public Key không đúng định dạng PEM.");
        }


    }

    // Kiểm tra định dạng pem.
    private boolean isValidPem(String publicKey) {
        return publicKey.startsWith("-----BEGIN PUBLIC KEY-----") &&
                publicKey.endsWith("-----END PUBLIC KEY-----") &&
                publicKey.contains("\n");
    }
}
