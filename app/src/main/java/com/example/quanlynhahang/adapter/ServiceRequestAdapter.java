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

public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ServiceRequestViewHolder> {

    private final List<ServiceRequest> serviceRequests = new ArrayList<>();

    public ServiceRequestAdapter(List<ServiceRequest> serviceRequests) {
        this.serviceRequests.addAll(serviceRequests);
    }

    public void capNhatDanhSach(List<ServiceRequest> danhSachMoi) {
        serviceRequests.clear();
        serviceRequests.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    public void addServiceRequest(ServiceRequest serviceRequest) {
        serviceRequests.add(0, serviceRequest);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public ServiceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_request, parent, false);
        return new ServiceRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceRequestViewHolder holder, int position) {
        holder.bind(serviceRequests.get(position));
    }

    @Override
    public int getItemCount() {
        return serviceRequests.size();
    }

    static class ServiceRequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvServiceRequestContent;
        private final TextView tvServiceRequestTime;
        private final TextView tvServiceRequestStatus;

        ServiceRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceRequestContent = itemView.findViewById(R.id.tvServiceRequestContent);
            tvServiceRequestTime = itemView.findViewById(R.id.tvServiceRequestTime);
            tvServiceRequestStatus = itemView.findViewById(R.id.tvServiceRequestStatus);
        }

        void bind(ServiceRequest serviceRequest) {
            Context context = itemView.getContext();
            tvServiceRequestContent.setText(serviceRequest.getNoiDung());
            tvServiceRequestTime.setText(serviceRequest.getThoiGianGui());

            if (serviceRequest.getTrangThai() == ServiceRequest.Status.PROCESSING) {
                tvServiceRequestStatus.setText(R.string.service_request_status_processing);
                int processingColor = ContextCompat.getColor(context, R.color.brand_orange);
                ViewCompat.setBackgroundTintList(tvServiceRequestStatus, ColorStateList.valueOf(processingColor));
            } else {
                tvServiceRequestStatus.setText(R.string.service_request_status_done);
                int doneColor = ContextCompat.getColor(context, R.color.brand_green);
                ViewCompat.setBackgroundTintList(tvServiceRequestStatus, ColorStateList.valueOf(doneColor));
            }
        }
    }
}
