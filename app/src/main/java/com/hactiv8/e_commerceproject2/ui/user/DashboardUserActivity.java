 package com.hactiv8.e_commerceproject2.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hactiv8.e_commerceproject2.Constants;
import com.hactiv8.e_commerceproject2.MainActivity;
import com.hactiv8.e_commerceproject2.R;
import com.hactiv8.e_commerceproject2.adapter.AdapterProduct;
import com.hactiv8.e_commerceproject2.adapter.AdapterProductUser;
import com.hactiv8.e_commerceproject2.databinding.ActivityDashboardUserBinding;
import com.hactiv8.e_commerceproject2.model.ModelProduct;
import com.hactiv8.e_commerceproject2.ui.admin.DashboardAdminActivity;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {

    private TextView tabProduct, tabOrder, filteredProduct;
    private EditText search;
    private ImageButton filterBtn;
    private RelativeLayout productRl, orderRl;
    private RecyclerView productsRc;

//    private String productUid;

    //view binding
    private ActivityDashboardUserBinding binding;

    //firebase Auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;
    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tabProduct = findViewById(R.id.tabProduct);
        tabOrder = findViewById(R.id.tabOrder);
        search = findViewById(R.id.search);
        filteredProduct = findViewById(R.id.filteredProduct);
        filterBtn = findViewById(R.id.filterBtn);
        productsRc = findViewById(R.id.productsRc);
        productRl = findViewById(R.id.productRl);
        orderRl = findViewById(R.id.orderRl);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu..");
        progressDialog.setCanceledOnTouchOutside(false);

//        productUid = getIntent().getStringExtra("productUid");

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        loadAllProducts();

        showProductsUI();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductUser.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        handle klik logout
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        binding.tabProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductsUI();
            }
        });

//        handle klik tabOrder
        binding.tabOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrdersUI();
            }
        });

        binding.filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardUserActivity.this);
                builder.setTitle("Memilih Category..")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constants.productCategories1[which];
                                filteredProduct.setText(selected);
                                if (selected.equals("All")){
                                    loadAllProducts();
                                }
                                else{
                                    loadFilteredProducts(selected);
                                }
                            }
                        })
                        .show();
            }
        });

    }

    private void loadFilteredProducts(String selected) {
        productsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){

                            String productCategory = ""+ds.child("productCategory").getValue();
                            if (selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productsList.add(modelProduct);
                            }

                        }
                        adapterProductUser = new AdapterProductUser(DashboardUserActivity.this, productsList);
                        productsRc.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productsList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }
                        adapterProductUser = new AdapterProductUser(DashboardUserActivity.this, productsList);
                        productsRc.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//    private void loadAllProducts() {
//        productsList = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
//        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    productsList.clear();
//                    for (DataSnapshot ds: dataSnapshot.getChildren()){
//                        String userType = ""+ds.child("userType").getValue();
//                        String productId = ""+ds.child("productId").getValue();
//
////                        titleTv = ""+ds.child("titleTv").getValue();
////                        productIcon = ""+ds.child("productIcon").getValue();
////                        nextTv = ""+ds.child("nextTv").getValue();
////                        discountNote = ""+ds.child("discountNote").getValue() ;
////                        addToCartTv = ""+ds.child("addToCartTv").getValue();
////                        discountPrice = ""+ds.child("discountPrice").getValue();
////                        originalPrice = ""+ds.child("originalPrice").getValue();
//
//                        loadProduct(productId);
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//    }
//
//    private void loadProduct(String productId) {
//        productsList = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
//        reference.orderByChild("userType").equalTo("admin")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        productsList.clear();
//                        for (DataSnapshot ds: dataSnapshot.getChildren()){
//                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
//
//                            String Product = ""+ds.child("productId").getValue();
//
//                            if (Product.equals(productId)){
//                                productsList.add(modelProduct);
//                            }
//                        }
//                        adapterProductUser = new AdapterProductUser(DashboardUserActivity.this, productsList);
//                        productsRc.setAdapter(adapterProductUser);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }

    private void showProductsUI() {
        //show product ui and hide order
        productRl.setVisibility(View.VISIBLE);
        orderRl.setVisibility(View.GONE);

        tabProduct.setTextColor(getResources().getColor(R.color.black));
        tabProduct.setBackgroundResource(R.drawable.shape_rect);

        tabOrder.setTextColor(getResources().getColor(R.color.white));
        tabOrder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    private void showOrdersUI() {
        //show order ui and hide product
        productRl.setVisibility(View.GONE);
        orderRl.setVisibility(View.VISIBLE);

        tabProduct.setTextColor(getResources().getColor(R.color.white));
        tabProduct.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrder.setTextColor(getResources().getColor(R.color.black));
        tabOrder.setBackgroundResource(R.drawable.shape_rect);
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
        else {
            //login get user info
            String email = firebaseUser.getEmail();
            //set in text view of  toolbar
            binding.subTitle.setText(email);
        }
    }
}