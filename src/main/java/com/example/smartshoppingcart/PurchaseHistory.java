package com.example.smartshoppingcart;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartshoppingcart.Model.Item;
import com.example.smartshoppingcart.Model.Purchase;
import com.example.smartshoppingcart.ViewHolder.ItemViewHolder;
import com.example.smartshoppingcart.ViewHolder.PurchaseViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PurchaseHistory extends AppCompatActivity {

    RecyclerView purchaseList;

    FirebaseDatabase database;
    DatabaseReference reference;

    String cust_id;
    FirebaseRecyclerAdapter<Purchase, PurchaseViewHolder> purchaseAdapter;
    FirebaseRecyclerAdapter<Item, ItemViewHolder> itemAdapter;
    RecyclerView.LayoutManager manager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);


        manager = new LinearLayoutManager(this);
        purchaseList = findViewById(R.id.purchaseList);
        purchaseList.setLayoutManager(manager);

        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        cust_id = intent.getStringExtra("cust_id");
        reference = database.getReference("PurchaseHistory").child(cust_id);

        FirebaseRecyclerOptions<Purchase> purchaseOption = new FirebaseRecyclerOptions.Builder<Purchase>()
                .setQuery(reference, Purchase.class)
                .build();

        purchaseAdapter = new FirebaseRecyclerAdapter<Purchase, PurchaseViewHolder>(purchaseOption) {
            @Override
            protected void onBindViewHolder(@NonNull PurchaseViewHolder holderPurchase, int position, @NonNull Purchase modelPurchase) {

                int noPurchase = Integer.parseInt(modelPurchase.getNoPurchase());

                holderPurchase.NoPurchase.setText("Purchase # " + String.valueOf(noPurchase));
                holderPurchase.TotalPurchase.setText("Rp. "+ String.valueOf(modelPurchase.getTotalPurchase()));


                FirebaseRecyclerOptions<Item> itemOption = new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(reference.child(modelPurchase.getNoPurchase()).child("Items"), Item.class)
                        .build();

                itemAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(itemOption) {
                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {

                        DatabaseReference itemData = database.getReference("ItemData").child(model.getItem_id());

                        itemData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String itemName = snapshot.child("item_name").getValue(String.class);
                                int itemPrice = snapshot.child("item_price").getValue(int.class);
                                holder.item_name.setText(itemName);
                                holder.item_price.setText(String.valueOf(itemPrice));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v2 = LayoutInflater.from(getBaseContext())
                                .inflate(R.layout.item_layout, parent, false);
                        return  new ItemViewHolder(v2);
                    }
                };

                itemAdapter.startListening();
                itemAdapter.notifyDataSetChanged();
                holderPurchase.itemList.setAdapter(itemAdapter);

            }

            @NonNull
            @Override
            public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.purchase_layout, parent, false);
                return  new PurchaseViewHolder(v1);
            }
        };

        purchaseAdapter.startListening();
        purchaseAdapter.notifyDataSetChanged();
        purchaseList.setAdapter(purchaseAdapter);
    }
}