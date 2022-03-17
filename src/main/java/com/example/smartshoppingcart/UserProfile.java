package com.example.smartshoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfile extends AppCompatActivity {

    TextInputLayout cust_name, cust_password;
    TextView custNameHeader, custIDHeader, cust_balance, noPurchase;
    MaterialCardView purchaseHistory;

    String name, password, id;
    int balance, purchase;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        reference = FirebaseDatabase.getInstance().getReference("User");

        //hooks
        cust_name = findViewById(R.id.cust_name);
        cust_password = findViewById(R.id.cust_password);
        custNameHeader = findViewById(R.id.custNameHeader);
        custIDHeader = findViewById(R.id.custIDHeader);
        cust_balance = findViewById(R.id.cust_balance);
        noPurchase = findViewById(R.id.noPurchase);
        purchaseHistory = findViewById(R.id.PurchaseHistory);

        //Intent Extra Data
        Intent intent = getIntent();
        name = intent.getStringExtra("cust_name");
        password = intent.getStringExtra("cust_password");
        id = intent.getStringExtra("cust_id");
        balance = intent.getIntExtra("cust_balance", 0);
        purchase = intent.getIntExtra("noPurchase", 0);

        //ShowAllData
        showAllUserData();

        showPurchaseHistory();
    }

    public void showPurchaseHistory() {
        purchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent history_page = new Intent(UserProfile.this, PurchaseHistory.class);
                history_page.putExtra("cust_id", id);
                startActivity(history_page);
            }
        });
    }

    private void showAllUserData() {


        custNameHeader.setText(name);
        String id1 = custIDHeader.getText().toString();
        custIDHeader.setText(id1 + id);
        int short_balance;
        if (balance >= 1000) {
            short_balance = balance / 1000;
        } else {
            short_balance = balance;
        }
        String blc = cust_balance.getText().toString();
        cust_balance.setText(blc + String.valueOf(short_balance) + "K");
        noPurchase.setText(String.valueOf(purchase));
        cust_name.getEditText().setText(name);
        cust_password.getEditText().setText(password);
    }

    public void update(View view) {

        int toast = 0;

        if (isNameChanged()) {
            custNameHeader.setText(cust_name.getEditText().getText().toString());
            toast = 1;
        }

        if (isPasswordChanged()) {
            if (toast == 1) {
                toast = 3;
            } else {
                toast = 2;
            }
        }

        if (toast == 1) {
            Toast.makeText(this, "Name has been updated", Toast.LENGTH_LONG).show();
        } else if (toast == 2) {
            Toast.makeText(this, "Password has been updated", Toast.LENGTH_LONG).show();
        } else if (toast == 3) {
            Toast.makeText(this, "Name and Password has been updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Data is same and cannot be updated", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isPasswordChanged() {

        if (!password.equals(cust_password.getEditText().getText().toString())) {
            reference.child(id).child("cust_password").setValue(cust_password.getEditText().getText().toString());
            password = cust_password.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }

    }

    private boolean isNameChanged() {

        if (!name.equals(cust_name.getEditText().getText().toString())) {
            reference.child(id).child("cust_name").setValue(cust_name.getEditText().getText().toString());
            name = cust_name.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }

    }
}