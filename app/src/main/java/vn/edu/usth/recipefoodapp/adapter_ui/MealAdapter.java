package vn.edu.usth.recipefoodapp.adapter_ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.usth.recipefoodapp.activity_ui.MealDetailActivity;
import vn.edu.usth.recipefoodapp.object_ui.MealSimple;
import vn.edu.usth.recipefoodapp.R;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.VH> {
    private List<MealSimple> list;
    private Context ctx;

    public MealAdapter(Context ctx, List<MealSimple> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MealSimple m = list.get(position);
        holder.txt.setText(m.getStrMeal());
        Glide.with(ctx).load(m.getStrMealThumb()).into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, MealDetailActivity.class);
            i.putExtra("meal_id", m.getIdMeal());
            ctx.startActivity(i);
        });
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txt;
        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgMeal);
            txt = itemView.findViewById(R.id.txtMealName);
        }
    }
}
