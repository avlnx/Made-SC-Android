package com.made.madesc.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PinpadViewModel extends ViewModel {
    private final MutableLiveData<List<String>> devices = new MutableLiveData<>();

    /*
    public LiveData<ArrayList<Store>> getStores() {
        if (stores == null) {
            // load stores
            stores = new MutableLiveData<>();
            loadStores();
        }
        return stores;
    }
     */

    public LiveData<List<String>> getDevices() {
        loadDevices();
        return devices;
    }

    public void setDevices(List<String> devicesList) {
//        if (devices == null) devices = new MutableLiveData<>();
        devices.setValue(devicesList);
    }

    public void loadDevices() {
        // Return a stub for now
        // get current list object
//        List<String> d = devices.getValue();
//        if (d == null) {
//            // first time around
//            d = new ArrayList<>();
//        }
        // Now set the data
        List<String> d = new ArrayList<>();
        Log.d("PinpadViewModel", "Clearing object d " + d.hashCode());
//        d.clear();
        d.add("Pinpad 1_23:23:45:45:56:56");
        d.add("Pinpad 2_12:12:12:12:12:12");
        d.add("Pinpad 3_45:45:45:45:45:45");
        d.add("Pinpad 4_34:34:34:34:34:34");
        devices.setValue(d);
    }
}
