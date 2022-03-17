package com.example.smartshoppingcart.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartshoppingcart.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView item_name;
    public TextView item_price;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        item_name = itemView.findViewById(R.id.item_name);
        item_price = itemView.findViewById(R.id.item_price);
    }
}
