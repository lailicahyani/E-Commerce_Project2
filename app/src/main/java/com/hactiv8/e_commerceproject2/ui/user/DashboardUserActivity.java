package com.hactiv8.e_commerceproject2.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
//import com.hactiv8.e_commerceproject2.adapter.AdapterCartItem;
import com.hactiv8.e_commerceproject2.adapter.AdapterCartItem;
import com.hactiv8.e_commerceproject2.adapter.AdapterProductUser;
import com.hactiv8.e_commerceproject2.databinding.ActivityDashboardUserBinding;
import com.hactiv8.e_commerceproject2.model.ModelCartItem;
import com.hactiv8.e_commerceproject2.model.ModelProduct;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

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

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

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

        deleteCartData();

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

//        binding.cart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               showCartDialog();
//            }
//        });

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


     private void deleteCartData() {
         EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                 .setTableName("ITEMS TABLE")  // You can ignore this line if you want
                 .addColumn(new Column("item_Id", new String[]{"text", "unique"}))
                 .addColumn(new Column("item_PID", new String[]{"text", "not null"}))
                 .addColumn(new Column("item_Name", new String[]{"text", "not null"}))
                 .addColumn(new Column("item_Price_Each", new String[]{"text", "not null"}))
                 .addColumn(new Column("item_Price", new String[]{"text", "not null"}))
                 .addColumn(new Column("item_Quantity", new String[]{"text", "not null"}))
                 .doneTableColumn();
         easyDB.deleteAllDataFromTable();
     }

     public double allTotalPrice = 0.000;
    public TextView sTotalTv, checkoutBtn;
    private void showCartDialog() {
        cartItemList = new ArrayList<>();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);
        RecyclerView cartItemRc = view.findViewById(R.id.cartItemRc);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        checkoutBtn = view.findViewById(R.id.checkoutBtn);

        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setView(view);

        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")  // You can ignore this line if you want
                .addColumn(new Column("item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pid = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);
            ModelCartItem modelCartItem = new ModelCartItem(
                    ""+id,
                    ""+pid,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );

            cartItemList.add(modelCartItem);
        }
        adapterCartItem = new AdapterCartItem(this, cartItemList);
        cartItemRc.setAdapter(adapterCartItem);

        sTotalTv.setText("Rp "+String.format ("%.3f",  allTotalPrice));


        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.000;
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