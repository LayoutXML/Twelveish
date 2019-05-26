package com.layoutxml.twelveish.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.layoutxml.twelveish.R;

import java.util.List;

public class SwitchRecyclerViewAdapter extends RecyclerView.Adapter<SwitchRecyclerViewAdapter.ViewHolder> {

    private List<Pair<String, Boolean>> mData;
    private LayoutInflater mInflater;
    private ItemClickSwitchListener mClickListener;
    private String name=""; //adapter name

    public SwitchRecyclerViewAdapter(Context context, List<Pair<String, Boolean>> data, String name) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.name = name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.switch_and_textview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = mData.get(position).first;
        Boolean value = mData.get(position).second;
        holder.myTextView.setText(title);
        holder.mySwtich.setChecked(value);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        Switch mySwtich;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.titleSwitchTextView);
            mySwtich = itemView.findViewById(R.id.switchSwitch);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if (mClickListener.onItemClickSwitch(view, getAdapterPosition(), !mData.get(getAdapterPosition()).second, name)) {
                    //success
                    mData.set(getAdapterPosition(),new Pair<String, Boolean>(mData.get(getAdapterPosition()).first,!mData.get(getAdapterPosition()).second));
                    mySwtich.setChecked(mData.get(getAdapterPosition()).second);
                }
            }
        }
    }

    String getItem(int id) {
        return mData.get(id).first;
    }

    public void setClickListener(ItemClickSwitchListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickSwitchListener {
        boolean onItemClickSwitch(View view, int position, boolean newValue, String name);
    }
}
