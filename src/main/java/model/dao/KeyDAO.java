package model.dao;

import model.db.JDBIConnector;

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

    public static void main(String[] args) {
        insertPublicKey("lenovo-thinkbook-g7",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2mlIGzpi9oZm8cKcnHMI", 15);
    }



}
