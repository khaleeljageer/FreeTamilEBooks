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

package org.geometerplus.fbreader.fbreader;

import org.geometerplus.zlibrary.core.util.ZLColor;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;
import org.geometerplus.zlibrary.text.view.ZLTextSimpleHighlighting;
import org.geometerplus.zlibrary.text.view.ZLTextView;

public final class TTSReadHighlighting extends ZLTextSimpleHighlighting {


    public TTSReadHighlighting(ZLTextView view, ZLTextPosition start, ZLTextPosition end) {
        super(view, start, end);
    }

    @Override
    public ZLColor getBackgroundColor() {
        return null;
    }

    @Override
    public ZLColor getForegroundColor() {
        return null;
    }

    @Override
    public ZLColor getOutlineColor() {
        return null;
    }

    @Override
    public ZLColor getBottomLineColor() {
        return new ZLColor(0, 0, 255);
    }
}
