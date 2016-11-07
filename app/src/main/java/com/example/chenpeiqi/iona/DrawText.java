package com.example.chenpeiqi.iona;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;

class DrawText implements Runnable {

  private float[][] ta;
  private ArrayList<float[]> posTan;
  private SurfaceHolder holder;
  private Context context;
  private String content;
  private boolean gettingReply;
  private String time;
  private Paint background;
  private int cw, ch, s_i, p_i;
  int[] wh;
  Paint yel;

  DrawText(Context context,int s_i,int p_i,float[][] ta,SurfaceHolder holder,
      ArrayList<float[]> posTan,int cw,int ch) {
    this.ta = ta;
    this.context = context;
    this.holder = holder;
    this.posTan = posTan;
    this.cw = cw; this.ch = ch; this.s_i = s_i; this.p_i = p_i;

    wh = new int[]{cw,ch};
    yel = initPaint();
    background = new Paint();
    background.setColor(Color.BLACK);
  }

  @Override
  public void run() {
    try {
      new Thread(new CMT(context,this,s_i,p_i)).join();
      switch (time) {
        case "tomorrow":
          Iona.myHandler.sendEmptyMessage(1);
          draw(holder,content);
          break;
        case "today":
          Intent intent = new Intent(context,Diary.class);
          intent.putExtra("content",content);
          context.startActivity(intent);
          break;
        case "yesterday":
          draw(holder,content);
          break;
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void draw(SurfaceHolder holder,String content)
      throws InterruptedException {
    ArrayList<float[]> previous = new ArrayList<>();
    int chSta = 0;
    char[] conCh = content.toCharArray();
    int reLen = conCh.length;
    ArrayList<ArrayList<Integer>> temp = oTOT(calBelongingStep1(ta));
    int contentPos = drawWhere(ta) ? 1 : 2;
    for (int poVer = 0;poVer < 10;poVer++) {
      if (temp.get(poVer).contains(contentPos)) {
//      if (reLen == 0) break;
//      int posHor = ta[poVer][0] == wh[0] && ta[poVer][1] == 0 ? 0
//      : ta[poVer][0] > wh[0]/3 ? 1 : wh[0]-ta[poVer][0] > wh[0]/3 ? 2 : -1;
//      while (posHor < 3 && reLen != 0) {
        float areLen = contentPos == 1 ? ta[poVer][0]-200 : wh[0]-ta[poVer][1]-200;
        float staXCor = contentPos == 2 ? ta[poVer][1]+150 : 50;
        float staYCor = (int) (wh[1]*(poVer+0.75)/10);
        int count = Math.min(yel.breakText(conCh,chSta,reLen,areLen,null),reLen);
        for (int l = 0;l < count;l++) {
          Canvas canvas = holder.lockCanvas();
//          new Thread(new DrawFP(
//              context,holder,null,new int[]{cw,ch},posTan,false,false)).join();
          for (float[] temp0 : previous) {
            canvas.drawText(
                conCh,(int) temp0[2],(int) temp0[3],temp0[0],temp0[1],yel);
          }
          canvas.drawText(conCh,chSta,l,staXCor,staYCor,yel);
          holder.unlockCanvasAndPost(canvas);
        }
        previous.add(new float[]{staXCor,staYCor,chSta,count});
        chSta += count;
        reLen -= count;
//
//        if (posHor == 0) break;
//        posHor++;
//      }
      }
    }
  }

  private boolean drawWhere(float[][] ta) {
    return driverAndRobber(oTOT(calBelongingStep1(ta)));
  }

  private ArrayList<ArrayList<Integer>> calBelongingStep1(float[][] ta) {
    ArrayList<ArrayList<Integer>> result = new ArrayList<>();
    for (int i = 0;i < 10;i++) {
      ArrayList<Integer> current = new ArrayList<>();
      if (ta[i][0] == wh[0] && ta[i][1] == 0) {
        current.add(0);
      } else {
        if (ta[i][0] > wh[0]/3) current.add(1);
        if (wh[0]-ta[i][0] > wh[0]/3) current.add(2);
      }
      result.add(current);
    }
    return result;
  }

  private ArrayList<ArrayList<Integer>> oTOT(ArrayList<ArrayList<Integer>> a) {
    for (int i = 0;i < 10;i++) {
      int temp;
      if ((temp = a.get(i).get(0)) != 0) {
        for (int j = 0;j < i;j++) {
          a.get(j).clear();
          a.get(j).add(temp);
        }
        break;
      }
    }
    for (int i = 9;i > -1;i--) {
      int temp;
      if ((temp = a.get(i).get(0)) != 0) {
        for (int j = 9;j > i;j--) {
          a.get(j).clear();
          a.get(j).add(temp);
        }
        break;
      }
    }
    return a;
  }

  private boolean driverAndRobber(ArrayList<ArrayList<Integer>> whole) {
    int oneCounter = 0, twoCounter = 0;
    for (ArrayList<Integer> temp : whole) {
      if (temp.contains(1)) oneCounter++;
      if (temp.contains(2)) twoCounter++;
    }
    return oneCounter > twoCounter;
  }

  private int[] getWidthHeight(SurfaceHolder holder) {
    Canvas canvas = holder.lockCanvas();
    int width = canvas.getWidth();
    int height = canvas.getHeight();
    holder.unlockCanvasAndPost(canvas);
    return new int[]{width,height};
  }

  private Paint initPaint() {
    Paint yel = new Paint();
    yel.setColor(Color.LTGRAY);
    yel.setTextSize(50);
    return yel;
  }

  void setGettingReply(boolean gettingReply) {
    this.gettingReply = gettingReply;
  }

  void setContent(String content) {
    this.content = content;
  }

  void setTime(String time) {
    this.time = time;
  }
}