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

package org.geometerplus.zlibrary.core.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.util.ZLColor;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.image.BitmapImageData;
import org.geometerplus.zlibrary.ui.android.image.ZLBitmapImage;

public abstract class SelectionCursor {
    public enum Which {
        Left,
        Right
    }

    public static void draw(ZLPaintContext context, Which which, int x, int y, ZLColor color) {
//        context.setFillColor(color);
//        final int dpi = ZLibrary.Instance().getDisplayDPI();
//        final int unit = dpi / 120;
//        final int xCenter = which == Which.Left ? x - unit - 1 : x + unit + 1;
//        context.fillRectangle(xCenter - unit, y + dpi / 8, xCenter + unit, y - dpi / 8);
//        if (which == Which.Left) {
//            context.fillCircle(xCenter, y - dpi / 8, unit * 6);
//        } else {
//            context.fillCircle(xCenter, y + dpi / 8, unit * 6);
//        }
//        if (which == Which.Left) {
//            FBReaderApp myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
//            int fontSize = myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption.getValue() * 2;
//            int lineHeight = myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption.getValue() * 2;
//            Bitmap bitmap = BitmapFactory.decodeResource(ZLApplication.Instance().getReader().getApplicationContext().getResources(), R.drawable.ic_fbreader_select_cursor_left);
//            context.drawImage(x - bitmap.getWidth() / 2, (y * 2 - fontSize - lineHeight) / 2 + lineHeight, BitmapImageData.get(new ZLBitmapImage(bitmap)),
//                    new ZLPaintContext.Size(bitmap.getWidth(), bitmap.getHeight()), ZLPaintContext.ScalingType.OriginalSize, ZLPaintContext.ColorAdjustingMode.NONE);
//        } else {
//            FBReaderApp myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
//            int fontSize = myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption.getValue() * 2;
//            fontSize += myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption.getValue() * 2;
//            Bitmap bitmap = BitmapFactory.decodeResource(ZLApplication.Instance().getReader().getApplicationContext().getResources(), R.drawable.ic_fbreader_select_cursor_right);
//            context.drawImage(x - bitmap.getWidth() / 2, y + fontSize, BitmapImageData.get(new ZLBitmapImage(bitmap)),
//                    new ZLPaintContext.Size(bitmap.getWidth(), bitmap.getHeight()), ZLPaintContext.ScalingType.OriginalSize, ZLPaintContext.ColorAdjustingMode.NONE);
//        }

        if (which == Which.Left) {
            Bitmap bitmap = BitmapFactory.decodeResource(ZLApplication.Instance().getReader().getApplicationContext().getResources(), R.drawable.ic_fbreader_select_cursor_left);
            context.drawImage(x - bitmap.getWidth() / 2, y, BitmapImageData.get(new ZLBitmapImage(bitmap)),
                    new ZLPaintContext.Size(bitmap.getWidth(), bitmap.getHeight()), ZLPaintContext.ScalingType.OriginalSize, ZLPaintContext.ColorAdjustingMode.NONE);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(ZLApplication.Instance().getReader().getApplicationContext().getResources(), R.drawable.ic_fbreader_select_cursor_right);
            context.drawImage(x - bitmap.getWidth() / 2, y + bitmap.getHeight(), BitmapImageData.get(new ZLBitmapImage(bitmap)),
                    new ZLPaintContext.Size(bitmap.getWidth(), bitmap.getHeight()), ZLPaintContext.ScalingType.OriginalSize, ZLPaintContext.ColorAdjustingMode.NONE);
        }
    }

    public static int getCursorWidth() {
        return BitmapFactory.decodeResource(ZLApplication.Instance().getReader().getApplicationContext().getResources(), R.drawable.ic_fbreader_select_cursor_left).getWidth();
    }
    public static int getCursorHeight() {
        return BitmapFactory.decodeResource(ZLApplication.Instance().getReader().getApplicationContext().getResources(), R.drawable.ic_fbreader_select_cursor_left).getHeight();
    }
}
