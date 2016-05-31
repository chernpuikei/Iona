package com.example.chenpeiqi.iona;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by chenpeiqi on 16/2/19.
 */
public class MyAdapter extends FragmentPagerAdapter {

    static int[] ids = {R.drawable.g0,R.drawable.g1,R.drawable.g2};

    public MyAdapter(FragmentManager fm) {
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
