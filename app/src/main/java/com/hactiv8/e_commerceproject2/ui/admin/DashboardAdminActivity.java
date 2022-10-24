package com.hactiv8.e_commerceproject2.ui.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hactiv8.e_commerceproject2.MainActivity;
import com.hactiv8.e_commerceproject2.R;
import com.hactiv8.e_commerceproject2.SplashActivity;
import com.hactiv8.e_commerceproject2.databinding.ActivityDashboardAdminBinding;

public class DashboardAdminActivity extends AppCompatActivity {

    //view binding
    private ActivityDashboardAdminBinding binding;

    //firebase Auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //handle klik logout
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser ==  null){
            //user not login
            //start main screean
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else{
            //login get user info
            String email =  firebaseUser.getEmail();
            //set in text view of  toolbar
            binding.subTitle.setText(email);
        }
    }
}