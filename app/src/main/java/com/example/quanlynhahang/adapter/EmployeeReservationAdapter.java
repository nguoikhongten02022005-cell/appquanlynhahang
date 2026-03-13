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
import com.example.quanlynhahang.model.Reservation;

import java.util.ArrayList;
import java.util.List;

public class EmployeeReservationAdapter extends RecyclerView.Adapter<EmployeeReservationAdapter.EmployeeReservationViewHolder> {

    public interface ActionListener {
        void onConfirm(Reservation reservation);

        void onComplete(Reservation reservation);

        void onCancel(Reservation reservation);
    }

    private final List<Reservation> reservations = new ArrayList<>();
    private final ActionListener actionListener;

    public EmployeeReservationAdapter(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void submitList(List<Reservation> newReservations) {
        reservations.clear();
        reservations.addAll(newReservations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee_reservation, parent, false);
        return new EmployeeReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeReservationViewHolder holder, int position) {
        holder.bind(reservations.get(position));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    class EmployeeReservationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTime;
        private final TextView tvTable;
        private final TextView tvGuestCount;
        private final TextView tvNote;
        private final TextView tvStatus;
        private final TextView btnConfirm;
        private final TextView btnComplete;
        private final TextView btnCancel;

        EmployeeReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvEmployeeReservationTime);
            tvTable = itemView.findViewById(R.id.tvEmployeeReservationTable);
            tvGuestCount = itemView.findViewById(R.id.tvEmployeeReservationGuestCount);
            tvNote = itemView.findViewById(R.id.tvEmployeeReservationNote);
            tvStatus = itemView.findViewById(R.id.tvEmployeeReservationStatus);
            btnConfirm = itemView.findViewById(R.id.btnEmployeeReservationConfirm);
            btnComplete = itemView.findViewById(R.id.btnEmployeeReservationComplete);
            btnCancel = itemView.findViewById(R.id.btnEmployeeReservationCancel);
        }

        void bind(Reservation reservation) {
            Context context = itemView.getContext();
            tvTime.setText(reservation.getTime());
            tvTable.setText(context.getString(R.string.reservation_table_format_display, reservation.getTableNumber()));
            tvGuestCount.setText(context.getString(R.string.reservation_guest_count_format, reservation.getGuestCount()));
            String note = reservation.getNote();
            tvNote.setText(note == null || note.trim().isEmpty()
                    ? context.getString(R.string.reservation_note_empty)
                    : context.getString(R.string.reservation_note_format, note));
            tvStatus.setText(getStatusText(reservation.getStatus()));
            ViewCompat.setBackgroundTintList(tvStatus, ColorStateList.valueOf(ContextCompat.getColor(context, getStatusColor(reservation.getStatus()))));

            bindAction(btnConfirm, reservation.getStatus() == Reservation.Status.PENDING_APPROVAL, v -> actionListener.onConfirm(reservation));
            bindAction(btnComplete, reservation.getStatus() == Reservation.Status.CONFIRMED, v -> actionListener.onComplete(reservation));
            bindAction(btnCancel, reservation.getStatus() == Reservation.Status.PENDING_APPROVAL || reservation.getStatus() == Reservation.Status.CONFIRMED, v -> actionListener.onCancel(reservation));
        }

        private void bindAction(TextView view, boolean visible, View.OnClickListener onClickListener) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            view.setOnClickListener(visible ? onClickListener : null);
        }
    }

    private int getStatusText(Reservation.Status status) {
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

    private int getStatusColor(Reservation.Status status) {
        if (status == Reservation.Status.PENDING_APPROVAL) {
            return R.color.warning;
        }
        if (status == Reservation.Status.CONFIRMED) {
            return R.color.success;
        }
        if (status == Reservation.Status.COMPLETED) {
            return R.color.primary;
        }
        return R.color.error;
    }
}
