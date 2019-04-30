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

package org.geometerplus.android.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class FileChooserUtil {
	private FileChooserUtil() {
	}

	public static void runFolderListDialog(
		Activity activity,
		int requestCode,
		String title,
		String fileChooserTitle,
		List<String> initialValue,
		boolean chooseWritableDirsOnly
	) {
		final Intent intent = new Intent(activity, FolderListDialogActivity.class);
		intent.putExtra(FolderListDialogActivity.Key.ACTIVITY_TITLE, title);
		intent.putExtra(FolderListDialogActivity.Key.CHOOSER_TITLE, fileChooserTitle);
		intent.putExtra(FolderListDialogActivity.Key.FOLDER_LIST, new ArrayList<String>(initialValue));
		intent.putExtra(FolderListDialogActivity.Key.WRITABLE_FOLDERS_ONLY, chooseWritableDirsOnly);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void runFileChooser(
		Activity activity,
		int requestCode,
		String title,
		String initialDir,
		String regexp
	) {
		FBReader.toast("Choose File："+title);
	}

	public static void runDirectoryChooser(
		Activity activity,
		int requestCode,
		String title,
		String initialValue,
		boolean chooseWritableDirsOnly
	) {
		FBReader.toast("Choose File："+title);
	}

	public static String folderPathFromData(Intent data) {
		return "";
	}

	public static List<String> filePathsFromData(Intent data) {
		final List<String> paths = new ArrayList<String>();
		Log.e("test","fileChooserUtil->fileToPath");
		return paths;
	}

	public static List<String> pathListFromData(Intent data) {
		return data.getStringArrayListExtra(FolderListDialogActivity.Key.FOLDER_LIST);
	}

	private static HashMap<String,String> textResources(String title) {
		final HashMap<String,String> map = new HashMap<String,String>();

		map.put("title", title);
		final ZLResource dialogResource = ZLResource.resource("dialog");
		final ZLResource buttonResource = dialogResource.getResource("button");
		map.put("ok", buttonResource.getResource("ok").getValue());
		map.put("cancel", buttonResource.getResource("cancel").getValue());
		final ZLResource resource = dialogResource.getResource("fileChooser");
		map.put("root", resource.getResource("root").getValue());
		map.put("newFolder", resource.getResource("newFolder").getValue());
		map.put("folderNameHint", resource.getResource("folderNameHint").getValue());
		final ZLResource menuResource = resource.getResource("menu");
		map.put("menuOrigin", menuResource.getResource("origin").getValue());
		map.put("menuReload", menuResource.getResource("reload").getValue());
		final ZLResource sortResource = resource.getResource("sortBy");
		map.put("sortBy", sortResource.getValue());
		map.put("sortByName", sortResource.getResource("name").getValue());
		map.put("sortBySize", sortResource.getResource("size").getValue());
		map.put("sortByDate", sortResource.getResource("date").getValue());
		map.put("permissionDenied", resource.getResource("permissionDenied").getValue());

		return map;
	}
}
