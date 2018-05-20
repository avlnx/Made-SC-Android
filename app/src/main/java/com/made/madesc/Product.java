package com.made.madesc;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Product {
    private BigDecimal costForUser;
    private String description;
    private BigDecimal publicPrice;
    private String title;
    private String image;
    private String productId;

    public Product() {}

    public Product(double costForUser, String description, double publicPrice, String title, String image, String productId) {
        this.costForUser = BigDecimal.valueOf(costForUser);
        this.description = description;
        this.publicPrice = BigDecimal.valueOf(publicPrice);
        this.title = title;
        this.image = image;
        this.productId = productId;
    }

//    public static Product findProductByIdFromCatalog(ArrayList<Product> products, String productId) {
//        Product productFound = null;
//        for (Product product : products) {
//            if (product.productId.equals(productId)) {
//                productFound = product;
//                break;
//            }
//        }
//        return productFound;
//    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getCostForUser() {
        return costForUser;
    }

    public void setCostForUser(double cost) {
        this.costForUser = BigDecimal.valueOf(cost);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPublicPrice() {
        return publicPrice;
    }

    public String getPublicPriceCurrencyRepresentation() {
        return NumberFormat.getCurrencyInstance().format(publicPrice);
    }

    public void setPublicPrice(double publicPrice) {
        this.publicPrice = BigDecimal.valueOf(publicPrice);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String imageUrl) {
        this.image = imageUrl;
    }
}
