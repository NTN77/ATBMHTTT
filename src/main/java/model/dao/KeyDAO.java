package model.dao;

import model.bean.Key;
import model.db.JDBIConnector;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class KeyDAO {
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
    //Set trạng thái không dùng nữa của khóa key là 0
    public static void updateKey(int idKey, Timestamp expiredDate){
        JDBIConnector.me().useHandle(handle ->
                handle.createUpdate("UPDATE `key_user` SET updatedTime=:expiredDate , status= 0 WHERE id=:idKey")
                        .bind("expiredDate",expiredDate)
                        .bind("idKey",idKey)
                        .execute());
    }
    public static void main(String[] args) {
        LocalDateTime expiredDate = LocalDateTime.now();
        Timestamp expiredDateFormat = Timestamp.valueOf(expiredDate);

        updateKey(1,expiredDateFormat);
    }
}
