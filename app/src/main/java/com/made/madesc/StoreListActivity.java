package com.made.madesc;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.made.madesc.model.StoreListViewModel;

import java.util.ArrayList;

public class StoreListActivity extends AppCompatActivity {

    // Views
    RecyclerView mStoreListRecyclerView;
    Button mLogoutButton;
    ProgressBar mPbLoader;
    TextView mMessageTextView;

    // Data
    FirebaseUser mCurrentUser;
    FirebaseFirestore mDb;
    ArrayList<Store> mStores;
    StoreListAdapter mStoreListAdapter;

    private StoreListViewModel mStoreModel;

    public static final String ACTIVE_STORE = "com.made.madesc.ACTIVE_STORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        // Set up instance properties
        mLogoutButton = findViewById(R.id.bt_log_out);
        mPbLoader = findViewById(R.id.pb_loader);
        mMessageTextView = findViewById(R.id.tv_message);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseFirestore.getInstance();

        mStoreModel = ViewModelProviders.of(this).get(StoreListViewModel.class);
        mStoreModel.getStores().observe(this, new Observer<ArrayList<Store>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Store> stores) {
                if (stores != null) {
                    mStores = stores;
                    // If StoreListAdapter is not set yet set it
                    if (mStoreListAdapter == null) {
                        setupStoreListAdapter();
                    }
                    // Notify changes in the stores adapter now that we have the data
                    mStoreListAdapter.notifyDataSetChanged();
                    updateUiAfterLoad();
                }
            }
        });

        String userEmail = "";
        try {
            userEmail = mCurrentUser.getEmail();
        }
        catch (NullPointerException e) {
            // User is logged out
            goToLoginScreen();
        }
        TextView textViewUserId = findViewById(R.id.tv_user_id);
        textViewUserId.setText(getResources().getString(R.string.hello_text, userEmail));
    }

    private void updateUiAfterLoad() {
        mMessageTextView.setText(R.string.store_list_title);
        mPbLoader.setVisibility(View.INVISIBLE);
        mStoreListRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setupStoreListAdapter() {
        mStoreListRecyclerView = (RecyclerView) findViewById(R.id.rv_stores_list);

        mStoreListAdapter = new StoreListAdapter(mStores, getCustomItemClickListener());
        mStoreListRecyclerView.setAdapter(mStoreListAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mStoreListRecyclerView.setLayoutManager(linearLayoutManager);

        mStoreListRecyclerView.setHasFixedSize(true);
    }

    private StoreListAdapter.CustomItemClickListener getCustomItemClickListener() {
        return new StoreListAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // TODO: Check if we have a pinpad connected, otherwise don't continue

                Log.d("StoreListActivity", "clicked position:" + position);
                String storeId = mStores.get(position).getStoreId();
                startKioskMode(storeId);
            }
        };
    }

    private void startKioskMode(final String storeId) {
        // User clicked one of the stores cards, show message pre loading
        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
        builder.setMessage(R.string.dialog_confirm_start_store_message)
                .setTitle(R.string.dialog_confirm_start_store_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Go to KioskMode
                Intent intent = new Intent(StoreListActivity.this, KioskActivity.class);
                intent.putExtra(ACTIVE_STORE, storeId);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Toast.makeText(StoreListActivity.this, "O modo Quiosque não será ativado", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onLogoutButtonPress(View v) {
        // disable logout button
        mLogoutButton.setEnabled(false);

        FirebaseAuth.getInstance().signOut();

        // Go to login activity
        goToLoginScreen();
    }
}
