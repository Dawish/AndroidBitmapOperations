package com.danxx.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.danxx.R;
import com.danxx.utils.BitmapClipUtils;
import com.danxx.utils.ShaderRoundUtil;

/**
 * @author danxx
 */

public class ClipView extends View {
    public ClipView(Context context) {
        super(context);
        init();
    }

    public ClipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint paint = new Paint();
    Bitmap bitmap;
    Rect rect = new Rect();

    private void init(){

        paint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bitmap_clip);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        rect.set(0,0,bitmap.getWidth(),bitmap.getHeight());
        ShaderRoundUtil.drawRoundBlurShader(canvas,bitmap,10,rect,100);

        super.onDraw(canvas);

        if(bitmap!=null){

            canvas.drawBitmap(bitmap,0,0,paint);


        }

    }
}
