package com.example.chenpeiqi.iona;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private static EditText email_widget;
    private static EditText password_widget;
    private static EditText confirmPassWord_widget;
    static String ACTION_FLAG;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ACTION_FLAG = "check";
        email_widget = (EditText) findViewById(R.id.email);
        password_widget = (EditText) findViewById(R.id.password);
        confirmPassWord_widget = (EditText) findViewById(R.id.password_confirm);
        email_widget.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                password_widget.setVisibility(View.VISIBLE);
                password_widget.requestFocus(View.FOCUS_DOWN);
                return true;
            }
        });
    }

    public void nextMove(View view) throws InterruptedException {
        Log.i("iona", "nextMove called,action flag is:" + ACTION_FLAG);
        String email = email_widget.getText().toString();
        String password = password_widget.getText().toString();
        switch (ACTION_FLAG) {
            case "check":
                email_widget.setFocusable(false);
                password_widget.setVisibility(View.VISIBLE);
                password_widget.requestFocus(View.FOCUS_DOWN);
                new Thread(new CMT(Login.this,ACTION_FLAG,email,null)).start();
                break;
            case "lor":
                if (checkSameOrNot()) {
                    new Thread(new CMT(Login.this, ACTION_FLAG,email,password)).start();
                } else {
                    Toast.makeText(Login.this, "两次输入内容不一致", Toast.LENGTH_SHORT).show();
                }
                break;
            case "confirm":
                confirmPassWord_widget.setVisibility(View.VISIBLE);
                confirmPassWord_widget.requestFocus(View.FOCUS_DOWN);
                ACTION_FLAG = "lor";
                break;
        }
    }

    private boolean checkSameOrNot() {
        return ACTION_FLAG.equals("lor") || password_widget.getText().toString()
                .equals(confirmPassWord_widget.getText().toString());
    }
}


