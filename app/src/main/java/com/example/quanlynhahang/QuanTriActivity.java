package com.example.quanlynhahang;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlynhahang.helper.DieuHuongNoiBoHelper;

public class QuanTriActivity extends AppCompatActivity {

    public static Intent taoIntent(Context context) {
        return new Intent(context, QuanTriActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moTrungTamQuanTri();
    }

    private void moTrungTamQuanTri() {
        String sectionDuocYeuCau = getIntent().getStringExtra(DieuHuongNoiBoHelper.EXTRA_SECTION_QUAN_TRI);
        if (sectionDuocYeuCau == null || sectionDuocYeuCau.trim().isEmpty()) {
            sectionDuocYeuCau = DieuHuongNoiBoHelper.SECTION_BAO_CAO;
        }
        Intent intent = TrungTamNoiBoActivity.taoIntentQuanTri(this, sectionDuocYeuCau);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
