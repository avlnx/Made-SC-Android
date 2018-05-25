package com.made.madesc;

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

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class ProductListFragment extends Fragment {

    private CartItemAdapter mCartItemAdapter;
    private RecyclerView mProductListItemRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

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

    public static ProductListFragment newInstance(CartItemAdapter cartItemAdapter) {
        ProductListFragment fragment = new ProductListFragment();
        fragment.mCartItemAdapter = cartItemAdapter;
        return fragment;
    }

    private void setupCartItemAdapter() {
        mCartItemAdapter = new CartItemAdapter(Store.getProductsInActiveInventory());
        mProductListItemRecyclerView.setAdapter(mCartItemAdapter);
        mProductListItemRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProductListItemRecyclerView.setHasFixedSize(true);
        // Add horizontal divider
//        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
//        itemDecor.setOrientation(VERTICAL);
//        mCartItemRecyclerView.addItemDecoration(itemDecor);
    }

    public void updateCartItemAdapterDataSet() {
        mCartItemAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kiosk_list, container, false);
        mProductListItemRecyclerView = view.findViewById(R.id.rv_product_list);
        mLinearLayoutManager = new LinearLayoutManager(view.getContext());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupCartItemAdapter();
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
