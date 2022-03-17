package com.example.smartshoppingcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class TopUpBalance extends AppCompatActivity {

    private static final int REQUEST_CODE_QR_SCAN = 101;
    TextInputLayout topup_id, topup_balance;
    ImageButton topup_scan;
    Button topup_btn;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_balance);

        reference = FirebaseDatabase.getInstance().getReference("User");

        topup_scan = findViewById(R.id.topup_scan);
        topup_id = findViewById(R.id.topup_id);
        topup_balance = findViewById(R.id.topup_balance);
        topup_btn = findViewById(R.id.topup_btn);

        callingScanQR();


    }


    public void updateBalance(View view)
    {
        if (!validateID() | !validateBalance()) {
            return;
        } else {
            updateData();
        }


    }

    public void updateData() {
        String id = topup_id.getEditText().getText().toString();
        String balance_str = topup_balance.getEditText().getText().toString();
        int balance = Integer.parseInt(balance_str);


        Query checkUser = reference.orderByChild("cust_id").equalTo(id);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    topup_id.setError(null);
                    topup_id.setErrorEnabled(false);

                    int dbBalance = snapshot.child(id).child("cust_balance").getValue(int.class);
                    int newBalance = dbBalance + balance;
                    reference.child(id).child("cust_balance").setValue(newBalance);

                    topup_id.getEditText().setText("");
                    topup_balance.getEditText().setText("");

                    showToast();
                }
                else {
                    topup_id.setError("User ID not exist");
                    topup_id.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void showToast()
    {
        Toast.makeText(this, "Top Up Succeed", Toast.LENGTH_LONG).show();
    }

    public void callingScanQR() {

        topup_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent i = new Intent(TopUpBalance.this, QrCodeActivity.class);
                                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                permissionDeniedResponse.getRequestedPermission();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(TopUpBalance.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            topup_id.getEditText().setText(result);

        }
    }

    private Boolean validateID() {
        String val = topup_id.getEditText().getText().toString();

        if (val.isEmpty()) {
            topup_id.setError("Field cannot be empty");
            return false;
        } else {
            topup_id.setError(null);
            topup_id.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateBalance() {
        String val = topup_balance.getEditText().getText().toString();

        if (val.isEmpty()) {
            topup_balance.setError("Field cannot be empty");
            return false;
        } else if(Integer.parseInt(val) < 100000) {
            topup_balance.setError("Minimum top up is Rp. 100.000");
            return false;
        } else{
            topup_balance.setError(null);
            topup_balance.setErrorEnabled(false);
            return true;
        }
    }

}

