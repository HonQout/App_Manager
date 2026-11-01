package com.android.appmanager.ui.packageinfo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.appmanager.R;
import com.android.appmanager.data.model.ActivityModel;
import com.android.appmanager.ui.recyclerview.adapter.FilterableClickableListAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityListAdapter
        extends FilterableClickableListAdapter<ActivityModel, ActivityListAdapter.ViewHolder> {
    private static final String TAG = "ActivityListAdapter";

    public static final DiffUtil.ItemCallback<ActivityModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ActivityModel oldItem, @NonNull ActivityModel newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ActivityModel oldItem, @NonNull ActivityModel newItem) {
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
                    ActivityModel item = getItem(position);
                    onItemClickListener.onItemClick(item, itemView);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    ActivityModel item = getItem(position);
                    return onItemClickListener.onItemLongClick(item, itemView);
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

    public ActivityListAdapter(List<ActivityModel> activityModelList) {
        super(DIFF_CALLBACK, activityModelList);
    }

    @NonNull
    @Override
    public ActivityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityListAdapter.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext().getApplicationContext();
        ActivityModel activityModel = getItem(position);

        Drawable defaultIcon = context.getPackageManager().getDefaultActivityIcon();
        try {
            Glide.with(context)
                    .load(activityModel)
                    .placeholder(defaultIcon)
                    .error(defaultIcon)
                    .into(holder.icon);
        } catch (Exception e) {
            holder.getIcon().setImageDrawable(defaultIcon);
        }
        holder.getTitle().setText(activityModel.getLabel());
        holder.getContent().setText(activityModel.getActivityName());
    }

    @NonNull
    @Override
    protected List<ActivityModel> filterList(List<ActivityModel> list, CharSequence constraint) {
        List<ActivityModel> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase();
        for (ActivityModel item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase().contains(cs);
            boolean isActivityNameMatch = item.getActivityName().toLowerCase().contains(cs);
            if (isLabelMatch || isActivityNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}