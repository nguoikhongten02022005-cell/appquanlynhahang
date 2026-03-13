package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AdminDishAdapter extends RecyclerView.Adapter<AdminDishAdapter.AdminDishViewHolder> {

    public interface ActionListener {
        void onEdit(DatabaseHelper.DishRecord dishRecord);

        void onDelete(DatabaseHelper.DishRecord dishRecord);

        void onToggleAvailability(DatabaseHelper.DishRecord dishRecord);
    }

    private final List<DatabaseHelper.DishRecord> dishes = new ArrayList<>();
    private final ActionListener actionListener;

    public AdminDishAdapter(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void submitList(List<DatabaseHelper.DishRecord> newDishes) {
        dishes.clear();
        dishes.addAll(newDishes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_dish, parent, false);
        return new AdminDishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDishViewHolder holder, int position) {
        holder.bind(dishes.get(position));
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    class AdminDishViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvCategory;
        private final TextView tvDescription;
        private final TextView tvStatus;
        private final TextView btnEdit;
        private final TextView btnDelete;
        private final TextView btnToggle;

        AdminDishViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminDishName);
            tvPrice = itemView.findViewById(R.id.tvAdminDishPrice);
            tvCategory = itemView.findViewById(R.id.tvAdminDishCategory);
            tvDescription = itemView.findViewById(R.id.tvAdminDishDescription);
            tvStatus = itemView.findViewById(R.id.tvAdminDishStatus);
            btnEdit = itemView.findViewById(R.id.btnAdminDishEdit);
            btnDelete = itemView.findViewById(R.id.btnAdminDishDelete);
            btnToggle = itemView.findViewById(R.id.btnAdminDishToggle);
        }

        void bind(DatabaseHelper.DishRecord dishRecord) {
            Context context = itemView.getContext();
            tvName.setText(dishRecord.getDishItem().getTenMon());
            tvPrice.setText(dishRecord.getDishItem().getGiaBan());
            tvCategory.setText(context.getString(R.string.admin_dish_category_format, dishRecord.getDishItem().getTenDanhMuc()));
            tvDescription.setText(dishRecord.getDescription());
            boolean available = dishRecord.getDishItem().isConPhucVu();
            tvStatus.setText(available ? R.string.dish_status_available : R.string.dish_status_unavailable);
            ViewCompat.setBackgroundTintList(tvStatus, ColorStateList.valueOf(ContextCompat.getColor(context, available ? R.color.success : R.color.error)));
            btnToggle.setText(available ? R.string.admin_toggle_dish_unavailable : R.string.admin_toggle_dish_available);
            btnEdit.setOnClickListener(v -> actionListener.onEdit(dishRecord));
            btnDelete.setOnClickListener(v -> actionListener.onDelete(dishRecord));
            btnToggle.setOnClickListener(v -> actionListener.onToggleAvailability(dishRecord));
        }
    }
}
