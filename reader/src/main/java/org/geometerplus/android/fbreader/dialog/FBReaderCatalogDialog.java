package org.geometerplus.android.fbreader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.fragment.FBReaderBookMarkFragment;
import org.geometerplus.android.fbreader.fragment.FBReaderCatalogFragment;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:目录dialog<br>
 * author : yuanbin<br>
 * date : 2018/10/12 20:17
 */
public class FBReaderCatalogDialog extends DialogFragment {

    private View mRootView;
    private TextView mTvTitle;
    private TextView mTvMenuCatalog;
    private TextView mTvMenuBookMark;
    private View mViewLine;
    private ViewPager mViewPager;

    private int blackColor;
    private int selectColor;
    private int lineColor;
    private int selectPosition;
    private int backgroundColor;

    public static FBReaderCatalogDialog newInstance() {
        FBReaderCatalogDialog fragment = new FBReaderCatalogDialog();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initLayout();
        mRootView = inflater.inflate(R.layout.dialog_fbreader_catalog, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initLayout() {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.LEFT);
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setWindowAnimations(R.style.anim_fbreader_catalog_dialog);
            getDialog().getWindow().getDecorView().setPadding(0, 0, 0, 0);
            // 设置宽度为屏宽、靠近屏幕底部。
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.width = ((WindowManager) ((FBReaderApp) ZLApplication.Instance()).getReader().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
            wlp.width = (int) (wlp.width * 0.85);
            wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }

    private void initView() {
        mTvTitle = (TextView) mRootView.findViewById(R.id.id_dialog_fbreader_catalog_title);
        mTvMenuCatalog = (TextView) mRootView.findViewById(R.id.id_dialog_fbreader_catalog_menu_catalog);
        mTvMenuBookMark = (TextView) mRootView.findViewById(R.id.id_dialog_fbreader_catalog_menu_book_mark);
        mViewLine = mRootView.findViewById(R.id.id_dialog_fbreader_catalog_line);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.id_dialog_fbreader_catalog_vp);
        FBReaderApp app = (FBReaderApp) ZLApplication.Instance();
        mTvTitle.setText(app.Model.Book.getTitle());
        backgroundColor = ZLAndroidColorUtil.rgb(app.ViewOptions.getColorProfile().MenuBackgroundOption.getValue());
        blackColor = ZLAndroidColorUtil.rgb(app.ViewOptions.getColorProfile().BlackTextOption.getValue());
        selectColor = ZLAndroidColorUtil.rgb(app.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        lineColor = ZLAndroidColorUtil.rgb(app.ViewOptions.getColorProfile().ThinLineOption.getValue());
        updatePreference();
    }

    private void initData() {
        selectPosition = 0;
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(FBReaderCatalogFragment.newInstance());
        fragments.add(FBReaderBookMarkFragment.newInstance());
        mViewPager.setAdapter(new Adapter(getChildFragmentManager(), fragments));
    }

    private void initListener() {
        mTvMenuBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPosition == 0) {
                    mViewPager.setCurrentItem(1);
                }
            }
        });
        mTvMenuCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPosition == 1) {
                    mViewPager.setCurrentItem(0);
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition = position;
                updatePreference();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updatePreference() {
        mRootView.setBackgroundColor(backgroundColor);
        mTvTitle.setTextColor(blackColor);
        mTvMenuBookMark.setTextColor(blackColor);
        mTvMenuCatalog.setTextColor(blackColor);
        mViewLine.setBackgroundColor(lineColor);
        if (selectPosition == 0) {
            mTvMenuCatalog.setTextColor(selectColor);
        } else {
            mTvMenuBookMark.setTextColor(selectColor);
        }
    }

    private class Adapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;

        public Adapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }
    }
}
