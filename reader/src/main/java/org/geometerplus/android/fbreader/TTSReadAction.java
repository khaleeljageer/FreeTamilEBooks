package org.geometerplus.android.fbreader;

import android.util.Log;
import org.geometerplus.android.util.TTSReadUtil;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;

import java.util.List;

/**
 * desc:tts阅读<br>
 * author : yuanbin<br>
 * date : 2018/9/20 13:49
 */
public class TTSReadAction extends FBAndroidAction {

    TTSReadAction(FBReader baseActivity, FBReaderApp fbreader) {
        super(baseActivity, fbreader);
        TTSReadUtil.getInstance().register(new TTSReadUtil.VoicePlayListener() {
            @Override
            public void onSpeakBegin() {
                Log.e("speadking", "onSpeakBegin");
            }

            @Override
            public void onSpeakPaused() {
                Log.e("speadking", "onSpeakPaused");
            }

            @Override
            public void onSpeakResumed() {
                Log.e("speadking", "onSpeakResumed");
            }

            @Override
            public void onStop() {
                Reader.getTextView().clearBottomLine();
            }

            @Override
            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
                Log.e("speadking", "onBufferProgress percent:" + percent + " begionPos:" + beginPos + " endPos:" + endPos + " info:" + info);
            }

            @Override
            public void onSpeakProgress(int percent, int beginPos, int endPos) {
                if (percent > playPercent) {
                    playSize += textLine.get(playPosition).getLine().length();
                    playPercent = (int) (100f * playSize / totalSize);
                    Reader.getTextView().bottomLine(textLine.get(playPosition).start, textLine.get(playPosition).end);
                    playPosition++;
                }
            }

            @Override
            public void onCompleted() {
                Log.e("speadking", "onCompleted");
                playNextPage();
            }

            @Override
            public void onError(String error) {
                Log.e("speadking", "onError:" + error);
            }
        });
    }

    @Override
    protected void run(Object... params) {
        TTSReadUtil.getInstance().stopSpeaking();
//        FBView fbView = Reader.getTextView();
//        textWordCursor = fbView.getStartCursor();
//        String startLine = fbView.getStartLine();
//        String paragraph = getParagraph();
//        paragraph = paragraph.substring(paragraph.indexOf(startLine),paragraph.length());
//        play(paragraph);
        play(convert(Reader.getTextView().getCurrentPageText()));
    }

    //region 播放下一页
    private void playNextPage() {
        play(convert(Reader.getTextView().getNextPageText()));
        BaseActivity.runAction(ActionCode.TURN_PAGE_FORWARD);
    }
    //endregion

    private void play(String content) {
        TTSReadUtil.getInstance().startSpeaking(content);
    }

    private String convert(List<TTSLineInfo> list) {
        StringBuilder sb = new StringBuilder("");
        if (list != null) {
            for (TTSLineInfo s : list) {
                sb.append(s.getLine());
            }
        }
        playPercent = 0;
        playSize = 0;
        playPosition = 0;
        totalSize = sb.length();
        textLine = list;
        return sb.toString();
    }

    private int playPercent;
    private int playSize;
    private int playPosition;
    private int totalSize;
    private List<TTSLineInfo> textLine;

    public static class TTSLineInfo {
        private String line;
        private ZLTextPosition start;
        private ZLTextPosition end;

        public TTSLineInfo(String line, ZLTextPosition start, ZLTextPosition end) {
            this.line = line;
            this.start = start;
            this.end = end;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public ZLTextPosition getStart() {
            return start;
        }

        public void setStart(ZLTextPosition start) {
            this.start = start;
        }

        public ZLTextPosition getEnd() {
            return end;
        }

        public void setEnd(ZLTextPosition end) {
            this.end = end;
        }
    }
}
