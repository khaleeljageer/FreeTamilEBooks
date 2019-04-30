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

package org.geometerplus.zlibrary.core.view;

import android.graphics.Rect;

import java.util.*;

public final class HorizontalConvexHull implements Hull {
	private final LinkedList<Rect> myRectangles = new LinkedList<Rect>();

	public HorizontalConvexHull(Collection<Rect> rects) {
		for (Rect r : rects) {
			addRect(r);
		}
		//normalize();
	}

	private void addRect(Rect rectangle) {
		if (myRectangles.isEmpty()) {
			myRectangles.add(new Rect(rectangle));
			return;
		}
		final int top = rectangle.top;
		final int bottom = rectangle.bottom;
		for (ListIterator<Rect> iter = myRectangles.listIterator(); iter.hasNext(); ) {
			Rect r = iter.next();
			if (r.bottom <= top) {
				continue;
			}
			if (r.top >= bottom) {
				break;
			}
			if (r.top < top) {
				final Rect before = new Rect(r);
				before.bottom = top;
				r.top = top;
				iter.previous();
				iter.add(before);
				iter.next();
			}
			if (r.bottom > bottom) {
				final Rect after = new Rect(r);
				after.top = bottom;
				r.bottom = bottom;
				iter.add(after);
			}
			r.left = Math.min(r.left, rectangle.left);
			r.right = Math.max(r.right, rectangle.right);
		}

		final Rect first = myRectangles.getFirst();
		if (top < first.top) {
			myRectangles.add(0, new Rect(rectangle.left, top, rectangle.right, Math.min(bottom, first.top)));
		}

		final Rect last = myRectangles.getLast();
		if (bottom > last.bottom) {
			myRectangles.add(new Rect(rectangle.left, Math.max(top, last.bottom), rectangle.right, bottom));
		}
	}

	private void normalize() {
		Rect previous = null;
		for (ListIterator<Rect> iter = myRectangles.listIterator(); iter.hasNext(); ) {
			final Rect current = iter.next();
			if (previous != null) {
				if ((previous.left == current.left) && (previous.right == current.right)) {
					previous.bottom = current.bottom;
					iter.remove();
					continue;
				}
				if ((previous.bottom != current.top) &&
					(current.left <= previous.right) &&
					(previous.left <= current.right)) {
					iter.previous();
					iter.add(new Rect(
						Math.max(previous.left, current.left),
						previous.bottom,
						Math.min(previous.right, current.right),
						current.top
					));
					iter.next();
				}
			}
			previous = current;
		}
	}

	public int distanceTo(int x, int y) {
		int distance = Integer.MAX_VALUE;
		for (Rect r : myRectangles) {
			final int xd = r.left > x ? r.left - x : (r.right < x ? x - r.right : 0);
			final int yd = r.top > y ? r.top - y : (r.bottom < y ? y - r.bottom : 0);
			distance = Math.min(distance, Math.max(xd, yd));
			if (distance == 0) {
				break;
			}
		}
		return distance;
	}

	public boolean isBefore(int x, int y) {
		for (Rect r : myRectangles) {
			if (r.bottom < y || (r.top < y && r.right < x)) {
				return true;
			}
		}
		return false;
	}

//	public void draw(ZLPaintContext context, int mode) {
//		if (mode == DrawMode.None) {
//			return;
//		}
//
//		final LinkedList<Rect> rectangles = new LinkedList<Rect>(myRectangles);
//		int wordHeight = context.getStringHeight();
//		while (!rectangles.isEmpty()) {
//			final LinkedList<Rect> connected = new LinkedList<Rect>();
//			Rect previous = null;
//			for (final Iterator<Rect> iter = rectangles.iterator(); iter.hasNext(); ) {
//				final Rect current = iter.next();
//				if ((previous != null) &&
//						((previous.left > current.right) || (current.left > previous.right))) {
//					break;
//				}
//				iter.remove();
//				connected.add(current);
//				previous = current;
//			}
//
//			final LinkedList<Integer> xList = new LinkedList<Integer>();
//			final LinkedList<Integer> yList = new LinkedList<Integer>();
//			int x = 0, xPrev = 0;
//
//			final ListIterator<Rect> iter = connected.listIterator();
//			Rect r = iter.next();
//			int lineHeight = (r.bottom - r.top - wordHeight - 12);
//			lineHeight = lineHeight <= 0 ? 0 : lineHeight;
//			x = r.right + 2;
//			xList.add(x);
//			yList.add(r.top + lineHeight);
//			while (iter.hasNext()) {
//				xPrev = x;
//				r = iter.next();
//				x = r.right + 2;
//				if (x != xPrev) {
//					final int y = (x < xPrev) ? r.top + 2 + lineHeight : r.top;
//					xList.add(xPrev);
//					yList.add(y);
//					xList.add(x);
//					yList.add(y);
//				}
//			}
//			xList.add(x);
//			yList.add(r.bottom + 2);
//
//			r = iter.previous();
//			x = r.left - 2;
//			xList.add(x);
//			yList.add(r.bottom + 2);
//			while (iter.hasPrevious()) {
//				xPrev = x;
//				r = iter.previous();
//				x = r.left - 2;
//				if (x != xPrev) {
//					final int y = (x > xPrev) ? r.bottom : r.bottom + 2;
//					xList.add(xPrev);
//					yList.add(y);
//					xList.add(x);
//					yList.add(y);
//				}
//			}
//			xList.add(x);
//			yList.add(r.top + lineHeight);
//
//			final int xs[] = new int[xList.size()];
//			final int ys[] = new int[yList.size()];
//			int count = 0;
//			for (int xx : xList) {
//				xs[count++] = xx;
//			}
//			count = 0;
//			for (int yy : yList) {
//				ys[count++] = yy;
//			}
//
//			if ((mode & DrawMode.Fill) == DrawMode.Fill) {
//				context.fillPolygon(xs, ys);
//			}
//			if ((mode & DrawMode.Outline) == DrawMode.Outline) {
//				context.drawOutline(xs, ys);
//			}
//		}
//	}

	public void draw(ZLPaintContext context, int mode){
		if (mode == DrawMode.None) {
			return;
		}

		final LinkedList<Rect> rectangles = new LinkedList<Rect>(myRectangles);
		int wordHeight = context.getStringHeight();
		while (!rectangles.isEmpty()) {
			final LinkedList<Rect> connected = new LinkedList<Rect>();
			Rect previous = null;
			for (final Iterator<Rect> iter = rectangles.iterator(); iter.hasNext(); ) {
				final Rect current = iter.next();
				if ((previous != null) &&
						((previous.left > current.right) || (current.left > previous.right))) {
					break;
				}
				iter.remove();
				connected.add(current);
				previous = current;
			}

			//画背景
			if ((mode & DrawMode.Fill) == DrawMode.Fill) {
				//context.fillPolygon(xs, ys);
				int[] areax = new int[5];
				int[] areay = new int[5];
				final ListIterator<Rect> fillIter = connected.listIterator();
				while (fillIter.hasNext()) {
					Rect r = fillIter.next();
					areax[0] = r.left;
					areax[1] = r.right;
					areax[2] = r.right;
					areax[3] = r.left;
					areax[4] = r.left;
					areay[0] = r.top;
					areay[1] = r.top;
					areay[2] = r.bottom;
					areay[3] = r.bottom;
					areay[4] = r.top;
					context.fillPolygon(areax, areay);
				}
			}

			//画边框
			if ((mode & DrawMode.Outline) == DrawMode.Outline) {
				//context.drawOutline(xs, ys);
				int[] areax = new int[4];
				int[] areay = new int[4];
				final ListIterator<Rect> outlineIter = connected.listIterator();
				while (outlineIter.hasNext()) {
					Rect r = outlineIter.next();
					areax[0] = r.left ;
					areax[1] = r.right ;
					areax[2] = r.right ;
					areax[3] = r.left ;
					areay[0] = r.top ;
					areay[1] = r.top ;
					areay[2] = r.bottom ;
					areay[3] = r.bottom ;
					context.drawOutline(areax, areay);
				}
			}

			//画底线
			if ((mode & DrawMode.BottomLine) == DrawMode.BottomLine) {
				final ArrayList<Integer> bottomLineX = new ArrayList<Integer>();
				final ArrayList<Integer> bottomLineY = new ArrayList<Integer>();
				final ListIterator<Rect> bottomLineIter = connected.listIterator();
				while (bottomLineIter.hasNext()) {
					Rect r = bottomLineIter.next();
					bottomLineX.add(r.left);
					bottomLineX.add(r.right);
					bottomLineY.add(r.bottom + 2);
					bottomLineY.add(r.bottom + 2);
				}
				for (int i = 0; i < bottomLineX.size(); i += 2) {
					context.drawLine(bottomLineX.get(i), bottomLineY.get(i), bottomLineX.get(i + 1), bottomLineY.get(i + 1));
				}
			}
		}
	}
}
