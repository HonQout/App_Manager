package com.android.appmanager.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.bean.item.TCItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TCVListAdapter extends ListAdapter<TCItem, TCVListAdapter.ViewHolder> {
    public static final DiffUtil.ItemCallback<TCItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull TCItem oldItem, @NonNull TCItem newItem) {
            boolean isTitleTheSame = Objects.equals(oldItem.getTitle(), newItem.getTitle());
            boolean isContentTheSame = Objects.equals(oldItem.getContent(), newItem.getContent());
            return isTitleTheSame && isContentTheSame;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TCItem oldItem, @NonNull TCItem newItem) {
            boolean isTitleTheSame = Objects.equals(oldItem.getTitle(), newItem.getTitle());
            boolean isContentTheSame = Objects.equals(oldItem.getContent(), newItem.getContent());
            return isTitleTheSame && isContentTheSame;
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

    public TCVListAdapter(List<TCItem> tcItemList) {
        super(DIFF_CALLBACK);
        setOverviewList(tcItemList);
    }

    public void setOverviewList(List<TCItem> tcItemList) {
        submitList(tcItemList == null ? new ArrayList<>() : new ArrayList<>(tcItemList));
    }

    @NonNull
    @Override
    public TCVListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tc_v, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TCVListAdapter.ViewHolder holder, int position) {
        TCItem tcItem = getItem(position);
        holder.getTitle().setText(tcItem.getTitle());
        holder.getContent().setText(tcItem.getContent());
    }
}