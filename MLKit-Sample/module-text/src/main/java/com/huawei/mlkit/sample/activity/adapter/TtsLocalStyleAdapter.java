/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.mlkit.sample.activity.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager;
import com.huawei.hms.mlsdk.tts.MLTtsLocalModel;
import com.huawei.mlkit.sample.R;

import java.util.List;
import java.util.Map;

public class TtsLocalStyleAdapter extends RecyclerView.Adapter<TtsLocalStyleAdapter.LanguageViewHolder> {
    private MLLocalModelManager localModelManager;
    private Context context;
    private List<String> list;
    private Map<String, String> speakerMap;

    public TtsLocalStyleAdapter(FragmentActivity styleFragment, List<String> styleList, Map<String, String> speakerMap) {
        this.context = styleFragment;
        this.list = styleList;
        this.speakerMap = speakerMap;
        this.localModelManager = MLLocalModelManager.getInstance();
    }


    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.tts_localstyle_item, parent, false);
        LanguageViewHolder languageViewHolder = new LanguageViewHolder(inflate);
        return languageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        holder.speaker.setText(speakerMap.get(list.get(position)));
        holder.speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.setOnStyleItemClick(list.get(position));
                }
            }
        });
        holder.delete_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.setOnDeleteClick(list.get(position));
                }
            }
        });
        holder.tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.setOnDownloadClick(list.get(position), holder.has_download);
                }
            }
        });
        MLTtsLocalModel model = new MLTtsLocalModel.Factory(list.get(position)).create();
        localModelManager.isModelExist(model).addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    holder.has_download.setVisibility(View.VISIBLE);
                    holder.has_download.setText(R.string.downloaded);
                    holder.tv_download.setEnabled(false);
                    holder.delete_model.setVisibility(View.VISIBLE);
                } else {
                    holder.has_download.setVisibility(View.GONE);
                    holder.tv_download.setEnabled(true);
                    holder.delete_model.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("adapter", e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public interface OnItemClickListener {
        /**
         *  ItemClick speaker
         *
         * @param speaker speaker
         */
        void setOnStyleItemClick(String speaker);

        /**
         * set Style ItemClick
         *
         * @param speaker speaker
         */
        void setOnDeleteClick(String speaker);

        /**
         * set Delete speaker
         *
         * @param speaker speaker
         * @param has_download has_download
         */
        void setOnDownloadClick(String speaker, TextView has_download);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {

        private final TextView speaker;
        private final TextView has_download;
        private final TextView tv_download;
        private final ImageView delete_model;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            speaker = itemView.findViewById(R.id.speaker);
            has_download = itemView.findViewById(R.id.has_download);
            delete_model = itemView.findViewById(R.id.delete_model);
            tv_download = itemView.findViewById(R.id.tv_download);
        }
    }
}
