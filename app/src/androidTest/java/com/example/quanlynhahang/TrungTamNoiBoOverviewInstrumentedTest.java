package com.example.quanlynhahang;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.data.SessionManager;
import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;
import com.example.quanlynhahang.model.DatBan;
import com.example.quanlynhahang.model.DonHang;
import com.example.quanlynhahang.model.MonAnDeXuat;
import com.example.quanlynhahang.model.NguoiDung;
import com.example.quanlynhahang.model.ThongKeTongQuanNhanVien;
import com.example.quanlynhahang.model.VaiTroNguoiDung;
import com.example.quanlynhahang.model.YeuCauPhucVu;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TrungTamNoiBoOverviewInstrumentedTest {

    private static final String EMAIL_NOI_BO = "noi.bo.overview@example.com";
    private static final String SO_DIEN_THOAI_NOI_BO = "0900000008";
    private static final String THOI_GIAN_DON_HANG_KIEM_THU = "20/04/2099 10:00";
    private static final String THOI_GIAN_DAT_BAN_KIEM_THU = "20/04/2099 18:30";
    private static final String THOI_GIAN_YEU_CAU_KIEM_THU = "20/04/2099 10:05";
    private static final String MA_DANH_DAU_TEST = "TEST-TASK4-OVERVIEW-ISOLATION";
    private static final String TIEN_TO_MA_DON = MA_DANH_DAU_TEST + "-ORDER-";
    private static final String TIEN_TO_MA_DAT_BAN = MA_DANH_DAU_TEST + "-RESERVATION-";
    private static final String TIEN_TO_NOI_DUNG_YEU_CAU = MA_DANH_DAU_TEST + "-REQUEST-";

    private Context appContext;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private long idNguoiDungNoiBo;
    private int soDonChoXacNhanGoc;
    private int soDatBanChoDuyetGoc;
    private int soYeuCauCanXuLyGoc;
    private String soDonChoXacNhanKyVong;
    private String soDatBanChoDuyetKyVong;
    private String soYeuCauCanXuLyKyVong;

    @Before
    public void setUp() {
        appContext = ApplicationProvider.getApplicationContext();
        databaseHelper = new DatabaseHelper(appContext);
        sessionManager = new SessionManager(appContext);

        databaseHelper.chuanBiCoSoDuLieu();
        sessionManager.xoaPhienDangNhap();
        dangNhapNhanVienNoiBo();
        xoaDuLieuKiemThuTheoDanhDau();
        chuanBiDuLieuThongKeTongQuan();
    }

    @After
    public void tearDown() {
        xoaDuLieuKiemThuTheoDanhDau();
        sessionManager.xoaPhienDangNhap();
    }

    @Test
    public void moKhuNoiBo_voiTabTongQuan_hienThiTieuDeTongQuan() {
        try (ActivityScenario<TrungTamNoiBoActivity> scenario =
                     ActivityScenario.launch(TrungTamNoiBoActivity.taoIntent(
                             appContext,
                             DieuHuongNoiBoHelper.TAB_TONG_QUAN
                     ))) {
            onView(withText(R.string.internal_shell_overview_title)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void moKhuNoiBo_voiTabKhongPhaiTongQuan_vanGiuExtraTabDaChuanHoa() {
        try (ActivityScenario<TrungTamNoiBoActivity> scenario =
                     ActivityScenario.launch(TrungTamNoiBoActivity.taoIntent(
                             appContext,
                             DieuHuongNoiBoHelper.TAB_DON_HANG
                     ))) {
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.TAB_DON_HANG,
                    activity.getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_TAB_NOI_BO)
            ));
        }
    }

    @Test
    public void moKhuNoiBo_luuRouteNoiBoDangHienThiLaOverview() {
        try (ActivityScenario<TrungTamNoiBoActivity> scenario =
                     ActivityScenario.launch(TrungTamNoiBoActivity.taoIntent(
                             appContext,
                             DieuHuongNoiBoHelper.TAB_DON_HANG
                     ))) {
            scenario.onActivity(activity -> assertEquals(
                    DieuHuongNoiBoHelper.taoRouteNoiBo(DieuHuongNoiBoHelper.TAB_TONG_QUAN),
                    sessionManager.layDuongDanNoiBoCuoi()
            ));
        }
    }

    @Test
    public void moKhuNoiBo_khiChuaDangNhap_duLoiSangDangNhap() {
        sessionManager.xoaPhienDangNhap();
        Intents.init();

        try (ActivityScenario<TrungTamNoiBoActivity> scenario =
                     ActivityScenario.launch(TrungTamNoiBoActivity.taoIntent(
                             appContext,
                             DieuHuongNoiBoHelper.TAB_TONG_QUAN
                     ))) {
            intended(hasComponent(DangNhapActivity.class.getName()));
        } finally {
            Intents.release();
        }
    }

    @Test
    public void moKhuNoiBo_hienThiDungBaChiSoTongQuanTheoDuLieuDaChuanBi() {
        try (ActivityScenario<TrungTamNoiBoActivity> scenario =
                     ActivityScenario.launch(TrungTamNoiBoActivity.taoIntent(
                             appContext,
                             DieuHuongNoiBoHelper.TAB_TONG_QUAN
                     ))) {
            onView(withId(R.id.tvNoiBoPendingOrdersCount)).check(matches(allOf(
                    isDisplayed(),
                    withText(soDonChoXacNhanKyVong)
            )));
            onView(withId(R.id.tvNoiBoPendingReservationsCount)).check(matches(allOf(
                    isDisplayed(),
                    withText(soDatBanChoDuyetKyVong)
            )));
            onView(withId(R.id.tvNoiBoProcessingRequestsCount)).check(matches(allOf(
                    isDisplayed(),
                    withText(soYeuCauCanXuLyKyVong)
            )));
        }
    }

    private void dangNhapNhanVienNoiBo() {
        NguoiDung nguoiDungNoiBo = databaseHelper.getUserByEmail(EMAIL_NOI_BO);
        if (nguoiDungNoiBo == null) {
            idNguoiDungNoiBo = databaseHelper.insertUser(
                    "Nhan vien tong quan noi bo",
                    EMAIL_NOI_BO,
                    SO_DIEN_THOAI_NOI_BO,
                    "12345678",
                    VaiTroNguoiDung.NHAN_VIEN,
                    true
            );
        } else {
            idNguoiDungNoiBo = nguoiDungNoiBo.layId();
        }

        if (idNguoiDungNoiBo <= 0) {
            throw new IllegalStateException("Khong the chuan bi nguoi dung noi bo cho test");
        }

        sessionManager.luuPhienNoiBo(idNguoiDungNoiBo, VaiTroNguoiDung.NHAN_VIEN);
        sessionManager.damBaoVaiTroSession(databaseHelper);
    }

    private void chuanBiDuLieuThongKeTongQuan() {
        ThongKeTongQuanNhanVien thongKeGoc = databaseHelper.layThongKeTongQuanNhanVien();
        assertNotNull("Thong ke tong quan goc khong duoc null", thongKeGoc);
        soDonChoXacNhanGoc = thongKeGoc.getPendingDonHangs();
        soDatBanChoDuyetGoc = thongKeGoc.getPendingReservations();
        soYeuCauCanXuLyGoc = thongKeGoc.getProcessingServiceRequests();

        List<MonAnDeXuat> danhSachMon = databaseHelper.layTatCaMonHienThi();
        assertFalse("Can co mon an de tao du lieu kiem thu", danhSachMon.isEmpty());
        MonAnDeXuat monMau = danhSachMon.get(0);
        List<DonHang.MonTrongDon> monTrongDon = Collections.singletonList(new DonHang.MonTrongDon(monMau, 1));

        long idDonChoXacNhan = databaseHelper.themDonHang(
                (int) idNguoiDungNoiBo,
                TIEN_TO_MA_DON + "PENDING-01",
                THOI_GIAN_DON_HANG_KIEM_THU,
                monMau.layGiaBan(),
                DonHang.TrangThai.CHO_XAC_NHAN,
                monTrongDon
        );
        long idDonDangChuanBi = databaseHelper.themDonHang(
                (int) idNguoiDungNoiBo,
                TIEN_TO_MA_DON + "PREPARING-01",
                THOI_GIAN_DON_HANG_KIEM_THU,
                monMau.layGiaBan(),
                DonHang.TrangThai.DANG_CHUAN_BI,
                monTrongDon
        );
        assertTrue("Can tao duoc don cho xac nhan", idDonChoXacNhan > 0);
        assertTrue("Can tao duoc don dang chuan bi de loai khoi KPI", idDonDangChuanBi > 0);

        long idDatBanChoDuyet = databaseHelper.themDatBan(
                idNguoiDungNoiBo,
                TIEN_TO_MA_DAT_BAN + "PENDING-01",
                THOI_GIAN_DAT_BAN_KIEM_THU,
                "B10",
                4,
                TIEN_TO_MA_DAT_BAN + "NOTE-PENDING",
                DatBan.TrangThai.PENDING,
                0
        );
        long idDatBanDaXuLy = databaseHelper.themDatBan(
                idNguoiDungNoiBo,
                TIEN_TO_MA_DAT_BAN + "ACTIVE-01",
                THOI_GIAN_DAT_BAN_KIEM_THU,
                "B11",
                2,
                TIEN_TO_MA_DAT_BAN + "NOTE-ACTIVE",
                DatBan.TrangThai.ACTIVE,
                0
        );
        assertTrue("Can tao duoc dat ban cho duyet", idDatBanChoDuyet > 0);
        assertTrue("Can tao duoc dat ban da xu ly de loai khoi KPI", idDatBanDaXuLy > 0);

        long idYeuCauDangCho = databaseHelper.insertServiceRequest(
                idNguoiDungNoiBo,
                TIEN_TO_NOI_DUNG_YEU_CAU + "DANG-CHO",
                THOI_GIAN_YEU_CAU_KIEM_THU,
                YeuCauPhucVu.TrangThai.DANG_CHO
        );
        long idYeuCauDangXuLy = databaseHelper.insertServiceRequest(
                idNguoiDungNoiBo,
                TIEN_TO_NOI_DUNG_YEU_CAU + "DANG-XU-LY",
                "20/04/2099 10:06",
                YeuCauPhucVu.TrangThai.DANG_XU_LY
        );
        long idYeuCauDaXuLy = databaseHelper.insertServiceRequest(
                idNguoiDungNoiBo,
                TIEN_TO_NOI_DUNG_YEU_CAU + "DA-XU-LY",
                "20/04/2099 10:07",
                YeuCauPhucVu.TrangThai.DA_XU_LY
        );
        assertTrue("Can tao duoc yeu cau dang cho", idYeuCauDangCho > 0);
        assertTrue("Can tao duoc yeu cau dang xu ly", idYeuCauDangXuLy > 0);
        assertTrue("Can tao duoc yeu cau da xu ly de loai khoi KPI", idYeuCauDaXuLy > 0);

        ThongKeTongQuanNhanVien thongKeSauChen = databaseHelper.layThongKeTongQuanNhanVien();
        assertNotNull("Thong ke tong quan sau chen khong duoc null", thongKeSauChen);

        soDonChoXacNhanKyVong = String.valueOf(soDonChoXacNhanGoc + 1);
        soDatBanChoDuyetKyVong = String.valueOf(soDatBanChoDuyetGoc + 1);
        soYeuCauCanXuLyKyVong = String.valueOf(soYeuCauCanXuLyGoc + 2);

        assertEquals("KPI don cho xac nhan phai tang dung 1 ban ghi test",
                soDonChoXacNhanGoc + 1,
                thongKeSauChen.getPendingDonHangs());
        assertEquals("KPI dat ban cho duyet phai tang dung 1 ban ghi test",
                soDatBanChoDuyetGoc + 1,
                thongKeSauChen.getPendingReservations());
        assertEquals("KPI yeu cau can xu ly phai gom ca dang cho va dang xu ly cua du lieu test",
                soYeuCauCanXuLyGoc + 2,
                thongKeSauChen.getProcessingServiceRequests());
    }

    private void xoaDuLieuKiemThuTheoDanhDau() {
        SQLiteDatabase writableDatabase = databaseHelper.getWritableDatabase();
        writableDatabase.delete(
                DatabaseHelper.TABLE_ORDER_ITEM,
                "order_id IN (SELECT id FROM " + DatabaseHelper.TABLE_ORDER + " WHERE code LIKE ?)",
                new String[]{TIEN_TO_MA_DON + "%"}
        );
        writableDatabase.delete(
                DatabaseHelper.TABLE_ORDER,
                "code LIKE ?",
                new String[]{TIEN_TO_MA_DON + "%"}
        );
        writableDatabase.delete(
                DatabaseHelper.TABLE_RESERVATION,
                "reservation_code LIKE ? OR note LIKE ?",
                new String[]{TIEN_TO_MA_DAT_BAN + "%", TIEN_TO_MA_DAT_BAN + "%"}
        );
        writableDatabase.delete(
                DatabaseHelper.TABLE_SERVICE_REQUEST,
                "content LIKE ?",
                new String[]{TIEN_TO_NOI_DUNG_YEU_CAU + "%"}
        );
    }
}
