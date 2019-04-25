package org.geometerplus.android.fbreader.util;

import android.content.Context;
import android.graphics.Color;

/**
 * desc:<br>
 * author : yuanbin<br>
 * date : 2018/8/31 19:36
 */
public class ColorUtil {
    /**
     * 获取颜色
     *
     * @param resId
     * @param defaultColor
     * @return
     */
    public static int getColor(Context context, int resId, String defaultColor) {
        try {
            return context.getApplicationContext().getResources().getColor(resId);
        } catch (Throwable t) {
            return Color.parseColor(defaultColor);
        }
    }
}
