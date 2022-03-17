package com.example.smartshoppingcart;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class CreateAccount extends AppCompatActivity {

    private static final int REQUEST_CODE_QR_SCAN = 101;

    //Variables
    TextInputLayout regName, regID, regPassword;
    Button regBtn, regToLoginBtn;
    ImageButton create_scan;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Hooks to all xml elements
        regName = findViewById(R.id.reg_name);
        regID = findViewById(R.id.reg_id);
        regPassword = findViewById(R.id.reg_pass);
        regBtn = (Button) findViewById(R.id.topup_btn);
        regToLoginBtn = (Button) findViewById(R.id.regToLogin_btn);

        create_scan = findViewById(R.id.create_scan);

        callingLogin();
        callingScanQR();
    }

    //Save data in Firebase on button click
    public void registerUser(View view) {

        if(!validateName() | !validateID() | !validatePassword())
        {
            return;
        }

        rootNode = FirebaseDatabase.getInstance(); //caling root node
        reference = rootNode.getReference("User");

        //Get all the values
        String name = regName.getEditText().getText().toString();
        String uid = regID.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();

        UserHelper helperClass = new UserHelper(name, uid, password, 0, 0);

        reference.child(uid).setValue(helperClass);

        Toast.makeText(this, "Account Registered!", Toast.LENGTH_LONG).show();

        regID.getEditText().setText("");
        regPassword.getEditText().setText("");
        regName.getEditText().setText("");

    }

    public void callingLogin() {
        Button login = (Button) findViewById(R.id.regToLogin_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_page = new Intent(CreateAccount.this, MainActivity.class);
                startActivity(add_page);
            }
        });
    }

    //Field Validation
    private Boolean validateName(){
        String val = regName.getEditText().getText().toString();

        if(val.isEmpty()){
            regName.setError("Field cannot be empty");
            return false;
        }
        else{
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateID(){
        String val = regID.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if(val.isEmpty()){
            regID.setError("Field cannot be empty");
            return false;
        }
        else if(val.length() >7 ){
            regID.setError("User ID too long");
            return false;
        }
        else if(val.length() < 7){
            regID.setError("User ID too short");
            return false;
        }
        else if(!val.matches(noWhiteSpace)){
            regID.setError("White Space are not allowed");
            return false;
        }
        else{
            regID.setError(null);
            regID.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = regPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("Password is too weak");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void callingScanQR() {

        create_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent i = new Intent(CreateAccount.this, QrCodeActivity.class);
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
                AlertDialog alertDialog = new AlertDialog.Builder(CreateAccount.this).create();
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
            regID.getEditText().setText(result);
        }
    }

}