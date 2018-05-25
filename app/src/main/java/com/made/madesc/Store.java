package com.made.madesc;

import java.util.ArrayList;
import java.util.HashMap;

public class Store {
    private HashMap<String, Integer> inventory;
    private boolean isActive;
    private String nickname;
    private String storeId;
    private static ArrayList<Product> productsInActiveInventory;

//    public static Store activeStore;

    static {
        productsInActiveInventory = new ArrayList<>();
    }

    public Store() {}

    public Store(HashMap<String, Integer> inventory, boolean isActive, String nickname) {
        this.inventory = inventory;
        this.isActive = isActive;
        this.nickname = nickname;
//        productsInActiveInventory = new ArrayList<>();
//        loadProductsInActiveInventory(inventory);
    }

//    private static void initializeActiveStore() {
//        activeStore = new Store(new HashMap<String, Integer>(), true, "Loading");
//    }
//
//    public static void updateActiveStore(Store store) {
//        activeStore = store;
//    }

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

    public static ArrayList<Product> getProductsInActiveInventory() {
        return productsInActiveInventory;
    }

    public static void loadProductsInActiveInventory(HashMap<String, Integer> inventory) {
        productsInActiveInventory.clear();
        for (HashMap.Entry<String, Integer> item : inventory.entrySet()) {
            productsInActiveInventory.add(Catalog.getProductWithId(item.getKey()));
        }
    }
}
