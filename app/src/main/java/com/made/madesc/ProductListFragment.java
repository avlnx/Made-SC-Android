package com.made.madesc;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.made.madesc.model.KioskViewModel;

import java.util.HashMap;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class ProductListFragment extends Fragment {

    private InventoryItemAdapter mInventoryItemAdapter;
    private RecyclerView mProductListItemRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private KioskViewModel mKioskModel;
    private Store mActiveStore;
    private HashMap<String, Product> mCatalog;

    public ProductListFragment() {}

    /*
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
     */

    public static ProductListFragment newInstance(InventoryItemAdapter cartItemAdapter) {
        ProductListFragment fragment = new ProductListFragment();
        fragment.mInventoryItemAdapter = cartItemAdapter;
        return fragment;
    }

    private void setupInventoryItemAdapter() {
        mInventoryItemAdapter = new InventoryItemAdapter(mActiveStore.getListOfProductsWithData());
        mProductListItemRecyclerView.setAdapter(mInventoryItemAdapter);
        mProductListItemRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProductListItemRecyclerView.setHasFixedSize(true);
        // Add horizontal divider
//        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
//        itemDecor.setOrientation(VERTICAL);
//        mCartItemRecyclerView.addItemDecoration(itemDecor);
    }

    public void updateCartItemAdapterDataSet() {
        mInventoryItemAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kiosk_list, container, false);
        mProductListItemRecyclerView = view.findViewById(R.id.rv_product_list);
        mLinearLayoutManager = new LinearLayoutManager(view.getContext());

        // Get viewmodel so we can get the store's data (and cart if needed)
        mKioskModel = ViewModelProviders.of(getActivity()).get(KioskViewModel.class);
        // Get catalog to get inventory
//        mKioskModel.getCatalog().observe(getActivity(), new Observer<HashMap<String, Product>>() {
//            @Override
//            public void onChanged(@Nullable HashMap<String, Product> stringProductHashMap) {
//                mCatalog = stringProductHashMap;
//            }
//        });

        mKioskModel.getActiveStore().observe(getActivity(), new Observer<Store>() {
            @Override
            public void onChanged(@Nullable Store store) {
                mActiveStore = store;
                if (mInventoryItemAdapter == null) {
                    // first time around, setup adapter
                    setupInventoryItemAdapter();
                }
                updateCartItemAdapterDataSet();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
