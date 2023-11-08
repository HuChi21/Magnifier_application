package com.example.baseproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baseproject.R;
import com.example.baseproject.model.TutorialModel;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialAdapterHorder> {
    private List<TutorialModel> tutorialModelList;

    public TutorialAdapter(Context context, List<TutorialModel> tutorialModelList){
        this.tutorialModelList = tutorialModelList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TutorialAdapterHorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutorial,parent,false);
        return new TutorialAdapterHorder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialAdapterHorder holder, int position) {
        TutorialModel tutorialModel = tutorialModelList.get(position);
        if(tutorialModelList==null){return;}
        holder.img_intro.setImageResource(tutorialModel.getImg());
        holder.tv_content.setText(tutorialModel.getContent());
    }

    @Override
    public int getItemCount() {
        if(tutorialModelList!=null){
            return tutorialModelList.size();
        }else{
            return 0;
        }
    }

    public class  TutorialAdapterHorder extends RecyclerView.ViewHolder{
        private ImageView img_intro;
        private TextView tv_content;
        public TutorialAdapterHorder(View itemView) {
            super(itemView);
            img_intro = itemView.findViewById(R.id.img_intro);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}


