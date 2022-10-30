package com.hactiv8.e_commerceproject2.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hactiv8.e_commerceproject2.R;
import com.hactiv8.e_commerceproject2.filter.FilterProduct;
import com.hactiv8.e_commerceproject2.filter.FilterProductUser;
import com.hactiv8.e_commerceproject2.model.ModelProduct;
import com.hactiv8.e_commerceproject2.ui.user.DashboardUserActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, DashboardUserActivity.class);
//                intent.putExtra("productUid", uid);
//                context.startActivity(intent);
            }
        });
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
