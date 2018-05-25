package com.made.madesc;

import java.util.HashMap;

public final class Catalog {
    private static HashMap<String, Product> products;

    private Catalog() {}    // private constructor so class isn't instantiated

    static {
        products = new HashMap<>();
    }

    public static void addToProducts(Product product) {
        products.put(product.getProductId(), product);
    }

    public static Product findProductWithBarcode(String barcode) {
        Product product = null;

        for (Product p : products.values()) {
            if (p.getBarcode().equals(barcode)) {
                product = p;
                break;
            }
        }

        return product;
    }

    public static Product getProductWithId(String productId) {
        return products.get(productId);
    }
}
