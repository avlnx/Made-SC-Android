package com.made.madesc;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.made.madesc.model.KioskViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

// TODO: Add to device disk that we opened this store
// TODO: Setup Kiosk mode
// TODO: Setup click handler to leave Kiosk mode
// TODO: set up fb snapshot querys for updates?

public class KioskActivity extends AppCompatActivity {

    // Views
//    private TextView mMessageTextView;
    private TextView mCartNumOfItemsTextView;
    private TextView mCartTotalTextView;
    private Button mCheckoutButton;
    private LinearLayout mCartSummaryLayout;
    private RecyclerView mCartItemRecyclerView;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    // Data
//    private HashMap<String, Product> mCatalog;
    FirebaseFirestore mDb;
    FirebaseUser mCurrentUser;
    private Store mActiveStore;
    private String mActiveStoreId;
    CartItemAdapter mCartItemAdapter;
    InventoryItemAdapter mProductsInInventoryAdapter;
    // Barcode
    private static final String TAG = KioskActivity.class.getSimpleName();
    private HashMap<String,Product> mCatalog;
    private ArrayList<Product> mProductsInInventory;



    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;

    public static final String CATALOG = "made";

    private KioskViewModel mKioskModel;

    private Cart mCart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        try {
            getSupportActionBar().setElevation(0);
        }
        catch (Exception e) {
            Log.d( "KioskActivity", "Could not disable elevation in action bar");
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_tabs);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.vp_interfaces);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // Get the storeId for the active store from the intent
        Intent intent = getIntent();
        mActiveStoreId = intent.getStringExtra(StoreListActivity.ACTIVE_STORE);

        // Get model and set active store id
        mKioskModel = ViewModelProviders.of(this).get(KioskViewModel.class);
        mKioskModel.setActiveStoreId(mActiveStoreId);


//        mMessageTextView = findViewById(R.id.tv_kiosk_message);
//        mMessageTextView.setText(R.string.kiosk_loading_catalog_message);

        mCartSummaryLayout = findViewById(R.id.lay_cart_summary);
        mCartNumOfItemsTextView = findViewById(R.id.tv_cart_num_of_items);
        mCartTotalTextView = findViewById(R.id.tv_cart_total);
        mCheckoutButton = findViewById(R.id.bt_checkout);
        mCartItemRecyclerView = findViewById(R.id.rv_cart_items);

//        mKioskModel.getCatalog().observe(this, new Observer<HashMap<String,Product>>() {
//            @Override
//            public void onChanged(@Nullable HashMap<String,Product> products) {
//                if (products != null) {
//                    // catalog loaded, load store
//                    mCatalog = products;
//                }
//            }
//        });

        mKioskModel.getActiveStore().observe(this, new Observer<Store>() {
            @Override
            public void onChanged(@Nullable Store store) {
                if (store != null) {
                    // store inventory loaded (which means catalog is also loaded)
                    mActiveStore = store;
                    mProductsInInventory = store.getListOfProductsWithData();
                    mProductsInInventoryAdapter = new InventoryItemAdapter(mProductsInInventory);
                }
            }
        });

        mKioskModel.getCart().observe(this, new Observer<Cart>() {
            @Override
            public void onChanged(@Nullable Cart cart) {
                if (cart != null) {
                    // Got a new cart, update ui, setup adapter if not set
                    mCart = cart;
                    if (mCartItemAdapter == null) {
                        // First time around, setup adapter and when done build initial interface
                        setupCartItemAdapter();
                        buildInitialInterface();
                    }
                    updateCartInterface();
                }
            }
        });
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class above).
//            return PlaceholderFragment.newInstance(position);
            if (position == 0) return ProductListFragment.newInstance(mProductsInInventoryAdapter);
            return new BarcodeFragment();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
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
        mCartItemAdapter = new CartItemAdapter(mCart.getProductsInCart(), mCart);
        mCartItemRecyclerView.setAdapter(mCartItemAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mCartItemRecyclerView.setLayoutManager(linearLayoutManager);

        mCartItemRecyclerView.setHasFixedSize(true);

        // Add horizontal divider
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        itemDecor.setOrientation(VERTICAL);
        mCartItemRecyclerView.addItemDecoration(itemDecor);
    }

    private void buildInitialInterface() {
        // Hide progress bar
        ProgressBar progressBar = findViewById(R.id.pb_loader);
        progressBar.setVisibility(View.GONE);

        // Show cart summary widget
        mCartSummaryLayout.setVisibility(View.VISIBLE);
    }

    private void updateCartInterface() {
        mCartTotalTextView.setText(mCart.getCartTotalCurrencyRepresentation());
        if (mCart.isEmpty()) {
            mCartNumOfItemsTextView.setVisibility(View.INVISIBLE);
        } else {
            mCartNumOfItemsTextView.setVisibility(View.VISIBLE);
            mCartNumOfItemsTextView.setText(mCart.getCartNumOfItemsRepresentation());
        }
        // Disable or enable checkout button based on the emptyness of the cart
        mCheckoutButton.setEnabled(!mCart.isEmpty());

        // Update local array of products in cart so the adapter can get notified
        mCartItemAdapter.notifyDataSetChanged();
    }

    public void addProductToCart(String productId) {
        // get product with this productId and call addProductToCart(Product product)
        addProductToCart(mActiveStore.getProductFromInventoryWithId(productId));
    }
    public void addProductToCart(Product product) {
        mCart.addProductToCart(product);
        String resourceString = getResources().getString(R.string.cart_product_added_successfully);
        Toast.makeText(this, String.format(resourceString, product.getTitle()), Toast.LENGTH_SHORT).show();
        updateCartInterface();
    }

    public void removeProductFromCart(String productId) {
        removeProductFromCart(mActiveStore.getProductFromInventoryWithId(productId));
    }
    public void removeProductFromCart(Product product) {
        boolean success = mCart.removeProductFromCart(product);
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
    public void onGoToTabs(View v) {
        Intent intent = new Intent(this, KioskTabbedActivity.class);
        startActivity(intent);
    }
    private Product getRandomProductFromInventory() {
        int numOfProducts = mActiveStore.getInventory().size();
        int randomItem = new Random().nextInt(numOfProducts);
        String productId = mActiveStore.getInventory().keySet().toArray()[randomItem].toString();
        return Catalog.getProductWithId(productId);
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
