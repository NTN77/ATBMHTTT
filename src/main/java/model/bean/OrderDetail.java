package model.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDetail {
    private int orderId;
    private int productId;
    private int quantity;

    private double sellingPrice;
    private double finalSellingPrice;
    private String explainPriceDifference;

    public OrderDetail() {
    }


    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setFinalSellingPrice(double finalSellingPrice) {
        this.finalSellingPrice = finalSellingPrice;
    }

    public void setExplainPriceDifference(String explainPriceDifference) {
        this.explainPriceDifference = explainPriceDifference;
    }

    @Override
    public String toString() {
        return
                "orderId=" + orderId +
                        "{" + productId + " - " + quantity +
                        '}';
    }
}
