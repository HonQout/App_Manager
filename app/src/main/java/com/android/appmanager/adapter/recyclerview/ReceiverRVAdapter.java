package com.android.appmanager.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.bean.ReceiverBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ReceiverRVAdapter extends FilterableListAdapter<ReceiverBean, ReceiverRVAdapter.ViewHolder> {
    private static final String TAG = "ReceiverRVAdapter";

    public static final DiffUtil.ItemCallback<ReceiverBean> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReceiverBean oldItem, @NonNull ReceiverBean newItem) {
            return Objects.equals(oldItem.getReceiverInfo(), newItem.getReceiverInfo());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReceiverBean oldItem, @NonNull ReceiverBean newItem) {
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
                    ReceiverBean item = getItem(position);
                    onItemClickListener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    ReceiverBean item = getItem(position);
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

    public ReceiverRVAdapter(List<ReceiverBean> receiverBeanList) {
        super(DIFF_CALLBACK, receiverBeanList);
    }

    @NonNull
    @Override
    public ReceiverRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tc_v, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiverRVAdapter.ViewHolder holder, int position) {
        ReceiverBean receiverBean = getItem(position);
        holder.getTitle().setText(receiverBean.getLabel());
        holder.getContent().setText(receiverBean.getName());
    }

    @NonNull
    @Override
    protected List<ReceiverBean> performFiltering(List<ReceiverBean> list, CharSequence constraint) {
        List<ReceiverBean> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase(Locale.ROOT);
        for (ReceiverBean item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase(Locale.ROOT).contains(cs);
            boolean isNameMatch = item.getName().toLowerCase(Locale.ROOT).contains(cs);
            if (isLabelMatch || isNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}