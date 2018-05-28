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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.made.madesc.Store;

import java.util.ArrayList;

public class StoreListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Store>> stores;
    FirebaseFirestore mDb;

    public LiveData<ArrayList<Store>> getStores() {
        if (stores == null) {
            // load stores
            stores = new MutableLiveData<>();
            loadStores();
        }
        return stores;
    }

    private void loadStores() {
        mDb = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        // Get the stores for the logged in user
        mDb.collection("users").document(currentUser.getUid()).collection("stores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Store> s = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("StoreListActivity", document.getId() + " => " + document.getData());
                                Store store = document.toObject(Store.class);
                                store.setStoreId(document.getId());
                                s.add(store);
                            }
                            // Notify changes in the stores adapter now that we have the data
                            stores.postValue(s);
                        } else {
                            Log.w("StoreListActivity", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
