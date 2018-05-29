package com.made.madesc;

import java.util.ArrayList;
import java.util.HashMap;

public class Store {
    private HashMap<String, Integer> inventory;
    private boolean isActive;
    private String nickname;
    private String storeId;
    private ArrayList<Product> productsInInventory;

    public Store() {
        productsInInventory = new ArrayList<>();
    }

    public Store(HashMap<String, Integer> inventory, boolean isActive, String nickname) {
        this.inventory = inventory;
        this.isActive = isActive;
        this.nickname = nickname;
        productsInInventory = new ArrayList<>();
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(HashMap<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public String getNickname() {
        return nickname;
    }

//    public ArrayList<Product> getListOfProductsWithData(HashMap<String,Product> catalog) {
//        productsInInventory.clear();
//        for (HashMap.Entry<String, Integer> item : inventory.entrySet()) {
//            productsInInventory.add(catalog.get(item.getKey()));
//        }
//
//        return productsInInventory;
//    }

    public ArrayList<Product> getListOfProductsWithData() {
        return productsInInventory;
    }

    public Product getProductFromInventoryWithId(String productId) {
        Product p = null;
        for (Product product : productsInInventory) {
            if (product.getProductId().equals(productId)) {
                p = product;
                break;
            }
        }
        return p;
    }


    public void addProductDataToInventory(HashMap<String,Product> catalog) {
        productsInInventory.clear();
        for (HashMap.Entry<String, Integer> item : inventory.entrySet()) {
            productsInInventory.add(catalog.get(item.getKey()));
        }
    }

//    public void loadProductData(ArrayList<Product> catalog) {
//        for (HashMap.Entry<String, Integer> item : inventory.entrySet()) {
//            productsInActiveInventory.add(Catalog.getProductWithId(item.getKey()));
//        }
//    }

//    public static ArrayList<Product> getProductsInActiveInventory() {
//        return productsInActiveInventory;
//    }

//    public ArrayList<Product> getProductsInInventoryAsList(ArrayList<Product> catalog) {
//        for (HashMap.Entry<String, Integer> item : inventory.entrySet()) {
//            productsInActiveInventory.add(Catalog.getProductWithId(item.getKey()));
//        }
//    }
//
//    public void loadProductsInInventory() {
//        productsInActiveInventory.clear();
//        for (HashMap.Entry<String, Integer> item : inventory.entrySet()) {
//            productsInActiveInventory.add(Catalog.getProductWithId(item.getKey()));
//        }
//    }
}
