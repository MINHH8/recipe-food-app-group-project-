package vn.edu.usth.recipefoodapp.adapter_ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import vn.edu.usth.recipefoodapp.object_ui.Category;
import vn.edu.usth.recipefoodapp.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    public interface OnCategoryClick { void onClick(Category category); }

    private List<Category> list;
    private OnCategoryClick listener;
    private boolean isDarkMode; // Flag to hold the current mode

    // The constructor accepts the dark mode state
    public CategoryAdapter(List<Category> list, OnCategoryClick listener, boolean isDarkMode) {
        this.list = list;
        this.listener = listener;
        this.isDarkMode = isDarkMode;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category c = list.get(position);
        holder.txt.setText(c.strCategory);
        Glide.with(holder.itemView.getContext()).load(c.strCategoryThumb).into(holder.img);
        holder.itemView.setOnClickListener(v -> listener.onClick(c));
        // This logic uses the flag we passed from the fragment
        if (isDarkMode) {
            holder.txt.setTextColor(Color.WHITE);
        } else {
            holder.txt.setTextColor(Color.parseColor("#222222")); // Dark Gray
        }
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txt;
        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgCategory);
            txt = itemView.findViewById(R.id.txtCategory);
        }
    }
}