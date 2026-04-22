package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;

public class TrungTamNoiBoActivity extends AppCompatActivity {

    private static final String TAG_TONG_QUAN = "tong_quan_noi_bo";
    private static final String TAG_DON_HANG = "don_hang_noi_bo";
    private static final String TAG_DAT_BAN = "dat_ban_noi_bo";
    private static final String TAG_YEU_CAU = "yeu_cau_noi_bo";

    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    public static Intent taoIntent(Context context, String tab) {
        Intent intent = new Intent(context, TrungTamNoiBoActivity.class);
        intent.putExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO, DieuHuongNoiBoHelper.chuanHoaTab(tab));
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trung_tam_noi_bo);
        setTitle(R.string.internal_shell_title);

        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.chuanBiCoSoDuLieu();

        if (!sessionManager.daDangNhapNoiBo() || !sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            duLoiDangNhap();
            return;
        }

        String tabNoiBoDuocYeuCau = DieuHuongNoiBoHelper.chuanHoaTab(
                getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO)
        );
        getIntent().putExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO, tabNoiBoDuocYeuCau);
        if (savedInstanceState == null) {
            hienThiNoiBo(tabNoiBoDuocYeuCau);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager == null || databaseHelper == null) {
            return;
        }
        if (!sessionManager.daDangNhapNoiBo() || !sessionManager.damBaoNguoiDungConHoatDong(databaseHelper)) {
            duLoiDangNhap();
        }
    }

    private void hienThiNoiBo(String tabNoiBo) {
        String tabHopLe = DieuHuongNoiBoHelper.chuanHoaTab(tabNoiBo);
        switch (tabHopLe) {
            case DieuHuongNoiBoHelper.TAB_DON_HANG:
                moDonHangNoiBo();
                break;
            case DieuHuongNoiBoHelper.TAB_DAT_BAN:
                moDatBanNoiBo();
                break;
            case DieuHuongNoiBoHelper.TAB_YEU_CAU:
                moYeuCauNoiBo();
                break;
            case DieuHuongNoiBoHelper.TAB_TONG_QUAN:
            default:
                moTongQuanNoiBo();
                break;
        }
    }

    private void moTongQuanNoiBo() {
        sessionManager.luuDuongDanNoiBoCuoi(
                DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_TONG_QUAN)
        );
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.noiBoFragmentContainer, new TongQuanNoiBoFragment(), TAG_TONG_QUAN)
                .commitNow();
    }

    private void moDonHangNoiBo() {
        sessionManager.luuDuongDanNoiBoCuoi(
                DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_DON_HANG)
        );
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.noiBoFragmentContainer, new DonHangNoiBoFragment(), TAG_DON_HANG)
                .commitNow();
    }

    private void moDatBanNoiBo() {
        sessionManager.luuDuongDanNoiBoCuoi(
                DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_DAT_BAN)
        );
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.noiBoFragmentContainer, new DatBanNoiBoFragment(), TAG_DAT_BAN)
                .commitNow();
    }

    private void moYeuCauNoiBo() {
        sessionManager.luuDuongDanNoiBoCuoi(
                DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_YEU_CAU)
        );
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.noiBoFragmentContainer, new YeuCauNoiBoFragment(), TAG_YEU_CAU)
                .commitNow();
    }

    private void duLoiDangNhap() {
        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.putExtra(DangNhapActivity.EXTRA_RETURN_TO_CALLER, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
