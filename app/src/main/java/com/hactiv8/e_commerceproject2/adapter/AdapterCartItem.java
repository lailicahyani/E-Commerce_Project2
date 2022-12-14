package com.hactiv8.e_commerceproject2.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hactiv8.e_commerceproject2.R;
import com.hactiv8.e_commerceproject2.model.ModelCartItem;
import com.hactiv8.e_commerceproject2.ui.user.DashboardUserActivity;


import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem  extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

    private Context context;
    public ArrayList<ModelCartItem> cartItems;


    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, int position) {
        ModelCartItem modelCartItem = cartItems.get(position);
        String id = modelCartItem.getId();
        String pid = modelCartItem.getPid();
        String title = modelCartItem.getName();
        final String cost = modelCartItem.getCost();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();

        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText(""+cost);
        holder.itemQuantityTv.setText(""+quantity);
        holder.itemPriceEachTv.setText(""+price);

        holder.itemRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("item_Id", new String[]{"text", "unique"}))
                        .addColumn(new Column("item_PID", new String[]{"text", "not null"}))
                        .addColumn(new Column("item_Name", new String[]{"text", "not null"}))
                        .addColumn(new Column("item_Price_Each", new String[]{"text", "not null"}))
                        .addColumn(new Column("item_Price", new String[]{"text", "not null"}))
                        .addColumn(new Column("item_Quantity", new String[]{"text", "not null"}))
                        .doneTableColumn();
                easyDB.deleteRow(1, id);
                Toast.makeText(context, "Hapus Cart..", Toast.LENGTH_SHORT).show();

                cartItems.remove(holder.getAdapterPosition());
                notifyItemChanged(holder.getAdapterPosition());
                notifyDataSetChanged();

                double tx = Double.parseDouble((((DashboardUserActivity) context).sTotalTv.getText().toString().trim().replace(",", ".")));
                double totalPrice = tx - Double.parseDouble(cost.replace(",", "."));
                ((DashboardUserActivity) context).allTotalPrice = 0.000;
                ((DashboardUserActivity) context).sTotalTv.setText("Rp " +String.format("%.3f", Double.parseDouble(String.format("%3f", totalPrice))));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{

        private ImageView productIconIv, itemRemove;
        private TextView itemTitleTv,itemPriceTv, itemPriceEachTv, itemQuantityTv;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            itemRemove = itemView.findViewById(R.id.itemRemove);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}
