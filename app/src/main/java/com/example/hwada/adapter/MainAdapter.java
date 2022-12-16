package com.example.hwada.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.Ads;
import com.example.hwada.Model.User;
import com.example.hwada.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<Ads> list = new ArrayList();

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        holder.jop.setText(list.get(position).getJop());
        holder.fullName.setText(list.get(position).getFullName());
        holder.distance.setText(list.get(position).getDistance());
        holder.date.setText(list.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Ads> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView jop , fullName , date , distance;
        public MainViewHolder(@NonNull View v) {
            super(v);
            userImage = v.findViewById(R.id.item_user_image);
            jop = v.findViewById(R.id.item_jop_title);
            fullName = v.findViewById(R.id.item_username);
            date = v.findViewById(R.id.item_date);
            distance = v.findViewById(R.id.item_user_distance);
        }
    }
}
