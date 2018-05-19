package com.made.madesc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Random;

public class KioskActivity extends AppCompatActivity {

    private String mActiveStoreId;
    private TextView mMessage;
    private HashMap<String, Product> mCatalog;
    FirebaseFirestore mDb;
    FirebaseUser mCurrentUser;
    private Store mActiveStore;

    public static final String CATALOG = "made";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk);

        // TODO: Add to device disk that we opened this store
        // TODO: Setup Kiosk mode
        // TODO: Setup click handler to leave Kiosk mode
        // TODO: set up snapshotquerys for updates?

        // Get the storeId for the active store from the intent
        Intent intent = getIntent();
        mActiveStoreId = intent.getStringExtra(StoreListActivity.ACTIVE_STORE);

        mMessage = findViewById(R.id.tv_kiosk_message);
        mMessage.setText(R.string.kiosk_loading_catalog_message);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDb = FirebaseFirestore.getInstance();

        mCatalog = new HashMap<>();

        // Load catalog (template)
        loadCatalog();
    }

    private void loadCatalog() {
        mDb.collection("catalog").document(CATALOG).collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("KioskActivity", document.getId() + " => " + document.getData());
                                Product product = document.toObject(Product.class);
                                product.setProductId(document.getId());
                                mCatalog.put(product.getProductId(), product);
                            }
                            // Done loading catalog, load inventory for this specific store
                            mMessage.setText(R.string.kiosk_loading_inventory_message);
                            loadInventory();
                        } else {
                            Log.w("KioskActivity", "Error getting catalog.", task.getException());
                        }
                    }
                });
    }

    private void loadInventory() {
        mDb.collection("users").document(mCurrentUser.getUid()).collection("stores")
                .document(mActiveStoreId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("KioskActivity", "DocumentSnapshot data: " + document.getData());
                                mActiveStore = document.toObject(Store.class);
                                if (mActiveStore != null) mActiveStore.setStoreId(mActiveStoreId);
                                loadFinished();
                            } else {
                                Log.d("KioskActivity", "No such document");
                            }
                        } else {
                            Log.d("KioskActivity", "Error getting inventory", task.getException());
                        }
                    }
                });
    }

    private void loadFinished() {
        // Finished loading, update ui
        mMessage.setText(R.string.kiosk_welcome_message);

        // Hide progress bar
        ProgressBar progressBar = findViewById(R.id.pb_loader);
        progressBar.setVisibility(View.GONE);

        Log.d("KioskActivity", "Catalog data: " + mCatalog.toString());

        // TODO: Show Barcode reader and Cart widget

    }

    private void debugProductAdd() {
        // get a random product from the inventory and add it to the scrollview
        int numOfProducts = mActiveStore.getInventory().size();
        int randomItem = new Random().nextInt(numOfProducts);
        String productId = mActiveStore.getInventory().keySet().toArray()[randomItem].toString();
        Product product = mCatalog.get(productId);
    }

    private void debugLogInventory() {
        for (HashMap.Entry<String, Integer> product : mActiveStore.getInventory().entrySet() ){
            Log.d("KioskActivity", String.format("%s: %d unidades", product.getKey(), product.getValue()));
        }
    }
}
