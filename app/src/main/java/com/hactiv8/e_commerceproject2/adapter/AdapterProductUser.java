package com.hactiv8.e_commerceproject2.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hactiv8.e_commerceproject2.R;
import com.hactiv8.e_commerceproject2.filter.FilterProduct;
import com.hactiv8.e_commerceproject2.filter.FilterProductUser;
import com.hactiv8.e_commerceproject2.model.ModelProduct;
import com.hactiv8.e_commerceproject2.ui.admin.EditProductActivity;
import com.hactiv8.e_commerceproject2.ui.user.DashboardUserActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList, filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        ModelProduct modelProduct = productsList.get(position);
        String id = modelProduct.getProductId();
        final String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productDesc = modelProduct.getProductDesc();
        String icon = modelProduct.getLogoProduct();
        String productQuantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();

        holder.titleTv.setText(title);
        holder.discountNote.setText(discountNote);
        holder.discountPrice.setText("Rp " +discountPrice);
        holder.originalPrice.setText("Rp " +originalPrice);

        if (discountAvailable.equals("true")){
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.discountNote.setVisibility(View.VISIBLE);
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.discountPrice.setVisibility(View.GONE);
            holder.discountNote.setVisibility(View.GONE);
            holder.originalPrice.setPaintFlags(0);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.shopping_cart).into(holder.productIcon);
        }
        catch (Exception e){
            holder.productIcon.setImageResource(R.drawable.shopping_cart);

        }

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = 0.000;
                cost = 0.000;
                showQuantityDialog(modelProduct);
                

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottomSheet(modelProduct);
            }
        });
    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view
        View view = LayoutInflater.from(context).inflate(R.layout.details_product_user, null);
        bottomSheetDialog.setContentView(view);


        //init views of bottomshet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageView productIcon = view.findViewById(R.id.productIcon);
        TextView discountNoteTv = view.findViewById(R.id.discountNote);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descTv = view.findViewById(R.id.descTv);
        TextView categoryTv = view.findViewById(R.id.categoryTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        TextView discountPriceTv = view.findViewById(R.id.discountPrice);
        TextView originalPriceTv = view.findViewById(R.id.originalPrice);

        //get data
        String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productDesc = modelProduct.getProductDesc();
        String icon = modelProduct.getLogoProduct();
        String quantity = modelProduct.getProductQuantity();
        String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();

        //set data
        titleTv.setText(title);
        descTv.setText(productDesc);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantity);
        discountNoteTv.setText(discountNote);
        discountPriceTv.setText("Rp " +discountPrice);
        originalPriceTv.setText("Rp " +originalPrice);

        if (discountAvailable.equals("true")){
            discountPriceTv.setVisibility(View.VISIBLE);
            discountNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            discountPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.shopping_cart).into(productIcon);
        }
        catch (Exception e){
            productIcon.setImageResource(R.drawable.shopping_cart);
        }

        //show dialog
        bottomSheetDialog.show();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismis bottom
                bottomSheetDialog.dismiss();
            }
        });
    }

    private double cost = 0.000;
    private double finalCost = 0.000;
    private int quantity = 0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);
        
        ImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView discountPriceTv = view.findViewById(R.id.discountPriceTv);
        TextView diskonNote = view.findViewById(R.id.discountNote);
        TextView pquantityTv = view.findViewById(R.id.pquantityTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        TextView finalPriceTv = view.findViewById(R.id.finalTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button addCart = view.findViewById(R.id.addCart);

        final String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String discountNote = modelProduct.getDiscountNote();
        String image = modelProduct.getLogoProduct();
        String productQuantity = modelProduct.getProductQuantity();
        
        final String price;
        if (modelProduct.getDiscountAvailable().equals("true")){
            price = modelProduct.getDiscountPrice();
            diskonNote.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            diskonNote.setVisibility(View.GONE);
            discountPriceTv.setVisibility(View.GONE);
            price = modelProduct.getOriginalPrice();
        }
        cost = Double.parseDouble(price.replaceAll(",","."));
        finalCost =  Double.parseDouble(price.replaceAll(",","."));
        quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        
        try {
            Picasso.get().load(image).placeholder(R.drawable.shopping_cart).into(productIv);
        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.shopping_cart);
        }
        titleTv.setText(""+title);
        pquantityTv.setText(""+productQuantity);
        diskonNote.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("Rp "+modelProduct.getOriginalPrice());
        discountPriceTv.setText("Rp "+modelProduct.getDiscountPrice());
        finalPriceTv.setText("Rp "+String.format ("%.3f",  finalCost));
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;
                
                finalPriceTv.setText("Rp "+String.format ("%.3f",  finalCost));
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity>1){
                    finalCost = finalCost - cost;
                    quantity --;
                    
                    finalPriceTv.setText("Rp "+String.format ("%.3f",  finalCost));
                    quantityTv.setText(""+quantity);
                }
            }
        });
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace(",", ".");
                String quantity = quantityTv.getText().toString().trim();
                
                addToCart(productId, title, priceEach, totalPrice, quantity);
                
                dialog.dismiss();
            }
        });
    }

    private int itemId = 1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        boolean done = easyDB.addData("item_Id", itemId)
                .addData("item_PID", productId)
                .addData("item_Name", title)
                .addData("item_Price_Each", priceEach)
                .addData("item_Price", price)
                .addData("item_Quantity", quantity)
                .doneDataAdding();

        Toast.makeText(context, "Add To Cart", Toast.LENGTH_SHORT).show();


    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProductUser(this, filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder {

        private ImageView productIcon, nextTv;
        private TextView discountNote, titleTv,addToCartTv, discountPrice, originalPrice ;


        public HolderProductUser(@NonNull View itemView) {

            super(itemView);

            productIcon = itemView.findViewById(R.id.productIcon);
            nextTv = itemView.findViewById(R.id.nextTv);
            discountNote = itemView.findViewById(R.id.discountNote);
            titleTv = itemView.findViewById(R.id.titleTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            discountPrice = itemView.findViewById(R.id.discountPrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
        }
    }
}
