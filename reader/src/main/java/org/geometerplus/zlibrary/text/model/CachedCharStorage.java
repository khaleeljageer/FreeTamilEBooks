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

package org.geometerplus.zlibrary.text.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

public final class CachedCharStorage {
	//myArray属性指向一个由char数组组成的ArrayList（char数组都设定为软引用WeakReference，保证了虚拟机会回收这些char数组，不会占用过多的内存）。
	// 这些char数组里面的元素就代表这.xhtml的文本信息与标签信息。char数组的长度最长不会超过这个长度（65536），一旦超过这个长度，代码就会新建一个char数组，同时就的数组会被持久化以便以后再用。
	protected final ArrayList<WeakReference<char[]>> myArray =
		new ArrayList<WeakReference<char[]>>();

	private final String myDirectoryName;
	private final String myFileExtension;

	public CachedCharStorage(String directoryName, String fileExtension, int blocksNumber) {
		myDirectoryName = directoryName + '/';
		myFileExtension = '.' + fileExtension;
		myArray.addAll(Collections.nCopies(blocksNumber, new WeakReference<char[]>(null)));
	}

	private String fileName(int index) {
		return myDirectoryName + index + myFileExtension;
	}

	public int size() {
		return myArray.size();
	}

	private String exceptionMessage(int index, String extra) {
		final StringBuilder buffer = new StringBuilder("Cannot read " + fileName(index));
		if (extra != null) {
			buffer.append("; ").append(extra);
		}
		buffer.append("\n");
		try {
			final File dir = new File(myDirectoryName);
			buffer.append("ts = ").append(System.currentTimeMillis()).append("\n");
			buffer.append("dir exists = ").append(dir.exists()).append("\n");
			for (File f : dir.listFiles()) {
				buffer.append(f.getName()).append(" :: ");
				buffer.append(f.length()).append(" :: ");
				buffer.append(f.lastModified()).append("\n");
			}
		} catch (Throwable t) {
			buffer.append(t.getClass().getName());
			buffer.append("\n");
			buffer.append(t.getMessage());
		}
		return buffer.toString();
	}

	public char[] block(int index) {
		if (index < 0 || index >= myArray.size()) {
			return null;
		}
		char[] block = myArray.get(index).get();
		if (block == null) {
			try {
				File file = new File(fileName(index));
				int size = (int)file.length();
				if (size < 0) {
					throw new CachedCharStorageException(exceptionMessage(index, "size = " + size));
				}
				block = new char[size / 2];
				InputStreamReader reader =
					new InputStreamReader(
						new FileInputStream(file),
						"UTF-16LE"
					);
				final int rd = reader.read(block);
				if (rd != block.length) {
					throw new CachedCharStorageException(exceptionMessage(index, "; " + rd + " != " + block.length));
				}
				reader.close();
			} catch (IOException e) {
				throw new CachedCharStorageException(exceptionMessage(index, null), e);
			}
			myArray.set(index, new WeakReference<char[]>(block));
		}
		return block;
	}
}
