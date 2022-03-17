package com.example.smartshoppingcart.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartshoppingcart.R;

public class PurchaseViewHolder extends RecyclerView.ViewHolder {

    public TextView TotalPurchase, NoPurchase;
    public RecyclerView itemList;
    public RecyclerView.LayoutManager manager;

    public PurchaseViewHolder(@NonNull View itemView) {
        super(itemView);

        manager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false);
        TotalPurchase = itemView.findViewById(R.id.total_purchase);
        NoPurchase = itemView.findViewById(R.id.no_purchase);
        itemList = itemView.findViewById(R.id.itemList);
        itemList.setLayoutManager(manager);
    }


}
