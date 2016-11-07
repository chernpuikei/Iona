package com.example.chenpeiqi.iona;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.ArrayList;

/**
 * Created by chenpeiqi on 16/11/3.
 */
public class DrawDate implements Runnable {

  ArrayList<float[]> posTan;
  Canvas canvas;
  int[] startDate;
  int alpha;
  SurfaceHolder holder;

  DrawDate(ArrayList<float[]> posTan,SurfaceHolder holder,int[] startDate,int alpha) {

    this.posTan = posTan;
    this.startDate = startDate;
    this.alpha = alpha;
    this.holder = holder;
  }

  @Override
  public void run() {
    Paint paint = new Paint();
    paint.setAlpha(alpha);
    int[] current = startDate;
    for (int i = 0;i < posTan.size();i++) {
      String date = ""+current[0]+current[1]+current[2];
      canvas.drawText(date,posTan.get(i)[0],posTan.get(i)[1],paint);
      current = getProperDate(current[0],current[1],current[2]);
    }

  }

  private int[] getProperDate(int year,int month,int day) {

    int curMonDay = 30;
    switch (month) {
      case 1: case 3: case 5: case 7: case 8: case 10: case 12:
        curMonDay = 31;
        break;
      case 2:
        curMonDay = year%4 == 0 ? 29 : 28;
        break;
    }
    day++;
    if (day > curMonDay) {  //+1的日期后大于当月最大值
      day = 1;
      if ((month++) > 12) {
        year++;
        month = 1;
      }
    }
    return new int[]{year,month,day};
  }

}
