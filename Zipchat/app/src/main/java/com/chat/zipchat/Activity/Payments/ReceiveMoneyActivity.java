package com.chat.zipchat.Activity.Payments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.zipchat.Adapter.Payment.MobileNumberAdapter;
import com.chat.zipchat.Model.GetMobileNumber.GetMobileNumberResponse;
import com.chat.zipchat.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.Common.BaseClass.apiInterfacePayment;
import static com.chat.zipchat.Common.BaseClass.isOnline;
import static com.chat.zipchat.Common.BaseClass.myLog;
import static com.chat.zipchat.Common.BaseClass.showSimpleProgressDialog;
import static com.chat.zipchat.Common.SessionManager.PHONE;
import static com.chat.zipchat.Common.SessionManager.PHONE_CODE;
import static com.chat.zipchat.Common.SessionManager.PREF_NAME;

public class ReceiveMoneyActivity extends AppCompatActivity {

    Toolbar mToolbarReceiveMoney;
    TextView mMobileNumber;
    ImageView mReceiveMoneyBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_money);

        mToolbarReceiveMoney = findViewById(R.id.mToolbarReceiveMoney);
        setSupportActionBar(mToolbarReceiveMoney);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.receive_money));
        mToolbarReceiveMoney.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbarReceiveMoney.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        SharedPreferences sharedpreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String phone = sharedpreferences.getString(PHONE, null);
        String phone_code = sharedpreferences.getString(PHONE_CODE, null);

        mMobileNumber = findViewById(R.id.mMobileNumber);
        mMobileNumber.setText(phone_code + " " + phone);

        mReceiveMoneyBarcode = findViewById(R.id.mReceiveMoneyBarcode);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap((phone), BarcodeFormat.QR_CODE, 600, 600);
            mReceiveMoneyBarcode.setImageBitmap(bitmap);
        } catch (Exception e) {

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

}
