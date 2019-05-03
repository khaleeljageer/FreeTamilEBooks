package org.geometerplus.android.fbreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.FooterOptions;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.text.model.ZLTextAlignmentType;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

/**
 * desc:设置界面<br>
 * author : yuanbin<br>
 * date : 2018/10/16 15:10
 */
public class FBReaderSettingActivity extends AppCompatActivity implements View.OnClickListener {

    public static void start(Context context) {
        Intent starter = new Intent(context, FBReaderSettingActivity.class);
        context.startActivity(starter);
    }

    FBReaderApp myFBReaderApp;
    private View mViewBack;
    private TextView mTvLineHeightSmall;
    private TextView mTvLineHeightSmallMiddle;
    private TextView mTvLineHeightMiddle;
    private TextView mTvLineHeightBigMiddle;
    private TextView mTvLineHeightBig;
    private TextView mTvAlignmentDefault;
    private TextView mTvAlignmentLeft;
    private TextView mTvAlignmentRight;
    private TextView mTvAlignmentCenter;
    private TextView mTvAlignmentJustified;
    private View mViewShowChapterContainer;
    private Switch mIvShowChapter;
    private View mViewShowTimeContainer;
    private Switch mIvShowTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_fbreader_setting);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mViewBack = findViewById(R.id.id_activity_fbreader_setting_back);
        mTvLineHeightBig = (TextView) findViewById(R.id.id_activity_fbreader_setting_line_height_big);
        mTvLineHeightBigMiddle = (TextView) findViewById(R.id.id_activity_fbreader_setting_line_height_big_middle);
        mTvLineHeightMiddle = (TextView) findViewById(R.id.id_activity_fbreader_setting_line_height_middle);
        mTvLineHeightSmallMiddle = (TextView) findViewById(R.id.id_activity_fbreader_setting_line_height_small_middle);
        mTvLineHeightSmall = (TextView) findViewById(R.id.id_activity_fbreader_setting_line_height_small);
        mTvAlignmentDefault = (TextView) findViewById(R.id.id_activity_fbreader_setting_alignment_default);
        mTvAlignmentLeft = (TextView) findViewById(R.id.id_activity_fbreader_setting_alignment_left);
        mTvAlignmentRight = (TextView) findViewById(R.id.id_activity_fbreader_setting_alignment_right);
        mTvAlignmentCenter = (TextView) findViewById(R.id.id_activity_fbreader_setting_alignment_center);
        mTvAlignmentJustified = (TextView) findViewById(R.id.id_activity_fbreader_setting_alignment_justified);
        mViewShowChapterContainer = findViewById(R.id.id_activity_fbreader_setting_show_chapter_name_container);
        mIvShowChapter = (Switch) findViewById(R.id.id_activity_fbreader_setting_show_chapter_name);
        mViewShowTimeContainer = findViewById(R.id.id_activity_fbreader_setting_show_time_container);
        mIvShowTime = (Switch) findViewById(R.id.id_activity_fbreader_setting_show_time);
    }

    private void initData() {
        myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
        initLineHeight();
        initAlignment();
        initTime();
    }

    private void initListener() {
        mTvLineHeightSmall.setOnClickListener(this);
        mTvLineHeightSmallMiddle.setOnClickListener(this);
        mTvLineHeightMiddle.setOnClickListener(this);
        mTvLineHeightBigMiddle.setOnClickListener(this);
        mTvLineHeightBig.setOnClickListener(this);
        mTvAlignmentDefault.setOnClickListener(this);
        mTvAlignmentLeft.setOnClickListener(this);
        mTvAlignmentRight.setOnClickListener(this);
        mTvAlignmentCenter.setOnClickListener(this);
        mTvAlignmentJustified.setOnClickListener(this);
        mViewBack.setOnClickListener(this);
        mViewShowTimeContainer.setOnClickListener(this);
        mViewShowChapterContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.id_activity_fbreader_setting_back) {
            finish();
        } else if (i == R.id.id_activity_fbreader_setting_line_height_big) {
            changeLineHeight(lineHeights[4]);
        } else if (i == R.id.id_activity_fbreader_setting_line_height_big_middle) {
            changeLineHeight(lineHeights[3]);
        } else if (i == R.id.id_activity_fbreader_setting_line_height_middle) {
            changeLineHeight(lineHeights[2]);
        } else if (i == R.id.id_activity_fbreader_setting_line_height_small_middle) {
            changeLineHeight(lineHeights[1]);
        } else if (i == R.id.id_activity_fbreader_setting_line_height_small) {
            changeLineHeight(lineHeights[0]);
        } else if (i == R.id.id_activity_fbreader_setting_alignment_default) {
            changeAlignment(ZLTextAlignmentType.ALIGN_UNDEFINED);
        } else if (i == R.id.id_activity_fbreader_setting_alignment_left) {
            changeAlignment(ZLTextAlignmentType.ALIGN_LEFT);
        } else if (i == R.id.id_activity_fbreader_setting_alignment_right) {
            changeAlignment(ZLTextAlignmentType.ALIGN_RIGHT);
        } else if (i == R.id.id_activity_fbreader_setting_alignment_center) {
            changeAlignment(ZLTextAlignmentType.ALIGN_CENTER);
        } else if (i == R.id.id_activity_fbreader_setting_alignment_justified) {
            changeAlignment(ZLTextAlignmentType.ALIGN_JUSTIFY);
        } else if (i == R.id.id_activity_fbreader_setting_show_chapter_name_container) {
            changeShowCatalog();
        } else if (i == R.id.id_activity_fbreader_setting_show_time_container) {
            changeShowTime();
        }
    }

    //region 行间距
    private int minHeight;
    private int maxHeight;
    private int currentHeight;
    private int[] lineHeights = new int[5];
    private TextView[] lineHeightViews = new TextView[5];

    private void initLineHeight() {
        ZLIntegerRangeOption spaceOption = myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption;
        maxHeight = spaceOption.MaxValue;
        minHeight = spaceOption.MinValue;
        currentHeight = spaceOption.getValue();
        int temp = (maxHeight - minHeight) / (lineHeights.length - 1);
        for (int i = 0; i < lineHeights.length; i++) {
            lineHeights[i] = minHeight + i * temp;
        }
        lineHeightViews[0] = mTvLineHeightSmall;
        lineHeightViews[1] = mTvLineHeightSmallMiddle;
        lineHeightViews[2] = mTvLineHeightMiddle;
        lineHeightViews[3] = mTvLineHeightBigMiddle;
        lineHeightViews[4] = mTvLineHeightBig;
        updateLineHeightView();
    }

    private void updateLineHeightView() {
        int blackText = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().BlackTextOption.getValue());
        int selectText = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        for (TextView lineHeightView : lineHeightViews) {
            lineHeightView.setTextColor(blackText);
        }
        for (int i = 0; i < lineHeights.length; i++) {
            if (currentHeight <= lineHeights[i]) {
                lineHeightViews[i].setTextColor(selectText);
                break;
            }
        }
    }

    private void changeLineHeight(int lineHeight) {
        currentHeight = lineHeight;
        myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption.setValue(lineHeight);
        myFBReaderApp.clearTextCaches();
        myFBReaderApp.getViewWidget().repaint();
        updateLineHeightView();
    }
    //endregion

    //region alignment
    private TextView[] alignmentViews = new TextView[5];

    private void initAlignment() {
        alignmentViews[0] = mTvAlignmentDefault;
        alignmentViews[1] = mTvAlignmentLeft;
        alignmentViews[2] = mTvAlignmentRight;
        alignmentViews[3] = mTvAlignmentCenter;
        alignmentViews[4] = mTvAlignmentJustified;
        updateAlignment();

    }

    private void updateAlignment() {
        ZLIntegerRangeOption spaceOption = myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().AlignmentOption;
        int alignment = spaceOption.getValue();
        int blackText = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().BlackTextOption.getValue());
        int selectText = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        for (TextView alignmentView : alignmentViews) {
            alignmentView.setTextColor(blackText);
        }

        if (myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().UseCSSTextAlignmentOption.getValue()) {
            alignmentViews[0].setTextColor(selectText);
        } else if (alignment == ZLTextAlignmentType.ALIGN_LEFT) {
            alignmentViews[1].setTextColor(selectText);
        } else if (alignment == ZLTextAlignmentType.ALIGN_RIGHT) {
            alignmentViews[2].setTextColor(selectText);
        } else if (alignment == ZLTextAlignmentType.ALIGN_CENTER) {
            alignmentViews[3].setTextColor(selectText);
        } else if (alignment == ZLTextAlignmentType.ALIGN_JUSTIFY) {
            alignmentViews[4].setTextColor(selectText);
        }
    }

    private void changeAlignment(int alignment) {
        ZLIntegerRangeOption spaceOption = myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().AlignmentOption;
        if (alignment == ZLTextAlignmentType.ALIGN_UNDEFINED) {
            myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().UseCSSTextAlignmentOption.setValue(true);
            spaceOption.setValue(ZLTextAlignmentType.ALIGN_JUSTIFY);
        } else if (alignment == ZLTextAlignmentType.ALIGN_LEFT) {
            myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().UseCSSTextAlignmentOption.setValue(false);
            spaceOption.setValue(alignment);
        } else if (alignment == ZLTextAlignmentType.ALIGN_RIGHT) {
            myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().UseCSSTextAlignmentOption.setValue(false);
            spaceOption.setValue(alignment);
        } else if (alignment == ZLTextAlignmentType.ALIGN_CENTER) {
            myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().UseCSSTextAlignmentOption.setValue(false);
            spaceOption.setValue(alignment);
        } else if (alignment == ZLTextAlignmentType.ALIGN_JUSTIFY) {
            myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().UseCSSTextAlignmentOption.setValue(false);
            spaceOption.setValue(alignment);
        }
        myFBReaderApp.clearTextCaches();
        myFBReaderApp.getViewWidget().repaint();
        updateAlignment();
    }
    //endregion

    //region time battery
    private void initTime() {
        FooterOptions footerOptions = myFBReaderApp.ViewOptions.getFooterOptions();
        mIvShowTime.setChecked(footerOptions.ShowClock.getValue() || footerOptions.ShowBattery.getValue());
        mIvShowChapter.setChecked(myFBReaderApp.ViewOptions.HeaderShowCatalogName.getValue());
    }

    private void changeShowTime() {
        FooterOptions footerOptions = myFBReaderApp.ViewOptions.getFooterOptions();
        if (footerOptions.ShowClock.getValue() || footerOptions.ShowBattery.getValue()) {
            footerOptions.ShowBattery.setValue(false);
            footerOptions.ShowClock.setValue(false);
            mIvShowTime.setChecked(false);
        } else {
            footerOptions.ShowBattery.setValue(true);
            footerOptions.ShowClock.setValue(true);
            mIvShowTime.setChecked(true);
        }
        myFBReaderApp.getViewWidget().reset();
        myFBReaderApp.getViewWidget().repaint();
    }

    private void changeShowCatalog() {
        if (myFBReaderApp.ViewOptions.HeaderShowCatalogName.getValue()) {
            myFBReaderApp.ViewOptions.HeaderShowCatalogName.setValue(false);
            mIvShowChapter.setChecked(false);
        } else {
            myFBReaderApp.ViewOptions.HeaderShowCatalogName.setValue(true);
            mIvShowChapter.setChecked(true);
        }
        myFBReaderApp.getViewWidget().reset();
        myFBReaderApp.getViewWidget().repaint();
    }
    //endregion
}
