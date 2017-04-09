package com.example.baby.tuling;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button TuLing;
    EditText input;
    String line;
    StringBuffer stringBuffer;
    ListView msgListView;
    MsgAdapter adapter;
    List<Msg> msgList = new ArrayList<>();
    String text = "";
    String menu_text;
    String input_menu = "";
    File f;
    SQLite sqLite;
    Msg msg1;
    boolean first = true;
    public static final int UPDATE_TEXT = 1;
    //创建一个Handler
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    //在这里可以进行UI操作
                    HttpPost();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqLite = new SQLite(this, "ChatLog.db", null, 1);
        adapter = new MsgAdapter(MainActivity.this, R.layout.msg_item, msgList);
        IntentView();
        FindFile();
        initMsgs();
    }

    private void FindFile() {
        f = new File("data//data//com.example.baby.tuling//databases//ChatLog.db");
        if (f.exists()) {

        } else {
            return;
        }
    }

    private void IntentView() {
        TuLing = (Button) findViewById(R.id.send);
        TuLing.setOnClickListener(this);
        input = (EditText) findViewById(R.id.input_text);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        msgListView.setDivider(null);
    }

    private void initMsgs() {
        if (first) {
            msg1 = new Msg("开心机器人豆豆报道~\\(≧▽≦)/~", Msg.TYPE_RECEIVED);
            msgList.add(msg1);
            adapter.notifyDataSetChanged();
        } else {
            Msg msg1 = new Msg(menu_text, Msg.TYPE_RECEIVED);
            msgList.add(msg1);
            adapter.notifyDataSetChanged();
            msgListView.setSelection(msgList.size()+1);
        }
    }

    private void HttpPost() {
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    bufferedWriter.write("key=d5ab0be9da584b41928c6467b68a2630&info=" + input_menu);
                    bufferedWriter.flush();
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    stringBuffer = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        Log.i("line", line);
                        stringBuffer.append(line);
                    }
                    Json();
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute("http://www.tuling123.com/openapi/api");
    }

    public void Json() {
        try {
            String menu = stringBuffer.toString();
            JSONObject jsonObject = new JSONObject(menu);
            text = jsonObject.getString("text");
            Log.i("text", text);
            menu_text = text + "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (!TextUtils.isEmpty(input.getText())) {
                    String content = input.getText().toString();
                    if (!"".equals(content)) {
                        first = false;
                        Msg msg2 = new Msg(input.getText().toString(), Msg.TYPE_SENT);
                        msgList.add(msg2);
                        input_menu = input.getText().toString();
                        adapter.notifyDataSetChanged();
                        input.setText("");
                        initMsgs();
                    } else {
                        Toast.makeText(this, "输入无效，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = UPDATE_TEXT;
                            handler.sendMessage(message);
                        }
                    }).start();
                    // 关闭软键盘
                    InputMethodManager imm_down = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // 得到InputMethodManager的实例
                    if (imm_down.isActive()) {
                        // 如果开启
                        imm_down.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    if (f.exists()) {
                        SQLiteDatabase database = sqLite.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("chat_log_left", input_menu);
                        values.put("chat_log_right", input.getText().toString());
                        database.insert("chat_log", null, values);
                        values.clear();
                        database.close();
                    } else {
                        sqLite.getWritableDatabase();
                        SQLiteDatabase database = sqLite.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("chat_log_left", input_menu);
                        values.put("chat_log_right", input.getText().toString());
                        database.insert("chat_log", null, values);
                        values.clear();
                        database.close();
                    }

                } else {
                    Toast.makeText(this, "输入无效，请重新输入", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

