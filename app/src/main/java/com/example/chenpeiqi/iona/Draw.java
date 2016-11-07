package com.example.chenpeiqi.iona;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.view.SurfaceHolder;

import java.util.ArrayList;

/**
 * Created on 16/9/8.
 */
class Draw implements Runnable {

  private Context context;
  private ArrayList<float[]> pos_tan;
  private int duration;
  private int[] wh;
  private Bitmap footprint;
  private static int delta;
  private Draw drawDate;
  private SurfaceHolder holder;
  private int[] firstDay;
  private boolean animating, showDate;

  Draw(Context context,SurfaceHolder holder,int[] wh,ArrayList<float[]> pt) {
    this.context = context;
    this.holder = holder;
    this.pos_tan = pt;
    this.wh = wh;
    footprint = sample(context.getResources(),R.drawable.footprint,25,25);
    this.duration = 25*pos_tan.size()+75;
  }

  public void setAnimating(boolean animating) {
    this.animating = animating;
  }

  @Override
  public void run() {
    int[] area = new int[]{0,25,50,75};
    Paint paint = new Paint();
    int maxSize = pos_tan.size();
    float dota = calDelta(calWholeSinA(pos_tan),pos_tan.get(0)[1] > wh[1]/2);
    if (animating) {
      for (int i = 0;i < duration;i += 3) {
        Canvas canvas = holder.lockCanvas();
        canvas.drawRGB(43,43,43);
        //第几个fp(in4)的第几个
        int[] a = new int[]{i/100,(i-25)/100,(i-50)/100,(i-75)/100};
        //alpha计算因子
        int[] b = new int[]{i%100,(i-25)%100,(i-50)%100,(i-75)%100};
        int iPosTan;//用于指明在数组中的绝对位置
        for (int j = 0;j < 4;j++) {
          if (i > area[j] && i < duration && (iPosTan = a[j]*4+j) < maxSize) {
            paint.setAlpha(100-Math.abs(b[j]-50)*2);
            float x = pos_tan.get(iPosTan)[0], y = pos_tan.get(iPosTan)[1],
                t = pos_tan.get(iPosTan)[2], g = pos_tan.get(iPosTan)[3];
            Matrix matrix = new Matrix();
            matrix.setTranslate(x-delta,y-delta);
            float degrees = g > 0 ? (float) (Math.toDegrees(Math.acos(t))+dota)
                : (float) (360-(Math.toDegrees(Math.acos(t)))+dota);
            matrix.postRotate(degrees,x,y);
            canvas.drawBitmap(footprint,matrix,paint);
          }
        }
        holder.unlockCanvasAndPost(canvas);
      }
    }

    for (int k = animating ? 0 : 100;k < 101;k += 10) { //全体渐显
      if (showDate) {
        for (int i = 0;i < 100;i++) {
          new Thread(new DrawDate(pos_tan,holder,Iona.date,i)).start();
        }
      }
      paint.setAlpha(k);
      Canvas canvas = holder.lockCanvas();
      canvas.drawRGB(43,43,43);
      for (int l = 0;l < maxSize;l++) {
        float x = pos_tan.get(l)[0], y = pos_tan.get(l)[1],
            t = pos_tan.get(l)[2], g = pos_tan.get(l)[3];

        Matrix matrix = new Matrix();
        float a = (float) (Math.toDegrees(Math.acos(t)));
        float degrees = g > 0 ? a+dota : 360-a+dota;
        matrix.setTranslate(x-delta,y-delta);
        matrix.postRotate(degrees,x,y);
        canvas.drawBitmap(footprint,matrix,paint);
      }
      holder.unlockCanvasAndPost(canvas);
    }
  }

  private static Bitmap sample(Resources res,int resId,int reqWidth,int reqHeight) {

    Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.footprint);

    //First decode with inJustDecodeBounds=true to check dimensions
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res,resId,options);

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
    options.inSampleSize = 3;
    BitmapFactory.decodeResource(res,resId,options);


    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    Bitmap result = BitmapFactory.decodeResource(res,resId,options);
    return result;
  }

  private float[][] getTanStaEnd(float x,float y,float a,float g) {
    float[][] result = new float[4][4];
    float t = a/g;
    int indicator = 0;
    float b = y-t*x;
    if (b >= 0 && b <= wh[1]) {
      result[indicator][0] = 0;
      result[indicator++][1] = b;
    }
    float temp = t*wh[0]+b;
    if (temp >= 0 && temp <= wh[1]) {
      result[indicator][0] = wh[0];
      result[indicator++][1] = temp;
    }
    temp = -b/t;
    if (temp >= 0 && temp <= wh[0]) {
      result[indicator][0] = temp;
      result[indicator++][1] = 0;
    }
    temp = (wh[1]-b)/t;
    if (temp >= 0 && temp <= wh[0]) {
      result[indicator][0] = temp;
      result[indicator][1] = wh[1];
    }
    return result;
  }

  private static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight) {
    // Raw height and width of image
    final int h = options.outHeight;
    final int w = options.outWidth;
    int inSampleSize = 1;

    while ((h/inSampleSize) > reqHeight && (w/inSampleSize) > reqWidth) {
      inSampleSize++;
    }

    delta = h/inSampleSize;
    return inSampleSize;
  }

  private float calDelta(boolean a,boolean b) {
    if ((a && b) || ((!a) && (!b))) return -90;
    return 90;
  }

  private boolean calWholeSinA(ArrayList<float[]> pt) {
    float whole = 0;
    for (float[] temp : pt) {
      whole += (float) Math.toDegrees(
          Math.asin(temp[3]));
    }
    return whole > 0;
  }

}
