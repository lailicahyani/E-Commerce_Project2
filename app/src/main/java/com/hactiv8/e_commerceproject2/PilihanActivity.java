package com.hactiv8.e_commerceproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.hactiv8.e_commerceproject2.databinding.ActivityPilihanBinding;
import com.hactiv8.e_commerceproject2.ui.admin.LoginAdminActivity;
import com.hactiv8.e_commerceproject2.ui.user.LoginActivity;

public class PilihanActivity extends AppCompatActivity {

    private ActivityPilihanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPilihanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //handle klik login admin screen
        binding.adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PilihanActivity.this, LoginAdminActivity.class));
            }
        });

        //handle klik login staff screen
        binding.staffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PilihanActivity.this, LoginAdminActivity.class));
            }
        });

        //handle klik login customer screen
        binding.customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PilihanActivity.this, LoginActivity.class));
            }
        });
    }
}