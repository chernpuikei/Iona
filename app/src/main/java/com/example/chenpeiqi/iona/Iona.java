package com.example.chenpeiqi.iona;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class Iona extends FragmentActivity implements ThisFragmentOfMine.PassCord,
        ThisFragmentOfMine.PassDire, GestureDetector.OnGestureListener {

    public final static String iona = "iona";
    private String direction;
    static int screen_indicate;
    private float x;
    private float y;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screen_indicate = 0;
        setContentView(R.layout.iona);
        gestureDetector = new GestureDetector(this, this);
        String user_name = getSharedPreferences("user_information", Context.MODE_PRIVATE)
                .getString("user_name", "nah");
        direction = getSharedPreferences("direction", MODE_PRIVATE)
                .getString("direction", "not exist!!!");
        getFragmentManager().beginTransaction()
                .add(R.id.container, ThisFragmentOfMine.actualCreate(0, 0)).commit();

    }

    @Override
    public void passCord(float[] end, int width, int height) {
        x = (end[0] == width ? 0 : end[0] == 0 ? width-50 : end[0]);
        y = (end[1] == height ? 0 : end[1] == 0 ? height-50 : end[1]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void passDire(String direction) {
        this.direction = direction;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float e1_x = e1.getX();
        float e2_x = e2.getX();
        float e1_y = e1.getY();
        float e2_y = e2.getY();

        if ((e2_x - e1_x < -150 && direction.equals("right")) ||
                (e2_x - e1_x > 150 && direction.equals("left")) ||
                (e2_y - e1_y > 150 && direction.equals("up")) ||
                (e2_y - e1_y < -150 && direction.equals("down"))) {
            screen_indicate++;
            Log.i("iona","screen indicate is:"+screen_indicate);
            getFragmentManager().beginTransaction().replace(R.id.container,
                    ThisFragmentOfMine.actualCreate(x, y)).commit();
        }

        return false;
    }

}
