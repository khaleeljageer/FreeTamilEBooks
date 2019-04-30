package org.geometerplus.android.fbreader.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import androidx.appcompat.widget.AppCompatTextView;
import org.geometerplus.android.fbreader.util.FBReaderPercentUtils;

/**
 * desc:按比例设置文字大小<br>
 * author : yuanbin<br>
 * date : 2018/10/15 18:30
 */
public class FBReaderPercentTextView extends AppCompatTextView {


    public FBReaderPercentTextView(Context context) {
        this(context, null);
    }

    public FBReaderPercentTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public FBReaderPercentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        FBReaderPercentUtils.resetTextSize(this, px2dp(getContext(), getTextSize()) * 2);
    }

    @Override
    public void setTextSize(int unit, float size) {
        if (TypedValue.COMPLEX_UNIT_PX == unit) {
            super.setTextSize(unit, size);
        } else {
            FBReaderPercentUtils.resetTextSize(this, (int) size * 2);
        }
    }

    /**
     * px转换成dp
     */
    private int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
