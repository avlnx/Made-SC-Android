package com.made.madesc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryItemViewHolder> {
    private ArrayList<Product> mDataSet;

    /*
    // Create new views (invoked by the layout manager)
    @Override
    public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);

        final CartItemViewHolder viewHolder = new CartItemViewHolder(view);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onItemClick(v, viewHolder.getAdapterPosition());
//            }
//        });

        return viewHolder;
    }
     */
    class InventoryItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mProductImageView;
//        TextView mQuantityTextView;
        TextView mProductTitleTextView;
        TextView mProductPrice;
        ImageButton mIncreaseQuantity;
        ImageButton mDecreaseQuantity;

        InventoryItemViewHolder(View view) {
            super(view);
            mProductImageView = (ImageView) view.findViewById(R.id.iv_product_image);
//            mQuantityTextView = (TextView) view.findViewById(R.id.tv_quantity);
            mProductTitleTextView = (TextView) view.findViewById(R.id.tv_product_title);
            mProductPrice = (TextView) view.findViewById(R.id.tv_price);
            mIncreaseQuantity = (ImageButton) view.findViewById(R.id.ib_increase_quantity);
            mDecreaseQuantity = (ImageButton) view.findViewById(R.id.ib_decrease_quantity);
        }
    }

    InventoryItemAdapter(ArrayList<Product> products) {
        mDataSet = products;
    }

    @Override
    public InventoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item, parent, false);

        final InventoryItemViewHolder viewHolder = new InventoryItemViewHolder(view);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(InventoryItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Product product = mDataSet.get(position);

//        holder.mQuantityTextView.setText(Cart.getQuantityInCartForProductRepresentation(product.getProductId()));
        holder.mProductPrice.setText(product.getPublicPriceCurrencyRepresentation());
        holder.mProductTitleTextView.setText(product.getTitle());
        holder.mIncreaseQuantity.setTag(product.getProductId());
        holder.mDecreaseQuantity.setTag(product.getProductId());

        // TODO: fetch and set image
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
