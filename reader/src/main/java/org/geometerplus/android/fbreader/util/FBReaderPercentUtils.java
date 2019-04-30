package org.geometerplus.android.fbreader.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * desc:<br>
 * author : yuanbin<br>
 * date : 2018/10/15 18:32
 */
public class FBReaderPercentUtils {
    private static float UI_HEIGHT = 1334f;
    private static float UI_WIDTH = 750f;

    /**
     * 比例高度
     *
     * @param view
     * @param originalHeight
     */
    public static void percentHeight(View view, int originalHeight) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = (int) (originalHeight / UI_HEIGHT * getScreenHeight());
        view.setLayoutParams(lp);
    }

    /**
     * 比例宽度
     *
     * @param view
     * @param originalWidth
     */
    public static void percentWidth(View view, int originalWidth) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = (int) (originalWidth / UI_WIDTH * getScreenWidth());
        view.setLayoutParams(lp);
    }

    /**
     * 宽高相同的比例
     *
     * @param view
     * @param rate
     */
    public static void samePercentWH(View view, int originalWidth, int originalHeight, float rate) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = (int) (originalWidth * rate);
        lp.height = (int) (originalHeight * rate);
        view.setLayoutParams(lp);
    }

    public static void samePercentMargin(View view, int originalLeft, int originalTop, int originalRight, int originalBottom, float rate) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof RelativeLayout.LayoutParams) {
            int marginLeft = originalLeft > 0 ? originalLeft : ((RelativeLayout.LayoutParams) lp).leftMargin;
            int marginTop = originalTop > 0 ? originalTop : ((RelativeLayout.LayoutParams) lp).topMargin;
            int marginRight = originalRight > 0 ? originalRight : ((RelativeLayout.LayoutParams) lp).rightMargin;
            int marginBottom = originalBottom > 0 ? originalBottom : ((RelativeLayout.LayoutParams) lp).bottomMargin;
            ((RelativeLayout.LayoutParams) lp).setMargins((int) (marginLeft * rate), (int) (marginTop * rate), (int) (marginRight * rate), (int) (marginBottom * rate));
            view.setLayoutParams(lp);
        }

    }

    /**
     * 比例宽高
     *
     * @param view
     * @param originalWidth
     * @param originalHeight
     */
    public static void percentWH(View view, int originalWidth, int originalHeight) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = (int) (originalWidth / UI_WIDTH * getScreenWidth());
        lp.height = (int) (originalHeight / UI_HEIGHT * getScreenHeight());
        view.setLayoutParams(lp);
    }

    /**
     * 获取比例宽
     *
     * @param originalWidth
     * @return
     */
    public static int getPercentWidth(int originalWidth) {
        return (int) (originalWidth / UI_WIDTH * getScreenWidth());
    }

    /**
     * 获取比例高
     *
     * @param originalHeight
     * @return
     */
    public static int getPercentHeight(int originalHeight) {
        return (int) (originalHeight / UI_HEIGHT * getScreenHeight());
    }

    /**
     * 获取水平比例
     *
     * @param value
     * @return
     */
    public static int getHorizontalPercent(int value) {
        return (int) (value / UI_WIDTH * getScreenWidth());
    }

    /**
     * 获取垂直比例
     *
     * @param value
     * @return
     */
    public static int getVerticalPercent(int value) {
        return (int) (value / UI_HEIGHT * getScreenHeight());
    }

    /**
     * 宽度比例
     *
     * @return
     */
    public static float getWidthRate() {
        return getScreenWidth() / UI_WIDTH;
    }

    /**
     * 高度比例
     *
     * @return
     */
    public static float getHeightRate() {
        return getScreenHeight() / UI_HEIGHT;
    }

    public static void resetTextSize(TextView tv, int size) {
        if (tv != null) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * getWidthPercent());
        }
    }

    public static float getWidthPercent() {
        return 1f * getScreenWidth() / UI_WIDTH;
    }


    /**
     * 获取屏幕高度
     *
     * @return 屏幕高度
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) FBReaderConfig.getContext().getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 获取屏幕
     *
     * @return 屏幕宽度
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) FBReaderConfig.getContext().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    public static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, FBReaderConfig.getContext().getResources().getDisplayMetrics());
    }

    public static int px2dp(float d) {
        final float scale = FBReaderConfig.getContext().getResources().getDisplayMetrics().density;
        return (int) (d / scale + 0.5f);
    }
}
