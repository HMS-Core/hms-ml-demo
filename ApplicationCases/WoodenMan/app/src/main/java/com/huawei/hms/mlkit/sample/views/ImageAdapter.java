/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hms.mlkit.sample.views;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.mlkit.sample.cn.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<ImageInfo> mImageInfoList;

    private OnItemClickListener mOnItemClickListener;

    private int thisPosition;

    private Context mContext;

    public ImageAdapter(Context context, List<ImageInfo> imageInfos) {
        mContext = context;
        mImageInfoList = imageInfos;
    }

    public int getThisPosition() {
        return thisPosition;
    }

    public void setThisPosition(int thisPosition) {
        this.thisPosition = thisPosition;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,parent,false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        setListener(imageViewHolder);
        return imageViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageInfo imageInfo = mImageInfoList.get(position);
        holder.imageView.setImageDrawable(imageInfo.getImageView());
        holder.textView.setText(imageInfo.getName());
        if (position == getThisPosition()) {
            ((ImageViewHolder) holder).imageView_empty.setVisibility(View.VISIBLE);
        } else {
            ((ImageViewHolder) holder).imageView_empty.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mImageInfoList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;
        private ImageView imageView_empty;

        private View mConvertView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mConvertView = itemView;
            imageView = itemView.findViewById(R.id.item_image);
            textView = itemView.findViewById(R.id.tv_item_name);
            imageView_empty = itemView.findViewById(R.id.iv_empty_pic);
        }

        View getCovertView() {
            return mConvertView;
        }
    }

    private void setListener(final ImageViewHolder imageViewHolder) {
        imageViewHolder.getCovertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    int position = imageViewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(view, imageViewHolder, position);
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
