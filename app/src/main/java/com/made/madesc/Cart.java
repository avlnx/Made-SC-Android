package com.made.madesc;

import android.util.Log;
import android.widget.ProgressBar;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Cart {
    private BigDecimal cartTotal;
    private int cartNumOfItems;
    private HashMap<String, Integer> itemQuantities;
    private ArrayList<Product> productsInCart;
    private Store mActiveStore;

    public Cart(Store store) {
        cartNumOfItems = 0;
        cartTotal = new BigDecimal("0");
        itemQuantities = new HashMap<>();
        productsInCart = new ArrayList<>();
        mActiveStore = store;
    }

    public ArrayList<Product> getProductsInCart() {
        return productsInCart;
    }

    public boolean isEmpty() {
        return cartNumOfItems == 0;
    }

    public BigDecimal getCartTotal() {
        return cartTotal;
    }

    public String getCartTotalCurrencyRepresentation() {
        return NumberFormat.getCurrencyInstance().format(cartTotal);
    }

    public int getCartNumOfItems() {
        return cartNumOfItems;
    }

    public String getCartNumOfItemsRepresentation() {
        return Integer.toString(cartNumOfItems);
    }

    public HashMap<String, Integer> getItems() {
        return itemQuantities;
    }

    private void updateProductsInCartArray() {
        productsInCart.clear();
        for (HashMap.Entry<String, Integer> item : itemQuantities.entrySet()) {
            productsInCart.add(mActiveStore.getProductFromInventoryWithId(item.getKey()));
        }
    }

    public int getQuantityInCartForProduct(String productId) {
        return (itemQuantities.get(productId) == null) ? 0 : itemQuantities.get(productId);
    }
    public String getQuantityInCartForProductRepresentation(String productId) {
        int quantity = getQuantityInCartForProduct(productId);
        return Integer.toString(quantity);
    }

    public void addProductToCart(Product product) {
        int currentQuantityInCart = getQuantityInCartForProduct(product.getProductId());
        itemQuantities.put(product.getProductId(), ++currentQuantityInCart);
        cartNumOfItems++;
        cartTotal = cartTotal.add(product.getPublicPrice());
        updateProductsInCartArray();
    }

    public boolean removeProductFromCart(Product product) {
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
