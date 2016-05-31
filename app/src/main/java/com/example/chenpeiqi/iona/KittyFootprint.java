package com.example.chenpeiqi.iona;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created on 16/4/14.
 */
class KittyFootprint extends View implements View.OnClickListener {
    private ShapeDrawable mDrawable;
    private Context context;

    public KittyFootprint(Context context,float posX,float posY) {

        super(context);
        this.context = context;
        int width = 50;

        mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds((int) posX, (int)posY, (int)posX + width, (int)posY + width);

        this.setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDrawable.draw(canvas);
    }

    @Override
    public void onClick(View v) {
        Log.i("iona","beingOnClicked");
        Toast.makeText(context,this+"BeingClicked",Toast.LENGTH_SHORT).show();
    }
}
