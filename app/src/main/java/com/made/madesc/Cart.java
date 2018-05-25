package com.made.madesc;

import android.util.Log;
import android.widget.ProgressBar;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public final class Cart {
    private static BigDecimal cartTotal;
    private static int cartNumOfItems;
    private static HashMap<String, Integer> itemQuantities;
    private static ArrayList<Product> productsInCart;

    private Cart() {}   // private constructor so class isn't instantiated

    static {
        clearCart();
    }

    public static void clearCart() {
        cartNumOfItems = 0;
        cartTotal = new BigDecimal("0");
        itemQuantities = new HashMap<>();
        productsInCart = new ArrayList<>();
    }

    public static ArrayList<Product> getProductsInCart() {
        return productsInCart;
    }

    public static boolean isEmpty() {
        return cartNumOfItems == 0;
    }

    public static BigDecimal getCartTotal() {
        return cartTotal;
    }

    public static String getCartTotalCurrencyRepresentation() {
        return NumberFormat.getCurrencyInstance().format(cartTotal);
    }

    public static int getCartNumOfItems() {
        return cartNumOfItems;
    }

    public static String getCartNumOfItemsRepresentation() {
        return Integer.toString(cartNumOfItems);
    }

    public static HashMap<String, Integer> getItems() {
        return itemQuantities;
    }

    private static void updateProductsInCartArray() {
        productsInCart.clear();
        for (HashMap.Entry<String, Integer> item : itemQuantities.entrySet()) {
            productsInCart.add(Catalog.getProductWithId(item.getKey()));
        }
    }

    public static int getQuantityInCartForProduct(String productId) {
        return (itemQuantities.get(productId) == null) ? 0 : itemQuantities.get(productId);
    }
    public static String getQuantityInCartForProductRepresentation(String productId) {
        int quantity = getQuantityInCartForProduct(productId);
        return Integer.toString(quantity);
    }

    public static void addProductToCart(Product product) {
        int currentQuantityInCart = getQuantityInCartForProduct(product.getProductId());
        itemQuantities.put(product.getProductId(), ++currentQuantityInCart);
        cartNumOfItems++;
        cartTotal = cartTotal.add(product.getPublicPrice());
        updateProductsInCartArray();
    }

    public static boolean removeProductFromCart(Product product) {
        int currentQuantityInCart = getQuantityInCartForProduct(product.getProductId());
        if (currentQuantityInCart == 0) return false;

        cartNumOfItems--;
        cartTotal = cartTotal.subtract(product.getPublicPrice());
        // cartTotal can't be negative. This could happen if the price for a product changed mid
        // operation for example from a Firebase snapshot
        if (cartTotal.compareTo(new BigDecimal("0")) < 0) cartTotal = new BigDecimal("0");

        currentQuantityInCart--;

        if (currentQuantityInCart == 0) {
            // delete key from items HashMap so adapter updates
            itemQuantities.remove(product.getProductId());
            updateProductsInCartArray();
            return true;
        }
        itemQuantities.put(product.getProductId(), currentQuantityInCart);
        return true;
    }
}
