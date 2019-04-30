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

package org.geometerplus.android.fbreader.preferences.background;

import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.util.FileChooserUtil;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.ui.android.R;

import java.util.List;

public class Chooser extends ListActivity implements AdapterView.OnItemClickListener {
	private final ZLResource myResource = ZLResource.resource("Preferences").getResource("colors").getResource("background");

	@Override
	protected void onStart() {
		super.onStart();
		setTitle(myResource.getValue());
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			this, R.layout.background_chooser_item, R.id.background_chooser_item_title
		);
		final ZLResource chooserResource = myResource.getResource("chooser");
		//纯色
		adapter.add(chooserResource.getResource("solidColor").getValue());
		//预定纹理
		adapter.add(chooserResource.getResource("predefined").getValue());
		//选择文件
		adapter.add(chooserResource.getResource("selectFile").getValue());
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}


	public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case 0:
			{
				//纯色
				final ZLResource buttonResource =
					ZLResource.resource("dialog").getResource("button");
				FBReader.toast("Solid Color");
				break;
			}
			case 1:
				//预定义纹理
				startActivityForResult(new Intent(this, PredefinedImages.class), 1);
				break;
			case 2:
			{
				//选择文件
				final String initialDir;
				final String currentValue =
					getIntent().getStringExtra(BackgroundPreference.VALUE_KEY);
				if (currentValue != null && currentValue.startsWith("/")) {
					initialDir = currentValue.substring(0, currentValue.lastIndexOf("/"));
				} else {
					final List<String> path = Paths.WallpaperPathOption.getValue();
					if (path.size() > 0) {
						initialDir = path.get(0);
					} else {
						initialDir = "";
					}
				}
				FileChooserUtil.runFileChooser(
					this, 2, myResource.getValue(), initialDir, ".+\\.(jpe?g|png)"
				);
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case 1:
					setResult(RESULT_OK, data);
					finish();
					break;
				case 2:
					final List<String> paths = FileChooserUtil.filePathsFromData(data);
					if (paths.size() == 1) {
						setResult(RESULT_OK, new Intent().putExtra(
							BackgroundPreference.VALUE_KEY, paths.get(0)
						));
						finish();
					}
					break;
			}
		}
	}
}
