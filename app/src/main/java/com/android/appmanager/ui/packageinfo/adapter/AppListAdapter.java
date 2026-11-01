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
import com.android.appmanager.data.model.PackageModel;
import com.android.appmanager.ui.recyclerview.adapter.FilterableClickableListAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppListAdapter
        extends FilterableClickableListAdapter<PackageModel, AppListAdapter.ViewHolder> {
    private static final String TAG = "AppListAdapter";

    public static final DiffUtil.ItemCallback<PackageModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull PackageModel oldItem, @NonNull PackageModel newItem) {
            return Objects.equals(oldItem.getPackageName(), newItem.getPackageName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PackageModel oldItem, @NonNull PackageModel newItem) {
            boolean isLabelTheSame = Objects.equals(oldItem.getLabel(), newItem.getLabel());
            boolean isPackageNameTheSame = Objects.equals(oldItem.getPackageName(), newItem.getPackageName());
            return isLabelTheSame && isPackageNameTheSame;
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
                    PackageModel item = getItem(position);
                    onItemClickListener.onItemClick(item, itemView);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    PackageModel item = getItem(position);
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

    public AppListAdapter(List<PackageModel> packageModelList) {
        super(DIFF_CALLBACK, packageModelList);
    }

    @NonNull
    @Override
    public AppListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppListAdapter.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        PackageModel packageModel = getItem(position);

        Drawable defaultIcon = context.getPackageManager().getDefaultActivityIcon();
        try {
            Glide.with(context)
                    .load(packageModel)
                    .placeholder(defaultIcon)
                    .error(defaultIcon)
                    .into(holder.icon);
        } catch (Exception e) {
            holder.getIcon().setImageDrawable(defaultIcon);
        }
        holder.getTitle().setText(packageModel.getLabel());
        holder.getContent().setText(packageModel.getPackageName());
    }

    @NonNull
    @Override
    protected List<PackageModel> filterList(List<PackageModel> list, CharSequence constraint) {
        List<PackageModel> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase();
        for (PackageModel item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase().contains(cs);
            boolean isPackageNameMatch = item.getPackageName().toLowerCase().contains(cs);
            if (isLabelMatch || isPackageNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}