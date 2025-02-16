package com.made.madesc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>  {
    private ArrayList<Product> mDataSet;
    private Cart mCart;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class CartItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView mProductImageView;
        TextView mQuantityTextView;
        TextView mProductTitleTextView;
        TextView mProductPrice;
//        ImageButton mIncreaseQuantity;
//        ImageButton mDecreaseQuantity;

        CartItemViewHolder(View view) {
            super(view);
            mProductImageView = (ImageView) view.findViewById(R.id.iv_product_image);
            mQuantityTextView = (TextView) view.findViewById(R.id.tv_quantity);
            mProductTitleTextView = (TextView) view.findViewById(R.id.tv_product_title);
            mProductPrice = (TextView) view.findViewById(R.id.tv_price);
//            mIncreaseQuantity = (ImageButton) view.findViewById(R.id.ib_increase_quantity);
//            mDecreaseQuantity = (ImageButton) view.findViewById(R.id.ib_decrease_quantity);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    CartItemAdapter(ArrayList<Product> products, Cart cart) {
        this.mDataSet = products;
        this.mCart = cart;
    }

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

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CartItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Product product = mDataSet.get(position);

        holder.mQuantityTextView.setText(mCart.getQuantityInCartForProductRepresentation(product.getProductId()));
        holder.mProductPrice.setText(product.getPublicPriceCurrencyRepresentation());
        holder.mProductTitleTextView.setText(product.getTitle());
//        holder.mIncreaseQuantity.setTag(product.getProductId());
//        holder.mDecreaseQuantity.setTag(product.getProductId());

        // TODO: fetch and set image
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
