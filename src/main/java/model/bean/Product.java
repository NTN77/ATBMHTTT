package model.bean;

import com.google.gson.annotations.Expose;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
    @Expose
    private int id;

    private String name;
    private String description;
    private int sellingPrice;
    private int stock;
    private int categoryId;
    private int discountId;
    private int isSale;



}

