package org.geometerplus.fbreader.util;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.util.FBReaderReadTimeUtils;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;

/**
 * desc:是否是换页逻辑换页<br>
 * author : yuanbin<br>
 * date : 2018/12/18 12:01
 */
public class TurnPageJudgeUtil {
    private static TurnPageJudgeUtil instance;
    private String content;
    private ZLTextWordCursor startPosition;
    private ZLTextWordCursor endPosition;
    private int readTime;
    private boolean canRecord;
    private PageInfo pageInfo;

    private TurnPageJudgeUtil() {
        readTime = 0;
        startPosition = null;
        canRecord = false;
        pageInfo = new PageInfo();
    }

    public static TurnPageJudgeUtil getInstance() {
        if (instance == null) {
            instance = new TurnPageJudgeUtil();
        }
        return instance;
    }

    public boolean checkIsTurnPage(ZLTextWordCursor position) {
        if (!canRecord) {
            return false;
        }
        if (position == null || position.isNull()) {
            return false;
        }
        if (startPosition == null) {
            return true;
        }
        return !startPosition.equals(position);
    }

    public void onResume() {
        canRecord = true;
        FBReaderReadTimeUtils.register(onReadTimeListener);
    }

    public void onPause() {
        canRecord = false;
        FBReaderReadTimeUtils.unregister(onReadTimeListener);
    }

    public void onFinish() {
        onPause();
        startPosition = null;
        readTime = 0;
    }

    public void onTurnPage(String currentPageTextString, ZLTextWordCursor startCursor, ZLTextWordCursor endCursor) {
        publishTurnPage();
        startPosition = new ZLTextWordCursor(startCursor);
        endPosition = endCursor;
        content = currentPageTextString;
    }

    private void publishTurnPage() {
        if (startPosition != null) {
            final int time = readTime;
            readTime = 0;
            if (FBReader.lifeCycle != null) {
                pageInfo.content = content;
                pageInfo.startParagraph = startPosition.getParagraphIndex();
                pageInfo.startElementIndex = startPosition.getElementIndex();
                pageInfo.startCharIndex = startPosition.getCharIndex();
                if (endPosition != null) {
                    pageInfo.endParagraph = endPosition.getParagraphIndex();
                    pageInfo.endElementIndex = endPosition.getElementIndex();
                    pageInfo.endCharIndex = endPosition.getCharIndex();
                }
                pageInfo.readTime = time;
                FBReader.lifeCycle.onTurnPage(pageInfo);
            }
        }
    }

    private FBReaderReadTimeUtils.OnReadTimeListener onReadTimeListener = new FBReaderReadTimeUtils.OnReadTimeListener() {
        @Override
        public void onReadTime(int s) {
            if (startPosition != null) {
                readTime++;
                FBReader.log("read Time: " + readTime);
            }
        }
    };

    public static class PageInfo {
        public String content;
        public int startParagraph;
        public int startElementIndex;
        public int startCharIndex;
        public int endParagraph;
        public int endElementIndex;
        public int endCharIndex;
        public int readTime;
    }
}
