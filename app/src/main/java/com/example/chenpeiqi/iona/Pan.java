package com.example.chenpeiqi.iona;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created on 16/4/19.
 */
class Pan extends SurfaceView implements SurfaceHolder.Callback,
    View.OnTouchListener {

  //构造方法参数
  private Bundle bundle;
  private Context context;
  private static final int m = Context.MODE_PRIVATE;
  private static final String tag = "Pan";

  //其他线程修改参数
  private int cw, ch;
  private boolean existed, gettingReply;
  private Path path;
  private ArrayList<float[]> pos_tan;
  private float[][] ta;
  private GestureDetector gestureDetector;
  private SurfaceHolder holder;

//   private enum Location {
//      TOP_LEFT(false,false),
//      TOP_RIGHT(false,true),
//      BOT_LEFT(true,false),
//      BOT_RIGHT(true,true);
//
//      boolean vertical;
//      boolean horizontal;
//
//      Location(boolean a,boolean b) {
//         this.vertical = a;
//         this.horizontal = b;
//      }
//
//   }

  Pan(Context context,Bundle bundle) {
    super(context);
    Log.i("iona","————————PanGur.onCreate()————————");
    this.context = context;
    this.bundle = bundle;
    pos_tan = new ArrayList<>();
    getHolder().addCallback(this);
    this.setOnTouchListener(this);
  }

  void setExisted(boolean existed) {
    this.existed = existed;
  }

  void setPos_tan(ArrayList<float[]> pos_tan) {
    this.pos_tan = pos_tan;
  }

  void setTA(float[][] ta) {
    this.ta = ta;
  }

  void setGettingReply(boolean gettingReply) {
    this.gettingReply = gettingReply;
  }

  void setPath(Path path) {
    this.path = path;
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    this.holder = holder;
    initSize(holder);
    int[] size = new int[]{cw,ch};
    new Thread(new CMT(context,Iona.s_i,this)).start();
    for (int i = 0;i < 100;i++) {
      if (gettingReply) {
        if (!existed) {
          path = createPath();
          ta = calTA(getAll(path),new int[]{cw,ch});
          pos_tan = initPosTan(path);
        }
        int[] wh = new int[]{cw,ch};
        Draw drawFootprints = new Draw(context,holder,wh,pos_tan);
        new Thread(drawFootprints).start();

        break;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Path createPath() {
    float staX = bundle.getFloat("x");
    float staY = bundle.getFloat("y");
    int u = Math.random() > 0.5 ? 1 : 0;
    int m = staY > ch/2 ? 0 : 1;
    int r = staX > cw/2 ? 0 : 1;
    float[][] x = {{0,cw},{(float) Math.random()*cw/4+cw/10,
        (float) Math.random()*cw/4+cw*3/4-cw/10}};
    float[][] y = {{(float) Math.random()*ch/4+ch/10,
        (float) Math.random()*ch/4+ch*3/4-ch/10},{0,ch}};
    float endX = x[u][r];
    float endY = y[u][m];
    float[] cp = initCP(staX,staY,endX,endY);

    path = initPath(staX,staY,cp[0],cp[1],cp[2],cp[3],endX,endY);

    String direction =
        (endX == 0 ? "left" : endX == cw ? "right" : endY == 0 ? "up" : "down");

    float[] temp = new float[]{staX,staY,endX,endY,cp[0],cp[1],cp[2],cp[3]};
    new Thread(new CMT(context,temp,Iona.s_i)).start();

    float[] end = new float[]{endX,endY};
    ((MyFrag.PassCord) context).passCord(end,cw,ch);
    ((MyFrag.PassDire) context).passDire(direction);

    return path;
  }

  float[] initCP(float sta_x,float sta_y,float end_x,float end_y) {
    float[] result = new float[4];
    int hw = cw/2, hh = ch/2;
    int[][] haha = new int[][]{new int[]{1,2},new int[]{3,4}};
    int first = haha[sta_x < hw ? 0 : 1][sta_y < hh ? 0 : 1];
    int second = haha[end_x < hw ? 0 : 1][end_y < hh ? 0 : 1];
    int[] whole = new int[]{1,2,3,4};
    int[] which_two = new int[2];
    int counter = 0;
    for (int temp : whole) {
      if (temp != first && temp != second) {
        which_two[counter++] = temp;
      }
    }
    int xi = 0, yi = 1;
    for (int i = 0;i < 2;i++) {  //循环两次当然是因为有两个数辣
      for (int tt : which_two) {
        switch (tt) {  //switch第一次是为了初始化x坐标
          case 1: case 3:
            result[xi+2*i] = (float) (hw+hw*Math.random());
            break;
          case 2: case 4:
            result[xi+2*i] = (float) (hw*Math.random());
        }
        switch (tt) {  //自然而然地，switch第二次是为了y坐标辣
          case 1: case 2:
            result[yi+2*i] = (float) (hh+hh*Math.random());
            break;
          case 3: case 4:
            result[yi+2*i] = (float) (hh*Math.random());
        }
      }
    }
    return result;
  }

  static Path initPath(float staX,float staY,float c1X,float c1Y,
      float c2X,float c2Y,float endX,float endY) {
    Path path = new Path();
    path.moveTo(staX,staY);
    path.cubicTo(c1X,c1Y,c2X,c2Y,endX,endY);
    return path;
  }

  private ArrayList<float[]> initPosTan(Path path) {
    ArrayList<float[]> temp = new ArrayList<>();
    int totalDistance = 50;
    PathMeasure pathMeasure = new PathMeasure(path,false);
    float length = pathMeasure.getLength();
    JSONArray allSteps = new JSONArray();
    boolean sod = true;//第单数还是复数个脚印
    float[] last = {-50,-50};
    while (totalDistance < length) {
      float[] pos_t = new float[2], tan_t = new float[2];
      pathMeasure.getPosTan(totalDistance,pos_t,tan_t);
      int delta = (int) (150+(Math.random()-0.5)*100);
      totalDistance = totalDistance+delta;
      pos_t = trans(pos_t,tan_t,sod,last);
      last = pos_t;
      sod = !sod;
      float[] singleFP = merge(pos_t,tan_t);
      temp.add(singleFP);
      try {//数据类型不同不能直接转换
        JSONArray oneStep = new JSONArray(singleFP);
        allSteps.put(oneStep);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    new Thread(new CMT(context,allSteps,0)).start();
    return temp;
  }

  private float[] trans(float[] pos,float[] tan,boolean sod,float[] last) {
    float[] res = new float[2];

    float ap = -tan[0]/tan[1], bp = pos[1]-ap*pos[0];
    float a = ap*ap+1, b = 2*(ap*bp-ap*pos[1]-pos[0]),
        c = (bp-pos[1])*(bp-pos[1])+pos[0]*pos[0]-2500;
    float x1 = (float) (-b+Math.sqrt(b*b-4*a*c))/(2*a);
    float x2 = (float) (-b-Math.sqrt(b*b-4*a*c))/(2*a);
    float y1 = ap*x1+bp, y2 = ap*x2+bp;


//    if (sod) {
//      res[0] = pos[0] > cw-50 ? cw-50 : pos[0];
//      res[1] = pos[1] > ch-50 ? ch-50 : pos[1];
//    } else {
//      res[0] = pos[0] < 100 ? 100 : pos[0]-50;
//      res[1] = pos[1] < 100 ? 100 : pos[1]-50;
//    }
//    if (crush(res,last)) {
//      String[] t = getLoc(res[1] > last[1],res[0] > last[0]).split("_");
//      if (Math.random()-0.5 > 0) {
//        switch (t[0]) {
//          case "t": res[0] = last[0]-100; break;
//          case "b": res[1] = last[1]+100; break;
//        }
//      } else {
//        switch (t[1]) {
//          case "l": res[1] = last[1]-100; break;
//          case "r": res[1] = last[1]+100; break;
//        }
//      }
//    }
    if (sod) {
      return new float[]{cureX(x1),cureY(y1)};
    } else {
      return new float[]{cureX(x2),cureY(y2)};
    }
  }

  private float cureX(float x) {
    float a = Math.min(x,cw-25);
    a = Math.max(25,a);
    return a;
  }

  private float cureY(float y) {
    float a = Math.min(y,ch-25);
    a = Math.max(25,a);
    return a;
  }

  private String getLoc(boolean a,boolean b) {
    return (a ? "b" : "t")+"_"+(b ? "r" : "l");
  }

  private boolean crush(float[] last,float[] current) {

    boolean temp = true;
    for (int i = 0;i < 2;i++)
      temp &= pre_crush(last[i],current[i]);
    return temp;
  }

  private boolean pre_crush(float pre,float cur) {
    return cur > pre-50 && cur < pre+50;
  }

  private float[] merge(float[] a,float[] b) {
    int c1 = a.length;
    int c2 = b.length;
    int c3 = c1+c2;
    float[] res = new float[c3];
    for (int i = 0;i < c1;i++) {
      res[i] = a[i];
      res[c1+i] = b[i];
    }
    return res;
  }

  static float[][] getAll(Path path) {

    PathMeasure pathMeasure = new PathMeasure(path,false);
    int length = (int) pathMeasure.getLength();   //测量路径长度
    float[][] positions = new float[length][2];
    for (int i = 0;i < length;i++) {
      float[] cord = new float[2];
      float[] tan = new float[2];
      if (pathMeasure.getPosTan(i,cord,tan)) {
        positions[i][0] = cord[0];
        positions[i][1] = cord[1];
      }
    }
    return positions;
  }

  static float[][] calTA(float[][] AOP,int[] size) {
    int width = size[0], height = size[1];
    float[][] acd = new float[10][2];//储存text area左右界
    for (int j = 0;j < 10;j++) {
      acd[j][0] = width;
      acd[j][1] = 0;
    }
    int length = AOP.length;
    for (int i = 0;i < length;i++) {
      int a = (int) AOP[i][1]*10/height;
      a = a == 10 ? 9 : a;
      acd[a][0] = AOP[i][0] < acd[a][0] ? AOP[i][0] : acd[a][0];
      acd[a][1] = AOP[i][0] > acd[a][1] ? AOP[i][0] : acd[a][1];
    }
    return acd;
  }

  void initSize(SurfaceHolder holder) {
    Canvas canvas = holder.lockCanvas();
    cw = canvas.getWidth();
    ch = canvas.getHeight();
    holder.unlockCanvasAndPost(canvas);
    if (Iona.s_i == 0) {
      new Thread(new CMT(context,new int[]{cw,ch})).start();
    }
  }

  static String calDir(Path path,int[] wah) {
    PathMeasure pathMeasure = new PathMeasure(path,false);
    float length = pathMeasure.getLength();
    float[] pos = new float[2];
    pathMeasure.getPosTan(length,pos,null);
    String result = pos[0] > -1 && pos[0] < 1 ? "left"
        : pos[0] > wah[0]-1 && pos[0] < wah[0]+1 ? "right"
            : pos[1] > -1 && pos[1] < 1 ? "up" : "down";
    return result;
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {}

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {}

  @Override
  public boolean onTouch(View v,MotionEvent event) {
    float touchX = event.getX(), touchY = event.getY();
    int length = pos_tan.size();
    double a = 37.5;
    for (int i = 0;i < length;i++) {
      if (touchX > pos_tan.get(i)[0]-a && touchX < pos_tan.get(i)[0]+a
          && touchY > pos_tan.get(i)[1]-a && touchY < pos_tan.get(i)[1]+a) {
        new Thread(
            new DrawText(context,Iona.s_i,i,ta,holder,pos_tan,cw,ch)).start();
      }
    }
    return false;
  }

  interface PassSomething {
    void passThread(Draw drawDate);
  }

}