package org.geometerplus.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import org.geometerplus.android.fbreader.FBReader;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

//import com.iflytek.cloud.ErrorCode;
//import com.iflytek.cloud.InitListener;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechError;
//import com.iflytek.cloud.SpeechSynthesizer;
//import com.iflytek.cloud.SpeechUtility;
//import com.iflytek.cloud.SynthesizerListener;

/**
 * desc:<br>
 * author : yuanbin<br>
 * date : 2018/9/20 14:11
 */
public class TTSReadUtil {
    private static TTSReadUtil instance;

    private TTSReadUtil() {
        isInit = false;
        initVoicers();
    }

    public static synchronized TTSReadUtil getInstance() {
        if (instance == null) {
            instance = new TTSReadUtil();
        }
        return instance;
    }

    private Context application;
    private boolean isInit;
//    private SpeechSynthesizer mTts;

    //region 初始化
    public void init(Context context) {
        if (context == null) {
            return;
        }
//        application = context.getApplicationContext();
//        String key = getTTSKey(context);
//        if (TextUtils.isEmpty(key)) {
//            showMessage("朗读引擎初始化失败_1001");
//            return;
//        }
//        SpeechUtility.createUtility(context.getApplicationContext(), SpeechConstant.APPID + "=" + key);
//        mTts = SpeechSynthesizer.createSynthesizer(application, new InitListener() {
//            @Override
//            public void onInit(int code) {
//                if (code != ErrorCode.SUCCESS) {
//                    showMessage("朗读引擎初始化失败" + code);
//                } else {
//                    // 初始化成功，之后可以调用startSpeaking方法
//                    // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
//                    // 正确的做法是将onCreate中的startSpeaking调用移至这里
//                    isInit = true;
//                }
//            }
//        });
    }

    private String getTTSKey(Context context) {
        String resultData = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString("TTS_APPKEY");
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }
    //endregion

    //region配置参数
    private final static String PREFER_NAME = "tts_settings";

    /**
     * 参数设置
     *
     * @return
     */
    private void setParam() {
        if (application == null) {
            return;
        }
        SharedPreferences mSharedPreferences = application.getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        // 清空参数
//        mTts.setParameter(SpeechConstant.PARAMS, null);
//        // 根据合成引擎设置相应参数
////        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//        //onevent回调接口实时返回音频流数据
//        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
//        // 设置在线合成发音人
//        int voicerPosition = mSharedPreferences.getInt("speed_voicer_position", 0);
//        mTts.setParameter(SpeechConstant.VOICE_NAME, voicers.get(voicerPosition).getId());
//        //设置合成语速
//        mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
//        //设置合成音调
//        mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
//        //设置合成音量
//        mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
////        }else {
////            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
////            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
////            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
////            /**
////             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
////             * 开发者如需自定义参数，请参考在线合成参数设置
////             */
////        }
//        //设置播放器音频流类型
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
//        // 设置播放合成音频打断音乐播放，默认为true
//        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, application.getCacheDir().getAbsolutePath() + "/msc/tts.pcm");
    }
    //endregion

    //region 声音列表
    private List<TTSVoicerBean> voicers;

    //region声音实体类
    public static class TTSVoicerBean {
        private String name;
        private String id;

        public TTSVoicerBean(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    //endregion
    private void initVoicers() {
        voicers = new ArrayList<TTSVoicerBean>();
        voicers.add(new TTSVoicerBean("小媛", "xiaoyuan"));
        voicers.add(new TTSVoicerBean("小燕—女青、中英、普通话", "xiaoyan"));
        voicers.add(new TTSVoicerBean("小宇—男青、中英、普通话", "xiaoyu"));
        voicers.add(new TTSVoicerBean("凯瑟琳—女青、英", "catherine"));
        voicers.add(new TTSVoicerBean("亨利—男青、英", "henry"));
        voicers.add(new TTSVoicerBean("玛丽—女青、英", "vimary"));
        voicers.add(new TTSVoicerBean("小研—女青、中英、普通话", "vixy"));
        voicers.add(new TTSVoicerBean("小琪—女青、中英、普通话", "xiaoqi"));
        voicers.add(new TTSVoicerBean("小峰—男青、中英、普通话", "vixf"));
        voicers.add(new TTSVoicerBean("小梅—女青、中英、粤语", "xiaomei"));
        voicers.add(new TTSVoicerBean("小莉—女青、中英、台湾普通话", "xiaolin"));
        voicers.add(new TTSVoicerBean("小蓉—女青、中、四川话", "xiaorong"));
        voicers.add(new TTSVoicerBean("小芸—女青、中、东北话", "xiaoqian"));
        voicers.add(new TTSVoicerBean("小坤—男青、中、河南话", "xiaokun"));
        voicers.add(new TTSVoicerBean("小强—男青、中、湖南话", "xiaoqiang"));
        voicers.add(new TTSVoicerBean("小莹—女青、中、陕西话", "vixying"));
        voicers.add(new TTSVoicerBean("小新—男童、中、普通话", "xiaoxin"));
        voicers.add(new TTSVoicerBean("楠楠—女童、中、普通话", "nannan"));
        voicers.add(new TTSVoicerBean("老孙—男老、中、普通话", "vils"));
    }

    public List<TTSVoicerBean> getVoicers() {
        return voicers;
    }
    //endregion

    //region tts播放listener
    public interface VoicePlayListener {
        /**
         * 开始播放
         */
        void onSpeakBegin();

        /**
         * 暂停播放
         */
        void onSpeakPaused();

        /**
         * 继续播放
         */
        void onSpeakResumed();

        /**
         * 停止播放
         */
        void onStop();

        /**
         * 合成进度
         *
         * @param percent
         * @param beginPos
         * @param endPos
         * @param info
         */
        void onBufferProgress(int percent, int beginPos, int endPos, String info);

        /**
         * 播放进度
         *
         * @param percent
         * @param beginPos
         * @param endPos
         */
        void onSpeakProgress(int percent, int beginPos, int endPos);

        /**
         * 播放完成
         */
        void onCompleted();

        /**
         * 播放出错
         *
         * @param error
         */
        void onError(String error);
    }

    private List<VoicePlayListener> listeners = new ArrayList<VoicePlayListener>();

    public void register(VoicePlayListener listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);
    }

    public void unregister(VoicePlayListener listener) {
        if (listener == null || !listeners.contains(listener)) {
            return;
        }
        listeners.remove(listener);
    }

    //endregion

    //region 开始播放
    public void startSpeaking(String content) {
//        if (mTts == null || !isInit) {
//            showMessage("朗读失败");
//            return;
//        }
//        if (mTts.isSpeaking()) {
//            mTts.stopSpeaking();
//        }
//        setParam();
//        mTts.startSpeaking(content, mTtsListener);
    }

    /**
     * 合成回调监听。
     */
//    private SynthesizerListener mTtsListener = new SynthesizerListener() {
//
//        @Override
//        public void onSpeakBegin() {
//            for (VoicePlayListener listener : listeners) {
//                if (listener != null) {
//                    listener.onSpeakBegin();
//                }
//            }
//        }
//
//        @Override
//        public void onSpeakPaused() {
//            for (VoicePlayListener listener : listeners) {
//                if (listener != null) {
//                    listener.onSpeakPaused();
//                }
//            }
//        }
//
//        @Override
//        public void onSpeakResumed() {
//            for (VoicePlayListener listener : listeners) {
//                if (listener != null) {
//                    listener.onSpeakResumed();
//                }
//            }
//        }
//
//        @Override
//        public void onBufferProgress(int percent, int beginPos, int endPos,
//                                     String info) {
//            for (VoicePlayListener listener : listeners) {
//                if (listener != null) {
//                    listener.onBufferProgress(percent, beginPos, endPos, info);
//                }
//            }
//        }
//
//        @Override
//        public void onSpeakProgress(int percent, int beginPos, int endPos) {
//            // 播放进度
//            for (VoicePlayListener listener : listeners) {
//                if (listener != null) {
//                    listener.onSpeakProgress(percent, beginPos, endPos);
//                }
//            }
//        }
//
//        @Override
//        public void onCompleted(SpeechError error) {
//            for (VoicePlayListener listener : listeners) {
//                if (listener != null) {
//                    if (error == null) {
//                        listener.onCompleted();
//                    } else {
//                        listener.onError(error.getPlainDescription(true));
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
//            // 若使用本地能力，会话id为null
////            Log.e(TAG,"TTS Demo onEvent >>>"+eventType);
////            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
////                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
////                Log.d(TAG, "session id =" + sid);
////            }
//        }
//    };

    //endregion

    public boolean isSpeaking() {
//        return isInit && mTts != null && mTts.isSpeaking();
        return false;
    }

    public void stopSpeaking() {
        if (isSpeaking()) {
            for (VoicePlayListener listener : listeners) {
                if (listener != null) {
                    listener.onStop();
                }
            }
           // mTts.stopSpeaking();
        }
    }

    private void showMessage(String msg) {
        FBReader.toast(msg);
    }
}
