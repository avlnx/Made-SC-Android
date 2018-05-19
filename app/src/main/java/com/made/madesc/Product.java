package com.made.madesc;


import java.math.BigDecimal;

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

    public void setPublicPrice(double publicPrice) {
        this.publicPrice = BigDecimal.valueOf(publicPrice);
    }

//    public void setPublicPrice(BigDecimal publicPrice) {
//        this.publicPrice = publicPrice;
//    }

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
