package com.example.chenpeiqi.iona;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.Date;

public class Diary extends AppCompatActivity {

private static EditText editText;
static Handler handler;
  private String content;

@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_diary);
  editText = (EditText) findViewById(R.id.content_text);
  handler = new MaiHandler(new WeakReference<>(this));
  this.content = getIntent().getStringExtra("content");
  editText.setText(content);
}

private static class MaiHandler extends Handler {

  WeakReference<Diary> ref;

  MaiHandler(WeakReference<Diary> ref) {
    this.ref = ref;
  }

  @Override
  public void handleMessage(Message msg) {
    super.handleMessage(msg);
    switch (msg.what) {
    case 10086:
      //从服务器获得数据并将其显示在EditText上
      Bundle bundle = msg.getData();
      String content = bundle.getString("content");
      editText.setText(content);
      break;
    }
  }
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
    String content = editText.getText().toString();
    String email =
        getSharedPreferences("l_i",MODE_PRIVATE).getString("email","aaa");
    new Thread(new CMT(this,email,"content",content)).start();
    Bundle bundle = new Bundle();
    Intent intent = new Intent(Diary.this,Iona.class);
    intent.putExtra("content",bundle);
    setResult(0,intent);
    this.finish();
    return true;
  default:
    return super.onOptionsItemSelected(item);
  }
}

}
