package com.hactiv8.e_commerceproject2.adapter;

import android.content.Context;
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

import com.hactiv8.e_commerceproject2.FilterProduct;
import com.hactiv8.e_commerceproject2.R;
import com.hactiv8.e_commerceproject2.model.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.HolderProduct> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    public AdapterProduct(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
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
        holder.discountPrice.setText("Rp" +discountPrice);
        holder.originalPrice.setText("Rp" +originalPrice);

        if (discountAvailable.equals("true")){
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.discountNote.setVisibility(View.VISIBLE);
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.discountPrice.setVisibility(View.GONE);
            holder.discountNote.setVisibility(View.GONE);
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
