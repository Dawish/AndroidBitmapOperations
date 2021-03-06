package danxx.bitmapkit;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.glidebitmappool.GlideBitmapPool;

import danxx.bitmapkit.blur.BlurKit;
import hugo.weaving.DebugLog;

/**
 * @author danxx
 * @date 2018/1/12
 * @desc 阴影圆角工具
 */

public class ShaderRoundUtils {

  /**
   * 返回一个抠图模糊渐变圆角图片
   *
   * @param srcBitmap 阴影处理图片
   * @param mRadius 圆角半径
   * @param currentRect 当前view的绘制矩形
   * @param shadowHeight 下面要绘制阴影的高度
   * @param clipRecycle srcBitmap原图裁剪后是否需要回收调
   */
  @DebugLog
  public static Bitmap processRoundBlurShader(Bitmap srcBitmap, int mRadius, Rect currentRect,
      int shadowHeight, boolean clipRecycle) {
    if (srcBitmap == null) {
      Log.d("danxx", "return rbsBitmap == null");
      return srcBitmap;
    }
    Log.d("danxx", "processRoundBlurShader...");

    Paint paint = new Paint();
    //取消透明
    //paint.setAlpha(20);
    /**把图片裁剪成阴影高度*/

    Log.i("danxx", "crop");

    Bitmap clipBitmap = clipBitmapBottom(srcBitmap, shadowHeight, clipRecycle);

    Bitmap blurBitmap = BlurKit.getInstance().blur(clipBitmap, 18);

    Log.i("danxx", "blurBitmap : " + blurBitmap.getHeight());

    RectF shadowRect = new RectF(currentRect);

    Log.i("danxx", "blurBitmap 1: " + shadowRect.height());

    shadowRect.bottom = blurBitmap.getHeight();

    Log.i("danxx", "blurBitmap 2: " + shadowRect.height());

    //取消偏移
    //shadowRect.offset(0,(currentRect.height() - (shadowHeight/2)));

    /**临时画布*/

//    Bitmap tempBitmap =
//        Bitmap.createBitmap(blurBitmap.getWidth(), blurBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Bitmap tempBitmap =
            GlideBitmapPool.getBitmap(blurBitmap.getWidth(), blurBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas tempCanvas = new Canvas(tempBitmap);

    /**渐变Bitmap*/
    BitmapShader bitmapShader =
        new BitmapShader(blurBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    /**线性渐变*/
    LinearGradient linearGradient =
        new LinearGradient(0, shadowRect.top, 0, shadowRect.bottom, Color.BLACK, Color.TRANSPARENT,
            Shader.TileMode.CLAMP);
    /**混合*/
    ComposeShader composeShader = new ComposeShader(bitmapShader, linearGradient,
        new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

    paint.setShader(composeShader);

    /**绘制圆角矩形*/
    tempCanvas.drawRoundRect(shadowRect, mRadius, mRadius, paint);

    /**回收之前的Bitmap*/
    if (clipBitmap != null && !clipBitmap.equals(tempBitmap) && !clipBitmap.isRecycled()) {
      Log.d("danxx","GlideBitmapPool 111 recycle : "+clipBitmap);
//      clipBitmap.recycle();
      GlideBitmapPool.putBitmap(clipBitmap);
    }
    return tempBitmap;
  }

  /**
   * 裁剪一定高度并缩放保留下面
   *
   * @param srcBitmap 原图
   * @param needHeight 裁剪后需要的高度
   * @param clipRecycle 原图裁剪后是否需要回收
   */
  @DebugLog
  private static Bitmap clipBitmapBottom(Bitmap srcBitmap, int needHeight,boolean clipRecycle) {

//    Bitmap cutBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), needHeight, Bitmap.Config.ARGB_8888);
    Bitmap cutBitmap = GlideBitmapPool.getBitmap(srcBitmap.getWidth(), needHeight, Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(cutBitmap);

    Rect srcRect =
        new Rect(0, srcBitmap.getHeight()-needHeight, srcBitmap.getWidth(), srcBitmap.getHeight());

    Rect desRect = new Rect(0, 0, srcBitmap.getWidth(), needHeight);

    canvas.drawBitmap(srcBitmap, srcRect, desRect, null);

    /**回收*/
    if (clipRecycle && srcBitmap != null && !srcBitmap.equals(cutBitmap) && !srcBitmap.isRecycled()) {
      Log.d("danxx","GlideBitmapPool 333 recycle : "+srcBitmap);
//      srcBitmap.recycle();
      GlideBitmapPool.putBitmap(srcBitmap);
    }

    return cutBitmap;
  }

  /**
   * @param canvas 当前view的画布
   * @param bitmap 阴影处理图片
   * @param currentRect 当前view的绘制矩形
   */
  public static Bitmap drawRoundBlurShader(Canvas canvas, Bitmap bitmap, Rect currentRect) {
    Bitmap rbsBitmap = null;
    if (bitmap == null) {
      return rbsBitmap;
    }

    int shadowHeight = bitmap.getHeight();

    RectF shadowRect = new RectF(currentRect);
    shadowRect.bottom = shadowHeight;

    Paint paint = new Paint();

    shadowRect.offset(0, (currentRect.height() - (shadowHeight / 2)));

    /**绘制圆角矩形*/
    canvas.drawBitmap(bitmap, null, shadowRect, paint);

    return rbsBitmap;
  }


  /**
   * Bitmap绘制成圆角返回
   */
  private Bitmap createRoundBitmap(Bitmap bitmap, int mRadius) {
    if (bitmap == null) {
      return null;
    }

    int mWidth = bitmap.getWidth();
    int mHeight = bitmap.getHeight();

    final Paint paint = new Paint();
    /**开启抗锯齿**/
    paint.setAntiAlias(true);
    /****/
//    Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
    Bitmap target = GlideBitmapPool.getBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

    /**
     * Construct a canvas with the specified bitmap to draw into. The       bitmapmust be mutable
     * 以bitmap对象创建一个画布，则将内容都绘制在bitmap上，bitmap不得为null;
     */
    Canvas canvas = new Canvas(target);
    /**新建一个矩形绘制区域,并给出左上角和右下角的坐标**/
    RectF rect = new RectF(0, 0, mWidth, mHeight);
    /**
     * 把图片缩放成我们想要的大小
     */
    bitmap = Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, false);
    /**在绘制矩形区域绘制用画笔绘制一个圆角矩形**/
    canvas.drawRoundRect(rect, mRadius, mRadius, paint);
    /**
     * 我简单理解为设置画笔在绘制时图形堆叠时候的显示模式
     * SRC_IN:取两层绘制交集。显示上层。
     */
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, 0, 0, paint);
    /****/
    return target;
  }
}
