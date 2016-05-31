package com.example.chenpeiqi.iona;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created on 16/3/25.
 */
class CMT implements Runnable {

    private Context context;
    private String requestType;
    private String email;
    private String poc;
    private float[] path;
    static int counter;
    private JSONArray pos_tan;
    private int width;
    private int height;
    private int screen_num;
    private int point_num;
    private String y;
    private String m;
    private String d;

    //region Constructors

    //确认发送email,注册登录发送password均在此构造器中进行
    //发送编辑内容时,日期由服务器生成并比对day_record表中的日期并插入至当天的footprint中
    CMT(Context context, String requestType, String email, String poc) {
        this.context = context;
        this.requestType = requestType;
        this.email = email;
        this.poc = poc;
    }

    CMT(Context context,String requestType,String y,String m,String d){
        this.context = context;
        this.requestType = requestType;
        this.y = y;
        this.m = m;
        this.d = d;
    }

    //四点,此构造器用于发送path
    CMT(Context context, String requestType, float staX, float staY,
        float endX, float endY, float c1X, float c1Y, float c2X, float c2Y,
        int width, int height,int screen_num) {
        this.context = context;
        this.requestType = requestType;
        path = new float[8];
        path[0] = staX;
        path[1] = staY;
        path[2] = endX;
        path[3] = endY;
        path[4] = c1X;
        path[5] = c1Y;
        path[6] = c2X;
        path[7] = c2Y;
        this.width = width;
        this.height = height;
        this.screen_num = screen_num;
    }

    //屏幕标记从客户端发送,服务器接收到pos_tan以后,在解析阶段对点进行标记
    CMT(Context context, String requestType, JSONArray pos_tan, int screen_num) {
        this.context = context;
        this.requestType = requestType;
        this.pos_tan = pos_tan;
        this.screen_num = screen_num;
    }
    //endregion

    @Override
    public void run() {
        try {
            Socket socket = new Socket("10.0.3.2", 2000);
            r_w(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void r_w(Socket socket) throws Exception {
        //发送数据部分
//        try {
        JSONObject requestJson = new JSONObject();
        String email = context.getSharedPreferences("login_info", Context.MODE_PRIVATE)
                .getString("email", "aaa");
        requestJson.put("email", email);  //标识出用户
        requestJson.put("requestType", requestType);

        //非必选部分,在构造期间加入con_i以表明使用哪个构造器
        //再用switch-case语句用con_i指明哪些东西需要放入requestJson,
        //从而用con_i作为中间变量在总体上实现构造器-请求结构的闭环
        switch (requestType) {
            //check,login,register
            case "lor":
                requestJson.put("poc", poc);
                break;
            //save a path
            case "path":
                requestJson.put("num", Iona.screen_indicate)
                        .put("width", width).put("height", height)
                        .put("screen_num",screen_num)
                        .put("staX", path[0] + "").put("staY", path[1] + "")
                        .put("endX", path[2] + "").put("endY", path[3] + "")
                        .put("c1X", path[4] + "").put("c1Y", path[5] + "")
                        .put("c2X", path[6] + "").put("c2Y", path[7] + "");
                break;
            //save a footprint
            case "footprint":
                requestJson.put("num", Iona.screen_indicate).put("pos_tan", pos_tan)
                .put("s_i",Iona.screen_indicate);
                counter++;
                break;
            case "content":
                //因为只能写当日日期,所以不需要发送年月日
                requestJson.put("content", poc);
                break;
            case "request_content":
                requestJson.put("y",y).put("m",m).put("d",d);
        }
        Log.i("iona", "requestJSON is" + requestJson);
        //放完就写
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(requestJson.toString());
        bufferedWriter.flush();
//        } catch (JSONException e) {
//            e.getStackTrace();
//        }

        //读取数据部分
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        String temp;
        if ((temp = bufferedReader.readLine()) != null) {
            Log.i("iona", "message received from server:" + temp);
//            try {
            JSONObject respondJSON = new JSONObject(temp);
            String respondType = respondJSON.getString("respondType");
            switch (respondType) {
                case "check":
                    Login.ACTION_FLAG = respondJSON.getBoolean("value") ? "lor" : "confirm";
                    context.getSharedPreferences("l_i", Context.MODE_PRIVATE).edit()
                            .putString("email", respondJSON.getString("email")).apply();
                    break;
                case "lor":
                    if (respondJSON.getBoolean("value")) {
                        //传入数值为真,即登录或注册操作成功
                        //下面应该将email或其他相关信息写入sp
                        context.getSharedPreferences("login_info", Context.MODE_PRIVATE)
                                .edit().putString("email", email).apply();
                        context.startActivity(new Intent(context, Iona.class));
                    } else {
                        Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "record":
                    if (respondJSON.getBoolean("value")) {
                        context.startActivity(new Intent(context, Iona.class));
                    } else {
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "request_content":
                    String content = respondJSON.getString("value");
                    Log.i("iona","content is:"+content);
                    Bundle bundle = new Bundle();
                    bundle.putString("content",content);
                    Message msg = new Message();
                    msg.what = 10086;
                    msg.setData(bundle);
                    Diary.handler.sendMessage(msg);
                    break;
            }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
}