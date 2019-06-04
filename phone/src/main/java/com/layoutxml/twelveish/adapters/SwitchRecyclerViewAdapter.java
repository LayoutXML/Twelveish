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
import com.layoutxml.twelveish.SettingsManager;

import java.util.List;

public class SwitchRecyclerViewAdapter extends RecyclerView.Adapter<SwitchRecyclerViewAdapter.ViewHolder> {

    private List<Pair<String, String>> mData;
    private LayoutInflater mInflater;
    private ItemClickSwitchListener mClickListener;
    private String name=""; //adapter name
    private SettingsManager settingsManager;

    public SwitchRecyclerViewAdapter(Context context, List<Pair<String, String>> data, String name, SettingsManager settingsManager) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.name = name;
        this.settingsManager = settingsManager;
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
        String key = mData.get(position).second;
        holder.myTextView.setText(title);
        holder.mySwtich.setChecked(settingsManager.booleanHashmap.get(key));
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
                String key = mData.get(getAdapterPosition()).second;
                if (key.equals("showDay") || key.equals("showDayAmbient") || key.equals("showSecondaryCalendarActive") || key.equals("showSecondaryCalendar") || key.equals("militarytextTime")) {
                    settingsManager.significantTimeChange = true;
                }
                boolean newValue = !settingsManager.booleanHashmap.get(key);
                settingsManager.booleanHashmap.put(key,newValue);
                mySwtich.setChecked(newValue);
                mClickListener.onItemClickSwitch(view, getAdapterPosition(), newValue, name);
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
        void onItemClickSwitch(View view, int position, boolean newValue, String name);
    }
}
