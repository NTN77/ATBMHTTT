package model.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Order implements Serializable {
    private int id;
    private double totalPrice;
    private Timestamp orderDate;
    private int status;
    private String consigneeName;
    private String consigneePhoneNumber;
    private String address;

    private double shippingFee;
    private int userId;
    private String note;
    private int publicKeyId;
    private String signature;


    public Order() {
    }

    public boolean isWaitConfirmOrder() {
        return this.getStatus() == 0;
    }

    public boolean isPreparing() {
        return this.getStatus() == 1;
    }

    public boolean isDeliveringOrder() {
        return this.getStatus() == 2;
    }

    public boolean isSucccessfulOrder() {
        return this.getStatus() == 3;
    }

    public boolean isCanceledOrder() {
        return this.getStatus() == 4;
    }

    public String getStatusAsName() {
        String re = null;
        if (this.isWaitConfirmOrder()) {
            re = "Cần xác nhận";
        } else if (this.isPreparing()) {
            re = "Đang đóng gói";
        } else if (this.isDeliveringOrder()) {
            re = "Đang giao";
        } else if (this.isCanceledOrder()) {
            re = "Đã hủy";
        } else if (this.isSucccessfulOrder()) {
            re = "Thành công";
        }
        return re;
    }
    public String getStatusAsColor(){
        String backgroundColor = null;
        if (this.isWaitConfirmOrder()) {
            backgroundColor = "#ffcc00";
        } else if (this.isPreparing()) {
            backgroundColor = "cadetblue";
        } else if (this.isDeliveringOrder()) {
            backgroundColor = "#0171d3";
        } else if (this.isCanceledOrder()) {
            backgroundColor = "#ff0000";
        } else if (this.isSucccessfulOrder()) {
            backgroundColor = "#4d8a54";
        }
        return backgroundColor;
    }

}
