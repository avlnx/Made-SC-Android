package com.made.madesc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

public class KioskActivity extends AppCompatActivity {

    // Views
    private TextView mMessageTextView;
    private TextView mCartNumOfItemsTextView;
    private TextView mCartTotalTextView;
    private Button mCheckoutButton;
    private LinearLayout mCartSummaryLayout;

    // Data
    private HashMap<String, Product> mCatalog;
    FirebaseFirestore mDb;
    FirebaseUser mCurrentUser;
    private Store mActiveStore;
    private String mActiveStoreId;

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

        mMessageTextView = findViewById(R.id.tv_kiosk_message);
        mMessageTextView.setText(R.string.kiosk_loading_catalog_message);
        mCartSummaryLayout = findViewById(R.id.lay_cart_summary);
        mCartNumOfItemsTextView = findViewById(R.id.tv_cart_num_of_items);
        mCartTotalTextView = findViewById(R.id.tv_cart_total);
        mCheckoutButton = findViewById(R.id.bt_checkout);

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
                            mMessageTextView.setText(R.string.kiosk_loading_inventory_message);
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
                                buildInitialInterface();
                            } else {
                                Log.d("KioskActivity", "No such document");
                            }
                        } else {
                            Log.d("KioskActivity", "Error getting inventory", task.getException());
                        }
                    }
                });
    }

    private void buildInitialInterface() {
        // Finished loading, update ui
        mMessageTextView.setText(R.string.kiosk_welcome_message);

        // Hide progress bar
        ProgressBar progressBar = findViewById(R.id.pb_loader);
        progressBar.setVisibility(View.GONE);

        // Show cart summary widget
        mCartSummaryLayout.setVisibility(View.VISIBLE);

        Log.d("KioskActivity", "Catalog data: " + mCatalog.toString());

        // get the current cart state and update interface
        updateCartInterface();

        // TODO: Show Barcode reader
    }

    private void updateCartInterface() {
        mCartTotalTextView.setText(Cart.getCartTotalCurrencyRepresentation());
        if (Cart.isEmpty()) {
            mCartNumOfItemsTextView.setVisibility(View.INVISIBLE);
        } else {
            mCartNumOfItemsTextView.setVisibility(View.VISIBLE);
            mCartNumOfItemsTextView.setText(Cart.getCartNumOfItemsRepresentation());
        }
        // Disable or enable checkout button based on the emptyness of the cart
        mCheckoutButton.setEnabled(!Cart.isEmpty());
    }

    private void addProductToCart(String productId) {
        // get product with this productId and call addProductToCart(Product product)
        addProductToCart(mCatalog.get(productId));
    }
    private void addProductToCart(Product product) {
        Cart.addProductToCart(product);
        String resourceString = getResources().getString(R.string.cart_product_added_successfully);
        Toast.makeText(this, String.format(resourceString, product.getTitle()), Toast.LENGTH_SHORT).show();
        updateCartInterface();
    }

    private void removeProductFromCart(String productId) {
        removeProductFromCart(mCatalog.get(productId));
    }
    private void removeProductFromCart(Product product) {
        boolean success = Cart.removeProductFromCart(product);
        if (success) {
            String resourceString = getResources().getString(R.string.cart_product_removed_successfully);
            Toast.makeText(this, String.format(resourceString, product.getTitle()), Toast.LENGTH_SHORT).show();
            updateCartInterface();
        }
        else {
            String resourceString = getResources().getString(R.string.cart_product_not_in_cart);
            Toast.makeText(this, String.format(resourceString, product.getTitle()), Toast.LENGTH_SHORT).show();
        }
    }

    private Product getRandomProductFromInventory() {
        int numOfProducts = mActiveStore.getInventory().size();
        int randomItem = new Random().nextInt(numOfProducts);
        String productId = mActiveStore.getInventory().keySet().toArray()[randomItem].toString();
        return mCatalog.get(productId);
    }

    // Debug
    public void debugAddToCart(View v) {
        // get a random product from the inventory and add it to the scrollview and cart summary
        Product product = getRandomProductFromInventory();
        Log.d("KioskActivity", String.format("Would add product %s to cart", product.getProductId()));
        addProductToCart(product);
    }
    public void debugRemoveFromCart(View v) {
        // get a random product from the inventory and remove it to the scrollview and cart summary
        Product product = getRandomProductFromInventory();
        Log.d("KioskActivity", String.format("Would remove product %s from cart", product.getProductId()));
        removeProductFromCart(product);
    }
    private void debugLogInventory() {
        for (HashMap.Entry<String, Integer> product : mActiveStore.getInventory().entrySet()) {
            Log.d("KioskActivity", String.format("%s: %d unidades", product.getKey(), product.getValue()));
        }
    }
}
