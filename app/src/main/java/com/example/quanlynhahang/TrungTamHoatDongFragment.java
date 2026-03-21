package com.example.quanlynhahang;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.widget.TextView;

import com.example.quanlynhahang.data.CartManager;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DichVuKhachHangHelper;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.YeuCauPhucVu;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TrungTamHoatDongFragment extends Fragment {

    public static final int TAB_ORDERS = 0;
    public static final int TAB_RESERVATIONS = 1;
    public static final int TAB_SERVICE_REQUESTS = 2;

    private static final String ARG_TAB_BAN_DAU = "initial_tab";
    private static final String TAG_DON_HANG = "orders";
    private static final String TAG_DAT_BAN = "reservations";
    private static final String TAG_YEU_CAU = "service_requests";

    private MaterialButton btnTabDonHangs;
    private MaterialButton btnTabRequests;
    private MaterialButton btnTabServiceRequests;
    private TextView tvServiceHubSummaryTable;
    private TextView tvServiceHubSummaryOrder;
    private TextView tvServiceHubSummarySupport;
    private TextView tvServiceHubSummaryEmpty;
    private TextView tvServiceHubSummaryChevron;
    private View cardServiceHubSummary;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private int tabDangChon = TAB_ORDERS;

    public static TrungTamHoatDongFragment newInstance(int tabBanDau) {
        TrungTamHoatDongFragment fragment = new TrungTamHoatDongFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_BAN_DAU, tabBanDau);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trung_tam_hoat_dong, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnTabDonHangs = view.findViewById(R.id.btnTabDonHangs);
        btnTabRequests = view.findViewById(R.id.btnTabRequests);
        btnTabServiceRequests = view.findViewById(R.id.btnTabServiceRequests);
        tvServiceHubSummaryTable = view.findViewById(R.id.tvServiceHubSummaryTable);
        tvServiceHubSummaryOrder = view.findViewById(R.id.tvServiceHubSummaryOrder);
        tvServiceHubSummarySupport = view.findViewById(R.id.tvServiceHubSummarySupport);
        tvServiceHubSummaryEmpty = view.findViewById(R.id.tvServiceHubSummaryEmpty);
        tvServiceHubSummaryChevron = view.findViewById(R.id.tvServiceHubSummaryChevron);
        cardServiceHubSummary = view.findViewById(R.id.cardServiceHubSummary);
        sessionManager = new SessionManager(requireContext());
        databaseHelper = new DatabaseHelper(requireContext());

        if (savedInstanceState != null) {
            tabDangChon = savedInstanceState.getInt(ARG_TAB_BAN_DAU, TAB_ORDERS);
        } else if (getArguments() != null) {
            tabDangChon = getArguments().getInt(ARG_TAB_BAN_DAU, TAB_ORDERS);
        }

        btnTabDonHangs.setOnClickListener(v -> chonTab(TAB_ORDERS));
        btnTabRequests.setOnClickListener(v -> chonTab(TAB_RESERVATIONS));
        btnTabServiceRequests.setOnClickListener(v -> chonTab(TAB_SERVICE_REQUESTS));
        if (cardServiceHubSummary != null) {
            cardServiceHubSummary.setOnClickListener(v -> {
                if (tvServiceHubSummarySupport != null && tvServiceHubSummarySupport.getVisibility() == View.VISIBLE) {
                    chonTab(TAB_SERVICE_REQUESTS);
                }
            });
        }

        chonTab(tabDangChon);
        capNhatTomTatDichVu();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_TAB_BAN_DAU, tabDangChon);
    }

    public void chonTab(int tab) {
        if (tab == TAB_RESERVATIONS) {
            tabDangChon = TAB_RESERVATIONS;
        } else if (tab == TAB_SERVICE_REQUESTS) {
            tabDangChon = TAB_SERVICE_REQUESTS;
        } else {
            tabDangChon = TAB_ORDERS;
        }
        capNhatTrangThaiNutChuyen();
        capNhatTomTatDichVu();
        hienNoiDungDangChon();
    }

    private void capNhatTrangThaiNutChuyen() {
        if (btnTabDonHangs == null || btnTabRequests == null || btnTabServiceRequests == null) {
            return;
        }

        int nenDangChon = ContextCompat.getColor(requireContext(), R.color.surface);
        int nenKhongChon = ContextCompat.getColor(requireContext(), R.color.surface_variant);
        int chuDangChon = ContextCompat.getColor(requireContext(), R.color.on_surface);
        int chuKhongChon = ContextCompat.getColor(requireContext(), R.color.on_surface_variant);

        capNhatNutTab(btnTabDonHangs, tabDangChon == TAB_ORDERS, nenDangChon, nenKhongChon, chuDangChon, chuKhongChon);
        capNhatNutTab(btnTabRequests, tabDangChon == TAB_RESERVATIONS, nenDangChon, nenKhongChon, chuDangChon, chuKhongChon);
        capNhatNutTab(btnTabServiceRequests, tabDangChon == TAB_SERVICE_REQUESTS, nenDangChon, nenKhongChon, chuDangChon, chuKhongChon);
    }

    private void capNhatNutTab(MaterialButton button,
                               boolean dangChon,
                               int nenDangChon,
                               int nenKhongChon,
                               int chuDangChon,
                               int chuKhongChon) {
        button.setSelected(dangChon);
        button.setBackgroundTintList(ColorStateList.valueOf(dangChon ? nenDangChon : nenKhongChon));
        button.setTextColor(dangChon ? chuDangChon : chuKhongChon);
    }

    private void hienNoiDungDangChon() {
        if (!isAdded()) {
            return;
        }

        Fragment fragment;
        String tag;
        if (tabDangChon == TAB_SERVICE_REQUESTS) {
            fragment = timHoacTaoYeuCauFragment();
            tag = TAG_YEU_CAU;
        } else if (tabDangChon == TAB_RESERVATIONS) {
            fragment = timHoacTaoDatBanFragment();
            tag = TAG_DAT_BAN;
        } else {
            fragment = timHoacTaoDonHangsFragment();
            tag = TAG_DON_HANG;
        }

        Fragment fragmentHienTai = getChildFragmentManager().findFragmentById(R.id.activityHubContentContainer);
        if (fragmentHienTai != null && tag.equals(fragmentHienTai.getTag())) {
            return;
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.activityHubContentContainer, fragment, tag)
                .commit();
    }

    private Fragment timHoacTaoDonHangsFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TAG_DON_HANG);
        if (fragment instanceof DonHangFragment) {
            return fragment;
        }
        DonHangFragment orderFragment = new DonHangFragment();
        Bundle args = new Bundle();
        args.putBoolean(DonHangFragment.ARG_EMBEDDED, true);
        orderFragment.setArguments(args);
        return orderFragment;
    }

    private void capNhatTomTatDichVu() {
        if (!isAdded()) {
            return;
        }
        long idNguoiDung = sessionManager.layIdNguoiDungHienTai();
        List<DonHang> danhSachDon = idNguoiDung > 0 ? databaseHelper.layDonHangTheoNguoiDung(idNguoiDung) : java.util.Collections.emptyList();
        List<YeuCauPhucVu> danhSachYeuCau = idNguoiDung > 0 ? databaseHelper.layYeuCauTheoNguoiDung(idNguoiDung) : java.util.Collections.emptyList();

        DonHang donDangHoatDong = DichVuKhachHangHelper.timDonHangDangHoatDong(danhSachDon);
        DonHang donTaiQuanDangHoatDong = DichVuKhachHangHelper.timDonHangTaiQuanDangHoatDong(danhSachDon);
        YeuCauPhucVu yeuCauDangCho = DichVuKhachHangHelper.timYeuCauHoTroDangXuLy(danhSachYeuCau);
        String banHienTai = DichVuKhachHangHelper.timBanHienTai(
                sessionManager.layBanHienTai(),
                donTaiQuanDangHoatDong,
                () -> CartManager.getInstance().layNguCanhDonHang().laySoBan()
        );

        capNhatDongTomTat(tvServiceHubSummaryTable,
                banHienTai != null,
                banHienTai == null ? null : getString(R.string.activity_hub_summary_table, banHienTai));

        if (donDangHoatDong != null) {
            int textRes = donDangHoatDong.laAnTaiQuan()
                    ? R.string.activity_hub_summary_dine_in_order
                    : R.string.activity_hub_summary_takeaway_order;
            capNhatDongTomTat(tvServiceHubSummaryOrder, true, getString(textRes));
        } else {
            capNhatDongTomTat(tvServiceHubSummaryOrder, false, null);
        }

        capNhatDongTomTat(tvServiceHubSummarySupport,
                yeuCauDangCho != null,
                yeuCauDangCho == null ? null : getString(R.string.activity_hub_summary_support_waiting));

        boolean khongCoGiHoatDong = banHienTai == null && donDangHoatDong == null && yeuCauDangCho == null;
        capNhatDongTomTat(tvServiceHubSummaryEmpty,
                khongCoGiHoatDong,
                khongCoGiHoatDong ? getString(R.string.activity_hub_summary_empty) : null);
        if (tvServiceHubSummaryChevron != null) {
            tvServiceHubSummaryChevron.setVisibility(yeuCauDangCho != null ? View.VISIBLE : View.GONE);
        }
    }

    private void capNhatDongTomTat(@Nullable TextView textView, boolean hienThi, @Nullable String noiDung) {
        if (textView == null) {
            return;
        }
        textView.setVisibility(hienThi ? View.VISIBLE : View.GONE);
        if (hienThi && noiDung != null) {
            textView.setText(noiDung);
        }
    }

    private Fragment timHoacTaoDatBanFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TAG_DAT_BAN);
        if (fragment instanceof DatBanFragment) {
            return fragment;
        }
        DatBanFragment reservationsFragment = new DatBanFragment();
        Bundle args = new Bundle();
        args.putBoolean(DatBanFragment.ARG_EMBEDDED, true);
        reservationsFragment.setArguments(args);
        return reservationsFragment;
    }

    private Fragment timHoacTaoYeuCauFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TAG_YEU_CAU);
        if (fragment instanceof YeuCauFragment) {
            return fragment;
        }
        YeuCauFragment requestsFragment = new YeuCauFragment();
        Bundle args = new Bundle();
        args.putBoolean(YeuCauFragment.ARG_EMBEDDED, true);
        requestsFragment.setArguments(args);
        return requestsFragment;
    }
}
