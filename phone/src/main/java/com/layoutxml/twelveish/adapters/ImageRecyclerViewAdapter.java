package com.layoutxml.twelveish.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.layoutxml.twelveish.R;

import java.util.List;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {

    private List<Pair<String, Integer>> mData;
    private LayoutInflater mInflater;
    private ItemClickImageListener mClickListener;
    private String name=""; //adapter name

    public ImageRecyclerViewAdapter(Context context, List<Pair<String, Integer>> data, String name) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.name = name;
    }

    @NonNull
    @Override
    public ImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.imageview_and_textview_item, parent, false);
        return new ImageRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = mData.get(position).first;
        Integer value = mData.get(position).second;
        holder.myTextView.setText(title);
        holder.myImage.setColorFilter(value, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView myImage;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.titleImageTextView);
            myImage = itemView.findViewById(R.id.imageImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                Integer newColor = mClickListener.onItemClickImage(view, getAdapterPosition(), mData.get(getAdapterPosition()).second, name);
                mData.set(getAdapterPosition(),new Pair<String, Integer>(mData.get(getAdapterPosition()).first,newColor));
                myImage.setColorFilter(newColor, android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

    String getItem(int id) {
        return mData.get(id) .first;
    }

    public void setClickListener(ItemClickImageListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickImageListener {
        Integer onItemClickImage(View view, int position, Integer currentColor, String name);
    }
}
