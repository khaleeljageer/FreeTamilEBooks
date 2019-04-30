package org.geometerplus.android.fbreader.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.FBReaderSettingActivity;
import org.geometerplus.android.fbreader.util.FBReaderPercentUtils;
import org.geometerplus.android.fbreader.util.FBReaderReadTimeUtils;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.zlibrary.core.util.ZLColor;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

/**
 * desc:<br>
 * author : yuanbin<br>
 * date : 2018/8/30 14:54
 */
public class MenuDialog extends Dialog {
    FBReader mContext;
    FBReaderApp myFBReaderApp;
    private View mViewMenuContainer;
    private View mViewMenuHeadContainer;
    private ImageView mIvMenuHeadBack;
    private ImageView mIvMenuHeadShare;
    private ImageView mIvMenuHeadBookMark;
    private View mViewMenuBottomContainer;
    private View mViewMenuBottomCatalog;
    private ImageView mMenuBottomCatalogIv;
    private TextView mMenuBottomCatalogTv;
    private View mViewMenuBottomProgress;
    private ImageView mMenuBottomProgressIv;
    private TextView mMenuBottomProgressTv;
    private View mViewMenuBottomSetting;
    private ImageView mMenuBottomSettingIv;
    private TextView mMenuBottomSettingTv;
    private View mViewMenuBottomLight;
    private ImageView mMenuBottomLightIv;
    private TextView mMenuBottomLightTv;

    private int selectedPosition;
    private boolean hasBookMark;

    public MenuDialog(@NonNull FBReader context, FBReaderApp myFBReaderApp) {
        super(context, R.style.style_fbreader_menu_dialog);
        this.mContext = context;
        this.myFBReaderApp = myFBReaderApp;
        selectedPosition = 0;
        setContentView(R.layout.dialog_fbreader_menu);
        initLayout();
        initView();
        initListener();
    }

    private void initLayout() {
        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setWindowAnimations(R.style.anim_fbreader_menu_dialog);
//        window.setBackgroundDrawableResource(R.color.transparent);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.dimAmount = 0f;
//        window.setAttributes(lp);
//        window.setGravity(Gravity.BOTTOM);
    }

    private void initListener() {
        mViewMenuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mIvMenuHeadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (myFBReaderApp != null) {
                    myFBReaderApp.runAction(ActionCode.SHOW_CANCEL_MENU);
                } else {
                    if (mContext != null) {
                        mContext.finish();
                    }
                }
            }
        });
        mIvMenuHeadShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null) {
                    mContext.runAction(ActionCode.SHARE_BOOK);
                }
                dismiss();
            }
        });
        mIvMenuHeadBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasBookMark) {
                    dismiss();
                    myFBReaderApp.addBookmark();
                    FBReader.toast(mContext.getString(R.string.fbreader_bookmark_add_success));
                } else {
                    dismiss();
                    myFBReaderApp.deleteBookmark();
                    FBReader.toast(mContext.getString(R.string.fbreader_bookmark_delete_success));
                }
            }
        });
        mViewMenuBottomCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext != null) {
                    mContext.runAction(ActionCode.SHOW_TOC);
                }
                selectedPosition = 0;
                updatePreference();
                dismiss();
            }
        });
        mViewMenuBottomProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mContext != null) {
//                    mContext.navigate();
//                }
//                dismiss();
                reset();
                selectedPosition = 1;
                updatePreference();
            }
        });
        mViewMenuBottomLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (myFBReaderApp != null) {
//                    if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
//                        myFBReaderApp.ViewOptions.ColorProfileName.setValue(ColorProfile.NIGHT);
//                        myFBReaderApp.getViewWidget().reset();
//                        myFBReaderApp.getViewWidget().repaint();
//                    } else {
//                        myFBReaderApp.ViewOptions.ColorProfileName.setValue(ColorProfile.DAY);
//                        myFBReaderApp.getViewWidget().reset();
//                        myFBReaderApp.getViewWidget().repaint();
//                    }
//                    updatePreference();
//                }
                reset();
                selectedPosition = 2;
                updatePreference();
            }
        });
        mViewMenuBottomSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mContext != null) {
//                    mContext.setting();
//                }
//                dismiss();
                reset();
                selectedPosition = 3;
                updatePreference();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                reset();
                FBReaderReadTimeUtils.unregister(readTimeListener);
            }
        });
    }

    private void reset() {
        selectedPosition = 0;
        resetProgress();
        resetLight();
        resetSetting();
    }

    private void initView() {
        mViewMenuContainer = findViewById(R.id.id_menu_container);
        mViewMenuHeadContainer = findViewById(R.id.id_menu_head_container);
        mIvMenuHeadBack = (ImageView) findViewById(R.id.id_menu_head_back);
        mIvMenuHeadShare = (ImageView) findViewById(R.id.id_menu_head_book_share);
        mIvMenuHeadBookMark = (ImageView) findViewById(R.id.id_menu_head_book_mark);
        mViewMenuBottomContainer = findViewById(R.id.id_menu_bottom_container);
        mViewMenuBottomCatalog = findViewById(R.id.id_menu_bottom_catalog);
        mMenuBottomCatalogIv = (ImageView) findViewById(R.id.id_menu_bottom_catalog_iv);
        mMenuBottomCatalogTv = (TextView) findViewById(R.id.id_menu_bottom_catalog_tv);
        mViewMenuBottomProgress = findViewById(R.id.id_menu_bottom_progress);
        mMenuBottomProgressIv = (ImageView) findViewById(R.id.id_menu_bottom_progress_iv);
        mMenuBottomProgressTv = (TextView) findViewById(R.id.id_menu_bottom_progress_tv);
        mViewMenuBottomSetting = findViewById(R.id.id_menu_bottom_setting);
        mMenuBottomSettingIv = (ImageView) findViewById(R.id.id_menu_bottom_setting_iv);
        mMenuBottomSettingTv = (TextView) findViewById(R.id.id_menu_bottom_setting_tv);
        mViewMenuBottomLight = findViewById(R.id.id_menu_bottom_light);
        mMenuBottomLightIv = (ImageView) findViewById(R.id.id_menu_bottom_light_iv);
        mMenuBottomLightTv = (TextView) findViewById(R.id.id_menu_bottom_light_tv);
        initProgressView();
        initLightView();
        initSettingView();
    }

    @Override
    public void show() {
        super.show();
        updatePreference();
        FBReaderReadTimeUtils.register(readTimeListener);
        if (mViewMenuBottomContainer != null) {
            mViewMenuBottomContainer.post(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mViewMenuBottomContainer, "translationY", mViewMenuBottomContainer.getMeasuredHeight(), 0);
                    animator.setDuration(200).start();
                }
            });
        }
    }

    //更新皮肤
    private void updatePreference() {
        if (myFBReaderApp != null) {
            int backgroundColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuBackgroundOption.getValue());
            int textColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuTextOption.getValue());
            mViewMenuHeadContainer.setBackgroundColor(backgroundColor);
            mViewMenuBottomContainer.setBackgroundColor(backgroundColor);

            if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
                //日间模式
                mIvMenuHeadBack.setImageResource(R.drawable.ic_fbreader_back_day);
                mIvMenuHeadShare.setImageResource(R.drawable.ic_fbreader_menu_head_share_day);
                mIvMenuHeadBookMark.setImageResource(R.drawable.ic_fbreader_menu_head_book_mark_unselected_day);
                mMenuBottomCatalogIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_catalog_unselected_day);
                mMenuBottomCatalogTv.setTextColor(textColor);
                mMenuBottomProgressIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_progress_unselected_day);
                mMenuBottomProgressTv.setTextColor(textColor);
                mMenuBottomSettingIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_setting_unselected_day);
                mMenuBottomSettingTv.setTextColor(textColor);
                mMenuBottomLightIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_unselected_day);
                mMenuBottomLightTv.setTextColor(textColor);
            } else {
                //夜间模式
                mIvMenuHeadBack.setImageResource(R.drawable.ic_fbreader_back_night);
                mIvMenuHeadShare.setImageResource(R.drawable.ic_fbreader_menu_head_share_night);
                mIvMenuHeadBookMark.setImageResource(R.drawable.ic_fbreader_menu_head_book_mark_unselected_night);
                mMenuBottomCatalogIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_catalog_unselected_night);
                mMenuBottomCatalogTv.setTextColor(textColor);
                mMenuBottomProgressIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_progress_unselected_night);
                mMenuBottomProgressTv.setTextColor(textColor);
                mMenuBottomSettingIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_setting_unselected_night);
                mMenuBottomSettingTv.setTextColor(textColor);
                mMenuBottomLightIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_unselected_night);
                mMenuBottomLightTv.setTextColor(textColor);
            }
            updateBookMarkStatus();
            if (selectedPosition == 0) {
                //目录被选中
                selectCatalog();
            } else if (selectedPosition == 1) {
                //进度被选中
                selectProgress();
            } else if (selectedPosition == 2) {
                //亮度被选中
                selectLight();
            } else if (selectedPosition == 3) {
                //设置被选中
                selectSetting();
            }
        }
    }

    private void updateBookMarkStatus() {
        if (myFBReaderApp.checkCurrentPageHasBookMark()) {
            hasBookMark = true;
            if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
                //日间模式
                mIvMenuHeadBookMark.setImageResource(R.drawable.ic_fbreader_menu_head_book_mark_selected_day);
            } else {
                //夜间模式
                mIvMenuHeadBookMark.setImageResource(R.drawable.ic_fbreader_menu_head_book_mark_selected_night);
            }
        } else {
            hasBookMark = false;
            if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
                //日间模式
                mIvMenuHeadBookMark.setImageResource(R.drawable.ic_fbreader_menu_head_book_mark_unselected_day);
            } else {
                //夜间模式
                mIvMenuHeadBookMark.setImageResource(R.drawable.ic_fbreader_menu_head_book_mark_unselected_night);
            }
        }
    }

    private void selectSetting() {
        int backgroundColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuBackgroundOption.getValue());
        int blackColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().BlackTextOption.getValue());
        int textColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
            //日间模式
            mMenuBottomSettingIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_setting_selected_day);
            mMenuBottomSettingTv.setTextColor(textColor);
            mViewSettingContainer.setBackgroundColor(backgroundColor);
            mTvSettingTurnPage.setTextColor(blackColor);
            mTvSettingFont.setTextColor(blackColor);
            //mTvSettingFontSelected.setTextColor(textColor);
            mTvSettingMore.setTextColor(blackColor);
        } else {
            //夜间模式
            mMenuBottomSettingIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_setting_selected_night);
            mMenuBottomSettingTv.setTextColor(textColor);
            mViewSettingContainer.setBackgroundColor(backgroundColor);
            mTvSettingTurnPage.setTextColor(blackColor);
            mTvSettingFont.setTextColor(blackColor);
            //mTvSettingFontSelected.setTextColor(textColor);
            mTvSettingMore.setTextColor(blackColor);
        }
        mViewSettingContainer.setVisibility(View.VISIBLE);
        mIvMenuBottomSettingShadow.setVisibility(View.VISIBLE);
        updateSettingView();
    }

    private void selectLight() {
        int backgroundColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuBackgroundOption.getValue());
        int blackTextColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().BlackTextOption.getValue());
        int textColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
            //日间模式
            mMenuBottomLightIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_selected_day);
            mMenuBottomLightTv.setTextColor(textColor);
            mViewLightContainer.setBackgroundColor(backgroundColor);
            mTvLightModeDaily.setTextColor(blackTextColor);
            mTvLightModeFresh.setTextColor(blackTextColor);
            mTvLightModeYellow.setTextColor(blackTextColor);
            mTvLightModeNight.setTextColor(blackTextColor);
            mTvLightModeEye.setTextColor(blackTextColor);
        } else {
            //夜间模式
            mMenuBottomLightIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_selected_night);
            mMenuBottomLightTv.setTextColor(textColor);
            mViewLightContainer.setBackgroundColor(backgroundColor);
            mTvLightModeDaily.setTextColor(blackTextColor);
            mTvLightModeFresh.setTextColor(blackTextColor);
            mTvLightModeYellow.setTextColor(blackTextColor);
            mTvLightModeNight.setTextColor(blackTextColor);
            mTvLightModeEye.setTextColor(blackTextColor);
        }
        mViewLightContainer.setVisibility(View.VISIBLE);
        mIvMenuBottomLightShadow.setVisibility(View.VISIBLE);
        updateLightView();
    }

    private void selectProgress() {
        int backgroundColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuBackgroundOption.getValue());
        int blackTextColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().BlackTextOption.getValue());
        int textColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
            //日间模式
            mMenuBottomProgressIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_progress_selected_day);
            mMenuBottomProgressTv.setTextColor(textColor);
            mViewProgressBottomContainer.setBackgroundColor(backgroundColor);
            mTvProgressTime.setTextColor(blackTextColor);
            mIvProgressLastChapter.setImageResource(R.drawable.ic_fbreader_back_day);
            mIvProgressNextChapter.setImageResource(R.drawable.ic_fbreader_back_day);
        } else {
            //夜间模式
            mMenuBottomProgressIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_progress_selected_night);
            mMenuBottomProgressTv.setTextColor(textColor);
            mViewProgressBottomContainer.setBackgroundColor(backgroundColor);
            mTvProgressTime.setTextColor(blackTextColor);
            mIvProgressLastChapter.setImageResource(R.drawable.ic_fbreader_back_night);
            mIvProgressNextChapter.setImageResource(R.drawable.ic_fbreader_back_night);
        }
        mViewProgressContainer.setVisibility(View.VISIBLE);
        updateProgressView();
    }

    private void selectCatalog() {
        int textColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
            //日间模式
           // mMenuBottomCatalogIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_catalog_selected_day);
            //mMenuBottomCatalogTv.setTextColor(textColor);
        } else {
            //夜间模式
           // mMenuBottomCatalogIv.setImageResource(R.drawable.ic_fbreader_menu_bottom_catalog_selected_night);
            //mMenuBottomCatalogTv.setTextColor(textColor);
        }
    }

    //region progress
    private View mViewProgressContainer;
    private View mViewProgressBottomContainer;
    private TextView mTvProgressTitle;
    private TextView mTvProgressProgress;
    private ImageView mIvProgressBack;
    private TextView mTvProgressTime;
    private ImageView mIvProgressLastChapter;
    private SeekBar mProgressSeekBar;
    private ImageView mIvProgressNextChapter;

    private ZLTextWordCursor startPosition = new ZLTextWordCursor();
    private ZLTextWordCursor endPosition = new ZLTextWordCursor();

    private void initProgressView() {
        mViewProgressContainer = findViewById(R.id.id_menu_bottom_progress_container);
        mViewProgressBottomContainer = findViewById(R.id.id_menu_bottom_progress_bottom_container);
        mTvProgressTitle = (TextView) findViewById(R.id.id_menu_bottom_progress_title);
        mTvProgressProgress = (TextView) findViewById(R.id.id_menu_bottom_progress_progress);
        mIvProgressBack = (ImageView) findViewById(R.id.id_menu_bottom_progress_back);
        mTvProgressTime = (TextView) findViewById(R.id.id_menu_bottom_progress_time);
        mIvProgressLastChapter = (ImageView) findViewById(R.id.id_menu_bottom_progress_last_chapter);
        mProgressSeekBar = (SeekBar) findViewById(R.id.id_menu_bottom_progress_seek_bar);
        int padding = FBReaderPercentUtils.dp2px(8);
        mProgressSeekBar.setPadding(padding, 0, padding, 0);
        mIvProgressNextChapter = (ImageView) findViewById(R.id.id_menu_bottom_progress_next_chapter);

        mViewProgressContainer.setOnClickListener(null);
        final ZLTextView.PagePosition pagePosition = myFBReaderApp.getTextView().pagePosition();
        mProgressSeekBar.setMax(pagePosition.Total - 1);
        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            public void onStartTrackingTouch(SeekBar seekBar) {
                startPosition.setCursor(myFBReaderApp.BookTextView.getStartCursor());
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                myFBReaderApp.storePosition();
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    final int page = progress + 1;
                    final ZLTextView view = myFBReaderApp.getTextView();
                    if (page == 1) {
                        view.gotoHome();
                    } else {
                        view.gotoPage(page);
                    }
                    myFBReaderApp.getViewWidget().reset();
                    myFBReaderApp.getViewWidget().repaint();
                    updateProgressView();
                }
            }
        });

        //region listener
        mIvProgressBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startPosition.getParagraphCursor() != null) {
                    endPosition.setCursor(myFBReaderApp.BookTextView.getStartCursor());
                    gotoPage(startPosition);
                    startPosition.reset();
                    updateProgressView();
                } else if (endPosition.getParagraphCursor() != null) {
                    startPosition.setCursor(myFBReaderApp.BookTextView.getStartCursor());
                    gotoPage(endPosition);
                    endPosition.reset();
                    updateProgressView();
                }
            }
        });

        mIvProgressNextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TOCTree tocElement = myFBReaderApp.getNextTOCElement();
                if (tocElement == null) {
                    FBReader.toast(mContext.getString(R.string.fbreader_turn_chapter_tips_cant_next));
                } else {
                    startPosition.setCursor(myFBReaderApp.BookTextView.getStartCursor());
                    myFBReaderApp.addInvisibleBookmark();
                    myFBReaderApp.BookTextView.gotoPosition(tocElement.getReference().ParagraphIndex, 0, 0);
                    myFBReaderApp.showBookTextView();
                    myFBReaderApp.storePosition();
                    updateProgressView();
                }
            }
        });

        mIvProgressLastChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TOCTree tocElement = myFBReaderApp.getPreTOCElement();
                if (tocElement == null) {
                    FBReader.toast(mContext.getString(R.string.fbreader_turn_chapter_tips_cant_previous));
                } else {
                    startPosition.setCursor(myFBReaderApp.BookTextView.getStartCursor());
                    myFBReaderApp.addInvisibleBookmark();
                    myFBReaderApp.BookTextView.gotoPosition(tocElement.getReference().ParagraphIndex, 0, 0);
                    myFBReaderApp.showBookTextView();
                    myFBReaderApp.storePosition();
                    updateProgressView();
                }
            }
        });

        //endregion

    }

    private void updateProgressView() {
        //更新title
        final TOCTree tocElement = myFBReaderApp.getCurrentTOCElement();
        if (tocElement != null) {
            mTvProgressTitle.setText(tocElement.getText());
        } else {
            mTvProgressTitle.setText(R.string.fbreader_unknown);
        }

        ZLTextView textView = myFBReaderApp.getTextView();
        ZLTextView.PagePosition pagePosition = textView.pagePosition();
        int currentPosition = pagePosition.Current;
        int totalPage = pagePosition.Total;
        float percent = 100f * currentPosition / totalPage;
        mTvProgressProgress.setText(mContext.getString(R.string.fbreader_progress_tips, currentPosition, totalPage, percent) + "%");
        mProgressSeekBar.setMax(pagePosition.Total - 1);
        mProgressSeekBar.setProgress(currentPosition - 1);

        if (startPosition.getParagraphCursor() == null) {
            mIvProgressBack.setImageResource(R.drawable.ic_fbreader_menu_progress_back);
        } else {
            mIvProgressBack.setImageResource(R.drawable.ic_fbreader_menu_progress_next);
        }

        readTime = FBReaderReadTimeUtils.getReadTime() / 60;
        mTvProgressTime.setText(createTime(FBReaderReadTimeUtils.getReadTime()));
        updateBookMarkStatus();
    }

    private void gotoPage(ZLTextWordCursor position) {
        myFBReaderApp.BookTextView.gotoPosition(position);
        myFBReaderApp.getViewWidget().reset();
        myFBReaderApp.getViewWidget().repaint();
    }

    private void resetProgress() {
        startPosition.reset();
        endPosition.reset();
        mViewProgressContainer.setVisibility(View.GONE);
    }

    private int readTime;
    private FBReaderReadTimeUtils.OnReadTimeListener readTimeListener = new FBReaderReadTimeUtils.OnReadTimeListener() {
        @Override
        public void onReadTime(int s) {
            if (readTime < s / 60 && isShowing() && mTvProgressTime != null) {
                readTime = s / 60;
                mTvProgressTime.setText(createTime(s));
            }
        }
    };

    private String createTime(int s) {
        int hour = s/60/60;
        int minute = s/60;
        return mContext.getString(R.string.fbreader_progress_read_time, hour, minute);
    }
    //endregion

    //region light
    private View mViewLightContainer;
    private ImageView mIvLightSmallLight;
    private SeekBar mLLightSeekBar;
    private ImageView mIvLightBigLight;
    private View mViewLightModeDailyContainer;
    private ImageView mIvLightModeDaily;
    private TextView mTvLightModeDaily;
    private View mViewLightModeFreshContainer;
    private ImageView mIvLightModeFresh;
    private TextView mTvLightModeFresh;
    private View mViewLightModeYellowContainer;
    private ImageView mIvLightModeYellow;
    private TextView mTvLightModeYellow;
    private View mViewLightModeNightContainer;
    private ImageView mIvLightModeNight;
    private TextView mTvLightModeNight;
    private View mViewLightModeEyeContainer;
    private ImageView mIvLightModeEye;
    private TextView mTvLightModeEye;
    private ImageView mIvMenuBottomLightShadow;
    private void initLightView() {
        mViewLightContainer = findViewById(R.id.id_menu_bottom_light_container);
        mIvMenuBottomLightShadow = (ImageView) findViewById(R.id.id_menu_bottom_light_shadow);
        mViewLightContainer.setOnClickListener(null);
        mIvLightSmallLight = (ImageView) findViewById(R.id.id_menu_bottom_light_small_light);
        mLLightSeekBar = (SeekBar) findViewById(R.id.id_menu_bottom_light_seek_bar);
        int padding = FBReaderPercentUtils.dp2px(8);
        mLLightSeekBar.setPadding(padding, 0, padding, 0);
        mIvLightBigLight = (ImageView) findViewById(R.id.id_menu_bottom_light_big_light);
        mViewLightModeDailyContainer = findViewById(R.id.id_menu_bottom_light_mode_daily_container);
        mIvLightModeDaily = (ImageView) findViewById(R.id.id_menu_bottom_light_mode_daily_iv);
        mTvLightModeDaily = (TextView) findViewById(R.id.id_menu_bottom_light_mode_daily_desc);
        mViewLightModeFreshContainer = findViewById(R.id.id_menu_bottom_light_mode_fresh_container);
        mIvLightModeFresh = (ImageView) findViewById(R.id.id_menu_bottom_light_mode_fresh_iv);
        mTvLightModeFresh = (TextView) findViewById(R.id.id_menu_bottom_light_mode_fresh_desc);
        mViewLightModeYellowContainer = findViewById(R.id.id_menu_bottom_light_mode_yellow_container);
        mIvLightModeYellow = (ImageView) findViewById(R.id.id_menu_bottom_light_mode_yellow_iv);
        mTvLightModeYellow = (TextView) findViewById(R.id.id_menu_bottom_light_mode_yellow_desc);
        mViewLightModeNightContainer = findViewById(R.id.id_menu_bottom_light_mode_night_container);
        mIvLightModeNight = (ImageView) findViewById(R.id.id_menu_bottom_light_mode_night_iv);
        mTvLightModeNight = (TextView) findViewById(R.id.id_menu_bottom_light_mode_night_desc);
        mViewLightModeEyeContainer = findViewById(R.id.id_menu_bottom_light_mode_eye_container);
        mIvLightModeEye = (ImageView) findViewById(R.id.id_menu_bottom_light_mode_eye_iv);
        mTvLightModeEye = (TextView) findViewById(R.id.id_menu_bottom_light_mode_eye_desc);

        mLLightSeekBar.setMax(100);
        mLLightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean myIsBrightnessAdjustmentInProgress;

            public void onStartTrackingTouch(SeekBar seekBar) {
                myIsBrightnessAdjustmentInProgress = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                myIsBrightnessAdjustmentInProgress = false;
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (myIsBrightnessAdjustmentInProgress) {
                        myFBReaderApp.getViewWidget().setScreenBrightness(progress);
                        return;
                    }
                }
            }
        });

        mViewLightModeDailyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#f4f4f4"));
            }
        });

        mViewLightModeFreshContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#d6fde7"));
            }
        });

        mViewLightModeYellowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#fff5e4"));
            }
        });

        mViewLightModeNightContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNightMode();
            }
        });

        mViewLightModeEyeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(Color.parseColor("#dbf1ff"));
            }
        });
    }

    private void updateLightView() {
        int progress = myFBReaderApp.getViewWidget().getScreenBrightness();
        mLLightSeekBar.setProgress(progress);
        mIvLightModeDaily.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_daily_unselected);
        mIvLightModeFresh.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_fresh_unselected);
        mIvLightModeYellow.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_yellow_unselected);
        mIvLightModeNight.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_night_unselected);
        mIvLightModeEye.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_blue_unselected);
        if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
            //日间模式
            ZLColor color = myFBReaderApp.ViewOptions.getColorProfile().BackgroundOption.getValue();
            if (color.equals(new ZLColor(Color.parseColor("#f4f4f4")))) {
                mIvLightModeDaily.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_daily_selected);
            } else if (color.equals(new ZLColor(Color.parseColor("#d6fde7")))) {
                mIvLightModeFresh.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_fresh_selected);
            } else if (color.equals(new ZLColor(Color.parseColor("#fff5e4")))) {
                mIvLightModeYellow.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_yellow_selected);
            } else if (color.equals(new ZLColor(Color.parseColor("#dbf1ff")))) {
                mIvLightModeEye.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_blue_selected);
            }
        } else {
            //夜间模式
            mIvLightModeNight.setImageResource(R.drawable.ic_fbreader_menu_bottom_light_mode_night_selected);
        }
    }

    private void resetLight() {
        mViewLightContainer.setVisibility(View.GONE);
        mIvMenuBottomLightShadow.setVisibility(View.GONE);
    }

    //修改背景颜色
    private void changeBg(int color) {
        //设置为日间模式
        myFBReaderApp.ViewOptions.ColorProfileName.setValue(ColorProfile.DAY);
        changeBg("");
        myFBReaderApp.ViewOptions.getColorProfile().BackgroundOption.setValue(new ZLColor(color));
        myFBReaderApp.ViewOptions.getColorProfile().RegularTextOption.setValue(new ZLColor(Color.argb(
                255 - Color.alpha(color),
                255 - Color.red(color),
                255 - Color.green(color),
                255 - Color.blue(color))));
        myFBReaderApp.getViewWidget().reset();
        myFBReaderApp.getViewWidget().repaint();
        updateLightView();
    }

    //修改背景 wallpapers/wood.png
    private void changeBg(String path) {
        myFBReaderApp.ViewOptions.getColorProfile().WallpaperOption.setValue(path);
        myFBReaderApp.getViewWidget().reset();
        myFBReaderApp.getViewWidget().repaint();
    }

    //夜间模式
    private void changeNightMode() {
        myFBReaderApp.ViewOptions.ColorProfileName.setValue(ColorProfile.NIGHT);
        myFBReaderApp.getViewWidget().reset();
        myFBReaderApp.getViewWidget().repaint();
        updateLightView();
    }
    //endregion

    //region setting
    private View mViewSettingContainer;
    private ImageView mIvSettingFontSub;
    private SeekBar mSettingSeekBar;
    private ImageView mIvSettingFontAdd;
    private TextView mTvSettingTurnPage;
    //仿真
    private TextView mTvSettingTurnPageCurl;
    //平移
    private TextView mTvSettingTurnPageShift;
    //滑动
    private TextView mTvSettingTurnPageSlide;
    //无
    private TextView mTvSettingTurnPageNone;

    private View mViewSettingFontContainer;
    private TextView mTvSettingFont;
    private TextView mTvSettingFontSelected;
    private ImageView mIvSettingFontMore;
    private View mViewSettingMoreContainer;
    private TextView mTvSettingMore;
    private ImageView mIvSettingMoreArrow;
    private ImageView mIvMenuBottomSettingShadow;

    private void initSettingView() {
        mViewSettingContainer = findViewById(R.id.id_menu_bottom_setting_container);
        mViewSettingContainer.setOnClickListener(null);
        mIvMenuBottomSettingShadow = (ImageView) findViewById(R.id.id_menu_bottom_setting_shadow);
        mIvSettingFontSub = (ImageView) findViewById(R.id.id_menu_bottom_setting_font_sub);
        mSettingSeekBar = (SeekBar) findViewById(R.id.id_menu_bottom_setting_font_seek_bar);
        int padding = FBReaderPercentUtils.dp2px(8);
        mSettingSeekBar.setPadding(padding, 0, padding, 0);
        mIvSettingFontAdd = (ImageView) findViewById(R.id.id_menu_bottom_setting_font_add);

        mTvSettingTurnPage = (TextView) findViewById(R.id.id_menu_bottom_setting_turning_page);
        mTvSettingTurnPageCurl = (TextView) findViewById(R.id.id_menu_bottom_setting_turning_page_curl);
        mTvSettingTurnPageShift = (TextView) findViewById(R.id.id_menu_bottom_setting_turning_page_shift);
        mTvSettingTurnPageSlide = (TextView) findViewById(R.id.id_menu_bottom_setting_turning_page_slide);
        mTvSettingTurnPageNone = (TextView) findViewById(R.id.id_menu_bottom_setting_turning_page_none);

        mViewSettingFontContainer = findViewById(R.id.id_menu_bottom_setting_font_container);
        mTvSettingFont = (TextView) findViewById(R.id.id_menu_bottom_setting_font);
        mTvSettingFontSelected = (TextView) findViewById(R.id.id_menu_bottom_setting_font_name);
        mIvSettingFontMore = (ImageView) findViewById(R.id.id_menu_bottom_setting_font_arrow);

        mViewSettingMoreContainer = findViewById(R.id.id_menu_bottom_setting_more_container);
        mTvSettingMore = (TextView) findViewById(R.id.id_menu_bottom_setting_more);
        mIvSettingMoreArrow = (ImageView) findViewById(R.id.id_menu_bottom_setting_more_arrow);


        mSettingSeekBar.setMax(myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption.MaxValue);
        mSettingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption.setValue(
                            24 + progress
                    );
                    myFBReaderApp.clearTextCaches();
                    myFBReaderApp.getViewWidget().repaint();
                }
            }
        });

        mIvSettingFontSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mSettingSeekBar.getProgress() - 1;
                mSettingSeekBar.setProgress(progress);
                myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption.setValue(
                        24 + progress
                );
                myFBReaderApp.clearTextCaches();
                myFBReaderApp.getViewWidget().repaint();
            }
        });

        mIvSettingFontAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mSettingSeekBar.getProgress() + 1;
                mSettingSeekBar.setProgress(progress);
                myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption.setValue(
                        24 + progress
                );
                myFBReaderApp.clearTextCaches();
                myFBReaderApp.getViewWidget().repaint();
            }
        });

        mTvSettingTurnPageCurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //仿真
                myFBReaderApp.PageTurningOptions.Animation.setValue(ZLView.Animation.curl);
                updatePageTurningMode();
            }
        });

        mTvSettingTurnPageShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //平移
                myFBReaderApp.PageTurningOptions.Animation.setValue(ZLView.Animation.shift);
                updatePageTurningMode();
            }
        });

        mTvSettingTurnPageSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //滑动
                myFBReaderApp.PageTurningOptions.Animation.setValue(ZLView.Animation.slide);
                updatePageTurningMode();
            }
        });

        mTvSettingTurnPageNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //无
                myFBReaderApp.PageTurningOptions.Animation.setValue(ZLView.Animation.none);
                updatePageTurningMode();
            }
        });

        mViewSettingFontContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "font click");
            }
        });

        mViewSettingMoreContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBReaderSettingActivity.start(getContext());
                dismiss();
            }
        });
    }

    private void updateSettingView() {
        int progress = myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption.getValue();
        mSettingSeekBar.setProgress(progress - 24);
        //mTvSettingFontSelected.setText(myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontFamilyOption.getValue());
        mTvSettingFontSelected.setText(R.string.fbreader_system);
        updatePageTurningMode();
    }

    private void updatePageTurningMode() {
        int selectedColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        int blackColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().BlackTextOption.getValue());
        mTvSettingTurnPageCurl.setTextColor(blackColor);
        mTvSettingTurnPageCurl.setBackgroundResource(0);
        mTvSettingTurnPageShift.setTextColor(blackColor);
        mTvSettingTurnPageShift.setBackgroundResource(0);
        mTvSettingTurnPageSlide.setTextColor(blackColor);
        mTvSettingTurnPageSlide.setBackgroundResource(0);
        mTvSettingTurnPageNone.setTextColor(blackColor);
        mTvSettingTurnPageNone.setBackgroundResource(0);
        int selectedBackground = ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue()) ?
                R.drawable.bg_fbreader_setting_turn_page_selected_day : R.drawable.bg_fbreader_setting_turn_page_selected_night;
        switch (myFBReaderApp.PageTurningOptions.Animation.getValue().toString()) {
            case "curl":
                //仿真
                mTvSettingTurnPageCurl.setTextColor(selectedColor);
                mTvSettingTurnPageCurl.setBackgroundResource(selectedBackground);
                return;
            case "slide":
                //滑动
                mTvSettingTurnPageSlide.setTextColor(selectedColor);
                mTvSettingTurnPageSlide.setBackgroundResource(selectedBackground);
                return;
            case "shift":
                //平移
                mTvSettingTurnPageShift.setTextColor(selectedColor);
                mTvSettingTurnPageShift.setBackgroundResource(selectedBackground);
                return;
            case "none":
                //无
                mTvSettingTurnPageNone.setTextColor(selectedColor);
                mTvSettingTurnPageNone.setBackgroundResource(selectedBackground);
                return;
        }
    }

    private void resetSetting() {
        mViewSettingContainer.setVisibility(View.GONE);
        mIvMenuBottomSettingShadow.setVisibility(View.GONE);
    }
    //endregion
}
