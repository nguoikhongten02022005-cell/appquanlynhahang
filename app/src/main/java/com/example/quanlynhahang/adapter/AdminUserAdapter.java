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
import com.example.quanlynhahang.model.User;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder> {

    public interface ActionListener {
        void onChangeRole(User user);

        void onToggleActive(User user);
    }

    private final List<User> users = new ArrayList<>();
    private final ActionListener actionListener;

    public AdminUserAdapter(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void submitList(List<User> newUsers) {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class AdminUserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvEmail;
        private final TextView tvPhone;
        private final TextView tvRole;
        private final TextView tvStatus;
        private final TextView btnRole;
        private final TextView btnToggle;

        AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminUserName);
            tvEmail = itemView.findViewById(R.id.tvAdminUserEmail);
            tvPhone = itemView.findViewById(R.id.tvAdminUserPhone);
            tvRole = itemView.findViewById(R.id.tvAdminUserRole);
            tvStatus = itemView.findViewById(R.id.tvAdminUserStatus);
            btnRole = itemView.findViewById(R.id.btnAdminUserRole);
            btnToggle = itemView.findViewById(R.id.btnAdminUserToggleActive);
        }

        void bind(User user) {
            Context context = itemView.getContext();
            tvName.setText(user.getName());
            tvEmail.setText(user.getEmail());
            tvPhone.setText(context.getString(R.string.admin_user_phone_format, user.getPhone()));
            tvRole.setText(context.getString(R.string.admin_user_role_format, getRoleLabel(user)));
            tvStatus.setText(user.isActive() ? R.string.admin_user_status_active : R.string.admin_user_status_locked);
            ViewCompat.setBackgroundTintList(tvStatus, ColorStateList.valueOf(ContextCompat.getColor(context, user.isActive() ? R.color.success : R.color.error)));
            btnToggle.setText(user.isActive() ? R.string.admin_lock_user : R.string.admin_unlock_user);
            btnRole.setOnClickListener(v -> actionListener.onChangeRole(user));
            btnToggle.setOnClickListener(v -> actionListener.onToggleActive(user));
        }

        private int getRoleLabel(User user) {
            if (user.laAdmin()) {
                return R.string.admin_role_admin;
            }
            if (user.laNhanVien()) {
                return R.string.admin_role_employee;
            }
            return R.string.admin_role_customer;
        }
    }
}
