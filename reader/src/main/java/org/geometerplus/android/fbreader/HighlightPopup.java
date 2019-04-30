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

import android.content.Intent;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.geometerplus.android.fbreader.util.FBReaderPercentUtils;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.TextBuildTraverser;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.view.SelectionCursor;
import org.geometerplus.zlibrary.text.view.ZLTextElementArea;
import org.geometerplus.zlibrary.ui.android.R;

public class HighlightPopup extends PopupPanel implements View.OnClickListener {
    public final static String ID = "HighlightPopup";

    private TextView mTvLight;
    private TextView mTvCopy;
    private TextView mTvDictionary;
    private TextView mTvShare;
    private Bookmark bookmark;
    ZLTextElementArea start;
    ZLTextElementArea end;

    HighlightPopup(FBReaderApp fbReader) {
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

        activity.getLayoutInflater().inflate(R.layout.highlight_panel, root);
        myWindow = (SimplePopupWindow) root.findViewById(R.id.highlight_selection_panel);

        mTvLight = (TextView) myWindow.findViewById(R.id.highlight_selection_panel_bookmark);
        mTvLight.setOnClickListener(this);
        mTvLight.setText(R.string.fbreader_selection_remove_highlight);
        mTvCopy = (TextView) myWindow.findViewById(R.id.highlight_selection_panel_copy);
        mTvCopy.setOnClickListener(this);
        mTvDictionary = (TextView) myWindow.findViewById(R.id.highlight_selection_panel_dictionary);
        mTvDictionary.setOnClickListener(this);
        mTvShare = (TextView) myWindow.findViewById(R.id.highlight_selection_panel_share);
        mTvShare.setOnClickListener(this);
    }

    public HighlightPopup move(ZLTextElementArea start, ZLTextElementArea end) {
        if (myWindow == null) {
            return this;
        }
        this.start = start;
        this.end = end;
        int selectionStartY = start.YStart;
        int selectionEndY = end.YEnd;

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
        return this;
    }

    public void bookmark(Bookmark mark) {
        bookmark = mark;
    }

    @Override
    protected void update() {
    }

    @Override
    protected void show_() {
        super.show_();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.highlight_selection_panel_copy) {
            if (start != null && end != null) {
                final TextBuildTraverser traverser = new TextBuildTraverser(((FBReaderApp) Application).BookTextView);
                traverser.traverse(start, end);
                final ClipboardManager clipboard =
                        (ClipboardManager) Application.getReader().getSystemService(android.app.Application.CLIPBOARD_SERVICE);
                clipboard.setText(traverser.getText());
                FBReader.toast("Copied");
            }
        } else if (i == R.id.highlight_selection_panel_share) {
            if (start != null && end != null) {
                final TextBuildTraverser traverser = new TextBuildTraverser(((FBReaderApp) Application).BookTextView);
                traverser.traverse(start, end);
                final String text = traverser.getText();
                final String title = ((FBReaderApp) Application).getCurrentBook().getTitle();

                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        ZLResource.resource("selection").getResource("quoteFrom").getValue().replace("%s", title)
                );
                intent.putExtra(Intent.EXTRA_TEXT, text);
                ((FBReaderApp) Application).getReader().startActivity(Intent.createChooser(intent, null));
            }
        } else if (i == R.id.highlight_selection_panel_dictionary) {
            Application.runAction(ActionCode.SELECTION_TRANSLATE);

        } else if (i == R.id.highlight_selection_panel_bookmark) {
            //Application.runAction(ActionCode.SELECTION_BOOKMARK);
            if (bookmark != null) {
                ((FBReaderApp) Application).deleteHighlight(bookmark);
            }

        }
        Application.hideActivePopup();
    }
}
