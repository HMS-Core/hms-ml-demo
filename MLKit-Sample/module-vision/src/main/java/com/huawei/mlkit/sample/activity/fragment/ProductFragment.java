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

package com.huawei.mlkit.sample.activity.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.huawei.hms.mlplugin.productvisionsearch.MLProductVisionSearchCapture;
import com.huawei.hms.mlsdk.productvisionsearch.MLProductVisionSearch;
import com.huawei.hms.mlsdk.productvisionsearch.MLVisionSearchProduct;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.adapter.BottomSheetAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Fragments of the result display of the product vision search, which is inherited from
 * MLProductVisionSearchCapture.AbstractUIExtendProxy and is used to display the information
 * detected by the product vision search plug-in interface.
 *
 * @since 2020-05-29
 */
public class ProductFragment extends MLProductVisionSearchCapture.AbstractProductFragment<MLProductVisionSearch> {
    private static final String TAG = ProductFragment.class.getSimpleName();

    private static final int PRODUCT_NUM = 0;

    private View root;

    private List<MLVisionSearchProduct> mlProducts = new ArrayList<>();

    private List<MLProductVisionSearch> productData;

    private GridView gridView;

    private BottomSheetAdapter adapter;

    private TextView prompt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = View.inflate(getContext(), R.layout.fragment_product, null);
        initView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        product(productData);
    }

    private void initView() {
        gridView = root.findViewById(R.id.gv);
        prompt = root.findViewById(R.id.prompt);
        gridView.setNumColumns(2);
        adapter = new BottomSheetAdapter(mlProducts, getContext());
        root.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        gridView.setAdapter(adapter);
    }

    @Override
    public List<MLProductVisionSearch> getProductList(List<MLProductVisionSearch> list) throws Exception {
        return list;
    }

    @Override
    public void onResult(List<MLProductVisionSearch> productList) {
        productData = productList;
        product(productList);

    }

    private void product(List<MLProductVisionSearch> productList) {
        if (null == productList) {
            return;
        }
        mlProducts.clear();

        if (productList.size() == PRODUCT_NUM) {
            prompt.setText(getString(R.string.empty_product));
            return;
        }
        for (MLProductVisionSearch search : productList) {
            mlProducts.addAll(search.getProductList());
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onError(Exception e) {
        Log.e(TAG, e.getMessage());
        return false;
    }
}
