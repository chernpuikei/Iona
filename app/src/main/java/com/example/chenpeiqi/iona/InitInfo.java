package com.example.chenpeiqi.iona;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class InitInfo extends DialogFragment {

  String storeNickname;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i("InitInfo","onCreate");
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Log.i("InitInfo","onCreateDialog");
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setMessage("提交个人资料");
    LayoutInflater layoutInflater = getLayoutInflater(savedInstanceState);
    View view = layoutInflater.inflate(R.layout.dialog_init_info,null);
    builder.setView(view);

    final EditText nickname = (EditText) view.findViewById(R.id.nickname);
    final EditText self_intro = (EditText) view.findViewById(R.id.self_intro);
    nickname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView,int i,KeyEvent keyEvent) {
        storeNickname = nickname.getText().toString();
        self_intro.requestFocus(View.FOCUS_DOWN);
        return true;
      }
    });
    self_intro.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView,int i,KeyEvent keyEvent) {
      return true;
      }
    });
    return builder.create();
  }
}
