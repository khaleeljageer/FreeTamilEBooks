/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
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

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.geometerplus.android.fbreader.util.FBReaderPercentUtils;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.zlibrary.core.view.SelectionCursor;
import org.geometerplus.zlibrary.ui.android.R;

public class SelectionPopup extends PopupPanel implements View.OnClickListener {
    public final static String ID = "SelectionPopup";

    private TextView mTvLight;
    private TextView mTvCopy;
    private TextView mTvDictionary;
    private TextView mTvShare;

    SelectionPopup(FBReaderApp fbReader) {
        super(fbReader);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void createControlPanel(FBReader activity, RelativeLayout root) {
        if (myWindow != null && activity == myWindow.getContext()) {
            return;
        }
        activity.getLayoutInflater().inflate(R.layout.selection_panel, root);
        myWindow = (SimplePopupWindow) root.findViewById(R.id.selection_panel);
        mTvLight = (TextView) myWindow.findViewById(R.id.selection_panel_bookmark);
        mTvLight.setOnClickListener(this);
        mTvCopy = (TextView) myWindow.findViewById(R.id.selection_panel_copy);
        mTvCopy.setOnClickListener(this);
        mTvDictionary = (TextView) myWindow.findViewById(R.id.selection_panel_dictionary);
        mTvDictionary.setOnClickListener(this);
        mTvShare = (TextView) myWindow.findViewById(R.id.selection_panel_share);
        mTvShare.setOnClickListener(this);
    }

    public void move(int selectionStartY, int selectionEndY) {
        if (myWindow == null) {
            return;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		final int verticalPosition;
//		final int screenHeight = ((View)myWindow.getParent()).getHeight();
//		final int diffTop = screenHeight - selectionEndY;
//		final int diffBottom = selectionStartY;
//		if (diffTop > diffBottom) {
//			verticalPosition = diffTop > myWindow.getHeight() + 20
//				? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
//		} else {
//			verticalPosition = diffBottom > myWindow.getHeight() + 20
//				? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
//		}
//
//		layoutParams.addRule(verticalPosition);
        final int screenHeight = ((View) myWindow.getParent()).getHeight();
        final int cursorHeight = SelectionCursor.getCursorHeight();
        final int windowHeight = FBReaderPercentUtils.dp2px(45);
        if (selectionEndY + windowHeight * 2 + cursorHeight < screenHeight) {
            int marginTop = selectionEndY + cursorHeight;
            layoutParams.setMargins(0, marginTop + 20, 0, 0);
        } else if (selectionStartY - windowHeight * 2 - cursorHeight > 0) {
            int marginTop = selectionStartY - windowHeight - cursorHeight;
            layoutParams.setMargins(0, marginTop, 0, 0);
        } else {
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        }
        myWindow.setLayoutParams(layoutParams);
    }

    @Override
    protected void update() {
    }

    @Override
    protected void show_() {
        super.show_();
        if (myWindow != null) {
            FBView view = getReader().BookTextView;
            move(view.getSelectionStartY(),
                    view.getSelectionEndY());
        }
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.selection_panel_copy) {
            Application.runAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD);

        } else if (i == R.id.selection_panel_share) {
            Application.runAction(ActionCode.SELECTION_SHARE);

        } else if (i == R.id.selection_panel_dictionary) {
            Application.runAction(ActionCode.SELECTION_TRANSLATE);

        } else if (i == R.id.selection_panel_bookmark) {
            Application.runAction(ActionCode.SELECTION_BOOKMARK);

        }
        Application.hideActivePopup();
    }
}
