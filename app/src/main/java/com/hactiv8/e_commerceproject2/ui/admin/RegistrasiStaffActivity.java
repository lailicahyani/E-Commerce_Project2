package com.hactiv8.e_commerceproject2.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hactiv8.e_commerceproject2.R;

import com.hactiv8.e_commerceproject2.databinding.ActivityRegistrasiStaffBinding;
import com.hactiv8.e_commerceproject2.ui.user.DashboardUserActivity;

import java.util.HashMap;

public class RegistrasiStaffActivity extends AppCompatActivity {


    //view binding
    private ActivityRegistrasiStaffBinding binding;

    //firebase Auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrasiStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar...");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle klik button register
        binding.RegisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //valodation
                validateData();
            }
        });
    }

    private String username = "", email = "", phone = "", password = "";
    private void validateData() {
        /*Sebeluam membuat akun*/
        //get data
        username = binding.inputUsername.getText().toString().trim();
        email = binding.inputEmail.getText().toString().trim();
        phone = binding.inputPhone.getText().toString().trim();
        password = binding.inputPassword.getText().toString().trim();
        String cPassword =  binding.inputPassword2.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(username)){

            Toast.makeText(this, "Masukkan nama", Toast.LENGTH_LONG).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            Toast.makeText(this, "Email Tidak Valid", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Password Harus Lebih Dari 6", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(cPassword)){

            Toast.makeText(this, "Konfirmasi Password", Toast.LENGTH_LONG).show();
        }
        else if (!password.equals(cPassword)){

            Toast.makeText(this, "Password Tidak Sama", Toast.LENGTH_LONG).show();
        }
        else {
            //validasi semua data
            createUserAccount();
        }
    }

    private void createUserAccount() {
        //show progress
        progressDialog.setMessage("Buat Akun...");
        progressDialog.show();

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account creating sukses, now add in firebase realtime database
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //account creating failed
                        progressDialog.dismiss();
                        Toast.makeText(RegistrasiStaffActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Save Data...");

        //timetamp
        final String timestamp = "" +System.currentTimeMillis();

        //get current user id
//        String uid = firebaseAuth.getUid();

        //setup data to add in db
        HashMap<String, Object> hashMap =  new HashMap<>();
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("email", email);
        hashMap.put("username", username);
        hashMap.put("userType", "staff");
        hashMap.put("timestamp", ""+timestamp);

        //set data ke db
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference("user");
        ref.child(firebaseAuth.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data add to db
                        progressDialog.dismiss();
                        Toast.makeText(RegistrasiStaffActivity.this, "Akun Dibuat...", Toast.LENGTH_LONG).show();
                        //akun berhasil dibuat, menampilkan dashboard user
                        startActivity(new Intent(RegistrasiStaffActivity.this, DashboardAdminActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //data failed to db
                        progressDialog.dismiss();
                        Toast.makeText(RegistrasiStaffActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}