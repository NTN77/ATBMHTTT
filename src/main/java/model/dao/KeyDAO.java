package model.dao;

import model.bean.Key;
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

    public static void main(String[] args) {
        System.out.println("key nè");
        System.out.println(  getPublicKeyActiveByUserId(15));

    }



}