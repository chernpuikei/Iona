package com.example.chenpeiqi.iona;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class Iona extends FragmentActivity implements
    MyFrag.PassCord, MyFrag.PassDire,
    GestureDetector.OnGestureListener, Pan.PassSomething,
    GestureDetector.OnDoubleTapListener {

  static String direction;
  static MyHandler myHandler;
  GestureDetector gestureDetector;
  Draw draw;
  static int s_i;
  static int[] date;
  private float x, y;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i("iona","—————————————Iona.onCreate()—————————————");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_iona);
    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE |
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    gestureDetector = new GestureDetector(this,this);
//    getWindow().setStatusBarColor(Color.TRANSPARENT);
//    initContentView(this);
    myHandler = new MyHandler(new WeakReference<>(this));
    MyFrag tf = MyFrag.actualCreate(50,50);
    getFragmentManager().beginTransaction().add(R.id.container,tf).commit();

  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    gestureDetector.onTouchEvent(event);
    return super.onTouchEvent(event);
  }

  @Override
  public boolean onDown(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public void onShowPress(MotionEvent motionEvent) {

  }

  @Override
  public boolean onSingleTapUp(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent motionEvent,MotionEvent motionEvent1,float v,float v1) {
    return false;
  }

  @Override
  public void onLongPress(MotionEvent motionEvent) {
  }

  @Override
  public boolean onFling(MotionEvent e1,MotionEvent e2,float v,float a) {
    float e1_x = e1.getX(), e2_x = e2.getX(),
        e1_y = e1.getY(), e2_y = e2.getY();
    if ((e2_x-e1_x < -150 && direction.equals("right"))
        || (e2_x-e1_x > 150 && direction.equals("left"))
        || (e2_y-e1_y > 150 && direction.equals("up"))
        || (e2_y-e1_y < -150 && direction.equals("down"))) {
      s_i++;
      Iona.this.getFragmentManager().beginTransaction().replace(
          R.id.container,MyFrag.actualCreate(x,y)).commit();
    }
    return true;
  }

  @Override
  public void passThread(Draw draw) {
    this.draw = draw;
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
    return false;
  }

  @Override
  public boolean onDoubleTap(MotionEvent motionEvent) {
    draw.setAnimating(false);
    new Thread(draw).start();
    return false;
  }

  @Override
  public boolean onDoubleTapEvent(MotionEvent motionEvent) {
    return false;
  }

  static class MyHandler extends Handler {

    WeakReference<Iona> ref;

    MyHandler(WeakReference<Iona> ref) {
      super();
      this.ref = ref;
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case 0:
          Intent intent = new Intent(ref.get(),Diary.class);
          this.ref.get().startActivityForResult(intent,0);
          break;
        case 1:
          Toast.makeText(ref.get(),"Meow",Toast.LENGTH_SHORT).show();
          break;
      }
    }
  }

  @Override
  public void passCord(float[] end,int width,int height) {
    x = end[0] == width ? 0 : end[0] == 0 ? width : end[0];
    y = end[1] == height ? 0 : end[1] == 0 ? height : end[1];
  }

  @Override
  public void passDire(String direction) {
    Iona.direction = direction;
  }

  @Override
  protected void onActivityResult(int requestCode,int rsc,Intent data) {
    super.onActivityResult(requestCode,rsc,data);
    switch (rsc) {
      case 0:
        break;
    }
  }

}
