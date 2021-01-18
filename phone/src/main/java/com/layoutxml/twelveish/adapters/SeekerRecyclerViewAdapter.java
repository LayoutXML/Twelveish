package com.layoutxml.twelveish.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.Triple;

import java.util.List;

public class SeekerRecyclerViewAdapter extends RecyclerView.Adapter<SeekerRecyclerViewAdapter.ViewHolder> {

    private List<Triple<String, Integer, Integer>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private SeekBar.OnSeekBarChangeListener mSeekBarListener;
    private String name=""; //adapter name

    public SeekerRecyclerViewAdapter(Context context, List<Triple<String, Integer, Integer>> data, String name) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.name = name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.slider_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = mData.get(position).first;
        int value = mData.get(position).second;
        int maxValue = mData.get(position).third;
        holder.myTextView.setText(title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.mySeekBar.setMin(0);
        }
        holder.mySeekBar.setMax(maxValue);
        holder.mySeekBar.setProgress(value);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements SeekBar.OnSeekBarChangeListener {
        TextView myTextView;
        SeekBar mySeekBar;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.titleTextView);
            mySeekBar = itemView.findViewById(R.id.seekBar);
            mySeekBar.setOnSeekBarChangeListener(this);
        }


        @Override
        public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
            if(mSeekBarListener != null)
                mSeekBarListener.onProgressChanged(seekBar, value, fromUser);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if(mSeekBarListener != null)
                mSeekBarListener.onStartTrackingTouch(seekBar);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(mSeekBarListener != null)
                mSeekBarListener.onStopTrackingTouch(seekBar);
        }
    }

    String getItem(int id) {
        return mData.get(id).first;
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener){
        this.mSeekBarListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, String name);
    }
}