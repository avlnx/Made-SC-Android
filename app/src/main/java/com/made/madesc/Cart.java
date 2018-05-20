package com.made.madesc;

import android.util.Log;
import android.widget.ProgressBar;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public final class Cart {
    private static BigDecimal cartTotal;
    private static int cartNumOfItems;
    private static HashMap<String, Integer> items;

    private Cart() {}   // private constructor so class isn't instantiated

    static {
        clearCart();
    }

    public static void clearCart() {
        cartNumOfItems = 0;
        cartTotal = new BigDecimal("0");
        items = new HashMap<>();
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
        return items;
    }

//    public static ArrayList<Product> getItemsWithData(HashMap<String, Product> catalog) {
//        ArrayList<Product> productsInCartWithData = new ArrayList<>();
//        for (HashMap.Entry<String, Integer> item : items.entrySet()) {
//            String productId = item.getKey();
////            Product product = Product.findProductByIdFromCatalog(catalog, productId);
////            if (product != null) {
////                productsInCartWithData.add(product);
////            }
//            productsInCartWithData.add(catalog.get(item.getKey()));
//        }
//        return productsInCartWithData;
//    }

    public static void updateProductsInCartArray(ArrayList<Product> products, HashMap<String, Product> catalog) {
        products.clear();
        for (HashMap.Entry<String, Integer> item : items.entrySet()) {
            String productId = item.getKey();
            products.add(catalog.get(item.getKey()));
        }
    }

    public static int getQuantityInCartForProduct(String productId) {
        return (items.get(productId) == null) ? 0 : items.get(productId);
    }

    public static String getQuantityInCartForProductRepresentation(String productId) {
        int quantity = getQuantityInCartForProduct(productId);
        return Integer.toString(quantity);
    }

    public static void addProductToCart(Product product) {
        int currentQuantityInCart = getQuantityInCartForProduct(product.getProductId());
        items.put(product.getProductId(), ++currentQuantityInCart);
        cartNumOfItems++;
        cartTotal = cartTotal.add(product.getPublicPrice());
    }

    public static boolean removeProductFromCart(Product product) {
        int currentQuantityInCart = getQuantityInCartForProduct(product.getProductId());
        if (currentQuantityInCart == 0) return false;

        cartNumOfItems--;
        cartTotal = cartTotal.subtract(product.getPublicPrice());
        // cartTotal can't be negative. This could happen if the price for a product changed mid
        // operation for example from a Firebase snapshot
        if (cartTotal.compareTo(new BigDecimal("0")) == -1) cartTotal = new BigDecimal("0");

        currentQuantityInCart--;

        if (currentQuantityInCart == 0) {
            // delete key from items HashMap so adapter updates
            items.remove(product.getProductId());
            return true;
        }
        items.put(product.getProductId(), currentQuantityInCart);
        return true;
    }
}
