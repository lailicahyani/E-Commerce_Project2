package com.hactiv8.e_commerceproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.os.Bundle;

import com.hactiv8.e_commerceproject2.databinding.ActivityLoginBinding;
import com.hactiv8.e_commerceproject2.ui.user.RegisterUserActivity;


public class LoginActivity extends AppCompatActivity{

    //view binding
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //handle klik register screen
        binding.createAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));
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
}
