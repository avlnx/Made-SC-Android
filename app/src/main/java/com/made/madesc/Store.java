package com.made.madesc;

import java.util.HashMap;

public class Store {
    private HashMap<String, Integer> inventory;
    private boolean isActive;
    private String nickname;
    private String storeId;

    public Store() {}

    public Store(HashMap inventory, boolean isActive, String nickname) {
        this.inventory = inventory;
        this.isActive = isActive;
        this.nickname = nickname;
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
}
