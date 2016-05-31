package com.example.chenpeiqi.iona;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Date;



public class Diary extends AppCompatActivity {

    private static EditText editText;
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        editText = (EditText) findViewById(R.id.content_text);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 10086:
                        Bundle bundle = msg.getData();
                        String content = bundle.getString("content");
                        editText.setText(content);
                        break;
                }

            }
        };
        String[] date = new Date().toString().split(" ");
        new Thread(new CMT(this,"request_content",date[5],date[1],date[2])).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.day_record,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_send:
                Log.i("iona","R.id.action_send triggered");
                new Thread(new CMT(this,"content",getSharedPreferences("l_i",MODE_PRIVATE)
                        .getString("email","aaa"),editText.getText().toString())).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
