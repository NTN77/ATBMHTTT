package controller.security;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.bean.Key;
import model.bean.KeyOrderDTO;
import model.service.KeyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/get-public-order")
public class GetKeyForOrder extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = Integer.parseInt(req.getParameter("userId"));
        try {
            // Lấy danh sách public keys của người dùng từ service
            List<KeyOrderDTO> keys = KeyService.getInstance().getKeyOrderDTOByUserId(userId);

            Gson gson = new Gson();

            // Convert danh sách keys thành JSON để trả về
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            String jsonResponse = gson.toJson(keys);
            resp.getWriter().write(jsonResponse);

        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"message\": \"Lỗi khi lấy khoá!\"}");
        }
    }
}