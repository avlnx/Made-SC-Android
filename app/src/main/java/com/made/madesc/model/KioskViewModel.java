package com.made.madesc.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.made.madesc.Cart;
import com.made.madesc.Catalog;
import com.made.madesc.Product;
import com.made.madesc.Store;

import java.util.ArrayList;
import java.util.HashMap;

public class KioskViewModel extends ViewModel {
    private HashMap<String,Product> catalog;
    private MutableLiveData<Store> activeStore;
    private String activeStoreId;
    private FirebaseFirestore mDb;
    private FirebaseUser mCurrentUser;
    public static final String CATALOG = "made";
    public MutableLiveData<Cart> cart;

    public LiveData<Cart> getCart() {
        if (cart == null) {
            cart = new MutableLiveData<>();
        }
        // Update cart on new storeLoad

        return cart;
//        if (activeStore == null || activeStore.getValue() == null) {
//            loadActiveStore();
//            return null; // we need a catalog and a store
//        }
//        if (cart == null) {
//            // get a new cart
//            Cart newCart = new Cart(activeStore.getValue());
//            cart = new MutableLiveData<>();
//            cart.setValue(newCart);
//        }
//        return cart;
    }

    public LiveData<Store> getActiveStore() {
        if (activeStore == null) {
            // load activeStore data
            activeStore = new MutableLiveData<>();
            loadActiveStore();
        }
        return activeStore;
    }

    public void setActiveStoreId(String activeStoreId) {
        this.activeStoreId = activeStoreId;
    }

//    public LiveData<HashMap<String,Product>> getCatalog() {
//        if (catalog == null) {
//            // load catalog
//            catalog = new MutableLiveData<>();
//            loadCatalog();
//        }
//        return catalog;
//    }

    private void loadCatalogThenLoadActiveStore() {
        if (mDb == null) mDb = FirebaseFirestore.getInstance();
        mDb.collection("catalog").document(CATALOG).collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                            HashMap<String,Product> products = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("KioskViewModel", document.getId() + " => " + document.getData());
                                Product product = document.toObject(Product.class);
                                product.setProductId(document.getId());
                                products.put(document.getId(), product);
//                                Catalog.addToProducts(product);
                            }
//                            catalog.postValue(products);
                            catalog = products;
                            loadActiveStore();
                            // Done loading catalog, load inventory for this specific store
//                            mMessageTextView.setText(R.string.kiosk_loading_inventory_message);
//                            loadInventory();
                        } else {
                            Log.w("KioskViewModel", "Error getting catalog.", task.getException());
                        }
                    }
                });
    }

    private void loadActiveStore() {
        if (mDb == null) mDb = FirebaseFirestore.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser == null) return;

        // Load catalog if not already in memory
        if (catalog == null) {
            loadCatalogThenLoadActiveStore();
            return;
        }

        mDb.collection("users").document(mCurrentUser.getUid()).collection("stores")
            .document(activeStoreId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("KioskViewModel", "DocumentSnapshot data: " + document.getData());
                            Store store = document.toObject(Store.class);
                            if (store != null) {
                                store.setStoreId(activeStoreId);
                                store.addProductDataToInventory(catalog);
                                activeStore.postValue(store);
                                // Create the cart
                                Cart newCart = new Cart(store);
                                cart.postValue(newCart);
                            }
                        } else {
                            Log.d("KioskViewModel", "No such document");
                        }
                    } else {
                        Log.d("KioskViewModel", "Error getting inventory", task.getException());
                    }
                }
            });
    }
}
