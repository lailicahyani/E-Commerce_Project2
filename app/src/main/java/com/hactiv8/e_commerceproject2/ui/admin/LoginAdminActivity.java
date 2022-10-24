package com.hactiv8.e_commerceproject2.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hactiv8.e_commerceproject2.LoginActivity;
import com.hactiv8.e_commerceproject2.R;
import com.hactiv8.e_commerceproject2.databinding.ActivityLoginAdminBinding;
import com.hactiv8.e_commerceproject2.ui.user.DashboardUserActivity;
import com.hactiv8.e_commerceproject2.ui.user.RegisterUserActivity;

public class LoginAdminActivity extends AppCompatActivity {

    //view binding
    private ActivityLoginAdminBinding binding;

    //firebase Auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar...");
        progressDialog.setCanceledOnTouchOutside(false);


        //handle klik button login
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        //handle klik lupa password screen
//        binding.createAcount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }
    private String email = "", password = "";
    private void validateData() {
        /*Sebeluam login*/
        //get data
        email = binding.inputUsername.getText().toString().trim();
        password = binding.inputPassword.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Username Salah!", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Password Salah!", Toast.LENGTH_LONG).show();
        }
        else{
            //data is validate, begin login
            loginUser();
        }
    }

    private void loginUser() {
        //show progress
        progressDialog.setTitle("Login...");
        progressDialog.show();

        //login user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login sukses, cek user atau admin
                        cekUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginAdminActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void cekUser() {
        progressDialog.setMessage("Cheking User..");
        //cek user / admin
        //get current user
        FirebaseUser firebaseUser =  firebaseAuth.getCurrentUser();

        //cek in db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        //get user type
                        String userType = ""+snapshot.child("userType").getValue();
                        //chek user type
                        if (userType.equals("admin")){
                            //this is simple admin,open admin dashboard
                            startActivity(new Intent(LoginAdminActivity.this, DashboardAdminActivity.class));
                            finish();
                        }
                        else if (userType.equals("staff")){
                            //this is simple staff,open staff dashboard
                            startActivity(new Intent(LoginAdminActivity.this, DashboardUserActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}