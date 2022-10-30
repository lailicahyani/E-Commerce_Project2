package com.hactiv8.e_commerceproject2.filter;

import android.widget.Filter;

import com.hactiv8.e_commerceproject2.adapter.AdapterProduct;
import com.hactiv8.e_commerceproject2.model.ModelProduct;

import java.util.ArrayList;

public class FilterProduct extends Filter {

    private AdapterProduct adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProduct(AdapterProduct adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint !=null && constraint.length() > 0){
            //search
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                if (filterList.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){

                    //add filter data di list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productList = (ArrayList<ModelProduct>) results.values;
        //refrresh
        adapter.notifyDataSetChanged();
    }
}
