package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.BoDieuHopMonQuanTri;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.helper.MoneyUtils;

import java.util.List;

public class MonAnQuanTriFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private BoDieuHopMonQuanTri boDieuHopMon;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mon_an_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        RecyclerView recyclerView = view.findViewById(R.id.rvMonAnQuanTri);
        tvEmptyState = view.findViewById(R.id.tvMonAnQuanTriEmpty);
        View btnThemMon = view.findViewById(R.id.btnAdminDishAdd);

        boDieuHopMon = new BoDieuHopMonQuanTri(new BoDieuHopMonQuanTri.HanhDongListener() {
            @Override
            public void khiSua(DatabaseHelper.DishRecord banGhiMon) {
                hienDialogThemHoacSuaMon(banGhiMon);
            }

            @Override
            public void khiXoa(DatabaseHelper.DishRecord banGhiMon) {
                xacNhanXoaMon(banGhiMon);
            }

            @Override
            public void khiBatTatTrangThaiPhucVu(DatabaseHelper.DishRecord banGhiMon) {
                boolean trangThaiMoi = !banGhiMon.layMonAn().laConPhucVu();
                boolean daCapNhat = databaseHelper.capNhatTrangThaiPhucVuMon(banGhiMon.layId(), trangThaiMoi);
                Toast.makeText(requireContext(), daCapNhat ? R.string.admin_dish_availability_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                if (daCapNhat) {
                    taiDanhSachMon();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(boDieuHopMon);
        btnThemMon.setOnClickListener(v -> hienDialogThemHoacSuaMon(null));

        taiDanhSachMon();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null && boDieuHopMon != null) {
            taiDanhSachMon();
        }
    }

    private void taiDanhSachMon() {
        List<DatabaseHelper.DishRecord> danhSachMon = databaseHelper.layTatCaMonAn();
        boDieuHopMon.capNhatDanhSach(danhSachMon);
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(danhSachMon.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void hienDialogThemHoacSuaMon(@Nullable DatabaseHelper.DishRecord banGhiMon) {
        if (!isAdded()) {
            return;
        }

        View noiDungDialog = getLayoutInflater().inflate(R.layout.dialog_add_edit_dish, null);
        EditText etTenMon = noiDungDialog.findViewById(R.id.etAdminDishName);
        EditText etGiaMon = noiDungDialog.findViewById(R.id.etAdminDishPrice);
        EditText etDanhMuc = noiDungDialog.findViewById(R.id.etAdminDishCategory);
        EditText etMoTa = noiDungDialog.findViewById(R.id.etAdminDishDescription);
        EditText etTenAnh = noiDungDialog.findViewById(R.id.etAdminDishImage);
        EditText etDiemDeXuat = noiDungDialog.findViewById(R.id.etAdminDishScore);
        CheckBox cbDangPhucVu = noiDungDialog.findViewById(R.id.cbAdminDishAvailable);

        if (banGhiMon != null) {
            etTenMon.setText(banGhiMon.layMonAn().layTenMon());
            etGiaMon.setText(String.valueOf(MoneyUtils.tachGiaTienTuChuoi(banGhiMon.layMonAn().layGiaBan())));
            etDanhMuc.setText(banGhiMon.layMonAn().layTenDanhMuc());
            etMoTa.setText(banGhiMon.layMoTa());
            etTenAnh.setText(banGhiMon.layTenAnhTaiNguyen());
            etDiemDeXuat.setText(String.valueOf(banGhiMon.layMonAn().layDiemDeXuat()));
            cbDangPhucVu.setChecked(banGhiMon.layMonAn().laConPhucVu());
        } else {
            cbDangPhucVu.setChecked(true);
            etDiemDeXuat.setText("0");
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(banGhiMon == null ? R.string.admin_dialog_add_dish_title : R.string.admin_dialog_edit_dish_title)
                .setView(noiDungDialog)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_save, null)
                .create();

        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String tenMon = layChuoiDaCatKhoangTrang(etTenMon);
            String giaNhap = layChuoiDaCatKhoangTrang(etGiaMon);
            String danhMuc = layChuoiDaCatKhoangTrang(etDanhMuc);
            String moTa = layChuoiDaCatKhoangTrang(etMoTa);
            String tenAnh = layChuoiDaCatKhoangTrang(etTenAnh);
            String diemNhap = layChuoiDaCatKhoangTrang(etDiemDeXuat);

            if (TextUtils.isEmpty(tenMon)
                    || TextUtils.isEmpty(giaNhap)
                    || TextUtils.isEmpty(danhMuc)
                    || TextUtils.isEmpty(moTa)) {
                Toast.makeText(requireContext(), R.string.admin_dish_validation_required, Toast.LENGTH_SHORT).show();
                return;
            }

            long giaTien = MoneyUtils.tachGiaTienTuChuoi(giaNhap);
            if (giaTien <= 0) {
                Toast.makeText(requireContext(), R.string.admin_dish_validation_price, Toast.LENGTH_SHORT).show();
                return;
            }

            if (moTa.length() < 10) {
                Toast.makeText(requireContext(), R.string.admin_dish_validation_description, Toast.LENGTH_SHORT).show();
                return;
            }

            int diemDeXuat;
            try {
                diemDeXuat = TextUtils.isEmpty(diemNhap) ? 0 : Integer.parseInt(diemNhap);
            } catch (NumberFormatException ex) {
                Toast.makeText(requireContext(), R.string.admin_dish_validation_score, Toast.LENGTH_SHORT).show();
                return;
            }
            if (diemDeXuat < 0) {
                Toast.makeText(requireContext(), R.string.admin_dish_validation_score, Toast.LENGTH_SHORT).show();
                return;
            }

            String giaBan = MoneyUtils.dinhDangTienViet(giaTien);
            String tenAnhTaiNguyen = TextUtils.isEmpty(tenAnh) ? null : tenAnh;
            boolean dangPhucVu = cbDangPhucVu.isChecked();

            boolean daLuu;
            if (banGhiMon == null) {
                daLuu = databaseHelper.themBanGhiMonAn(
                        tenMon,
                        giaBan,
                        moTa,
                        tenAnhTaiNguyen,
                        dangPhucVu,
                        danhMuc,
                        diemDeXuat
                ) > 0;
            } else {
                daLuu = databaseHelper.capNhatBanGhiMonAn(
                        banGhiMon.layId(),
                        tenMon,
                        giaBan,
                        moTa,
                        tenAnhTaiNguyen,
                        dangPhucVu,
                        danhMuc,
                        diemDeXuat
                );
            }

            if (!daLuu) {
                Toast.makeText(requireContext(), R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(
                    requireContext(),
                    banGhiMon == null ? R.string.admin_dish_create_success : R.string.admin_dish_update_success,
                    Toast.LENGTH_SHORT
            ).show();
            dialog.dismiss();
            taiDanhSachMon();
        }));
        dialog.show();
    }

    private void xacNhanXoaMon(DatabaseHelper.DishRecord banGhiMon) {
        if (!isAdded()) {
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_delete_confirm_title)
                .setMessage(R.string.admin_delete_confirm_message)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_delete_dish, (dialog, which) -> {
                    boolean daXoa = databaseHelper.xoaMonAnTheoId(banGhiMon.layId());
                    Toast.makeText(requireContext(), daXoa ? R.string.admin_dish_delete_success : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                    if (daXoa) {
                        taiDanhSachMon();
                    }
                })
                .show();
    }

    private String layChuoiDaCatKhoangTrang(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
