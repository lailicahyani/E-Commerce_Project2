package com.hactiv8.e_commerceproject2.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hactiv8.e_commerceproject2.filter.FilterProduct;
import com.hactiv8.e_commerceproject2.R;
import com.hactiv8.e_commerceproject2.model.ModelProduct;
import com.hactiv8.e_commerceproject2.ui.admin.EditProductActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.HolderProduct> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    private ProgressDialog progressDialog;

    public AdapterProduct(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Tunggu Sebentar...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product, parent, false);
        return new HolderProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProduct holder, int position) {
        ModelProduct modelProduct = productList.get(position);
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

        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item click
                detailsBottomSheet(modelProduct);
            }
        });
    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        //bottom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        //inflate view
        View view = LayoutInflater.from(context).inflate(R.layout.details_product, null);
        bottomSheetDialog.setContentView(view);


        //init views of bottomshet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
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

        //edit
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //open edit product
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("productId", id);
                context.startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //buka delete
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete..")
                        .setMessage("Apakah Anda yakin ingin menghapus produk " +title+ "?")
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProduct(id);
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismis bottom
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void deleteProduct(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
        reference.child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Produk Berhasil di hapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProduct extends RecyclerView.ViewHolder{

        private ImageView productIcon, nextTv;
        private TextView discountNote, titleTv, quantityTv, discountPrice, originalPrice;

        public HolderProduct(@NonNull View itemView) {
            super(itemView);

            productIcon = itemView.findViewById(R.id.productIcon);
            discountNote = itemView.findViewById(R.id.discountNote);
            titleTv = itemView.findViewById(R.id.titleTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            discountPrice = itemView.findViewById(R.id.discountPrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            nextTv = itemView.findViewById(R.id.nextTv);

        }
    }
}
