package com.example.chenpeiqi.iona;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Created on 16/9/27.
 */
public class Login extends DialogFragment {

  static EditText email_widget;
  private static EditText nickname_widget;
  private static EditText self_intro_widget;
  private static EditText password_widget;
  private Context context;
  static String ACT;
  static android.os.Handler handler;
  public static final int SHOW_LOGIN = 13800;
  public static final int SHOW_REGIS = 13801;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getActivity();
    handler = new android.os.Handler() {
      @Override
      public void handleMessage(Message msg) {
        Log.i("Login.handler","receiving message "+msg.what);
        switch (msg.what) {
          case SHOW_LOGIN:
            password_widget.setVisibility(View.VISIBLE);
            password_widget.requestFocus(View.FOCUS_DOWN);
            break;
          case SHOW_REGIS:
            nickname_widget.setVisibility(View.VISIBLE);
            nickname_widget.requestFocus(View.FOCUS_DOWN);
            self_intro_widget.setVisibility(View.VISIBLE);
            password_widget.setVisibility(View.VISIBLE);
            break;
        }
      }
    };
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater layoutInflater = getActivity().getLayoutInflater();
    builder.setTitle("登陆、或注册");
//  builder.setView(R.layout.activity_login);
    final View content = layoutInflater.inflate(R.layout.activity_login,null);
    builder.setView(content);
    email_widget = (EditText) content.findViewById(R.id.email);
    nickname_widget = (EditText) content.findViewById(R.id.nickname);
    self_intro_widget = (EditText) content.findViewById(R.id.self_intro);
    password_widget = (EditText) content.findViewById(R.id.password);
    ACT = "check";
    email_widget.setOnEditorActionListener(
        new OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v,int actionId,KeyEvent e) {
            final String email = email_widget.getText().toString();
            password_widget.setVisibility(View.VISIBLE);
            password_widget.requestFocus(View.FOCUS_DOWN);
            new Thread(new CMT(getActivity(),email)).start();
            return true;
          }
        });
    password_widget.setOnEditorActionListener(new OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView,int i,KeyEvent ke) {
        if (ke.getAction() == KeyEvent.ACTION_UP) {
          Log.i("Login.password_widget","being clicked "+ACT);
          switch (ACT) {
            case "register":
              String email = email_widget.getText().toString();
              String password = password_widget.getText().toString();
              String nickname = nickname_widget.getText().toString();
              String self_intro = self_intro_widget.getText().toString();
              new Thread(new CMT(context,email,password,nickname,self_intro))
                  .start();
              return true;
            case "login":
              final String email_l = email_widget.getText().toString();
              String password_l = password_widget.getText().toString();
              new Thread(new CMT(context,email_l,"login",password_l)).start();
              return true;
          }
        }
        return true;
      }
    });
    nickname_widget.setOnEditorActionListener(new OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView,int i,KeyEvent ke) {
        self_intro_widget.requestFocus(View.FOCUS_DOWN);
        return true;
      }
    });
    self_intro_widget.setOnEditorActionListener(new OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView,int i,KeyEvent ke) {
        password_widget.requestFocus(View.FOCUS_DOWN);
        return true;
      }
    });

    return builder.create();
  }

}
