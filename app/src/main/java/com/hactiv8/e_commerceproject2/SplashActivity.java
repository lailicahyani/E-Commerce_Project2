package com.hactiv8.e_commerceproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hactiv8.e_commerceproject2.ui.admin.DashboardAdminActivity;
import com.hactiv8.e_commerceproject2.ui.user.DashboardUserActivity;

public class SplashActivity extends AppCompatActivity {

    //firebase Auth
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
//                // start main screen
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish();
            }
        },2000);
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser ==  null){
            //user not login
            //start main screean
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
        else {
            //user log in chek user type
            //cek in db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //get user type
                            String userType = ""+snapshot.child("userType").getValue();
                            //chek user type
                            if (userType.equals("user")){
                                //this is simple user,open user dashboard
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                            else if (userType.equals("admin")){
                                //this is simple admin,open admin dashboard
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                            else if (userType.equals("staff")){
                                //this is simple staff,open staff dashboard
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}