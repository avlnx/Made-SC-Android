package com.made.madesc;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PinpadListAdapter extends RecyclerView.Adapter<PinpadListAdapter.PinpadViewHolder>  {
    private List<String> mDataSet;
//    private CustomItemClickListener mListener;

//    public interface CustomItemClickListener {
//        void onItemClick(View v, int position);
//    }

    // Provide a suitable constructor (depends on the kind of dataset)
//    PinpadListAdapter(ArrayList<String> devices, CustomItemClickListener listener) {
//        this.mDataSet = devices;
//        this.mListener = listener;
//    }
    PinpadListAdapter(List<String> devices) {
        this.mDataSet = devices;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class PinpadViewHolder extends RecyclerView.ViewHolder {
        TextView mPinpadName;
        TextView mPinpadMacAddress;

        PinpadViewHolder(View view) {
            super(view);
            mPinpadName = (TextView) view.findViewById(R.id.tv_pinpad_name);
            mPinpadMacAddress = (TextView) view.findViewById(R.id.tv_pinpad_mac_address);
        }
    }

    @NonNull
    @Override
    public PinpadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pinpad_list_row, parent, false);
        PinpadViewHolder viewHolder = new PinpadViewHolder(view);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PinpadViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String device = mDataSet.get(position);
        // The string device above holds the name and the mac address separated by a _
        String[] pinpadInfo = device.split("_");

        holder.mPinpadName.setText(pinpadInfo[0]);
        holder.mPinpadMacAddress.setText(pinpadInfo[1]);
        holder.mPinpadName.setTag(device);
    }

    public void setDataSet(List<String> devices) {
        mDataSet = devices;
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
