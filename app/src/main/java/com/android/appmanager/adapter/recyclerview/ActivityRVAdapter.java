package com.android.appmanager.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.bean.ActivityBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ActivityRVAdapter extends FilterableListAdapter<ActivityBean, ActivityRVAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "ActivityRVAdapter";

    public static final DiffUtil.ItemCallback<ActivityBean> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ActivityBean oldItem, @NonNull ActivityBean newItem) {
            return Objects.equals(oldItem.getActivityInfo(), newItem.getActivityInfo());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ActivityBean oldItem, @NonNull ActivityBean newItem) {
            boolean isPackageNameTheSame = Objects.equals(oldItem.getPackageName(), newItem.getPackageName());
            boolean isActivityNameTheSame = Objects.equals(oldItem.getActivityName(), newItem.getActivityName());
            return isPackageNameTheSame && isActivityNameTheSame;
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView title;
        private final TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.item_itc_icon);
            title = (TextView) itemView.findViewById(R.id.item_itc_title);
            content = (TextView) itemView.findViewById(R.id.item_itc_content);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    ActivityBean item = getItem(position);
                    onItemClickListener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    ActivityBean item = getItem(position);
                    return onItemClickListener.onItemLongClick(item);
                }
                return false;
            });
        }

        public ImageView getIcon() {
            return icon;
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getContent() {
            return content;
        }
    }

    public ActivityRVAdapter(List<ActivityBean> activityBeanList) {
        super(DIFF_CALLBACK, activityBeanList);
    }

    @NonNull
    @Override
    public ActivityRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityRVAdapter.ViewHolder holder, int position) {
        ActivityBean activityBean = getItem(position);
        holder.getIcon().setImageDrawable(activityBean.getIcon());
        holder.getTitle().setText(activityBean.getLabel());
        holder.getContent().setText(activityBean.getActivityName());
    }

    @NonNull
    @Override
    protected List<ActivityBean> performFiltering(List<ActivityBean> list, CharSequence constraint) {
        List<ActivityBean> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase(Locale.ROOT);
        for (ActivityBean item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase(Locale.ROOT).contains(cs);
            boolean isActivityNameMatch = item.getActivityName().toLowerCase(Locale.ROOT).contains(cs);
            if (isLabelMatch || isActivityNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}