package com.best.grocery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.best.grocery.R;
import com.best.grocery.entity.FileObject;

import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener listener;
    private Context mContext;
    private ArrayList<FileObject> mData;

    public FileAdapter(Context context, ArrayList<FileObject> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_backup, parent, false);
        return new FileObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        FileObjectHolder holder = (FileObjectHolder) viewHolder;
        FileObject object = mData.get(i);
        Log.d("adapter","position: " + i + ", name: " + object.getCreated());
        holder.textViewName.setText(object.getCreated());
        String path = object.getPath();
        holder.textViewPath.setText(path.substring(path.indexOf(mContext.getString(R.string.app_name)) -1 ));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class FileObjectHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewPath;
        public ConstraintLayout layoutItem;

        public FileObjectHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.item_file_backup_name);
            textViewPath = itemView.findViewById(R.id.item_file_backup_path);
            layoutItem = itemView.findViewById(R.id.item_backup_layout);
            layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(mData.get(position),position);
                    }

                }
            });

        }

    }

    public interface OnItemClickListener {
        void onItemClick(FileObject fileObject, int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
