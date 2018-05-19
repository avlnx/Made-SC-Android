package com.made.madesc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StoreListActivity extends AppCompatActivity {

    FirebaseUser mCurrentUser;
    Button mLogoutButton;
    FirebaseFirestore mDb;
    ArrayList<Store> mStores;
    RecyclerView mStoreListRecyclerView;
    StoreListAdapter mStoreListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        // Set up instance properties
        mLogoutButton = findViewById(R.id.bt_log_out);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseFirestore.getInstance();

        mStores = new ArrayList<Store>();

        // set up adapter with empty data, we later update it
        setupStoreListAdapter();

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

        // Get the stores for the logged in user
        mDb.collection("users").document(mCurrentUser.getUid()).collection("stores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("StoreListActivity", document.getId() + " => " + document.getData());
                                Store store = document.toObject(Store.class);
                                mStores.add(store);
                            }
                            // Notify changes in the stores adapter now that we have the data
                            mStoreListAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("StoreListActivity", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void setupStoreListAdapter() {
        mStoreListRecyclerView = (RecyclerView) findViewById(R.id.rv_stores_list);

        mStoreListAdapter = new StoreListAdapter(mStores);
        mStoreListRecyclerView.setAdapter(mStoreListAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mStoreListRecyclerView.setLayoutManager(linearLayoutManager);

        mStoreListRecyclerView.setHasFixedSize(true);
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
