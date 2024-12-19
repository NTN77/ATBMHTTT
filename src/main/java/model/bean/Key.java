package model.bean;

import lombok.*;
import java.io.Serializable;
import java.sql.Timestamp;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Key implements Serializable {
    private int id;
    private String title;
    private String publicKey;
    private int status;
    private Timestamp createdTime;
    private Timestamp updatedTime;
    private int userId;
}

