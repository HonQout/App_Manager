package com.android.appmanager.ui.packageinfo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.data.model.ReceiverModel;
import com.android.appmanager.ui.recyclerview.adapter.FilterableClickableListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ReceiverListAdapter extends FilterableClickableListAdapter<ReceiverModel, ReceiverListAdapter.ViewHolder> {
    private static final String TAG = "ReceiverListAdapter";

    public static final DiffUtil.ItemCallback<ReceiverModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReceiverModel oldItem, @NonNull ReceiverModel newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReceiverModel oldItem, @NonNull ReceiverModel newItem) {
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
                    ReceiverModel item = getItem(position);
                    onItemClickListener.onItemClick(item, itemView);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    ReceiverModel item = getItem(position);
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

    public ReceiverListAdapter(List<ReceiverModel> receiverModelList) {
        super(DIFF_CALLBACK, receiverModelList);
    }

    @NonNull
    @Override
    public ReceiverListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tc_v, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiverListAdapter.ViewHolder holder, int position) {
        ReceiverModel receiverModel = getItem(position);
        holder.getTitle().setText(receiverModel.getLabel());
        holder.getContent().setText(receiverModel.getName());
    }

    @NonNull
    @Override
    protected List<ReceiverModel> filterList(List<ReceiverModel> list, CharSequence constraint) {
        List<ReceiverModel> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase(Locale.ROOT);
        for (ReceiverModel item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase(Locale.ROOT).contains(cs);
            boolean isNameMatch = item.getName().toLowerCase(Locale.ROOT).contains(cs);
            if (isLabelMatch || isNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}