package com.example.app7;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomPagerAdapter extends RecyclerView.Adapter<CustomPagerAdapter.CustomPagerViewHolder> {

    private List<String> titles;

    public CustomPagerAdapter(List<String> titles) {
        this.titles = titles;
    }

    @NonNull
    @Override
    public CustomPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_homes, parent, false);
        return new CustomPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomPagerViewHolder holder, int position) {
        holder.bind(titles.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class CustomPagerViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;

        public CustomPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.fg_name);
        }

        public void bind(String title) {
            titleTextView.setText(title);
        }
    }
}