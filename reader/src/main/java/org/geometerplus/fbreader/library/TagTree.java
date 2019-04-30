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

package org.geometerplus.fbreader.library;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookEvent;
import org.geometerplus.fbreader.book.Filter;
import org.geometerplus.fbreader.book.IBookCollection;
import org.geometerplus.fbreader.formats.PluginCollection;

import java.util.List;

public final class TagTree extends FilteredTree {
	public final org.geometerplus.fbreader.book.Tag Tag;

	TagTree(IBookCollection collection, PluginCollection pluginCollection, org.geometerplus.fbreader.book.Tag tag) {
		super(collection, pluginCollection, new Filter.ByTag(tag));
		Tag = tag;
	}

	TagTree(LibraryTree parent, org.geometerplus.fbreader.book.Tag tag, int position) {
		super(parent, new Filter.ByTag(tag), position);
		Tag = tag;
	}

	@Override
	public String getName() {
		return Tag.NULL.equals(Tag) ? resource().getResource("booksWithNoTags").getValue() : Tag.Name;
	}

	@Override
	protected String getStringId() {
		return "@TagTree " + getName();
	}

	protected String getSortKey() {
		return Tag.NULL.equals(Tag) ? null : Tag.Name;
	}

	@Override
	public boolean containsBook(Book book) {
		if (book == null) {
			return false;
		}
		if (Tag.NULL.equals(Tag)) {
			return book.tags().isEmpty();
		}
		for (org.geometerplus.fbreader.book.Tag t : book.tags()) {
			for (; t != null; t = t.Parent) {
				if (t == Tag) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void waitForOpening() {
		clear();
		if (!Tag.NULL.equals(Tag)) {
			for (org.geometerplus.fbreader.book.Tag t : Collection.tags()) {
				if (Tag.equals(t.Parent)) {
					createTagSubtree(t);
				}
			}
		}
		createBookSubtrees();
	}

	@Override
	public boolean onBookEvent(BookEvent event, Book book) {
		switch (event) {
			case Added:
			{
				boolean changed = false;
				final List<org.geometerplus.fbreader.book.Tag> bookTags = book.tags();
				if (bookTags.isEmpty()) {
					changed &= Tag.NULL.equals(Tag) && createBookWithAuthorsSubtree(book);
				} else {
					for (org.geometerplus.fbreader.book.Tag t : bookTags) {
						if (Tag.equals(t)) {
							changed &= createBookWithAuthorsSubtree(book);
						} else if (Tag.equals(t.Parent)) {
							changed &= createTagSubtree(t);
						}
					}
				}
				return changed;
			}
			case Removed:
				// TODO: remove empty tag trees (?)
				return super.onBookEvent(event, book);
			case Updated:
			{
				// TODO: remove empty tag trees (?)
				boolean changed = removeBook(book);
				final List<org.geometerplus.fbreader.book.Tag> bookTags = book.tags();
				if (bookTags.isEmpty()) {
					changed &= Tag.NULL.equals(Tag) && createBookWithAuthorsSubtree(book);
				} else {
					for (org.geometerplus.fbreader.book.Tag t : bookTags) {
						if (Tag.equals(t)) {
							changed &= createBookWithAuthorsSubtree(book);
						} else if (Tag.equals(t.Parent)) {
							changed &= createTagSubtree(t);
						}
					}
				}
				return changed;
			}
			default:
				return super.onBookEvent(event, book);
		}
	}

	@Override
	protected boolean createSubtree(Book book) {
		return createBookWithAuthorsSubtree(book);
	}
}
