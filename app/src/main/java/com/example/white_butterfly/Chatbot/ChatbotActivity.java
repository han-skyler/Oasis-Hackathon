package com.example.white_butterfly.Chatbot;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.white_butterfly.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotActivity extends AppCompatActivity {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 변수
    RecyclerView recycler_view;
    TextView tv_welcome;
    EditText et_msg;
    ImageButton btn_send;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    TextToSpeech tts;
    TextView text_chatbot;
    TextView text_user;

    // Chat GPT
    OkHttpClient client;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private String url = "https://api.openai.com/v1/chat/completions";
    private static String MY_SECRET_KEY = "";

    // TAG
    private String TAG = "ChatbotActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot2);

        Log.w(TAG, "--- ChatbotActivity ---");

        getKey();
        initializeViews();
        text_chatbot.setText("할머니, 안녕하세요! 오늘은 뭐 했어요?");


        // EditText에서 엔터 키 눌렀을 때 이벤트 처리
        et_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = et_msg.getText().toString().trim();
                addToChat(question, Message.SENT_BY_ME);
                et_msg.setText("");
                callAPI(question);
            }
        });

        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

    }

    private void initializeViews() {
        //recycler_view = findViewById(R.id.recycler_view);
        //tv_welcome = findViewById(R.id.tv_welcome);
        text_chatbot = findViewById(R.id.text_chatbot);
        //text_user = findViewById(R.id.text_user);
        et_msg = findViewById(R.id.et_msg);
        btn_send = findViewById(R.id.btn_send);


        //recycler_view.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        //recycler_view.setLayoutManager(manager);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        //recycler_view.setAdapter(messageAdapter);

        // EditText에서 엔터 키 눌렀을 때 이벤트 처리
        et_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = et_msg.getText().toString().trim();
                addToChat(question, Message.SENT_BY_ME);
                et_msg.setText("");
                callAPI(question);
            }
        });
    }

    private void getKey()
    {
        docRef = db.collection("Box").document("Doc");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    MY_SECRET_KEY = String.valueOf(documentSnapshot.getString("Key"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting document", e);
            }
        });
    }

    private void sendMessage()
    {
        String question = et_msg.getText().toString().trim();
        addToChat(question, Message.SENT_BY_ME);
        et_msg.setText("");
        callAPI(question);
    }

    void addToChat(String message, String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                /*
                messageAdapter.notifyDataSetChanged();
                recycler_view.smoothScrollToPosition(messageAdapter.getItemCount());
                 */
            }
        });
    }

    void addResponse(String response){
        //messageList.remove(messageList.size()-1);
        addToChat(response, Message.SENT_BY_BOT);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_chatbot.setText(response);
                btn_send.setEnabled(true);
            }
        });

        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.KOREA);

                // TextView의 텍스트를 TTS로 읽기
                String textToRead = response.toString();

                tts.setPitch(1.0f);  // 음성 톤 설정 (1.0은 기본 값)
                tts.setSpeechRate(1.0f); // 음성 속도 설정 (1.0은 기본 값)
                tts.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    void callAPI(String question){
        //okhttp
        //messageList.add(new Message("...", Message.SENT_BY_BOT));

        text_chatbot.setText("...");

        JSONArray arr = new JSONArray();
        JSONObject baseAi_system = new JSONObject();
        JSONObject baseAi_assistant = new JSONObject();
        JSONObject userMsg = new JSONObject();
        try {
            // AI 속성 설정
            baseAi_system.put("role", "system");
            baseAi_system.put("content", "너는 할머니와 대화하고 있는 5살 남자아이야. 50자 이내로 대답해.");

            baseAi_assistant.put("role", "assistant");
            baseAi_assistant.put("content", messageList);

            // 유저 메세지
            userMsg.put("role", "user");
            userMsg.put("content", question);

            //text_user.setText(question);
            btn_send.setEnabled(false);
            text_chatbot.setText("");

            // array로 담아서 한번에 보낸다
            arr.put(baseAi_system);
            arr.put(baseAi_assistant);
            arr.put(userMsg);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject();
        try {
            object.put("model", "gpt-3.5-turbo");
            object.put("messages", arr);

            /*
            object.put("model", "text-davinci-003");
            object.put("prompt", question);
            object.put("max_tokens", 4000);
            object.put("temperature", 0);
             */
        } catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message").getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to "+response.body().string());
                    Log.w(TAG, "Failed to load response due to unsuccessful response: " + response);
                }
            }
        });
    }
}