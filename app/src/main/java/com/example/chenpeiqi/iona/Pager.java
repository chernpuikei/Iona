package com.example.chenpeiqi.iona;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Pager extends AppCompatActivity implements
    ViewPager.OnPageChangeListener {

  private boolean readyToStart;
  private boolean started;
  private static final int m = Context.MODE_PRIVATE;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
    MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
    viewPager.setAdapter(adapter);
    viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    getWindow().setStatusBarColor(Color.TRANSPARENT);
    viewPager.setOnPageChangeListener(this);
    clearUserInfo();

    getSharedPreferences("l_i",m).edit().putString("email","aaa").apply();
    startActivity(new Intent(this,Iona.class));
  }

  @Override
  public void onPageScrolled(
      int position,float positionOffset,int positionOffsetPixels) {
    if (position == 2 && positionOffset == 0) {
      if (readyToStart && !started) {
        Login login = new Login();
        login.show(getSupportFragmentManager(),"login");
        started = true;
      } else {
        readyToStart = true;
      }
    }
  }

  @Override
  public void onPageSelected(int position) {

  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }

  private class MyAdapter extends FragmentPagerAdapter {

    int[] ids = {R.drawable.g0,R.drawable.g1,R.drawable.g2};

    MyAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      return MyFragment.actualCreate(ids[position]);
    }

    @Override
    public int getCount() {
      return 3;
    }
  }

  private void clearUserInfo() {
    getSharedPreferences("l_i",MODE_PRIVATE).edit().clear().apply();
  }

}




