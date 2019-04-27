package org.geometerplus.android.fbreader.util;

import android.content.Context;

/**
 * desc:<br>
 * author : yuanbin<br>
 * date : 2018/10/15 18:33
 */
public class FBReaderConfig {
    private static Context context;

    public static void init(Context con) {
        context = con.getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
