package model.bean;

import com.google.gson.annotations.Expose;
import lombok.*;

/**
 * Tất cả thông tin cần thiết để hash 1 đơn hàng.
 * 1. userId.
 * 2. toàn bộ thông tin có trong Cart.
 * 3. shippingFee (phí vận chuyển).
 * 4. totalAmount (giá cuối cùng).
 */
@ToString
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class HashInput {
    @Expose
    private int userId;
    @Expose
    private double shippingFee;
    @Expose
    private double totalPrice;
    @Expose
    private Cart cartInfo;


    @Override
    public String toString() {
        return "HashInput{" +
                "userId=" + userId +
                ", shippingFee=" + shippingFee +
                ", totalPrice=" + totalPrice +
                ", cartInfo=" + cartInfo.toStringForHash() +
                '}';
    }
}
