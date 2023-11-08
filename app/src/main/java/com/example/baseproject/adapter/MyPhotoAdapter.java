package com.example.baseproject.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baseproject.R;
import com.example.baseproject.databinding.ItemMyphotoBinding;
import com.example.baseproject.interfaces.PhotoClickListener;
import com.example.baseproject.model.MyPhotoModel;
import com.example.baseproject.ui.MyPhotoActivity;
import com.example.baseproject.ui.PreviewPhotoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.MyViewHolder> {
    private Context context;
    private List<MyPhotoModel> list;
    public List<MyPhotoModel> selectList ;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 200;
    PhotoClickListener clickListener;
    public MyPhotoAdapter(Context context, List<MyPhotoModel> list,PhotoClickListener clickListener) {
        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
        this.selectList = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyPhotoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myphoto, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemMyphotoBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMyphotoBinding.bind(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyPhotoAdapter.MyViewHolder holder, int position) {
        MyPhotoModel item = list.get(position);
        Glide.with(context).load(item.getPhotoPath()).dontAnimate().into(holder.binding.imgPhoto);

        if (selectList.size() == 0) {
            holder.binding.imgSelect.setVisibility(View.GONE);

        } else {
            holder.binding.imgSelect.setVisibility(View.VISIBLE);
        }

        holder.binding.imgSelect.setImageResource(selectList.contains(item) ? R.drawable.icon_cbk_checked : R.drawable.icon_cbk_uncheck);

        holder.itemView.setOnClickListener(view -> {
            //prevent multi tap itemview
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;

            if (selectList.size()!=0) {
                if (selectList.contains(item)) {
                    selectList.remove(item);
                    if(selectList.size()==0) {
//                        selectMode =false;
                        clickListener.isSelect(false);
                        notifyDataSetChanged();
                    }
                } else {
                    selectList.add(item);
                    clickListener.isSelect(true);

                }
                notifyItemChanged(position);
            } else {
                clickListener.onImageClick( item.getPhotoPath());

            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            selectList.add(item);
            clickListener.isSelect(true);
            notifyDataSetChanged();
            return true;
        });

    }

    public void selectAll() {
        selectList.clear();
        selectList.addAll(list);
        notifyDataSetChanged();
    }

}