package com.example.chenpeiqi.iona;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created on 16/4/19.
 */
class ThisViewOfMine extends SurfaceView implements SurfaceHolder.Callback,
        View.OnTouchListener {

    private Bundle bundle;
    private Context context;
    private ArrayList<float[]> pos_tan;
    private SurfaceHolder holder;
    private float[][] ta;
    private int cw;
    private int ch;
    private KittyFootprint kittyFootprint;

    ThisViewOfMine(Context context, Bundle bundle) {
        super(context);
        this.context = context;
        this.bundle = bundle;
        getHolder().addCallback(this);
        this.setOnTouchListener(this);
        CMT.counter = 0;
    }

    //计算起终点,控制点,路径,着脚点均在此方法中进行,返回一条包含有所有点数据的ArrayList
    private void getPath(SurfaceHolder holder) {

        pos_tan = new ArrayList<>();
        JSONArray allSteps = new JSONArray();


        //通过holder获得画布的宽度和高度
        Canvas canvas = holder.lockCanvas();
        cw = canvas.getWidth();
        ch = canvas.getHeight();
        holder.unlockCanvasAndPost(canvas);

        //设置起点
        float staX = bundle.getFloat("x");
        float staY = bundle.getFloat("y");

        //设置三个指示器,根据指示器从二维数组中获得终点
        int u = Math.random() > 0.5 ? 1 : 0;
        int m = staY > ch / 2 ? 0 : 1;
        int r = staX > cw / 2 ? 0 : 1;
        float[][] x = {{0, cw}, {(float) Math.random() * cw / 4 + cw / 10,
                (float) Math.random() * cw / 4 + cw * 3 / 4 - cw / 10}};
        float[][] y = {{(float) Math.random() * ch / 4 + ch / 10,
                (float) Math.random() * ch / 4 + ch * 3 / 4 - ch / 10}, {0, ch}};
        float endX = x[u][r];
        float endY = y[u][m];
        float[] temp4 = {endX, endY};

        //生成path
        Path path = new Path();
        path.moveTo(staX, staY);
        float con1_x, con1_y, con2_x, con2_y;
        path.cubicTo(con1_x = (float) Math.random() * cw,
                con1_y = (float) Math.random() * ch,
                con2_x = (float) Math.random() * cw,
                con2_y = (float) Math.random() * ch, endX, endY);
        new Thread(new CMT(context, "path", staX, staY, endX, endY,
                con1_x, con1_y, con2_x, con2_y,cw,ch,Iona.screen_indicate)).start();

        //获取当前屏幕走向,写入SharedPreference
        String direction = (endX == 0 ? "left" :
                endX == cw ? "right" : endY == 0 ? "up" : "down");
        context.getSharedPreferences("direction", Context.MODE_PRIVATE).edit().
                putString("direction", direction).apply();

        float[][] temp = getAll(path);
        ta = calculateTextArea(temp, cw, ch);

        //根据path生成着脚点,位置方向均存在在链表中
        int totalDistance = 0;
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();
        boolean fagot = true;
        while (totalDistance < length) {
            float[] pos_t = new float[2];
            float[] tan_t = new float[2];
            pathMeasure.getPosTan(totalDistance, pos_t, tan_t);
            int deltaDistance = (int) (150 + (Math.random() - 0.5) * 100);
            totalDistance = totalDistance + deltaDistance;
            if (fagot) {
                fagot = false;
            } else {
                pos_t = trans(pos_t);
                fagot = true;
            }
            float[] singleFP = merge(pos_t, tan_t);
            pos_tan.add(singleFP);

            try {
                JSONArray oneStep = new JSONArray(singleFP);
                allSteps.put(oneStep);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        new Thread(new CMT(context, "footprint", allSteps,0)).start();
        ((ThisFragmentOfMine.PassCord) context).passCord(temp4, cw, ch);
        ((ThisFragmentOfMine.PassDire) context).passDire(direction);
    }

    //对所有偶数的脚印进行往屏幕左上方进行位移,位移后的坐标储存在pos_tan中
    private float[] trans(float[] original) {
        float[] cinderella = new float[2];
        int i = 0;
        while (i < 2) {
            cinderella[i] = original[i] - 50;
            i++;
        }
        return cinderella;
    }

    //合并由getPosTan初始化的位置以及方向数组,整合后储存在pos_tan中
    private float[] merge(float[] a, float[] b) {
        int c1 = a.length;
        int c2 = b.length;
        int c3 = c1 + c2;
        float[] cinderella = new float[c3];
        int i = 0;
        while (i < c1) {
            cinderella[i] = a[i];
            cinderella[c1 + i] = b[i];
            i++;
        }
        return cinderella;
    }

    //此方法用于获取Path上的所有点
    private float[][] getAll(Path path) {
        int i = 0;
        PathMeasure pathMeasure = new PathMeasure(path, false);
        int length = (int) pathMeasure.getLength();   //测量路径长度
        float[][] positions = new float[length][2]; //建立数组储存x坐标和y坐标
        while (i < length) { //遍历路径,将x坐标储存在0的位置,将y坐标储存在1的位置
            float[] cord = new float[2];
            float[] tan = new float[2];
            if (pathMeasure.getPosTan(i, cord, tan)) {
                positions[i][0] = cord[0];
                positions[i][1] = cord[1];
            }
            i++;
        }
        return positions;
    }

    //此方法用于确定各text area的右界和左界
    private float[][] calculateTextArea(float[][] AOP, int width, int height) {

        float[][] acd = new float[10][2];  //用于储存10个text area左右界的数组
        int j = 0;
        while (j < 10) {
            acd[j][0] = width;
            acd[j][1] = 0;
            j++;
        }
        int length = AOP.length;
        int i = 0;
        while (i < length) {
            int ind = (int) AOP[i][1] * 10 / height;
            ind = ind == 10 ? 9 : ind;
            acd[ind][0] = AOP[i][0] < acd[ind][0] ? AOP[i][0] : acd[ind][0];
            acd[ind][1] = AOP[i][0] > acd[ind][1] ? AOP[i][0] : acd[ind][1];
            i++;
        }
        return acd;
    }

    //surface三人组
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        new Thread(new DrawSomething()).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    //点击脚印事件判断及其处理
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        int length = pos_tan.size();
        int i = 0;
        while (i < length) {
            if (touchX > pos_tan.get(i)[0] && touchX < pos_tan.get(i)[0] + 50 &&
                    touchY > pos_tan.get(i)[1] && touchY < pos_tan.get(i)[1] + 50) {
                //事件处理代码
                new Thread(new DrawText()).start();
            }
            i++;
        }
        return false;
    }

    private class DrawSomething implements Runnable {

        @Override
        public void run() {
            Log.i("iona","draw something starting to run");
            getPath(holder);
            int i = 0;
            Log.i("iona","pos_tan.size():"+pos_tan.size());
            while (i < pos_tan.size()) {
                int j = 0;
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.DKGRAY);
                while (j <= i) {
                    float x = pos_tan.get(j)[0];
                    float y = pos_tan.get(j)[1];

                    kittyFootprint = new KittyFootprint(context, x, y);
                    kittyFootprint.draw(canvas);
                    j++;
                }

                holder.unlockCanvasAndPost(canvas);
                i++;
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DrawText implements Runnable {

        String temp = "abcdefghijklmnopqrstuvwxyz";
        char[] test_text;
        char[] content;
        int char_cou;
        Paint yellow;
        int char_sta;
        int length;
        int haveFun;
        float makeItRight;

        DrawText() {

            content = "2016.5.3".toCharArray();
            length = content.length;
            haveFun = -1;
            yellow = new Paint();
            yellow.setColor(Color.LTGRAY);
            yellow.setAlpha(80);
            yellow.setTextSize(100);
            makeItRight = (float) 0.8;
            int i = -1;
            while (i++ < 2) {
                temp += temp;
            }
            test_text = temp.toCharArray();
        }

        @Override
        public void run() {
            Canvas canvas = holder.lockCanvas();
            kittyFootprint.draw(canvas);
            biggerDrawInOne(canvas);
            holder.unlockCanvasAndPost(canvas);
        }

        //全部写出来
        private void biggerDrawInOne(Canvas canvas) {
            int POS = -1;
            int for_POS = 0;
            int for_DIR = 0;
            while (POS++ < 9) {
                int DIR = ta[POS][0] == cw && ta[POS][1] == 0 ? 0 : CIB(POS, 1) ? 1 :
                        CIB(POS, 2) ? 2 : -1;
                if (DIR != -1) {
                    drawInOne(POS, DIR, canvas);
                    for_POS = POS;
                    for_DIR = DIR;
                    break;
                }
            }
            POS = -1;
            while (POS++ < 9) {
                int DIR = ta[POS][0] == cw && ta[POS][1] == 0 ? 0 :
                        ta[POS][0] > cw / 3 ? 1 : cw - ta[POS][0] > cw / 3 ? 2 : -1;
                while (DIR < 3) {
                    if ((DIR != for_DIR && POS != 0) || POS != for_POS) {
                        drawInOne(POS, DIR, canvas);
                    }
                    if (DIR == 0) {
                        break;
                    }
                    DIR++;
                }
            }
        }

        private void drawInOne(int POS, int DIR, Canvas canvas) {
            float textLength = DIR == 0 ? ta[POS][0] :
                    DIR == 1 ? ta[POS][0] - 50 : cw - ta[POS][1] - 100;
            float staXCor = DIR == 2 ? ta[POS][1] + 100 : 0;
            float staYCor = (int) (ch * (POS + 0.75) / 10);
            char_cou = yellow.breakText(content, char_sta, length, textLength, null);
            canvas.drawText(content, char_sta, char_cou, staXCor, staYCor, yellow);
            char_sta += char_cou;
            length = length - char_cou; //fuck,就是介个小婊砸搞得劳资程序crush一天
            if (length == 0) {
                content = test_text;
                length = content.length;
                char_sta = 0;
                yellow.setTextSize(60);
            }
        }

        private boolean CIB(int POS, int DIR) {
            float textLength = DIR == 0 ? ta[POS][0] :
                    DIR == 1 ? ta[POS][0] - 150 : cw - ta[POS][1] - 150;
            char_cou = yellow.breakText(content, char_sta, length, textLength, null);
            return char_cou >= content.length;
        }
    }
}