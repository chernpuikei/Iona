package com.example.chenpeiqi.iona;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created on 16/3/25.
 */
class CMT implements Runnable {

  private Context context;
  private String requestType, email, poc, password, updateInfo,
      zzz, nickname, selfIntro;
  private int s_i, p_i, width, height;
  private float[] path;
  private JSONArray pos_tan;//只用于给服务器发送数据
  private Pan pan;
  private DrawText drawText;
  private final static int m = Context.MODE_PRIVATE;
  private final static String tag = "CMT";

  //检查注册还是登陆状态使用此构造
  CMT(Context context,String email) {
    this.context = context;
    this.email = email;
    this.requestType = "check";//check完直接将
  }

  //注册登陆提交日志共用此构造
  CMT(Context context,String email,String reqType,String poc) {
    this.context = context;
    this.requestType = reqType;
    this.email = email;
    this.poc = poc;
  }

  CMT(Context context,int[] wah) {
    this.context = context;
    this.width = wah[0];
    this.height = wah[1];
    this.requestType = "initScreenSize";
  }

  CMT(Context context,String email,String psw,String nick,String selfInt) {
    this.context = context;
    this.email = email;
    this.password = psw;
    this.nickname = nick;
    this.selfIntro = selfInt;
    this.requestType = "register";
  }

  CMT(Context context,DrawText drawText,int s_i,int p_i) {
    Log.i("CMT#request_content","constructor");
    this.context = context;
    this.requestType = "request_content";
    this.drawText = drawText;
    this.s_i = s_i;
    this.p_i = p_i;
    run();
  }

  CMT(Context context,int s_i,int p_i) {
    this.context = context; this.s_i = s_i; this.p_i = p_i;
    this.requestType = "requestDate";
  }

  //四点,此构造器用于发送path
  CMT(Context context,float[] path,int s_i) {
    this.context = context;
    this.requestType = "path";
    this.path = path;
    this.s_i = s_i;
  }

  CMT(Context context,int s_i,Pan pan) {
    this.context = context;
    this.requestType = "request_footprints";
    Log.i(tag,"requestType:"+ requestType);
    this.s_i = s_i;
    this.pan = pan;
  }

  //屏幕标记从客户端发送,服务器接收到pos_tan以后,在解析阶段对点进行标记
  CMT(Context context,JSONArray pos_tan,int s_i) {
    this.context = context;
    this.requestType = "writing_footprint";
    this.pos_tan = pos_tan;
    this.s_i = s_i;
  }
//endregion

  @Override
  public void run() {
    Log.i(tag,"run()"+requestType);
    Socket socket;
    try {
//      socket = new Socket("192.168.43.182",2000); r_w(socket);//cp wifi host
      socket = new Socket("10.0.3.2",2000); r_w(socket);//genymotion
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void r_w(Socket socket) throws Exception {
    request(socket);
    getReply(socket);
    socket.close();
  }

  private void request(Socket socket) throws Exception {
    //发送数据部分
    JSONObject requestJson = new JSONObject();
//    email = context.getSharedPreferences("l_i",m)
//        .getString("email",Login.email_widget.getText().toString());
    email = "aaa";
    requestJson.put("email",email).put("requestType",requestType);
    //非必选部分,在构造期间加入con_i以表明使用哪个构造器
    //再用switch-case语句用con_i指明哪些东西需要放入requestJson,
    //从而用con_i作为中间变量在总体上实现构造器-请求结构的闭环
    switch (requestType) {
      //check,login,register
      case "login":
        requestJson.put("poc",poc);
        break;
      case "initScreenSize":
        requestJson.put("width",width).put("height",height);
        break;
      case "register":
        requestJson.put("password",password).put("nickname",nickname)
            .put("self_intro",selfIntro);
        break;
      case "path":
        requestJson.put("s_i",s_i).put("staX",path[0]).put("staY",path[1])
            .put("endX",path[2]).put("endY",path[3]).put("c1X",path[4])
            .put("c1Y",path[5]).put("c2X",path[6]).put("c2Y",path[7]);
        break;
      case "writing_footprint":
        requestJson.put("pos_tan",pos_tan).put("s_i",Iona.s_i);
        break;
      case "content":
        requestJson.put("content",poc);
        break;
      case "request_content":
        requestJson.put("s_i",s_i).put("p_i",p_i);
        Log.i("request_content-JSON",requestJson+"");
        break;
      case "request_footprints":
        requestJson.put("which_path",s_i);
        break;
      case "updateInfo":
        requestJson.put("which",zzz).put("updateInfo",updateInfo);
        break;
    }
    BufferedWriter bufferedWriter = new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream()));
    bufferedWriter.write(requestJson.toString());
    Log.i("CMT","sending:"+requestJson.getString("requestType"));
    bufferedWriter.flush();
  }

  private void getReply(Socket socket) throws Exception {
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String temp;
    if ((temp = bufferedReader.readLine()) != null) {
      JSONObject resJS = new JSONObject(temp);
      String respondType = resJS.getString("respondType");
      Log.i("respond",respondType);
      switch (respondType) {
        case "check":
          Login.ACT = resJS.getBoolean("value") ? "login" : "register";
          Login.handler.sendEmptyMessage(
              resJS.getBoolean("value") ? Login.SHOW_LOGIN : Login.SHOW_REGIS);
          break;
        case "login":
          if (!resJS.getString("value").equals("nah")) {
            SharedPreferences l_i = context.getSharedPreferences("l_i",m);
            l_i.edit().putString("email",email).apply();
            context.startActivity(new Intent(context,Iona.class));
          }
          break;
        case "register":
          context.getSharedPreferences("l_i",m).edit().putString(
              "email",Login.email_widget.getText().toString()).apply();
          if (resJS.getBoolean("value")) {
            context.startActivity(new Intent(context,Iona.class));
          }
          break;
        case "record":
          if (resJS.getBoolean("value"))
            context.startActivity(new Intent(context,Iona.class));
          break;
        case "request_content":
          drawText.setContent(jaToString(resJS.getJSONArray("value")));
          Log.i("CMT","resJS.getValue"+resJS.getString("value"));
          drawText.setTime(resJS.getString("time"));
          Log.i("CMT#request_content",resJS.getString("time"));
          drawText.setGettingReply(true);
          break;
        case "writing_footprints":
          break;
        case "path":
          break;
        case "request_footprints":
          ArrayList<float[]> temp_arr = new ArrayList<>();//pos_tan
          if (resJS.getBoolean("existed")) {
            JSONArray fp = resJS.getJSONArray("footprints");
            float staX = (float) resJS.getDouble("staX");
            float staY = (float) resJS.getDouble("staY");
            float endX = (float) resJS.getDouble("endX");
            float endY = (float) resJS.getDouble("endY");
            float c1X = (float) resJS.getDouble("c1X");
            float c1Y = (float) resJS.getDouble("c1Y");
            float c2X = (float) resJS.getDouble("c2X");
            float c2Y = (float) resJS.getDouble("c2Y");
            int width = resJS.getInt("width");
            int height = resJS.getInt("height");
            JSONArray date = resJS.getJSONArray("date");
            int year = date.getInt(0);
            int month = date.getInt(1);
            int day = date.getInt(2);
            Iona.date = new int[]{year,month,day};

            Path path = Pan.initPath(staX,staY,c1X,c1Y,c2X,c2Y,endX,endY);
            float[] end = new float[]{endX,endY};
            ((MyFrag.PassCord) context).passCord(end,width,height);
            int length = fp.length();
            for (int i = 0;i < length;i++) {//init pos_tan
              JSONObject jObj = fp.getJSONObject(i);
              float posX = (float) jObj.getDouble("posX");
              float posY = (float) jObj.getDouble("posY");
              float tan1 = (float) jObj.getDouble("tan1");
              float tan2 = (float) jObj.getDouble("tan2");
              float[] tempStep = {posX,posY,tan1,tan2};
              temp_arr.add(tempStep);
            }
            pan.setExisted(true);
            pan.setPos_tan(temp_arr);
            pan.setPath(path);
            pan.setTA(Pan.calTA(Pan.getAll(path),new int[]{width,height}));
            Iona.direction = Pan.calDir(path,new int[]{width,height});
            Log.i("Iona.direction@CMT",Iona.direction);
          }
          pan.setGettingReply(true);
          break;
      }
    }
  }

  private String jaToString(JSONArray ja) throws JSONException {
    String totalTemp = "";
    int length = ja.length();
    for (int i = 0;i < length;i++) {
      totalTemp += ja.getString(i);
    }
    return totalTemp;
  }

}