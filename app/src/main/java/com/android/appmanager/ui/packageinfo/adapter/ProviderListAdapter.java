package com.android.appmanager.ui.packageinfo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.data.model.ProviderModel;
import com.android.appmanager.ui.recyclerview.adapter.FilterableClickableListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProviderListAdapter
        extends FilterableClickableListAdapter<ProviderModel, ProviderListAdapter.ViewHolder> {
    private static final String TAG = "ProviderListAdapter";

    public static final DiffUtil.ItemCallback<ProviderModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProviderModel oldItem, @NonNull ProviderModel newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProviderModel oldItem, @NonNull ProviderModel newItem) {
            boolean isNameTheSame = Objects.equals(oldItem.getName(), newItem.getName());
            boolean isLabelTheSame = Objects.equals(oldItem.getLabel(), newItem.getLabel());
            return isNameTheSame && isLabelTheSame;
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_tc_v_title);
            content = (TextView) itemView.findViewById(R.id.item_tc_v_content);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    ProviderModel item = getItem(position);
                    onItemClickListener.onItemClick(item, itemView);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    ProviderModel item = getItem(position);
                    return onItemClickListener.onItemLongClick(item, itemView);
                }
                return false;
            });
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getContent() {
            return content;
        }
    }

    public ProviderListAdapter(List<ProviderModel> providerModelList) {
        super(DIFF_CALLBACK, providerModelList);
    }

    @NonNull
    @Override
    public ProviderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tc_v, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderListAdapter.ViewHolder holder, int position) {
        ProviderModel providerModel = getItem(position);
        holder.getTitle().setText(providerModel.getLabel());
        holder.getContent().setText(providerModel.getName());
    }

    @NonNull
    @Override
    protected List<ProviderModel> filterList(List<ProviderModel> list, CharSequence constraint) {
        List<ProviderModel> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase();
        for (ProviderModel item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase().contains(cs);
            boolean isNameMatch = item.getName().toLowerCase().contains(cs);
            if (isLabelMatch || isNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}