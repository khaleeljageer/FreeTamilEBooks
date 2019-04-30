package org.geometerplus.android.fbreader;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.geometerplus.android.fbreader.util.ColorUtil;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.util.ZLColor;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

/**
 * desc:<br>
 * author : yuanbin<br>
 * date : 2018/8/31 15:01
 */
public class SettingPopup extends PopupPanel {
    final static String ID = "SettingPopup";

    private final FBReaderApp myFBReader;
    //文字大小
    private ZLIntegerRangeOption fontSizeOption;
    //翻页方式
    private boolean pageTurningMode;

    private View mViewBottomContainer;
    private ImageView mIvSeekBarLeft;
    private SeekBar mSeekBarLight;
    private ImageView mIvSeekBarRight;
    private TextView mTvFontSizeSub;
    private TextView mTvFontSize;
    private TextView mTvFontSizeAdd;
    private TextView mTvLineSub;
    private TextView mTvLine;
    private TextView mTvLineAdd;

    //仿真
    private TextView mTvPageTurning1;
    //覆盖
    private TextView mTvPageTurning2;
    //滑动
    private TextView mTvPageTurning3;
    //无
    private TextView mTvPageTurning4;


    SettingPopup(FBReaderApp fbReader) {
        super(fbReader);
        myFBReader = fbReader;
    }

    @Override
    protected void show_() {
        super.show_();
        update();
    }

    @Override
    public void createControlPanel(FBReader activity, RelativeLayout root) {
        if (myWindow != null && activity == myWindow.getContext()) {
            return;
        }
        fontSizeOption = myFBReader.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption; // 字体
        pageTurningMode = myFBReader.PageTurningOptions.Horizontal.getValue(); // 当前翻页方式
        activity.getLayoutInflater().inflate(R.layout.setting_panel, root);
        myWindow = activity.findViewById(R.id.id_setting_panel);
        View container = activity.findViewById(R.id.id_setting_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_();
            }
        });
        mViewBottomContainer = activity.findViewById(R.id.id_setting_panel_bottom_container);
        mViewBottomContainer.setOnClickListener(null);
        mSeekBarLight = activity.findViewById(R.id.light_slider);
        mIvSeekBarLeft = (ImageView) activity.findViewById(R.id.light_slider_left);
        mIvSeekBarRight = (ImageView) activity.findViewById(R.id.light_slider_right);
        mSeekBarLight.setMax(100);
        mSeekBarLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean myIsBrightnessAdjustmentInProgress;

            public void onStartTrackingTouch(SeekBar seekBar) {
                myIsBrightnessAdjustmentInProgress = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myIsBrightnessAdjustmentInProgress) {
                    myIsBrightnessAdjustmentInProgress = false;
                }
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (myIsBrightnessAdjustmentInProgress) {
                        myFBReader.getViewWidget().setScreenBrightness(progress);
                        return;
                    }
                }
            }
        });
        mTvFontSizeSub = activity.findViewById(R.id.tv_font_minus);
        mTvFontSizeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = fontSizeOption.getValue();
                fontSizeOption.setValue(size - 2);
                myFBReader.clearTextCaches();
                myFBReader.getViewWidget().repaint();
                updateFontSize();
            }
        });
        mTvFontSize = activity.findViewById(R.id.tv_font_size);
        mTvFontSizeAdd = activity.findViewById(R.id.tv_font_add);
        mTvFontSizeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = fontSizeOption.getValue();
                fontSizeOption.setValue(size + 2);
                myFBReader.clearTextCaches();
                myFBReader.getViewWidget().repaint();
                updateFontSize();
            }
        });
        mTvLineSub = activity.findViewById(R.id.tv_lineSpace_minus);
        mTvLineSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFBReader.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption.setValue(myFBReader.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption.getValue() - 1);
                myFBReader.clearTextCaches();
                myFBReader.getViewWidget().repaint();
                updateLineHeight();
            }
        });
        mTvLine = activity.findViewById(R.id.tv_lineSpace_size);
        mTvLineAdd = activity.findViewById(R.id.tv_lineSpace_add);
        mTvLineAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFBReader.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption.setValue(myFBReader.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption.getValue() + 1);
                myFBReader.clearTextCaches();
                myFBReader.getViewWidget().repaint();
                updateLineHeight();
            }
        });
        //仿真
        mTvPageTurning1 = activity.findViewById(R.id.tv_page_simulation);
        mTvPageTurning1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFBReader.PageTurningOptions.Animation.setValue(ZLView.Animation.curl);
                updatePageTurningMode();
            }
        });
        //覆盖
        mTvPageTurning2 = activity.findViewById(R.id.tv_page_cover);
        mTvPageTurning2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFBReader.PageTurningOptions.Animation.setValue(ZLView.Animation.slide);
                updatePageTurningMode();
            }
        });
        //滑动
        mTvPageTurning3 = activity.findViewById(R.id.tv_page_slide);
        mTvPageTurning3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFBReader.PageTurningOptions.Animation.setValue(ZLView.Animation.shift);
                updatePageTurningMode();
            }
        });
        //无
        mTvPageTurning4 = activity.findViewById(R.id.tv_page_none);
        mTvPageTurning4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFBReader.PageTurningOptions.Animation.setValue(ZLView.Animation.none);
                updatePageTurningMode();
            }
        });
        View bg = activity.findViewById(R.id.bg_1);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#f7f7f7"));
            }
        });
        bg = activity.findViewById(R.id.bg_2);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#decfad"));
            }
        });
        bg = activity.findViewById(R.id.bg_3);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#d5edc7"));
            }
        });
        bg = activity.findViewById(R.id.bg_4);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#264458"));
            }
        });
        bg = activity.findViewById(R.id.bg_5);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#e7f0db"));
            }
        });
        bg = activity.findViewById(R.id.bg_6);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#453232"));
            }
        });
        bg = activity.findViewById(R.id.bg_7);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#f5d5d7"));
            }
        });

    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected void update() {
        if (myWindow == null) {
            return;
        }
        updateLight();
        updateFontSize();
        updateLineHeight();
        updatePageTurningMode();
        updateBg();
    }

    //亮度设置
    private void updateLight() {
        boolean isDark = ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue());
        Context context = mSeekBarLight.getContext().getApplicationContext();
        int progress = myFBReader.getViewWidget().getScreenBrightness();
        if (!isDark) {
            //日间模式
            mSeekBarLight.setThumb(context.getResources().getDrawable(R.drawable.bg_navigation_seekbar_thumb_day));
            Rect bounds = mSeekBarLight.getProgressDrawable().getBounds();
            Drawable drawable = context.getResources().getDrawable(R.drawable.drawable_navigation_seekbar_day);
            mSeekBarLight.setProgressDrawable(drawable);
            mSeekBarLight.getProgressDrawable().setBounds(bounds);
            mIvSeekBarLeft.setImageResource(R.drawable.ic_setting_light_day);
            mIvSeekBarRight.setImageResource(R.drawable.ic_setting_light_day);
        } else {
            //夜间模式
            mSeekBarLight.setThumb(context.getResources().getDrawable(R.drawable.bg_navigation_seekbar_thumb_night));
            Rect bounds = mSeekBarLight.getProgressDrawable().getBounds();
            Drawable drawable = context.getResources().getDrawable(R.drawable.drawable_navigation_seekbar_night);
            mSeekBarLight.setProgressDrawable(drawable);
            mSeekBarLight.getProgressDrawable().setBounds(bounds);
            mIvSeekBarLeft.setImageResource(R.drawable.ic_setting_light_night);
            mIvSeekBarRight.setImageResource(R.drawable.ic_setting_light_night);
        }
        mSeekBarLight.setProgress(progress);
    }

    //更新文字大小
    private void updateFontSize() {
        boolean isDark = ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue());
        int textColor = ZLAndroidColorUtil.rgb(myFBReader.ViewOptions.getColorProfile().MenuTextOption.getValue());
        mTvFontSize.setTextColor(textColor);
        mTvFontSizeAdd.setTextColor(textColor);
        mTvFontSizeSub.setTextColor(textColor);
        if (isDark) {
            //夜间模式
            mTvFontSizeAdd.setBackgroundResource(R.drawable.bg_setting_font_night);
            mTvFontSizeSub.setBackgroundResource(R.drawable.bg_setting_font_night);
        } else {
            //日间模式
            mTvFontSizeAdd.setBackgroundResource(R.drawable.bg_setting_font_day);
            mTvFontSizeSub.setBackgroundResource(R.drawable.bg_setting_font_day);
        }
        mTvFontSize.setText(fontSizeOption.getValue() + "");
    }

    //行高设置
    private void updateLineHeight() {
        boolean isDark = ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue());
        int textColor = ZLAndroidColorUtil.rgb(myFBReader.ViewOptions.getColorProfile().MenuTextOption.getValue());
        mTvLine.setTextColor(textColor);
        mTvLineAdd.setTextColor(textColor);
        mTvLineSub.setTextColor(textColor);
        if (isDark) {
            //夜间模式
            mTvLineAdd.setBackgroundResource(R.drawable.bg_setting_font_night);
            mTvLineSub.setBackgroundResource(R.drawable.bg_setting_font_night);
        } else {
            //日间模式
            mTvLineAdd.setBackgroundResource(R.drawable.bg_setting_font_day);
            mTvLineSub.setBackgroundResource(R.drawable.bg_setting_font_day);
        }
        mTvLine.setText(myFBReader.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption.getValue() + "");
    }

    //更新翻页方式
    private void updatePageTurningMode() {
        boolean isDark = ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue());
        int textColor = isDark ?
                ColorUtil.getColor(mTvPageTurning1.getContext(), R.color.color_fbreader_menu_unselected_night, "#ffffff") :
                ColorUtil.getColor(mTvPageTurning1.getContext(), R.color.color_fbreader_menu_unselected_day, "#000000");
        int bg = isDark ? R.drawable.bg_setting_font_night : R.drawable.bg_setting_font_day;
        mTvPageTurning1.setTextColor(textColor);
        mTvPageTurning1.setBackgroundResource(bg);
        mTvPageTurning2.setTextColor(textColor);
        mTvPageTurning2.setBackgroundResource(bg);
        mTvPageTurning3.setTextColor(textColor);
        mTvPageTurning3.setBackgroundResource(bg);
        mTvPageTurning4.setTextColor(textColor);
        mTvPageTurning4.setBackgroundResource(bg);

        textColor = ColorUtil.getColor(mTvPageTurning1.getContext(), R.color.color_fbreader_menu_select, "#ff5500");
        switch (myFBReader.PageTurningOptions.Animation.getValue().toString()) {
            case "curl":
                mTvPageTurning1.setTextColor(textColor);
                mTvPageTurning1.setBackgroundResource(R.drawable.bg_setting_font_select);
                return;
            case "slide":
                mTvPageTurning2.setTextColor(textColor);
                mTvPageTurning2.setBackgroundResource(R.drawable.bg_setting_font_select);
                return;
            case "shift":
                mTvPageTurning3.setTextColor(textColor);
                mTvPageTurning3.setBackgroundResource(R.drawable.bg_setting_font_select);
                return;
            case "none":
                mTvPageTurning4.setTextColor(textColor);
                mTvPageTurning4.setBackgroundResource(R.drawable.bg_setting_font_select);
                return;
        }
    }

    //更新背景
    private void updateBg() {
        int backgroundColor = ZLAndroidColorUtil.rgb(myFBReader.ViewOptions.getColorProfile().MenuBackgroundOption.getValue());
        mViewBottomContainer.setBackgroundColor(backgroundColor);
    }

    //修改背景颜色
    private void changeBg(int color) {
        if (color != -1) {
            //设置为日间模式
            myFBReader.ViewOptions.ColorProfileName.setValue(ColorProfile.DAY);
            changeBg("");
            myFBReader.ViewOptions.getColorProfile().BackgroundOption.setValue(new ZLColor(color));
            myFBReader.ViewOptions.getColorProfile().RegularTextOption.setValue(new ZLColor(Color.argb(
                    255 - Color.alpha(color),
                    255 - Color.red(color),
                    255 - Color.green(color),
                    255 - Color.blue(color))));
            myFBReader.getViewWidget().reset();
            myFBReader.getViewWidget().repaint();
        }
    }

    //修改背景 wallpapers/wood.png
    private void changeBg(String path) {
        myFBReader.ViewOptions.getColorProfile().WallpaperOption.setValue(path);
        myFBReader.getViewWidget().reset();
        myFBReader.getViewWidget().repaint();
    }
}
