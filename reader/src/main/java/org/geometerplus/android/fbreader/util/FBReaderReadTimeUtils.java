package org.geometerplus.android.fbreader.util;

import android.os.Handler;

import java.util.LinkedList;

/**
 * desc:阅读时长<br>
 * author : yuanbin<br>
 * date : 2018/10/15 19:48
 */
public class FBReaderReadTimeUtils {

    private static Handler handler;
    private static int timeS;
    private static boolean isPause = false;
    private static LinkedList<OnReadTimeListener> listeners = new LinkedList<OnReadTimeListener>();


    public static void register(OnReadTimeListener listener) {
        listeners.add(listener);
    }

    public static void unregister(OnReadTimeListener listener) {
        listeners.remove(listener);
    }

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (handler != null && !isPause) {
                timeS++;
                handler.postDelayed(runnable, 1000);
                publicTime();
            }
        }
    };

    private static void publicTime() {
        for (OnReadTimeListener listener : listeners) {
            if (listener != null) {
                listener.onReadTime(timeS);
            }
        }
    }

    public static void startRead() {
        isPause = false;
        if (handler == null) {
            handler = new Handler();
            timeS = 0;
            handler.postDelayed(runnable, 1000);
        } else {
            handler.postDelayed(runnable, 1000);
        }
    }

    public static void stopRead() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        listeners.clear();
        handler = null;
        timeS = 0;
    }

    public static void pauseRead() {
        isPause = true;
        handler.removeCallbacksAndMessages(null);
    }

    public static int getReadTime() {
        return timeS;
    }

    public interface OnReadTimeListener {
        void onReadTime(int s);
    }
}
