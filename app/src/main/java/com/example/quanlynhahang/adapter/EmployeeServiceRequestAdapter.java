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
import com.example.quanlynhahang.model.ServiceRequest;

import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceRequestAdapter extends RecyclerView.Adapter<EmployeeServiceRequestAdapter.EmployeeServiceRequestViewHolder> {

    public interface ActionListener {
        void onMarkDone(ServiceRequest request);
    }

    private final List<ServiceRequest> requests = new ArrayList<>();
    private final ActionListener actionListener;

    public EmployeeServiceRequestAdapter(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void submitList(List<ServiceRequest> newRequests) {
        requests.clear();
        requests.addAll(newRequests);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeServiceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee_service_request, parent, false);
        return new EmployeeServiceRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeServiceRequestViewHolder holder, int position) {
        holder.bind(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class EmployeeServiceRequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContent;
        private final TextView tvTime;
        private final TextView tvStatus;
        private final TextView btnDone;

        EmployeeServiceRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvEmployeeServiceRequestContent);
            tvTime = itemView.findViewById(R.id.tvEmployeeServiceRequestTime);
            tvStatus = itemView.findViewById(R.id.tvEmployeeServiceRequestStatus);
            btnDone = itemView.findViewById(R.id.btnEmployeeServiceRequestDone);
        }

        void bind(ServiceRequest request) {
            Context context = itemView.getContext();
            tvContent.setText(request.getNoiDung());
            tvTime.setText(request.getThoiGianGui());
            boolean processing = request.getTrangThai() == ServiceRequest.Status.PROCESSING;
            tvStatus.setText(processing ? R.string.service_request_status_processing : R.string.service_request_status_done);
            ViewCompat.setBackgroundTintList(tvStatus, ColorStateList.valueOf(ContextCompat.getColor(context, processing ? R.color.warning : R.color.success)));
            btnDone.setVisibility(processing ? View.VISIBLE : View.GONE);
            btnDone.setOnClickListener(processing ? v -> actionListener.onMarkDone(request) : null);
        }
    }
}
