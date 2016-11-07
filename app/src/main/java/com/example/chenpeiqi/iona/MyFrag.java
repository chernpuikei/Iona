package com.example.chenpeiqi.iona;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 16/3/22.
 */
public class MyFrag extends Fragment {

  static MyFrag actualCreate(float staX,float staY) {
    MyFrag myFrag = new MyFrag();
    Bundle bundle = new Bundle();
    bundle.putFloat("x",staX);
    bundle.putFloat("y",staY);
    myFrag.setArguments(bundle);
    return myFrag;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
    Log.i("MyFrag","onCreateView");
    return (new Pan(getActivity(),getArguments()));
  }

  interface PassCord {
    void passCord(float[] end,int width,int height);
  }

  interface PassDire {
    void passDire(String direction);
  }

}


