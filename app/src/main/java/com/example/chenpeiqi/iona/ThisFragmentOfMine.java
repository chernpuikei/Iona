package com.example.chenpeiqi.iona;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.*;


/**
 * Created on 16/3/22.
 */
public class ThisFragmentOfMine extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    static ThisFragmentOfMine actualCreate(float staX, float staY) {
        ThisFragmentOfMine thisFragmentOfMine = new ThisFragmentOfMine();
        Bundle bundle = new Bundle();
        bundle.putFloat("x", staX);
        bundle.putFloat("y", staY);
        thisFragmentOfMine.setArguments(bundle);
        return thisFragmentOfMine;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return (new ThisViewOfMine(getActivity(), getArguments()));

    }

    interface PassCord {
        void passCord(float[] end, int width, int height);
    }

    interface PassDire{
        void passDire(String direction);
    }

}


