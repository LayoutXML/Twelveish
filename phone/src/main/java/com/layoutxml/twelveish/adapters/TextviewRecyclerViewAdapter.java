package com.layoutxml.twelveish.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.layoutxml.twelveish.R;

import java.util.List;

public class TextviewRecyclerViewAdapter extends RecyclerView.Adapter<TextviewRecyclerViewAdapter.ViewHolder> {

    private List<Pair<String, String>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String name=""; //adapter name

    public TextviewRecyclerViewAdapter(Context context, List<Pair<String, String>> data, String name) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.name = name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.textview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = mData.get(position).first;
        String content = mData.get(position).second;
        holder.myTextView.setText(title);
        holder.myTextView2.setText(content);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView, myTextView2;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.titleTextView);
            myTextView2 = itemView.findViewById(R.id.contentTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), name);
        }
    }

    String getItem(int id) {
        return mData.get(id).first;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, String name);
    }
}