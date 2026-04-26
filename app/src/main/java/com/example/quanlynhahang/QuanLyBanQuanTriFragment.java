package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.BoDieuHopBanAnQuanTri;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.BanAn;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class QuanLyBanQuanTriFragment extends androidx.fragment.app.Fragment {

    private DatabaseHelper databaseHelper;
    private BoDieuHopBanAnQuanTri boDieuHopBan;
    private final List<BanAn> danhSachTatCaBan = new ArrayList<>();
    private TextView tvEmptyState;
    private TextView tvQuanLyBanOccupancyRate;
    private ProgressBar progressQuanLyBanOccupancy;
    private EditText etQuanLyBanSearch;
    private MaterialAutoCompleteTextView autoCompleteQuanLyBanStatusFilter;
    private String trangThaiDangChon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quan_ly_ban_quan_tri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.chuanBiCoSoDuLieu();

        etQuanLyBanSearch = view.findViewById(R.id.etQuanLyBanSearch);
        autoCompleteQuanLyBanStatusFilter = view.findViewById(R.id.autoCompleteQuanLyBanStatusFilter);
        TextView btnThemBan = view.findViewById(R.id.btnThemBan);
        tvEmptyState = view.findViewById(R.id.tvQuanLyBanEmpty);
        tvQuanLyBanOccupancyRate = view.findViewById(R.id.tvQuanLyBanOccupancyRate);
        progressQuanLyBanOccupancy = view.findViewById(R.id.progressQuanLyBanOccupancy);
        RecyclerView rvDanhSachBan = view.findViewById(R.id.rvDanhSachBan);
        TextViewCompat.setCompoundDrawableTintList(
                etQuanLyBanSearch,
                android.content.res.ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.on_surface))
        );

        boDieuHopBan = new BoDieuHopBanAnQuanTri(new BoDieuHopBanAnQuanTri.HanhDongListener() {
            @Override
            public void khiXemChiTiet(BanAn banAn) {
                hienChiTietBan(banAn);
            }

            @Override
            public void khiSua(BanAn banAn) {
                hienDialogChinhSuaBan(banAn);
            }

            @Override
            public void khiXoa(BanAn banAn) {
                xacNhanXoaBan(banAn);
            }
        });

        rvDanhSachBan.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDanhSachBan.setNestedScrollingEnabled(false);
        rvDanhSachBan.setAdapter(boDieuHopBan);

        caiDatBoLocTrangThai();
        etQuanLyBanSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                apDungBoLoc();
            }
        });
        btnThemBan.setOnClickListener(v -> hienDialogChinhSuaBan(null));

        taiDanhSachBan();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            taiDanhSachBan();
        }
    }

    private void caiDatBoLocTrangThai() {
        List<String> luaChonTrangThai = new ArrayList<>();
        luaChonTrangThai.add(getString(R.string.quan_ly_ban_trang_thai_tat_ca));
        luaChonTrangThai.add(getString(R.string.quan_ly_ban_trang_thai_trong));
        luaChonTrangThai.add(getString(R.string.quan_ly_ban_trang_thai_dang_phuc_vu));
        luaChonTrangThai.add(getString(R.string.quan_ly_ban_trang_thai_da_dat));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                luaChonTrangThai
        );
        autoCompleteQuanLyBanStatusFilter.setAdapter(adapter);
        trangThaiDangChon = getString(R.string.quan_ly_ban_trang_thai_tat_ca);
        autoCompleteQuanLyBanStatusFilter.setText(trangThaiDangChon, false);
        autoCompleteQuanLyBanStatusFilter.setOnClickListener(v -> autoCompleteQuanLyBanStatusFilter.showDropDown());
        autoCompleteQuanLyBanStatusFilter.setOnItemClickListener((parent, view, position, id) -> {
            trangThaiDangChon = (String) parent.getItemAtPosition(position);
            apDungBoLoc();
        });
    }

    private void taiDanhSachBan() {
        danhSachTatCaBan.clear();
        danhSachTatCaBan.addAll(databaseHelper.layTatCaBanAn());
        apDungBoLoc();
    }

    private void apDungBoLoc() {
        String tuKhoa = etQuanLyBanSearch == null ? "" : etQuanLyBanSearch.getText().toString().trim().toLowerCase();
        List<BanAn> danhSachLoc = new ArrayList<>();
        for (BanAn banAn : danhSachTatCaBan) {
            if (!khopTuKhoa(banAn, tuKhoa) || !khopTrangThai(banAn)) {
                continue;
            }
            danhSachLoc.add(banAn);
        }
        boDieuHopBan.capNhatDanhSach(danhSachLoc);
        tvEmptyState.setVisibility(danhSachLoc.isEmpty() ? View.VISIBLE : View.GONE);
        capNhatTongQuanBan(danhSachLoc);
    }

    private void capNhatTongQuanBan(List<BanAn> danhSachLoc) {
        if (tvQuanLyBanOccupancyRate == null || progressQuanLyBanOccupancy == null) {
            return;
        }
        int tongSoBan = danhSachLoc == null ? 0 : danhSachLoc.size();
        int soBanDangDung = 0;
        if (danhSachLoc != null) {
            for (BanAn banAn : danhSachLoc) {
                if (banAn.layTrangThai() == BanAn.TrangThai.DANG_PHUC_VU) {
                    soBanDangDung++;
                }
            }
        }
        int tiLe = tongSoBan == 0 ? 0 : Math.round(soBanDangDung * 100f / tongSoBan);
        tvQuanLyBanOccupancyRate.setText(tiLe + "%");
        progressQuanLyBanOccupancy.setProgress(tiLe);
    }

    private boolean khopTuKhoa(BanAn banAn, String tuKhoa) {
        if (TextUtils.isEmpty(tuKhoa)) {
            return true;
        }
        return banAn.layMaBan().toLowerCase().contains(tuKhoa)
                || banAn.layTenBan().toLowerCase().contains(tuKhoa)
                || banAn.layKhuVuc().toLowerCase().contains(tuKhoa);
    }

    private boolean khopTrangThai(BanAn banAn) {
        if (TextUtils.isEmpty(trangThaiDangChon)
                || getString(R.string.quan_ly_ban_trang_thai_tat_ca).equals(trangThaiDangChon)) {
            return true;
        }
        if (getString(R.string.quan_ly_ban_trang_thai_trong).equals(trangThaiDangChon)) {
            return banAn.layTrangThai() == BanAn.TrangThai.TRONG;
        }
        if (getString(R.string.quan_ly_ban_trang_thai_dang_phuc_vu).equals(trangThaiDangChon)) {
            return banAn.layTrangThai() == BanAn.TrangThai.DANG_PHUC_VU;
        }
        return banAn.layTrangThai() == BanAn.TrangThai.DA_DAT;
    }

    private void hienChiTietBan(BanAn banAn) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.quan_ly_ban_chi_tiet_tieu_de)
                .setMessage(getString(
                        R.string.quan_ly_ban_chi_tiet_noi_dung,
                        banAn.layTenBan(),
                        banAn.layMaBan(),
                        banAn.laySoCho(),
                        banAn.layKhuVuc(),
                        layTenTrangThai(banAn.layTrangThai())
                ))
                .setPositiveButton(R.string.dialog_close, null)
                .show();
    }

    private void xacNhanXoaBan(BanAn banAn) {
        if (banAn.layTrangThai() != BanAn.TrangThai.TRONG) {
            Toast.makeText(requireContext(), R.string.quan_ly_ban_khong_the_xoa, Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.quan_ly_ban_xoa_ban)
                .setMessage(R.string.quan_ly_ban_xac_nhan_xoa)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.quan_ly_ban_xoa_ban, (dialog, which) -> {
                    boolean daXoa = databaseHelper.xoaBanAnNeuTrong(banAn.layId());
                    Toast.makeText(requireContext(), daXoa ? R.string.quan_ly_ban_xoa_thanh_cong : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
                    if (daXoa) {
                        taiDanhSachBan();
                    }
                })
                .show();
    }

    private void hienDialogChinhSuaBan(@Nullable BanAn banAn) {
        LinearLayout form = taoFormBanAn();
        EditText etMaBan = (EditText) form.getChildAt(0);
        EditText etTenBan = (EditText) form.getChildAt(1);
        EditText etSoCho = (EditText) form.getChildAt(2);
        EditText etKhuVuc = (EditText) form.getChildAt(3);

        if (banAn != null) {
            etMaBan.setText(banAn.layMaBan());
            etTenBan.setText(banAn.layTenBan());
            etSoCho.setText(String.valueOf(banAn.laySoCho()));
            etKhuVuc.setText(banAn.layKhuVuc());
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(banAn == null ? R.string.quan_ly_ban_them_ban_tieu_de : R.string.quan_ly_ban_sua_ban_tieu_de)
                .setView(form)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_save, null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String maBan = etMaBan.getText().toString().trim();
            String tenBan = etTenBan.getText().toString().trim();
            String soChoRaw = etSoCho.getText().toString().trim();
            String khuVuc = etKhuVuc.getText().toString().trim();
            int soCho = 0;
            try {
                soCho = Integer.parseInt(soChoRaw);
            } catch (NumberFormatException ignored) {
            }

            if (TextUtils.isEmpty(maBan) || TextUtils.isEmpty(tenBan) || TextUtils.isEmpty(khuVuc) || soCho <= 0) {
                Toast.makeText(requireContext(), R.string.quan_ly_ban_validation_required, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean daLuu;
            if (banAn == null) {
                daLuu = databaseHelper.themBanAn(maBan, tenBan, soCho, khuVuc, BanAn.TrangThai.TRONG) > 0;
                Toast.makeText(requireContext(), daLuu ? R.string.quan_ly_ban_them_thanh_cong : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
            } else {
                daLuu = databaseHelper.capNhatBanAn(banAn.layId(), maBan, tenBan, soCho, khuVuc, banAn.layTrangThai());
                Toast.makeText(requireContext(), daLuu ? R.string.quan_ly_ban_cap_nhat_thanh_cong : R.string.admin_action_failed, Toast.LENGTH_SHORT).show();
            }

            if (daLuu) {
                dialog.dismiss();
                taiDanhSachBan();
            }
        });
    }

    private LinearLayout taoFormBanAn() {
        LinearLayout form = new LinearLayout(requireContext());
        form.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * requireContext().getResources().getDisplayMetrics().density);
        form.setPadding(padding, padding / 2, padding, padding / 2);
        form.addView(taoEditText(R.string.quan_ly_ban_ma_ban_hint, InputType.TYPE_CLASS_TEXT));
        form.addView(taoEditText(R.string.quan_ly_ban_ten_ban_hint, InputType.TYPE_CLASS_TEXT));
        form.addView(taoEditText(R.string.quan_ly_ban_so_cho_hint, InputType.TYPE_CLASS_NUMBER));
        form.addView(taoEditText(R.string.quan_ly_ban_khu_vuc_hint, InputType.TYPE_CLASS_TEXT));
        return form;
    }

    private EditText taoEditText(int hintRes, int inputType) {
        EditText editText = new EditText(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = (int) (12 * requireContext().getResources().getDisplayMetrics().density);
        editText.setLayoutParams(params);
        editText.setHint(hintRes);
        editText.setInputType(inputType);
        editText.setMaxLines(1);
        editText.setPadding(24, 24, 24, 24);
        editText.setBackgroundResource(R.drawable.bg_search_rounded);
        return editText;
    }

    private String layTenTrangThai(BanAn.TrangThai trangThai) {
        if (trangThai == BanAn.TrangThai.DANG_PHUC_VU) {
            return getString(R.string.quan_ly_ban_trang_thai_dang_phuc_vu);
        }
        if (trangThai == BanAn.TrangThai.DA_DAT) {
            return getString(R.string.quan_ly_ban_trang_thai_da_dat);
        }
        return getString(R.string.quan_ly_ban_trang_thai_trong);
    }
}
