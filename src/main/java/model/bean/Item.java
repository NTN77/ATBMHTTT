package model.bean;

import com.google.gson.annotations.Expose;
import lombok.ToString;

@ToString
public class Item {
    /**
     *  product: Sản phẩm mà mình thêm vào giỏ hàng
     * quantity: Số lượng saản phẩm muốn lấy
     * price: Giá hiện tai (bao gồm khuyến mãi v..v)
     */
    @Expose
    private Product product;

    @Expose
    private int quantity;

    @Expose
    private double price;

    public Item() {

    }



    public Item(Product product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


    public String toStringForHash() {
        return "Item{" +
                "productId=" + product.getId() +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void incQuantity(int quantity) {
        this.quantity += quantity;

    }
    public void decQuantity(int quantity) {
    this.quantity-=quantity;
    if(this.quantity <= 0)this.quantity += quantity;
    }
}
