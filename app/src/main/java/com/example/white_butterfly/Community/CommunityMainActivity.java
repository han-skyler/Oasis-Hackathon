package com.example.white_butterfly.Community;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.Community.CommunityWriteActivity;
import com.example.white_butterfly.HospitalData;
import com.example.white_butterfly.HospitalInfoActivity;
import com.example.white_butterfly.MainActivity;
import com.example.white_butterfly.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommunityMainActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;
    DatabaseReference docRef;
    private Button writeButton;
    private ListView communitylistView;
    String id;
    String titleText;
    String dateText;
    String idText;
    private static final String TAG = "CommunityMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_main);

        Log.w(TAG, "--- CommunityMainActivity ---");

        FirebaseApp.initializeApp(CommunityMainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        docRef = firebaseDatabase.getReference();

        ImageView image_before = findViewById(R.id.image_before);
        image_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_main = new Intent(getApplication(), MainActivity.class);
                startActivity(intent_main);
            }
        });

        String kakao_email = getIntent().getStringExtra("Email");
        if (kakao_email != null && !kakao_email.isEmpty()) {
            // 이전 액티비티에서 받아온 값이 있는 경우
            id = kakao_email;
        } else {
            // 이전 액티비티에서 값이 없는 경우
            id = currentUser.getEmail();
        }

        String name = getIntent().getStringExtra("name");

        communitylistView = findViewById(R.id.community_list);
        writeButton = findViewById(R.id.btn_add);

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityMainActivity.this, CommunityWriteActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        // 리스트뷰 초기화
        ArrayList<String> nodeNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_community_list, nodeNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_community_list, parent, false);
                }

                TextView Title = convertView.findViewById(R.id.txt_title);
                TextView Date = convertView.findViewById(R.id.txt_date);
                TextView Id = convertView.findViewById(R.id.txt_id);

                String itemData = nodeNames.get(position); // 현재 항목의 데이터
                String[] dataParts = itemData.split("\n"); // 데이터를 줄 단위로 분할
                titleText = dataParts[0]; // 첫 번째 줄은 제목
                dateText = dataParts[1]; // 두 번째 줄은 날짜
                idText = dataParts[2]; // 세 번째 줄은 작성자

                Title.setText(titleText); // 텍스트뷰에 제목 설정
                Date.setText(dateText); // 텍스트뷰에 날짜 설정
                Id.setText(idText); // 텍스트뷰에 작성자 설정

                return convertView;
            }
        };
        communitylistView.setAdapter(adapter);

        communitylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemData = nodeNames.get(position); // 클릭한 항목의 데이터
                String[] dataParts = itemData.split("\n"); // 데이터를 줄 단위로 분할
                titleText = dataParts[0]; // 첫 번째 줄은 제목
                dateText = dataParts[1]; // 두 번째 줄은 날짜
                idText = dataParts[2]; // 세 번째 줄은 작성자

                // 클릭한 항목의 타이틀, 날짜, 아이디 값을 인텐트로 전달하고 새 액티비티를 시작
                Intent intent = new Intent(CommunityMainActivity.this, CommunityReadActivity.class);
                intent.putExtra("Title", titleText);
                intent.putExtra("Date", dateText);
                intent.putExtra("Id", idText);

                Log.w(TAG, "Title : " + titleText);
                Log.w(TAG, "Date : " + dateText);
                Log.w(TAG, "Id : " + idText);
                startActivity(intent);
            }
        });

        docRef.child("Community").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nodeNames.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String DateName = snapshot.getKey();
                    Log.w(TAG, "DateName : " + DateName);

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String IdName = childSnapshot.getKey();
                        Log.w(TAG, "IdName : " + IdName);

                        for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                            String TitleName = grandChildSnapshot.getKey();
                            Log.w(TAG, "TitleName : " + TitleName);
                            if (TitleName != null) {
                                String itemData = TitleName + "\n" + DateName + "\n" + IdName;
                                nodeNames.add(itemData);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "데이터 가져오기 실패: " + databaseError.getMessage());
            }
        });
    }
}