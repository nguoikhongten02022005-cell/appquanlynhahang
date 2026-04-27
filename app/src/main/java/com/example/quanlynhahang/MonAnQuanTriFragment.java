package com.example.quanlynhahang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.MonAnQuanTriAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.helper.MoneyUtils;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MonAnQuanTriFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private MonAnQuanTriAdapter monAnQuanTriAdapter;
    private final List<DatabaseHelper.DishRecord> danhSachTatCaMon = new ArrayList<>();
    private TextView tvEmptyState;
    private TextView tvTongQuan;
    private EditText etTimKiem;
    private LinearLayout layoutDanhMucChips;
    private ActivityResultLauncher<String[]> chonAnhMonLauncher;
    private ImageView ivAnhDialogDangMo;
    private EditText etTenAnhDialogDangMo;
    private String danhMucDangChon = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chonAnhMonLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri == null || ivAnhDialogDangMo == null || etTenAnhDialogDangMo == null) {
                return;
            }
            requireContext().getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
            etTenAnhDialogDangMo.setText(uri.toString());
            ivAnhDialogDangMo.setImageURI(uri);
        });
    }

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
        tvTongQuan = view.findViewById(R.id.tvAdminDishSummary);
        etTimKiem = view.findViewById(R.id.etAdminDishSearch);
        layoutDanhMucChips = view.findViewById(R.id.layoutAdminDishCategoryChips);
        View btnThemMon = view.findViewById(R.id.btnAdminDishAdd);

        monAnQuanTriAdapter = new MonAnQuanTriAdapter(new MonAnQuanTriAdapter.HanhDongListener() {
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
        recyclerView.setAdapter(monAnQuanTriAdapter);
        btnThemMon.setOnClickListener(v -> hienDialogThemHoacSuaMon(null));
        etTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                apDungBoLocMon();
            }
        });

        taiDanhSachMon();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null && monAnQuanTriAdapter != null) {
            taiDanhSachMon();
        }
    }

    private void taiDanhSachMon() {
        danhSachTatCaMon.clear();
        danhSachTatCaMon.addAll(databaseHelper.layTatCaMonAn());
        if (!danhMucConTonTai()) {
            danhMucDangChon = "";
        }
        capNhatTongQuanMon();
        capNhatChipDanhMuc();
        apDungBoLocMon();
    }

    private boolean danhMucConTonTai() {
        if (TextUtils.isEmpty(danhMucDangChon)) {
            return true;
        }
        for (DatabaseHelper.DishRecord banGhiMon : danhSachTatCaMon) {
            if (danhMucDangChon.equalsIgnoreCase(banGhiMon.layMonAn().layTenDanhMuc())) {
                return true;
            }
        }
        return false;
    }

    private void capNhatTongQuanMon() {
        int soMonDangPhucVu = 0;
        for (DatabaseHelper.DishRecord banGhiMon : danhSachTatCaMon) {
            if (banGhiMon.layMonAn().laConPhucVu()) {
                soMonDangPhucVu++;
            }
        }
        tvTongQuan.setText(getString(R.string.admin_dish_summary_format, danhSachTatCaMon.size(), soMonDangPhucVu));
    }

    private void capNhatChipDanhMuc() {
        layoutDanhMucChips.removeAllViews();
        themChipDanhMuc(getString(R.string.admin_filter_all), "");
        Set<String> danhMuc = new LinkedHashSet<>();
        for (DatabaseHelper.DishRecord banGhiMon : danhSachTatCaMon) {
            if (!TextUtils.isEmpty(banGhiMon.layMonAn().layTenDanhMuc())) {
                danhMuc.add(banGhiMon.layMonAn().layTenDanhMuc());
            }
        }
        for (String item : danhMuc) {
            themChipDanhMuc(item, item);
        }
    }

    private void themChipDanhMuc(String nhan, String giaTri) {
        TextView chip = new TextView(requireContext());
        boolean duocChon = giaTri.equalsIgnoreCase(danhMucDangChon);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if (layoutDanhMucChips.getChildCount() > 0) {
            params.setMarginStart(dp(8));
        }
        chip.setLayoutParams(params);
        chip.setPadding(dp(14), dp(8), dp(14), dp(8));
        chip.setText(nhan);
        chip.setTextSize(12f);
        chip.setTypeface(chip.getTypeface(), android.graphics.Typeface.BOLD);
        chip.setBackgroundResource(duocChon ? R.drawable.bg_button_orange : R.drawable.bg_search_rounded);
        chip.setTextColor(ContextCompat.getColor(requireContext(), duocChon ? android.R.color.white : R.color.on_surface_variant));
        chip.setOnClickListener(v -> {
            danhMucDangChon = giaTri;
            capNhatChipDanhMuc();
            apDungBoLocMon();
        });
        layoutDanhMucChips.addView(chip);
    }

    private void apDungBoLocMon() {
        String tuKhoa = etTimKiem.getText() == null ? "" : etTimKiem.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<DatabaseHelper.DishRecord> ketQua = new ArrayList<>();
        for (DatabaseHelper.DishRecord banGhiMon : danhSachTatCaMon) {
            if (!khopDanhMuc(banGhiMon) || !khopTuKhoa(banGhiMon, tuKhoa)) {
                continue;
            }
            ketQua.add(banGhiMon);
        }
        monAnQuanTriAdapter.capNhatDanhSach(ketQua);
        tvEmptyState.setVisibility(ketQua.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean khopDanhMuc(DatabaseHelper.DishRecord banGhiMon) {
        return TextUtils.isEmpty(danhMucDangChon)
                || danhMucDangChon.equalsIgnoreCase(banGhiMon.layMonAn().layTenDanhMuc());
    }

    private boolean khopTuKhoa(DatabaseHelper.DishRecord banGhiMon, String tuKhoa) {
        if (TextUtils.isEmpty(tuKhoa)) {
            return true;
        }
        return banGhiMon.layMonAn().layTenMon().toLowerCase(Locale.ROOT).contains(tuKhoa)
                || banGhiMon.layMonAn().layTenDanhMuc().toLowerCase(Locale.ROOT).contains(tuKhoa)
                || banGhiMon.layMoTa().toLowerCase(Locale.ROOT).contains(tuKhoa);
    }

    private void hienDialogThemHoacSuaMon(@Nullable DatabaseHelper.DishRecord banGhiMon) {
        if (!isAdded()) {
            return;
        }

        View noiDungDialog = getLayoutInflater().inflate(R.layout.dialog_add_edit_dish, null);
        EditText etTenMon = noiDungDialog.findViewById(R.id.etAdminDishName);
        EditText etGiaMon = noiDungDialog.findViewById(R.id.etAdminDishPrice);
        MaterialAutoCompleteTextView etDanhMuc = noiDungDialog.findViewById(R.id.autoCompleteAdminDishCategory);
        EditText etMoTa = noiDungDialog.findViewById(R.id.etAdminDishDescription);
        EditText etTenAnh = noiDungDialog.findViewById(R.id.etAdminDishImage);
        CheckBox cbDangPhucVu = noiDungDialog.findViewById(R.id.cbAdminDishAvailable);
        ImageView ivAnhPreview = noiDungDialog.findViewById(R.id.ivAdminDishPreview);
        TextView btnChonAnh = noiDungDialog.findViewById(R.id.btnAdminDishPickImage);
        caiDatLuaChonDanhMuc(etDanhMuc);

        if (banGhiMon != null) {
            etTenMon.setText(banGhiMon.layMonAn().layTenMon());
            etGiaMon.setText(String.valueOf(MoneyUtils.tachGiaTienTuChuoi(banGhiMon.layMonAn().layGiaBan())));
            etDanhMuc.setText(banGhiMon.layMonAn().layTenDanhMuc());
            etMoTa.setText(banGhiMon.layMoTa());
            etTenAnh.setText(banGhiMon.layTenAnhTaiNguyen());
            cbDangPhucVu.setChecked(banGhiMon.layMonAn().laConPhucVu());
            hienAnhTrongDialog(ivAnhPreview, banGhiMon.layTenAnhTaiNguyen(), banGhiMon.layMonAn().layIdAnhTaiNguyen());
        } else {
            cbDangPhucVu.setChecked(true);
            ivAnhPreview.setImageDrawable(null);
            ivAnhPreview.setBackgroundResource(R.drawable.bg_dish_image_placeholder);
        }

        ivAnhDialogDangMo = ivAnhPreview;
        etTenAnhDialogDangMo = etTenAnh;
        btnChonAnh.setOnClickListener(v -> chonAnhMonLauncher.launch(new String[]{"image/*"}));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(banGhiMon == null ? R.string.admin_dialog_add_dish_title : R.string.admin_dialog_edit_dish_title)
                .setView(noiDungDialog)
                .setNegativeButton(R.string.account_cancel_action, null)
                .setPositiveButton(R.string.admin_save, null)
                .create();

        dialog.setOnDismissListener(ignored -> {
            ivAnhDialogDangMo = null;
            etTenAnhDialogDangMo = null;
        });
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String tenMon = layChuoiDaCatKhoangTrang(etTenMon);
            String giaNhap = layChuoiDaCatKhoangTrang(etGiaMon);
            String danhMuc = layChuoiDaCatKhoangTrang(etDanhMuc);
            String moTa = layChuoiDaCatKhoangTrang(etMoTa);
            String tenAnh = layChuoiDaCatKhoangTrang(etTenAnh);

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

            int diemDeXuat = layDiemDeXuatMacDinh(banGhiMon);

            long idMonBoQua = banGhiMon == null ? 0 : banGhiMon.layId();
            if (databaseHelper.tenMonAnDangTonTai(tenMon, idMonBoQua)) {
                Toast.makeText(requireContext(), R.string.admin_dish_validation_duplicate_name, Toast.LENGTH_SHORT).show();
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

    private int layDiemDeXuatMacDinh(@Nullable DatabaseHelper.DishRecord banGhiMon) {
        return banGhiMon == null ? 0 : Math.max(0, banGhiMon.layMonAn().layDiemDeXuat());
    }

    private void caiDatLuaChonDanhMuc(MaterialAutoCompleteTextView etDanhMuc) {
        List<String> danhMuc = new ArrayList<>();
        for (DatabaseHelper.DishRecord banGhiMon : danhSachTatCaMon) {
            String tenDanhMuc = banGhiMon.layMonAn().layTenDanhMuc();
            if (!TextUtils.isEmpty(tenDanhMuc) && !danhMuc.contains(tenDanhMuc)) {
                danhMuc.add(tenDanhMuc);
            }
        }
        etDanhMuc.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, danhMuc));
        etDanhMuc.setThreshold(0);
    }

    private void xacNhanXoaMon(DatabaseHelper.DishRecord banGhiMon) {
        if (!isAdded()) {
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_delete_confirm_title)
                .setMessage(getString(R.string.admin_delete_confirm_message_named, banGhiMon.layMonAn().layTenMon()))
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

    private void hienAnhTrongDialog(ImageView imageView, String tenAnh, int anhMacDinh) {
        if (!TextUtils.isEmpty(tenAnh) && tenAnh.startsWith("content://")) {
            imageView.setImageURI(Uri.parse(tenAnh));
            return;
        }
        if (anhMacDinh == 0) {
            imageView.setImageDrawable(null);
            imageView.setBackgroundResource(R.drawable.bg_dish_image_placeholder);
            return;
        }
        imageView.setImageResource(anhMacDinh);
    }

    private int dp(int value) {
        return Math.round(value * requireContext().getResources().getDisplayMetrics().density);
    }
}
