package model.dao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.bean.*;
import model.db.JDBIConnector;
import model.service.DiscountService;
import model.service.OrderService;
import model.service.UserService;
import org.h2.util.json.JSONObject;
import org.jdbi.v3.core.Jdbi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrderDAO {
    public static List<Order> getAllOrder() {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` order by orderDate desc")
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static Order getOrderById(String orderId) {
        Optional<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where id=:orderId")
                        .bind("orderId", orderId)
                        .mapToBean(Order.class)
                        .stream().findFirst()
        );
        return orders.isEmpty() ? null : orders.get();
    }

    public static List<OrderDetail> getOrderDetailsByOrderId(String orderId) {
        List<OrderDetail> orderDetails = JDBIConnector.me().withHandle(detail_handle ->
                detail_handle.createQuery("select * from order_details where orderId=:orderId")
                        .bind("orderId", orderId)
                        .mapToBean(OrderDetail.class).stream().collect(Collectors.toList())
        );
        return orderDetails;
    }

    public static int ordersNumber(String customerId, int status) {
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select count(id) from `order` where userId=? and status=?")
                        .bind(0, customerId)
                        .bind(1, status)
                        .mapTo(Integer.class).one());
    }

    public static int ordersNumber(String userId) {
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select count(id) from `order` where userId=?")
                        .bind(0, userId)
                        .mapTo(Integer.class).one());
    }

    public static long waitConfirmOrdersNumber() {
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select count(id) from `order` where status=0")
                        .mapTo(Long.class).one());
    }

    public static long isPreparingOrdersNumber() {
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select count(id) from `order` where status=1")
                        .mapTo(Long.class).one());
    }


    public static long deliveringOrdersNumber() {
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select count(id) from `order` where status=2")
                        .mapTo(Long.class).one());
    }

    public static List<Order> getWaitConfirmOrders() {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where status=0 order by orderDate desc")
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static List<Order> getPreparingOrders() {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where status=1 order by orderDate desc")
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static List<Order> getDeliveringOrders() {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where status=2 order by orderDate desc")
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static List<Order> getCanceledOrders() {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where status=4 order by orderDate desc")
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static List<Order> getSucccessfulOrders() {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where status=3 order by orderDate desc")
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static List<Order> getOrderByCustomerId(String customerId) {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where userId=? order by orderDate desc")
                        .bind(0, customerId)
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static List<Order> getOrderByCustomerId(String customerId, int limit, int offset) {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where userId=? order by orderDate desc LIMIT ? OFFSET ?")
                        .bind(0, customerId)
                        .bind(1, limit)
                        .bind(2, offset)
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static List<Order> getOrderByCustomerId(String customerId, int limit, int offset, int statusNumber) {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where userId=? and status=? order by orderDate desc LIMIT ? OFFSET ?")
                        .bind(0, customerId)
                        .bind(1, statusNumber)
                        .bind(2, limit)
                        .bind(3, offset)
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static List<Order> getOrderByCustomerNamePart(String customerNamePart) {
        List<Order> orders = JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select * from `order` where userId IN(" +
                                "select id from user where name like ?) order by orderDate desc")
                        .bind(0, "%" + customerNamePart + "%")
                        .mapToBean(Order.class)
                        .stream().collect(Collectors.toList())
        );
        return orders;
    }

    public static void cancelOrder(String orderId) {
        JDBIConnector.me().useHandle(handle ->
                handle.createUpdate("UPDATE `order` SET status = 4 WHERE id=?")
                        .bind(0, orderId)
                        .execute()
        );
    }

    public static void confirmOrder(String orderId) {
        JDBIConnector.me().useHandle(handle ->
                handle.createUpdate("UPDATE `order` SET status = 1 WHERE id=?")
                        .bind(0, orderId)
                        .execute()
        );
    }

    public static void preparingOder(String orderId) {
        JDBIConnector.me().useHandle(handle ->
                handle.createUpdate("UPDATE `order` SET status = 2 WHERE id=?")
                        .bind(0, orderId)
                        .execute()
        );
    }

    public static void deliveredOrder(String orderId) {
        JDBIConnector.me().useHandle(handle ->
                handle.createUpdate("UPDATE `order` SET status = 3 WHERE id=?")
                        .bind(0, orderId)
                        .execute()
        );
    }

    public static double getRevenueForMonth(int month, int year) {
        String sql = "SELECT SUM(totalPrice) FROM `order` WHERE MONTH(orderDate) = ? AND YEAR(orderDate) = ? AND status=3";
        Double re = JDBIConnector.me().withHandle(handle ->
                handle.createQuery(sql)
                        .bind(0, month)
                        .bind(1, year)
                        .mapTo(Double.class).one());
        return re != null ? re : 0;
    }

    public static double getMonthRevenueMax(int year) {
        String sql = "SELECT MAX(totalPrice) FROM (SELECT SUM(totalPrice) AS totalPrice FROM `order` WHERE YEAR(orderDate) = ? AND status=3 GROUP BY MONTH(orderDate)) AS monthlyRevenue";
        Double re = JDBIConnector.me().withHandle(handle ->
                handle.createQuery(sql)
                        .bind(0, year)
                        .mapTo(Double.class).one());
        return re != null ? re : 0;
    }

    public static int getCancelOrderNumber(int userId) {
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery("select count(id) from `order` where userId=? and status=4")
                        .bind(0, userId)
                        .mapTo(Integer.class).one());
    }

    /**
     * Thêm giỏ hàng - đặt hàng vào dâtabase !
     *
     * @param order
     * @param cart
     * @param u
     */
    public void addOrder(Order order, Cart cart, User u) {
        // Kiểm tra order có null không?
        if (order == null) {
            throw new IllegalArgumentException("Order object is null");
        }

        LocalDateTime curDate = LocalDateTime.now();
        String date = curDate.toString();

        // Lệnh SQL chèn vào bảng order
        String sql = "INSERT INTO `order` (totalPrice, orderDate, status, consigneeName, consigneePhoneNumber, address, shippingFee, userId, note, publicKeyId, signature, hashCode)"
                + " VALUES(:totalPrice, :orderDate, :status, :consigneeName, :consigneePhoneNumber, :address, :shippingFee, :userId, :note, :publicKeyId, :signature, :hashCode)";
        try {
            JDBIConnector.me().useHandle(handle -> {

                //Lấy id của order
                int orderId = handle.createUpdate(sql)
                        .bind("totalPrice", order.getTotalPrice())
                        .bind("orderDate", date)
                        .bind("status", 0)
                        .bind("consigneeName", order.getConsigneeName())
                        .bind("consigneePhoneNumber", order.getConsigneePhoneNumber())
                        .bind("address", order.getAddress())
                        .bind("shippingFee", order.getShippingFee())
                        .bind("userId", u.getId())
                        .bind("note", order.getNote())
                        .bind("publicKeyId", order.getPublicKeyId())
                        .bind("signature", order.getSignature())
                        .bind("hashCode", order.getHashCode())
                        .executeAndReturnGeneratedKeys("id")
                        .mapTo(Integer.class)
                        .one();
                System.out.println("check 0: " + orderId);
                for (Item i : cart.getItems().values()) {
                    System.out.println("check 1: " + i.getProduct().getId());
                    //add vào bảng order-details !
                    String sql1 = "INSERT INTO `order_details` (orderId, productId, quantity, sellingPrice, finalSellingPrice)"
                            + "VALUES(:orderId, :productId, :quantity, :sellingPrice, :finalSellingPrice)";
                    JDBIConnector.me().useHandle(handle1 -> {
                        handle1.createUpdate(sql1)
                                .bind("orderId", orderId)
                                .bind("productId", i.getProduct().getId())
                                .bind("quantity", i.getQuantity())
                                .bind("sellingPrice", i.getProduct().getSellingPrice())
                                .bind("finalSellingPrice", i.getPrice())
                                .execute();
                    });

                    System.out.println("check final: ");
                }

            });

//            cập nhật lại số lượng sản phẩm
            String sql3 = "UPDATE `inventory` SET soldOut= soldOut + :quantityOrder where productId= :id";
            for (Item item : cart.getItems().values()) {
                JDBIConnector.me().useHandle(handle3 ->
                        handle3.createUpdate(sql3)
                                .bind("quantityOrder", item.getQuantity())
                                .bind("id", item.getProduct().getId())
                                .execute());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert order into the database", e);
        }
    }

    public static void setStatus (int orderId, int status) {
        String sql = "UPDATE `order` SET status = :status WHERE id = :orderId";

        try {
            JDBIConnector.me().useHandle(handle -> {
                handle.createUpdate(sql)
                        .bind("status", status)      // Gắn giá trị status
                        .bind("orderId", orderId)    // Gắn giá trị orderId
                        .execute();                  // Thực thi câu lệnh
            });
            System.out.println("Order status updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update order status", e);
        }
    }


    //Xây dựng file json dựa vào database đã insert vào server.
    public static String getOrderJsonById(String orderId) {
        Jdbi jdbi = JDBIConnector.me();

        // Lấy thông tin đơn hàng từ bảng `order`
        Map<String, Object> orderData = jdbi.withHandle(handle ->
                handle.createQuery("SELECT userId, shippingFee, totalPrice FROM `order` WHERE id = :orderId")
                        .bind("orderId", orderId)
                        .mapToMap()
                        .findOnly()
        );

        // Kiểm tra các giá trị `null`
        int userId = orderData.get("userid") != null ? ((Number) orderData.get("userid")).intValue() : -1;
        double shippingFee = orderData.get("shippingfee") != null ? ((Number) orderData.get("shippingfee")).doubleValue() : 0.0;
        double totalPrice = orderData.get("totalprice") != null ? ((Number) orderData.get("totalprice")).doubleValue() : 0.0;

        // Lấy chi tiết đơn hàng từ bảng `order_details`
        List<Map<String, Object>> orderDetails = jdbi.withHandle(handle ->
                handle.createQuery("SELECT productId, quantity, sellingPrice FROM `order_details` WHERE orderId = :orderId")
                        .bind("orderId", orderId)
                        .mapToMap()
                        .list()
        );

        // Tạo đối tượng JSON với Gson
        JsonObject orderJson = new JsonObject();
        orderJson.addProperty("userId", userId);
        orderJson.addProperty("shippingFee", shippingFee);
        orderJson.addProperty("totalPrice", totalPrice);

        JsonObject cartInfo = new JsonObject();
        JsonObject items = new JsonObject();

        if (!orderDetails.isEmpty()) {
            for (Map<String, Object> detail : orderDetails) {
                JsonObject productJson = new JsonObject();
                productJson.addProperty("id", ((Number) detail.get("productid")).intValue());

                JsonObject itemJson = new JsonObject();
                itemJson.add("product", productJson);
                itemJson.addProperty("quantity", ((Number) detail.get("quantity")).intValue());
                itemJson.addProperty("price", ((Number) detail.get("sellingprice")).doubleValue());

                items.add(String.valueOf(((Number) detail.get("productid")).intValue()), itemJson);
            }
        }

        cartInfo.add("items", items);
        orderJson.add("cartInfo", cartInfo);

        // Chuyển đổi sang chuỗi JSON
        Gson gson = new Gson();
        return gson.toJson(orderJson);

    }



    public static void main(String[] args) {
        // Giả sử có Order ID trong cơ sở dữ liệu, ví dụ: "O123"
        String orderId = "42";

        try {
            // Gọi phương thức getOrderJsonById để lấy JSON
            String orderJson = OrderDAO.getOrderJsonById(orderId);

            // In ra JSON kết quả
            System.out.println("JSON Data for Order ID " + orderId + ":");
            System.out.println(orderJson);
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu có lỗi xảy ra
            System.err.println("Error retrieving order data: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static double getExactlyTotalPriceNoShippingFee(String orderId) {
        double re = 0;
        for (OrderDetail orderDetail : OrderService.getInstance().getOrderDetailsByOrderId(orderId))
            re += orderDetail.getQuantity() * orderDetail.getFinalSellingPrice();

        return re;
    }


}
