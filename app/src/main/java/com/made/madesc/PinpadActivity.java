package com.made.madesc;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.made.madesc.model.PinpadViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class PinpadActivity extends AppCompatActivity {

    RecyclerView mPinpadsRecyclerView;
    TextView mMessageTextView;
//    final PinpadViewModel mPinpadViewModel;
    List<String> mDevices = new ArrayList<>();
    PinpadListAdapter mPinpadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad);

        // Initialize widgets
        mPinpadsRecyclerView = findViewById(R.id.rv_pinpads);
        mMessageTextView = findViewById(R.id.tv_message);

        // Initialize ViewModel
        final PinpadViewModel mPinpadViewModel = ViewModelProviders.of(this).get(PinpadViewModel.class);
//        mDevices = new ArrayList<String>();
//        mPinpadViewModel.setDevices(mDevices);
        mPinpadViewModel.getDevices().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> devices) {
                if (devices != null) {
//                    transformDevicesList(devices);
                    mDevices = devices;
                    if (mPinpadListAdapter == null) setupPinpadListAdapter();
//                    mPinpadListAdapter.setDataSet(devices);
                    mPinpadListAdapter.notifyDataSetChanged();
                }
            }
        });

    }

//    private void transformDevicesList(List<String> updatedDevices) {
//        mDevices.clear();
//        mDevices.addAll(updatedDevices);
//    }

    private void setupPinpadListAdapter() {
        mPinpadListAdapter = new PinpadListAdapter(mDevices);
        mPinpadsRecyclerView.setAdapter(mPinpadListAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mPinpadsRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        itemDecor.setOrientation(VERTICAL);
        mPinpadsRecyclerView.addItemDecoration(itemDecor);
    }

    public void onPinpadListItemClicked(View v) {
        Toast.makeText(this, "You clicked the pinpad " + v.getTag(), Toast.LENGTH_SHORT).show();
    }
}
