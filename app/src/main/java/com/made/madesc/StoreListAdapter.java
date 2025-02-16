package com.made.madesc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreViewHolder> {
    private ArrayList<Store> mDataSet;
    private CustomItemClickListener mListener;

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class StoreViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mNickname;

        StoreViewHolder(View view) {
            super(view);
            mNickname = (TextView) view.findViewById(R.id.tv_nickname);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    StoreListAdapter(ArrayList<Store> stores, CustomItemClickListener listener) {
        this.mDataSet = stores;
        this.mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_list_row, parent, false);

        final StoreViewHolder viewHolder = new StoreViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Store store = mDataSet.get(position);
        holder.mNickname.setText(store.getNickname());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
