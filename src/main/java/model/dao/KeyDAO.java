package model.dao;

import model.bean.Key;
import model.bean.KeyOrderDTO;
import model.db.JDBIConnector;

import java.util.List;

public class KeyDAO {

    /**
     * Thêm public key mới từ user người dùng
     */

    public static void insertPublicKey(String title, String publicKey, int userId) {
        JDBIConnector.me().useHandle(handle -> {
            handle.createUpdate("INSERT INTO key_user (title,publicKey, userId) VALUES (:title,:publicKey,:userId)")
                    .bind("title", title)
                    .bind("publicKey", publicKey)
                    .bind("userId", userId)
                    .execute();
        });
    }

    /**
     * Có 3 status của public key gồm:
     * 1 = active : key đang được sử dụng.
     * Thông tin hiển thị:
     *
     * 2 = revoked : key đã bị thu hồi vĩnh viễn do bị lộ.
     * 3 = archived : key do người dùng xoá do không còn sử dụng nữa.
     *
     */
        public static List<Key> getPublicKeyActiveByUserId(int userId) {
            String sql = "select id, title, publicKey, status, createdTime, updatedTime, userId from key_user WHERE userId = :userId AND status = 1";
            List<Key> keys = JDBIConnector.me().withHandle(handle ->  handle.createQuery(sql)
                    .bind("userId", userId)
                    .mapToBean(Key.class)
                    .list()
            );
            return keys;
        }

    /**
     * HIển thị thông tin khoá trong trang thanh toán.
     */

    public static List<KeyOrderDTO> getInformationPublicKeyForPayment(int userId) {
        String sql = "Select id, title from key_user where userId = :userId and status =1";
        List<KeyOrderDTO>keys = JDBIConnector.me().withHandle(
                handle -> handle.createQuery(sql)
                        .bind("userId", userId)
                        .map((rs, ctx) -> new KeyOrderDTO(
                                rs.getInt("id"),
                                rs.getString("title")
                        )).list()
        );
        return keys;
    }

    /**
     * Phục vụ cho quá trình xác minh đơn hàng.
     * 1. Từ id public key : Kiểm tra trạng thái key.
     * + Nếu active =1, okey cho phép get key.
     * + Nếu active #, okey key này đã hỏng.
     */
    public static String getActivePublicKeyByKeyID(int keyId) {
        String sql = "SELECT publicKey, status FROM key_user WHERE id = :keyId";
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("keyId", keyId)
                        .map((rs, ctx) -> {
                            int status = rs.getInt("status");
                            if (status == 1) {
                                return rs.getString("publicKey");
                            } else {
                                throw new IllegalStateException("Khoá không hợp lệ. Trạng thái: " + status);
                            }
                        })
                        .findOne()
                        .orElseThrow(() -> new IllegalArgumentException("Khoá không tồn tại."))
        );
    }

    public static void main(String[] args) {
        System.out.println("key nè");
        System.out.println(  getInformationPublicKeyForPayment(15));

    }



}
