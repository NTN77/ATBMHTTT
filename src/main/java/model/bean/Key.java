package model.bean;

import lombok.*;

import java.sql.Timestamp;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Key {
    private int id;
    private String title;
    private String publicKey;
    private int status;
    private Timestamp createAt;
    private Timestamp updatetedTime;
    private int userId;
}

