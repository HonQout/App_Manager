package com.android.appmanager.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.bean.ProviderBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProviderRVAdapter extends FilterableListAdapter<ProviderBean, ProviderRVAdapter.ViewHolder> {
    private static final String TAG = "ProviderAdapter";

    public static final DiffUtil.ItemCallback<ProviderBean> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProviderBean oldItem, @NonNull ProviderBean newItem) {
            boolean isNameTheSame = Objects.equals(oldItem.getName(), newItem.getName());
            boolean isLabelTheSame = Objects.equals(oldItem.getLabel(), newItem.getLabel());
            return isNameTheSame && isLabelTheSame;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProviderBean oldItem, @NonNull ProviderBean newItem) {
            boolean isProviderInfoTheSame = oldItem.getProviderInfo() == newItem.getProviderInfo();
            return isProviderInfoTheSame;
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_tc_v_title);
            content = (TextView) itemView.findViewById(R.id.item_tc_v_content);
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getContent() {
            return content;
        }
    }

    public ProviderRVAdapter(List<ProviderBean> providerBeanList) {
        super(DIFF_CALLBACK, providerBeanList);
    }

    @NonNull
    @Override
    public ProviderRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tc_v, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderRVAdapter.ViewHolder holder, int position) {
        ProviderBean providerBean = getItem(position);
        holder.getTitle().setText(providerBean.getLabel());
        holder.getContent().setText(providerBean.getName());
    }

    @NonNull
    @Override
    protected List<ProviderBean> performFiltering(List<ProviderBean> list, CharSequence constraint) {
        List<ProviderBean> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase(Locale.ROOT);
        for (ProviderBean item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase(Locale.ROOT).contains(cs);
            boolean isNameMatch = item.getName().toLowerCase(Locale.ROOT).contains(cs);
            if (isLabelMatch || isNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}