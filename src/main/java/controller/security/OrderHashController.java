package controller.security;

import com.google.common.collect.BoundType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.bean.Cart;
import model.bean.HashInput;
import model.bean.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@WebServlet("/create-hash-order")
public class OrderHashController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("auth");
        Cart cart = (Cart) session.getAttribute("cart");

        if(user == null || cart == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\": false, \"message\": \"Lỗi lấy dữ liệu từ người dùng và giỏ hàng\"}");
            return;
        }

        String shippingFee = req.getParameter("shippingFee");
        String totalAmount = req.getParameter("totalAmount");



        if (shippingFee == null || totalAmount == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Trả về lỗi 400 nếu dữ liệu không hợp lệ
            resp.getWriter().print("{\"success\": false, \"message\": \"Lỗi khi lấy phí ship hoặc tổng tiền.\"}");
            return;
        }


        HashInput hashInput = new HashInput();
        hashInput.setUserId(user.getId());
        hashInput.setShippingFee(Integer.parseInt(shippingFee));
        hashInput.setTotalPrice(Integer.parseInt(totalAmount));
        hashInput.setCartInfo(cart);
        System.out.println("In OrderHashController: " + hashInput);
        try {
            String hash = generateSHA256Hash(hashInput);
            File hashFile = createHashFile(hash);


            // gửi:
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=\"order_hash.txt\"");
            try (OutputStream outputStream = resp.getOutputStream()) {
                Files.copy(hashFile.toPath(), outputStream);
                outputStream.flush(); // Đảm bảo dữ liệu được gửi hết
            } finally {
                // Xóa file tạm sau khi gửi
                if (hashFile.exists()) {
                    hashFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"Đã xảy ra lỗi trong quá trình tạo mã băm.\"}");
        }
    }




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

    // Tạo file để tải xuống.
    private File createHashFile(String hash) throws IOException {
        File hashFile = File.createTempFile("order_hash", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(hashFile))) {
            writer.write(hash);
        }
        return hashFile;
    }

}
