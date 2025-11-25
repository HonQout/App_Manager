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
import com.android.appmanager.bean.PackageBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AppRVAdapter extends FilterableListAdapter<PackageBean, AppRVAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "AppRVAdapter";

    public static final DiffUtil.ItemCallback<PackageBean> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull PackageBean oldItem, @NonNull PackageBean newItem) {
            return Objects.equals(oldItem.getPackageName(), newItem.getPackageName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PackageBean oldItem, @NonNull PackageBean newItem) {
            boolean isIconTheSame = oldItem.getIcon() == newItem.getIcon();
            boolean isLabelTheSame = Objects.equals(oldItem.getLabel(), newItem.getLabel());
            boolean isPackageNameTheSame = Objects.equals(oldItem.getPackageName(), newItem.getPackageName());
            return isIconTheSame && isLabelTheSame && isPackageNameTheSame;
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
                    PackageBean item = getItem(position);
                    onItemClickListener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    PackageBean item = getItem(position);
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

    public AppRVAdapter(List<PackageBean> packageBeanList) {
        super(DIFF_CALLBACK, packageBeanList);
    }

    @NonNull
    @Override
    public AppRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppRVAdapter.ViewHolder holder, int position) {
        PackageBean packageBean = getItem(position);
        holder.getIcon().setImageDrawable(packageBean.getIcon());
        holder.getTitle().setText(packageBean.getLabel());
        holder.getContent().setText(packageBean.getPackageInfo().packageName);
    }

    @NonNull
    @Override
    protected List<PackageBean> performFiltering(List<PackageBean> list, CharSequence constraint) {
        List<PackageBean> resultList = new ArrayList<>();
        CharSequence cs = constraint.toString().toLowerCase(Locale.ROOT);
        for (PackageBean item : list) {
            boolean isLabelMatch = item.getLabel().toLowerCase(Locale.ROOT).contains(cs);
            boolean isPackageNameMatch = item.getPackageInfo().packageName.toLowerCase(Locale.ROOT).contains(cs);
            if (isLabelMatch || isPackageNameMatch) {
                resultList.add(item);
            }
        }
        return resultList;
    }
}