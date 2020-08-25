package com.mlkit.sample.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlkit.sample.R;

public class CustModelAdapter extends RecyclerView.Adapter<CustModelAdapter.Hodler> {

    private String[][] data = null;

    public void setData(String[][] data) {
        this.data = data;
    }

    public CustModelAdapter() {
    }

    public CustModelAdapter(String[][] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public CustModelAdapter.Hodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Hodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cust_model_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Hodler holder, int position) {
        if (data != null) {
            holder.tv_type.setText(String.valueOf(data[position][0]));
            holder.tv_num.setText(String.valueOf(data[position][1]));
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    class Hodler extends RecyclerView.ViewHolder {
        TextView tv_type, tv_num;

        public Hodler(@NonNull View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_num = itemView.findViewById(R.id.tv_num);
        }
    }
}
