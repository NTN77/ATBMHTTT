package model.dao;

import model.bean.Key;
import model.bean.KeyOrderDTO;
import model.db.JDBIConnector;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import java.util.List;

public class KeyDAO {

    /**
    * Kiên làm
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

  
// Nghĩa làm


    public static List<Key> getPublicKey(int userId){
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery("SELECT * FROM key_user WHERE userId =:id AND status = 1")
                        .bind("id",userId)
                        .mapToBean(Key.class)
                        .list());
    }
    public static int getIdKey(String title){
        return  JDBIConnector.me().withHandle(handle ->
                handle.createQuery("SELECT id FROM key_user WHERE title=:title")
                        .bind("title",title)
                        .mapTo(Integer.class)
                        .one());
    }

    public static int getStatusKey(int keyId){
        return JDBIConnector.me().withHandle(
                handle -> handle.createQuery("Select status from key_user WHERE id=:id")
                        .bind("id", keyId)
                        .mapTo(Integer.class)
                        .one()
        );
    }


    //Set trạng thái không dùng nữa của khóa key là 0
    public static void updateKey(int idKey, Timestamp expiredDate){
        JDBIConnector.me().useHandle(handle ->
                handle.createUpdate("UPDATE `key_user` SET updatedTime=:expiredDate , status= 0 WHERE id=:idKey")
                        .bind("expiredDate",expiredDate)
                        .bind("idKey",idKey)
                        .execute());
    }

    public static void removeKey(int idKey){
        JDBIConnector.me().useHandle(handle ->
                handle.createUpdate("UPDATE `key_user` SET status= 2 WHERE id=:idKey")
                        .bind("idKey",idKey)
                        .execute());
    }
    public static void main(String[] args) {
        System.out.println(getStatusKey(12));
    }


    public static boolean isExistPublicKey(int userId, String title) {
        String sql = "SELECT COUNT(*) FROM key_user WHERE userId = :userId AND title = :title";
        return JDBIConnector.me().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("userId", userId)
                        .bind("title", title)
                        .mapTo(Integer.class)
                        .one() > 0
        );
    }
}
