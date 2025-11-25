package com.android.appmanager.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.bean.ServiceBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ServiceRVAdapter extends FilterableListAdapter<ServiceBean, ServiceRVAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "ServiceRVAdapter";

    public static final DiffUtil.ItemCallback<ServiceBean> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ServiceBean oldItem, @NonNull ServiceBean newItem) {
            boolean isNameTheSame = Objects.equals(oldItem.getName(), newItem.getName());
            boolean isLabelTheSame = Objects.equals(oldItem.getLabel(), newItem.getLabel());
            return isNameTheSame && isLabelTheSame;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ServiceBean oldItem, @NonNull ServiceBean newItem) {
            boolean isServiceInfoTheSame = oldItem.getServiceInfo() == newItem.getServiceInfo();
            return isServiceInfoTheSame;
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
                    ServiceBean item = getItem(position);
                    onItemClickListener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    ServiceBean item = getItem(position);
                    return onItemClickListener.onItemLongClick(item);
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

    public ServiceRVAdapter(List<ServiceBean> serviceBeanList) {
        super(DIFF_CALLBACK, serviceBeanList);
    }

    @NonNull
    @Override
    public ServiceRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tc_v, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceRVAdapter.ViewHolder holder, int position) {
        ServiceBean serviceBean = getItem(position);
        holder.getTitle().setText(serviceBean.getLabel());
        holder.getContent().setText(serviceBean.getName());
    }

    @NonNull
    @Override
    protected List<ServiceBean> performFiltering(List<ServiceBean> list, CharSequence constraint) {
        List<ServiceBean> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase(Locale.ROOT);
        for (ServiceBean item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase(Locale.ROOT).contains(cs);
            boolean isNameMatch = item.getName().toLowerCase(Locale.ROOT).contains(cs);
            if (isLabelMatch || isNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}