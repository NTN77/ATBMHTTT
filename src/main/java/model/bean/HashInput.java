package model.bean;

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
    private int userId;
    private Cart cartInfo;
    private double shippingFee;
    private double totalPrice;

}
