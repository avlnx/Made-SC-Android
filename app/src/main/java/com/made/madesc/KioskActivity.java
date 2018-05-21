package com.made.madesc;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class KioskActivity extends AppCompatActivity {

    // Views
    private TextView mMessageTextView;
    private TextView mCartNumOfItemsTextView;
    private TextView mCartTotalTextView;
    private Button mCheckoutButton;
    private LinearLayout mCartSummaryLayout;
    private RecyclerView mCartItemRecyclerView;

    // Data
    private HashMap<String, Product> mCatalog;
    FirebaseFirestore mDb;
    FirebaseUser mCurrentUser;
    private Store mActiveStore;
    private String mActiveStoreId;
    CartItemAdapter mCartItemAdapter;
    ArrayList<Product> mProductsInCart;
    // Barcode
    private static final String TAG = KioskActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;

    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;

    public static final String CATALOG = "made";

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            beepManager.playBeepSoundAndVibrate();
            Product product = findProductWithBarcode(lastText);
            if (product != null) {
                addProductToCart(product);
            } else {
                // Product not found
                String resourceString = getResources().getString(R.string.barcode_product_not_found);
                Toast.makeText(KioskActivity.this, String.format(resourceString, lastText), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk);

        // Barcode stuff
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.CODE_39,
                BarcodeFormat.UPC_A,
                BarcodeFormat.CODABAR,
                BarcodeFormat.CODE_93,
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13,
                BarcodeFormat.UPC_A,
                BarcodeFormat.AZTEC,
                BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_EAN_EXTENSION,
                BarcodeFormat.CODE_128,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.ITF,
                BarcodeFormat.MAXICODE
        );
        barcodeView.setStatusText(getResources().getString(R.string.barcode_instructions));
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);

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
        mCartItemRecyclerView = findViewById(R.id.rv_cart_items);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDb = FirebaseFirestore.getInstance();

        mCatalog = new HashMap<>();

        mProductsInCart = new ArrayList<Product>();
        // set up adapter with empty data, we later update it
        setupCartItemAdapter();

        // Load catalog (template)
        loadCatalog();
    }

    // Barcode Scanning functionality, following two methods
    public void onClickScan(View v) {
        new IntentIntegrator(this).initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CUSTOMIZED_REQUEST_CODE && requestCode != IntentIntegrator.REQUEST_CODE) {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        switch (requestCode) {
            case CUSTOMIZED_REQUEST_CODE: {
                Toast.makeText(this, "REQUEST_CODE = " + requestCode, Toast.LENGTH_LONG).show();
                break;
            }
            default:
                break;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);

        if(result.getContents() == null) {
            Log.d("MainActivity", "Cancelled scan");
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            Log.d("MainActivity", "Scanned");
            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupCartItemAdapter() {
        mCartItemAdapter = new CartItemAdapter(mProductsInCart);
        mCartItemRecyclerView.setAdapter(mCartItemAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mCartItemRecyclerView.setLayoutManager(linearLayoutManager);

        mCartItemRecyclerView.setHasFixedSize(true);

        // Add horizontal divider
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        itemDecor.setOrientation(VERTICAL);
        mCartItemRecyclerView.addItemDecoration(itemDecor);
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

        // Show Barcode reader
        barcodeView.resume();
        barcodeView.setVisibility(View.VISIBLE);
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

        // Update local array of products in cart so the adapter can get notified
        Cart.updateProductsInCartArray(mProductsInCart, mCatalog);
        mCartItemAdapter.notifyDataSetChanged();
    }

    private Product findProductWithBarcode(String barcode) {
        Product product = null;

        for (Product p : mCatalog.values()) {
            if (p.getBarcode().equals(barcode)) {
                product = p;
            }
        }

        return product;
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

    public void onIncreaseQuantityInCartForProduct(View v) {
        String productId = (String) v.getTag();
        addProductToCart(productId);
    }

    public void onDecreaseQuantityInCartForProduct(View v) {
        String productId = (String) v.getTag();
        removeProductFromCart(productId);
    }

    // Debug
    private Product getRandomProductFromInventory() {
        int numOfProducts = mActiveStore.getInventory().size();
        int randomItem = new Random().nextInt(numOfProducts);
        String productId = mActiveStore.getInventory().keySet().toArray()[randomItem].toString();
        return mCatalog.get(productId);
    }
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
