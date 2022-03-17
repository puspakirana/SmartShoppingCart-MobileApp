package com.example.smartshoppingcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

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

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QR_SCAN = 101;
    TextInputLayout userID, userPassword;
    ImageButton scan_qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView brgapp = (ImageView) findViewById(R.id.brgapp);
        LinearLayout textsplash = (LinearLayout) findViewById(R.id.textsplash);
        LinearLayout texthome = (LinearLayout) findViewById(R.id.texthome);
        LinearLayout menus = (LinearLayout) findViewById(R.id.menus);
        scan_qr = findViewById(R.id.scan_qr);

        Animation explore = AnimationUtils.loadAnimation(this, R.anim.explore);

        brgapp.animate().translationY(-1900).setDuration(1000).setStartDelay(400);
        textsplash.animate().translationY(300).alpha(0).setDuration(500).setStartDelay(400);

        texthome.startAnimation(explore);
        menus.startAnimation(explore);

        userID = (TextInputLayout) findViewById(R.id.cust_id);
        userPassword = (TextInputLayout) findViewById(R.id.cust_password);

        callingCreateAccount();

        callingScanQR();

    }

    private Boolean validateID() {
        String val = userID.getEditText().getText().toString();

        if (val.isEmpty()) {
            userID.setError("Field cannot be empty");
            return false;
        } else {
            userID.setError(null);
            userID.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = userPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            userPassword.setError("Field cannot be empty");
            return false;
        } else {
            userPassword.setError(null);
            userPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        //Validate login info
        if (!validateID() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        String userEnteredID = userID.getEditText().getText().toString().trim();
        String userEnteredPassword = userPassword.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

        Query checkUser = reference.orderByChild("cust_id").equalTo(userEnteredID);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    userID.setError(null);
                    userID.setErrorEnabled(false);

                    String passwordFromDB = snapshot.child(userEnteredID).child("cust_password").getValue(String.class);

                    if (passwordFromDB.equals(userEnteredPassword)) {

                        userID.setError(null);
                        userID.setErrorEnabled(false);


                        String idFromDB = snapshot.child(userEnteredID).child("cust_id").getValue(String.class);


                        if (idFromDB.equals("admin")) {

                            Intent topup_page = new Intent(MainActivity.this, TopUpBalance.class);
                            startActivity(topup_page);

                        } else {
                            //go to dashboard
                            String nameFromDB = snapshot.child(userEnteredID).child("cust_name").getValue(String.class);
                            int balanceFromDB = snapshot.child(userEnteredID).child("cust_balance").getValue(int.class);
                            int purchaseFromDB = snapshot.child(userEnteredID).child("noPurchase").getValue(int.class);

                            Intent dash_page = new Intent(getApplicationContext(), UserProfile.class);
                            dash_page.putExtra("cust_name", nameFromDB);
                            dash_page.putExtra("cust_id", idFromDB);
                            dash_page.putExtra("cust_balance", balanceFromDB);
                            dash_page.putExtra("noPurchase", purchaseFromDB);
                            dash_page.putExtra("cust_password", passwordFromDB);

                            startActivity(dash_page);
                        }


                        userPassword.getEditText().setText("");
                        userID.getEditText().setText("");

                    } else {
                        userPassword.setError("Wrong Password");
                        userPassword.requestFocus();
                    }
                } else {
                    userID.setError("User ID not exist");
                    userID.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void callingCreateAccount() {
        Button create_account = (Button) findViewById(R.id.create_account);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_page = new Intent(MainActivity.this, CreateAccount.class);
                startActivity(add_page);
            }
        });
    }

    public void callingScanQR() {

        scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
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
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
            userID.getEditText().setText(result);

        }
    }
}