package com.huawei.mlkit.sample.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.mlsdk.fr.MLFormRecognitionTablesAttribute;
import com.huawei.mlkit.sample.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    public List<MLFormRecognitionTablesAttribute.TablesContent.TableAttribute.TableCellAttribute> list;
    public ItemAdapter() {
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);

        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.tv.setText(list.get(position).getStartCol()+":"+list.get(position).getStartRow()+":"+list.get(position).getEndCol()+":"+list.get(position).getEndRow()+" "+list.get(position).getTextInfo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tv =itemView.findViewById(R.id.tv);
        }
    }
}
