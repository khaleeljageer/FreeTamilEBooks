/*
 * Copyright (C) 2009-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

@Deprecated
final class NavigationPopup extends ZLApplication.PopupPanel {
    final static String ID = "NavigationPopup";

    private volatile NavigationWindow myWindow;
    private volatile FBReader myActivity;
    private volatile RelativeLayout myRoot;
    private ZLTextWordCursor myStartPosition;
    private final FBReaderApp myFBReader;
    private volatile boolean myIsInProgress;
    private SeekBar mSeekBar;
    private TextView mTvTitle;
    private View mViewBottomContainer;
    private boolean isShowing;
    NavigationPopup(FBReaderApp fbReader) {
        super(fbReader);
        myFBReader = fbReader;
    }

    public void setPanelInfo(FBReader activity, RelativeLayout root) {
        myActivity = activity;
        myRoot = root;
    }

    public void runNavigation() {
        if (myWindow == null || myWindow.getVisibility() == View.GONE) {
            myIsInProgress = false;
            if (myStartPosition == null) {
                myStartPosition = new ZLTextWordCursor(myFBReader.getTextView().getStartCursor());
            }
            Application.showPopup(ID);
        }
    }

    @Override
    protected void show_() {
        isShowing = true;
        if (myActivity != null) {
            createPanel(myActivity, myRoot);
        }
        if (myWindow != null) {
            myWindow.show();
            setupNavigation();
        }
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    @Override
    protected void hide_() {
        isShowing =false;
        if (myWindow != null) {
            myWindow.hide();
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected void update() {
        if (!myIsInProgress && myWindow != null) {
            setupNavigation();
        }
    }

    private void createPanel(FBReader activity, RelativeLayout root) {
        if (myWindow != null && activity == myWindow.getContext()) {
            return;
        }

        activity.getLayoutInflater().inflate(R.layout.navigation_panel, root);
        myWindow = (NavigationWindow) root.findViewById(R.id.navigation_panel);
        mViewBottomContainer = root.findViewById(R.id.id_navigation_panel_bottom_container);
        mViewBottomContainer.setOnClickListener(null);
        mSeekBar = (SeekBar) myWindow.findViewById(R.id.navigation_slider);
        mTvTitle = (TextView) myWindow.findViewById(R.id.navigation_text);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private void gotoPage(int page) {
                final ZLTextView view = myFBReader.getTextView();
                if (page == 1) {
                    view.gotoHome();
                } else {
                    view.gotoPage(page);
                }
                myFBReader.getViewWidget().reset();
                myFBReader.getViewWidget().repaint();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                myIsInProgress = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                myIsInProgress = false;
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    final int page = progress + 1;
                    final int pagesNumber = seekBar.getMax() + 1;
                    gotoPage(page);
                    mTvTitle.setText(makeProgressText(page, pagesNumber));
                }
            }
        });
        //region 隐藏确定和取消
        //		final Button btnOk = (Button)myWindow.findViewById(R.id.navigation_ok);
        //		final Button btnCancel = (Button)myWindow.findViewById(R.id.navigation_cancel);
        //		View.OnClickListener listener = new View.OnClickListener() {
        //			public void onClick(View v) {
        //				final ZLTextWordCursor position = myStartPosition;
        //				if (v == btnCancel && position != null) {
        //					myFBReader.getTextView().gotoPosition(position);
        //				} else if (v == btnOk) {
        //					if (myStartPosition != null &&
        //						!myStartPosition.equals(myFBReader.getTextView().getStartCursor())) {
        //						myFBReader.addInvisibleBookmark(myStartPosition);
        //						myFBReader.storePosition();
        //					}
        //				}
        //				myStartPosition = null;
        //				Application.hideActivePopup();
        //				myFBReader.getViewWidget().reset();
        //				myFBReader.getViewWidget().repaint();
        //			}
        //		};
        //		btnOk.setOnClickListener(listener);
        //		btnCancel.setOnClickListener(listener);
        //		btnOk.setText(buttonResource.getResource("ok").getValue());
        //		btnCancel.setText(buttonResource.getResource("cancel").getValue());
        // endregion
        myWindow.findViewById(R.id.id_navigation_panel_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartPosition = null;
                Application.hideActivePopup();
                myFBReader.getViewWidget().reset();
                myFBReader.getViewWidget().repaint();
            }
        });
        final ZLResource buttonResource = ZLResource.resource("dialog").getResource("button");
    }

    private void setupNavigation() {
        if (mSeekBar != null && mTvTitle != null) {
            int backgroundColor = ZLAndroidColorUtil.rgb(myFBReader.ViewOptions.getColorProfile().MenuBackgroundOption.getValue());
            int textColor = ZLAndroidColorUtil.rgb(myFBReader.ViewOptions.getColorProfile().MenuTextOption.getValue());
            mTvTitle.setTextColor(textColor);
            mViewBottomContainer.setBackgroundColor(backgroundColor);
            if (ColorProfile.DAY.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
                //日间模式
                mSeekBar.setThumb(myActivity.getResources().getDrawable(R.drawable.bg_navigation_seekbar_thumb_day));
                Rect bounds = mSeekBar.getProgressDrawable().getBounds();
                Drawable drawable = myActivity.getResources().getDrawable(R.drawable.drawable_navigation_seekbar_day);
                mSeekBar.setProgressDrawable(drawable);
                mSeekBar.getProgressDrawable().setBounds(bounds);
            } else {
                //夜间模式
                mSeekBar.setThumb(myActivity.getResources().getDrawable(R.drawable.bg_navigation_seekbar_thumb_night));
                Rect bounds = mSeekBar.getProgressDrawable().getBounds();
                Drawable drawable = myActivity.getResources().getDrawable(R.drawable.drawable_navigation_seekbar_night);
                mSeekBar.setProgressDrawable(drawable);
                mSeekBar.getProgressDrawable().setBounds(bounds);
            }
            final ZLTextView textView = myFBReader.getTextView();
            final ZLTextView.PagePosition pagePosition = textView.pagePosition();

            if (mSeekBar.getMax() != pagePosition.Total - 1 || mSeekBar.getProgress() != pagePosition.Current - 1) {
                mSeekBar.setMax(pagePosition.Total - 1);
                mSeekBar.setProgress(pagePosition.Current - 1);
                mTvTitle.setText(makeProgressText(pagePosition.Current, pagePosition.Total));
            }
        }
    }

    private String makeProgressText(int page, int pagesNumber) {
        final StringBuilder builder = new StringBuilder();
        builder.append(page);
        builder.append("/");
        builder.append(pagesNumber);
        final TOCTree tocElement = myFBReader.getCurrentTOCElement();
        if (tocElement != null) {
            builder.append("  ");
            builder.append(tocElement.getText());
        }
        return builder.toString();
    }

    final void removeWindow(Activity activity) {
        if (myWindow != null && activity == myWindow.getContext()) {
            final ViewGroup root = (ViewGroup) myWindow.getParent();
            myWindow.hide();
            root.removeView(myWindow);
            myWindow = null;
        }
    }
}
