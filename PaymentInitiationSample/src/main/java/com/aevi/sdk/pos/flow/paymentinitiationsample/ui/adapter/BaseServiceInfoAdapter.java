/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.aevi.sdk.pos.flow.paymentinitiationsample.ui.adapter;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aevi.sdk.pos.flow.paymentinitiationsample.R;

import java.util.Arrays;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseServiceInfoAdapter<T> extends RecyclerView.Adapter<BaseServiceInfoAdapter.ViewHolder> {

    protected Context context;
    protected T info;
    protected String yes, no;
    protected String[] labels;
    protected int[] resIds;

    public BaseServiceInfoAdapter(Context context, int labelsTypedArrayResource, T info) {
        this.context = context;
        this.info = info;
        TypedArray typedArray = context.getResources().obtainTypedArray(labelsTypedArrayResource);
        this.labels = new String[typedArray.length()];
        this.resIds = new int[typedArray.length()];
        for (int i = 0; i < labels.length; i++) {
            this.labels[i] = typedArray.getString(i);
            this.resIds[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        this.yes = context.getString(R.string.yes);
        this.no = context.getString(R.string.no);
    }

    @Override
    public BaseServiceInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_two_line_item_medium_title, parent, false);
        return new BaseServiceInfoAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return labels.length;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    protected String getArrayValue(String[] array) {
        if (array.length == 0) {
            return context.getString(R.string.na);
        } else if (array.length == 1) {
            return array[0];
        } else {
            return Arrays.toString(array);
        }
    }

    protected String getSetValue(Set<String> collection) {
        if (collection.isEmpty()) {
            return context.getString(R.string.na);
        } else if (collection.size() == 1) {
            return collection.iterator().next();
        } else {
            return Arrays.toString(collection.toArray(new String[collection.size()]));
        }
    }

    protected String getYesNo(boolean b) {
        if (b) {
            return yes;
        }
        return no;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_title)
        TextView label;

        @BindView(R.id.item_subtitle)
        TextView value;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
