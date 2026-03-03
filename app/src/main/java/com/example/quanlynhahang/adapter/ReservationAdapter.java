package com.example.quanlynhahang.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.R;
import com.example.quanlynhahang.model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    public interface OnCancelReservationClickListener {
        void onCancelReservation(Reservation reservation, int position);
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private final OnCancelReservationClickListener onCancelReservationClickListener;

    public ReservationAdapter(List<Reservation> reservations,
                              OnCancelReservationClickListener onCancelReservationClickListener) {
        this.reservations.addAll(reservations);
        this.onCancelReservationClickListener = onCancelReservationClickListener;
    }

    public void setReservations(List<Reservation> newReservations) {
        reservations.clear();
        reservations.addAll(newReservations);
        notifyDataSetChanged();
    }

    public void addReservation(Reservation reservation) {
        reservations.add(0, reservation);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation_status, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        holder.bind(reservations.get(position));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    class ReservationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvReservationTime;
        private final TextView tvReservationGuestCount;
        private final TextView tvReservationNote;
        private final TextView tvReservationStatus;
        private final Button btnCancelReservation;

        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
            tvReservationGuestCount = itemView.findViewById(R.id.tvReservationGuestCount);
            tvReservationNote = itemView.findViewById(R.id.tvReservationNote);
            tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
            btnCancelReservation = itemView.findViewById(R.id.btnCancelReservation);
        }

        void bind(Reservation reservation) {
            Context context = itemView.getContext();
            tvReservationTime.setText(reservation.getTime());
            tvReservationGuestCount.setText(
                    context.getString(R.string.reservation_guest_count_format, reservation.getGuestCount())
            );

            String note = reservation.getNote();
            if (note == null || note.trim().isEmpty()) {
                tvReservationNote.setText(context.getString(R.string.reservation_note_empty));
            } else {
                tvReservationNote.setText(
                        context.getString(R.string.reservation_note_format, note)
                );
            }

            tvReservationStatus.setText(getStatusTextRes(reservation.getStatus()));
            int statusColor = ContextCompat.getColor(context, getStatusColorRes(reservation.getStatus()));
            ViewCompat.setBackgroundTintList(tvReservationStatus, ColorStateList.valueOf(statusColor));

            btnCancelReservation.setVisibility(reservation.canCancel() ? View.VISIBLE : View.GONE);
            btnCancelReservation.setOnClickListener(v -> {
                int adapterPosition = getBindingAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION || !reservation.canCancel()) {
                    return;
                }
                onCancelReservationClickListener.onCancelReservation(reservation, adapterPosition);
            });
        }
    }

    private int getStatusTextRes(Reservation.Status status) {
        if (status == Reservation.Status.PENDING_APPROVAL) {
            return R.string.reservation_status_pending;
        }
        if (status == Reservation.Status.CONFIRMED) {
            return R.string.reservation_status_confirmed;
        }
        if (status == Reservation.Status.COMPLETED) {
            return R.string.reservation_status_completed;
        }
        return R.string.reservation_status_canceled;
    }

    private int getStatusColorRes(Reservation.Status status) {
        if (status == Reservation.Status.PENDING_APPROVAL) {
            return R.color.brand_orange;
        }
        if (status == Reservation.Status.CONFIRMED) {
            return R.color.brand_green;
        }
        if (status == Reservation.Status.COMPLETED) {
            return R.color.hero_top;
        }
        return R.color.brand_red;
    }
}
