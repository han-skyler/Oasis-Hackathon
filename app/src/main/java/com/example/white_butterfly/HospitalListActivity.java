package com.example.white_butterfly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HospitalListActivity extends AppCompatActivity {

    // 뷰
    ArrayList<HospitalData> hospitalDataList;
    ListView listView;
    TextView text_reservation;
    TextView text_name;
    TextView text_sub;
    TextView text_adr;

    // 변수
    String hospitalName;
    String hospitalSub;
    String hospitalAddress;

    // 태그
    private static final String TAG = "ReservationListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);

        Log.w(TAG, "--- ReservationListActivity ---");

        initializeViews();

        this.InitializeHospitalData();

        listView = (ListView)findViewById(R.id.hospital_list);
        final HospitalAdapter hospitalAdapter = new HospitalAdapter(this, hospitalDataList);
        listView.setAdapter(hospitalAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                HospitalData clickedItem = (HospitalData) parent.getItemAtPosition(position);

                hospitalName = clickedItem.getHospital_name();
                hospitalSub = clickedItem.getHospital_sub();
                hospitalAddress = clickedItem.getHospital_adr();

                Log.w(TAG, "clickedItem: " + clickedItem);

                ProgressDialog progressDialog = ProgressDialog.show(HospitalListActivity.this, "", "로딩 중...", true);

                // Intent 생성 및 데이터 전달 작업 수행
                Intent intent = new Intent(getApplication(), HospitalInfoActivity.class);

                intent.putExtra("hospitalName", hospitalName);
                intent.putExtra("hospitalSub", hospitalSub);
                intent.putExtra("hospitalAddress", hospitalAddress);

                // ProgressDialog 닫기
                progressDialog.dismiss();

                startActivity(intent);
            }
        });

        text_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), HospitalInfoActivity.class);

                intent.putExtra("hospitalName", text_name.getText());
                intent.putExtra("hospitalSub", text_sub.getText());
                intent.putExtra("hospitalAddress", text_adr.getText());

                startActivity(intent);
            }
        });
    }

    private void initializeViews()
    {
        text_reservation = findViewById(R.id.text_reservation);
        text_name = findViewById(R.id.text_name);
        text_sub = findViewById(R.id.text_sub);
        text_adr = findViewById(R.id.text_address);
    }

    public void InitializeHospitalData()
    {
        hospitalDataList = new ArrayList<HospitalData>();

        hospitalDataList.add(new HospitalData(R.drawable.icon_star,"해피뷰병원", "종합병원","광주광역시 북구 유동"));
        hospitalDataList.add(new HospitalData(R.drawable.icon_star,"북구치매주간병원", "요양병원","광주광역시 북구 태봉로"));

        hospitalDataList.add(new HospitalData(R.color.back,"중앙신경과의원", "신경과","광주광역시 서구 금호동"));
        hospitalDataList.add(new HospitalData(R.color.back,"허욱신경과의원", "신경과","광주광역시 서구 금호동"));
    }
}
