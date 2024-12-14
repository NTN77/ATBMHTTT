package controller.giaohangnhanh;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet(value = "/shippingFee")
public class ShippingFee extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String apiUrl = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee?service_id=53321&service_type_id=2";
        String token = "764f6d03-4121-11ef-8e53-0a00184fe694";
        String shopId = "192867";
        String totalPrice = req.getParameter("tempPrice");
        String wardCode = req.getParameter("wardCode");
        String districtId = req.getParameter("districtId");
        String weight = req.getParameter("totalWeight");
        System.out.println("totalPrice:" + totalPrice + "wardCode: " + wardCode + "districtId: " + districtId + "weight: "+ weight);
        try {
            URL url = new URL(apiUrl+ "&insurance_value=" + totalPrice+ "&to_ward_code="+ wardCode+ "&to_district_id="+ districtId + "&weight=" + weight);
            System.out.println("URL : " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token",token);
            conn.setRequestProperty("shop_id", shopId);
            conn.setRequestProperty("Content-Type", "application/json");


            // Get the response code
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response body
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                reader.close();

//                Parse Json từ response
                String jsonResponse = responseBuilder.toString();
                System.out.println("JSON Response: " + jsonResponse);
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                // lấy dữ liệu tại field data.
                int totalFee = jsonObject.getAsJsonObject("data").get("total").getAsInt();
//
                req.setAttribute("shippingFee", totalFee);
                System.out.println("Total: " + totalFee);
                req.getRequestDispatcher("/views/PaymentPage/payment.jsp");
                // Set content type and write JSON response to Servlet response
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(jsonResponse);
            } else {
                // Handle non-OK response
                resp.setStatus(responseCode);
                resp.getWriter().write("Failed to fetch data from API. Status code: " + responseCode);
            }

            conn.disconnect();

        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Internal Server Error Occurred");
        }

    }
}