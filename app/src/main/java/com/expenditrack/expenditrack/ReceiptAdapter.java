package com.expenditrack.expenditrack;

/*
 * Created by Aaron on 22/01/2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReceiptAdapter extends ArrayAdapter<Receipt> {

    public ReceiptAdapter(Context context, ArrayList<Receipt> item) {
        super(context, 0, item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Receipt receipt = getItem(position);
        //Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.receipt_adapter_layout, parent, false);
        }

        //Lookup view for data population
        TextView tvSupplierName = convertView.findViewById(R.id.tvSupplierName);
        TextView tvTimestamp = convertView.findViewById(R.id.tvTimestamp);
        TextView tvTotalSpent = convertView.findViewById(R.id.tvTotalSpent);
        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView category = convertView.findViewById(R.id.category);

        try {
            tvSupplierName.setText(receipt.supplierName);
            tvTimestamp.setText(receipt.timeStamp);
            tvTotalSpent.setText("€"+receipt.totalSpent);
            tvUsername.setText(receipt.username);
            category.setText(receipt.category);
        }catch (Exception e){

        }
        return convertView;

    }

}
