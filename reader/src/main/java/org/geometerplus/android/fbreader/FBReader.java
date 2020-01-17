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

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.geometerplus.android.fbreader.api.*;
import org.geometerplus.android.fbreader.dialog.MenuDialog;
import org.geometerplus.android.fbreader.dict.DictionaryUtil;
import org.geometerplus.android.fbreader.formatPlugin.PluginUtil;
import org.geometerplus.android.fbreader.httpd.DataService;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.fbreader.sync.SyncOperations;
import org.geometerplus.android.fbreader.tips.TipsActivity;
import org.geometerplus.android.fbreader.util.FBReaderConfig;
import org.geometerplus.android.fbreader.util.FBReaderReadTimeUtils;
import org.geometerplus.android.util.*;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookUtil;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.DictionaryHighlighting;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.CancelMenuHelper;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.fbreader.formats.ExternalFormatPlugin;
import org.geometerplus.fbreader.formats.PluginCollection;
import org.geometerplus.fbreader.tips.TipsManager;
import org.geometerplus.fbreader.util.TurnPageJudgeUtil;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.application.ZLApplicationWindow;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.Config;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.view.ZLViewWidget;
import org.geometerplus.zlibrary.text.view.ZLTextRegion;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.error.ErrorKeys;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public final class FBReader extends FBReaderMainActivity implements ZLApplicationWindow {
	public static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
	public static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;
	public static ReaderLifeCycle lifeCycle;

	public static Intent defaultIntent(Context context) {
		return new Intent(context, FBReader.class)
			.setAction(FBReaderIntents.Action.VIEW)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}

	public static void openBookActivity(Context context, Book book, Bookmark bookmark) {
		openBookActivity(context, book, bookmark, null);
	}

	public static void openBookActivity(Context context, Book book, Bookmark bookmark, ReaderLifeCycle lifeCycleListener) {
		lifeCycle = lifeCycleListener;
		final Intent intent = defaultIntent(context);
		FBReaderIntents.putBookExtra(intent, book);
		FBReaderIntents.putBookmarkExtra(intent, bookmark);
		context.startActivity(intent);
	}

	//通过路径打开书籍
	public static void openBookActivity(Context context, String path) {
		if (TextUtils.isEmpty(path)) {
			return;
		}
		openBookActivity(context, path, null);
	}

	//通过路径打开书籍
	public static void openBookActivity(Context context, String path, ReaderLifeCycle lifeCycleListener) {
		if (TextUtils.isEmpty(path)) {
			return;
		}
		lifeCycle = lifeCycleListener;
		final Intent intent = defaultIntent(context);
		intent.setData(Uri.parse(path));
		context.startActivity(intent);
	}

	private FBReaderApp myFBReaderApp;
	private volatile Book myBook;

	private RelativeLayout myRootView;
	private ZLAndroidWidget myMainView;

	private volatile boolean myShowStatusBarFlag;
	private String myMenuLanguage;

	final DataService.Connection DataConnection = new DataService.Connection();

	volatile boolean IsPaused = false;
	private volatile long myResumeTimestamp;
	volatile Runnable OnResumeAction = null;

	private Intent myCancelIntent = null;
	private Intent myOpenBookIntent = null;
	private final FBReaderApp.Notifier myNotifier = new AppNotifier(this);

	private static final String PLUGIN_ACTION_PREFIX = "___";
	private final List<PluginApi.ActionInfo> myPluginActions =
		new LinkedList<PluginApi.ActionInfo>();
	private final BroadcastReceiver myPluginInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final ArrayList<PluginApi.ActionInfo> actions = getResultExtras(true).<PluginApi.ActionInfo>getParcelableArrayList(PluginApi.PluginInfo.KEY);
			if (actions != null) {
				synchronized (myPluginActions) {
					int index = 0;
					while (index < myPluginActions.size()) {
						myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
					}
					myPluginActions.addAll(actions);
					index = 0;
					for (PluginApi.ActionInfo info : myPluginActions) {
						myFBReaderApp.addAction(
							PLUGIN_ACTION_PREFIX + index++,
							new RunPluginAction(FBReader.this, myFBReaderApp, info.getId())
						);
					}
				}
			}
		}
	};

	private synchronized void openBook(Intent intent, final Runnable action, boolean force) {
		if (!force && myBook != null) {
			return;
		}

		myBook = FBReaderIntents.getBookExtra(intent, myFBReaderApp.Collection);
		final Bookmark bookmark = FBReaderIntents.getBookmarkExtra(intent);
		if (myBook == null) {
			final Uri data = intent.getData();
			if (data != null) {
				if (data.getScheme() != null && "content".equals(data.getScheme().toLowerCase())) {
					myBook = createBookForFile(ZLFile.createFileByPath(getFilePathFromContentUri(data, getContentResolver())));
				} else {
					myBook = createBookForFile(ZLFile.createFileByPath(data.getPath()));
				}
			}
		}
		if (myBook != null) {
			ZLFile file = BookUtil.fileByBook(myBook);
			if (!file.exists()) {
				if (file.getPhysicalFile() != null) {
					file = file.getPhysicalFile();
				}
				UIMessageUtil.showErrorMessage(this, "fileNotFound", file.getPath());
				myBook = null;
			} else {
				NotificationUtil.drop(this, myBook);
			}
		}
		Config.Instance().runOnConnect(new Runnable() {
			public void run() {
				myFBReaderApp.openBook(myBook, bookmark, action, myNotifier);
				AndroidFontUtil.clearFontCache();
			}
		});
	}

	public static String getFilePathFromContentUri(Uri selectedVideoUri,
												   ContentResolver contentResolver) {
		String filePath;
		String[] filePathColumn = {MediaStore.MediaColumns.DATA};

		Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//	    也可用下面的方法拿到cursor
//	    Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		filePath = cursor.getString(columnIndex);
		cursor.close();
		return filePath;
	}
	private Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		Book book = myFBReaderApp.Collection.getBookByFile(file.getPath());
		if (book != null) {
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = myFBReaderApp.Collection.getBookByFile(child.getPath());
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	private Runnable getPostponedInitAction() {
		return new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						new TipRunner().start();
						DictionaryUtil.init(FBReader.this, null);
						final Intent intent = getIntent();
						if (intent != null && FBReaderIntents.Action.PLUGIN.equals(intent.getAction())) {
							new RunPluginAction(FBReader.this, myFBReaderApp, intent.getData()).run();
						}
					}
				});
			}
		};
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (lifeCycle != null) {
			lifeCycle.onCreate(this);
		}
		bindService(
			new Intent(this, DataService.class),
			DataConnection,
			DataService.BIND_AUTO_CREATE
		);

		final Config config = Config.Instance();
		config.runOnConnect(new Runnable() {
			public void run() {
				config.requestAllValuesForGroup("Options");
				config.requestAllValuesForGroup("Style");
				config.requestAllValuesForGroup("LookNFeel");
				config.requestAllValuesForGroup("Fonts");
				config.requestAllValuesForGroup("Colors");
				config.requestAllValuesForGroup("Files");
			}
		});

		final ZLAndroidLibrary zlibrary = getZLibrary();
		myShowStatusBarFlag = zlibrary.ShowStatusBarOption.getValue();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		myRootView = (RelativeLayout)findViewById(R.id.root_view);
		myMainView = (ZLAndroidWidget)findViewById(R.id.main_view);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		myFBReaderApp = (FBReaderApp)FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(Paths.systemInfo(this), new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		myBook = null;

		myFBReaderApp.setWindow(this);
		myFBReaderApp.initWindow();

		myFBReaderApp.setExternalFileOpener(new ExternalFileOpener(this));

		if(myShowStatusBarFlag){
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					0
			);
		} else {
			Window window = getWindow();
			window.setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN
			);
			try {
				WindowManager.LayoutParams wlp = window.getAttributes();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
					wlp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
				}
				window.setAttributes(wlp);
			} catch (Throwable t) {
			}
			try {
				View decorView = window.getDecorView();
				int systemUiVisibility = decorView.getSystemUiVisibility();
				int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_FULLSCREEN;
				systemUiVisibility |= flags;
				window.getDecorView().setSystemUiVisibility(systemUiVisibility);
			} catch (Throwable t) {
			}
		}
//		NotchFit.fit(this, NotchScreenType.FULL_SCREEN, null);
		//文字搜索 popupWindow：向左箭头，关闭、向右箭头
		if (myFBReaderApp.getPopupById(TextSearchPopup.ID) == null) {
			new TextSearchPopup(myFBReaderApp);
		}
		//快速翻看 popupWindow
		if (myFBReaderApp.getPopupById(NavigationPopup.ID) == null) {
			new NavigationPopup(myFBReaderApp);
		}
		//长按文本 popupwindow
		if (myFBReaderApp.getPopupById(SelectionPopup.ID) == null) {
			new SelectionPopup(myFBReaderApp);
		}
		//设置
		if(myFBReaderApp.getPopupById(SettingPopup.ID) == null){
			new SettingPopup(myFBReaderApp);
		}
		//高亮文字点击
		if(myFBReaderApp.getPopupById(HighlightPopup.ID) == null){
			new HighlightPopup(myFBReaderApp);
		}
		//本地书柜
		myFBReaderApp.addAction(ActionCode.SHOW_LIBRARY, new ShowLibraryAction(this, myFBReaderApp));
		//阅读相关设置
		myFBReaderApp.addAction(ActionCode.SHOW_PREFERENCES, new ShowPreferencesAction(this, myFBReaderApp));
		//书籍信息
		myFBReaderApp.addAction(ActionCode.SHOW_BOOK_INFO, new ShowBookInfoAction(this, myFBReaderApp));
		//本书目录
		myFBReaderApp.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this, myFBReaderApp));
		//我的书签
		myFBReaderApp.addAction(ActionCode.SHOW_BOOKMARKS, new ShowBookmarksAction(this, myFBReaderApp));
		//在线书库
		myFBReaderApp.addAction(ActionCode.SHOW_NETWORK_LIBRARY, new ShowNetworkLibraryAction(this, myFBReaderApp));

		//显示目录，中间下部区域,触发的时机是ZLAndroidWidget.OnTouchEvent 、 区域定义:assets/default/tapzones
		myFBReaderApp.addAction(ActionCode.SHOW_MENU, new ShowMenuAction(this, myFBReaderApp));
		//快速翻看
		myFBReaderApp.addAction(ActionCode.SHOW_NAVIGATION, new ShowNavigationAction(this, myFBReaderApp));
		//内容查找 myFBReaderApp.getTextView().search(pattern, true, false, false, false)
		myFBReaderApp.addAction(ActionCode.SEARCH, new SearchAction(this, myFBReaderApp));

		//长按选中文本，松开手指，显示弹窗
		myFBReaderApp.addAction(ActionCode.SELECTION_SHOW_PANEL, new SelectionShowPanelAction(this, myFBReaderApp));
		//隐藏弹窗
		myFBReaderApp.addAction(ActionCode.SELECTION_HIDE_PANEL, new SelectionHidePanelAction(this, myFBReaderApp));
		//复制到剪切板
		myFBReaderApp.addAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD, new SelectionCopyAction(this, myFBReaderApp));
		//分享
		myFBReaderApp.addAction(ActionCode.SELECTION_SHARE, new SelectionShareAction(this, myFBReaderApp));

		//长按文本内容-》弹窗第一个图标（点击加入书签）
		myFBReaderApp.addAction(ActionCode.SELECTION_BOOKMARK, new SelectionBookmarkAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.DISPLAY_BOOK_POPUP, new DisplayBookPopupAction(this, myFBReaderApp));


		myFBReaderApp.addAction(ActionCode.OPEN_VIDEO, new OpenVideoAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.HIDE_TOAST, new HideToastAction(this, myFBReaderApp));

		//返回键
		myFBReaderApp.addAction(ActionCode.SHOW_CANCEL_MENU, new ShowCancelMenuAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.OPEN_START_SCREEN, new StartScreenAction(this, myFBReaderApp));
		//region横竖屏设置
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SYSTEM, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_SYSTEM));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SENSOR, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_SENSOR));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_PORTRAIT));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_LANDSCAPE));
		if (getZLibrary().supportsAllOrientations()) {
			myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_REVERSE_PORTRAIT));
			myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
		}
		//endregion

		//帮助
		myFBReaderApp.addAction(ActionCode.OPEN_WEB_HELP, new OpenWebHelpAction(this, myFBReaderApp));

		//白字黑底-日渐模式
		myFBReaderApp.addAction(ActionCode.SWITCH_TO_DAY_PROFILE, new SwitchProfileAction(this, myFBReaderApp, ColorProfile.DAY));
		//黑底白字-夜间模式
		myFBReaderApp.addAction(ActionCode.SWITCH_TO_NIGHT_PROFILE, new SwitchProfileAction(this, myFBReaderApp, ColorProfile.NIGHT));

		//TTSRead
		myFBReaderApp.addAction(ActionCode.TTS_READ, new TTSReadAction(this, myFBReaderApp));

		final Intent intent = getIntent();
		final String action = intent.getAction();

		myOpenBookIntent = intent;
		//当用户点击Home，从历史中选择该Activity，系统会自动加上这个Flag
		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
			//不是从历史中返回的
			if (FBReaderIntents.Action.CLOSE.equals(action)) {
				myCancelIntent = intent;
				myOpenBookIntent = null;
			} else if (FBReaderIntents.Action.PLUGIN_CRASH.equals(action)) {
				myFBReaderApp.ExternalBook = null;
				myOpenBookIntent = null;
				getCollection().bindToService(this, new Runnable() {
					public void run() {
						myFBReaderApp.openBook(null, null, null, myNotifier);
					}
				});
			}
		}
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		final String action = intent.getAction();
		final Uri data = intent.getData();

		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
			super.onNewIntent(intent);
		} else if (Intent.ACTION_VIEW.equals(action)
				   && data != null && "fbreader-action".equals(data.getScheme())) {
			myFBReaderApp.runAction(data.getEncodedSchemeSpecificPart(), data.getFragment());
		} else if (Intent.ACTION_VIEW.equals(action) || FBReaderIntents.Action.VIEW.equals(action)) {
			myOpenBookIntent = intent;
			if (myFBReaderApp.Model == null && myFBReaderApp.ExternalBook != null) {
				final BookCollectionShadow collection = getCollection();
				final Book b = FBReaderIntents.getBookExtra(intent, collection);
				if (!collection.sameBook(b, myFBReaderApp.ExternalBook)) {
					try {
						final ExternalFormatPlugin plugin =
							(ExternalFormatPlugin)BookUtil.getPlugin(
								PluginCollection.Instance(Paths.systemInfo(this)),
								myFBReaderApp.ExternalBook
							);
						startActivity(PluginUtil.createIntent(plugin, FBReaderIntents.Action.PLUGIN_KILL));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if (FBReaderIntents.Action.PLUGIN.equals(action)) {
			new RunPluginAction(this, myFBReaderApp, data).run();
		} else if (Intent.ACTION_SEARCH.equals(action)) {
			final String pattern = intent.getStringExtra(SearchManager.QUERY);
			final Runnable runnable = new Runnable() {
				public void run() {
					final TextSearchPopup popup = (TextSearchPopup)myFBReaderApp.getPopupById(TextSearchPopup.ID);
					popup.initPosition();
					myFBReaderApp.MiscOptions.TextSearchPattern.setValue(pattern);
					if (myFBReaderApp.getTextView().search(pattern, true, false, false, false) != 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								myFBReaderApp.showPopup(popup.getId());
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								UIMessageUtil.showErrorMessage(FBReader.this, "textNotFound");
								popup.StartPosition = null;
							}
						});
					}
				}
			};
			UIUtil.wait("search", runnable, this);
		} else if (FBReaderIntents.Action.CLOSE.equals(intent.getAction())) {
			myCancelIntent = intent;
			myOpenBookIntent = null;
		} else if (FBReaderIntents.Action.PLUGIN_CRASH.equals(intent.getAction())) {
			final Book book = FBReaderIntents.getBookExtra(intent, myFBReaderApp.Collection);
			myFBReaderApp.ExternalBook = null;
			myOpenBookIntent = null;
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					final BookCollectionShadow collection = getCollection();
					Book b = collection.getRecentBook(0);
					if (collection.sameBook(b, book)) {
						b = collection.getRecentBook(1);
					}
					myFBReaderApp.openBook(b, null, null, myNotifier);
				}
			});
		} else {
			super.onNewIntent(intent);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (lifeCycle != null) {
			lifeCycle.onStart(this);
		}
		getCollection().bindToService(this, new Runnable() {
			public void run() {
				new Thread() {
					public void run() {
						getPostponedInitAction().run();
					}
				}.start();

				myFBReaderApp.getViewWidget().repaint();
			}
		});

		initPluginActions();

		final ZLAndroidLibrary zlibrary = getZLibrary();

		Config.Instance().runOnConnect(new Runnable() {
			public void run() {
				final boolean showStatusBar = zlibrary.ShowStatusBarOption.getValue();
				if (showStatusBar != myShowStatusBarFlag) {
					finish();
					startActivity(new Intent(FBReader.this, FBReader.class));
				}
				zlibrary.ShowStatusBarOption.saveSpecialValue();
				myFBReaderApp.ViewOptions.ColorProfileName.saveSpecialValue();
				SetScreenOrientationAction.setOrientation(FBReader.this, zlibrary.getOrientationOption().getValue());
			}
		});

		((PopupPanel)myFBReaderApp.getPopupById(TextSearchPopup.ID)).setPanelInfo(this, myRootView);
		((PopupPanel)myFBReaderApp.getPopupById(SettingPopup.ID)).setPanelInfo(this, myRootView);
		((NavigationPopup)myFBReaderApp.getPopupById(NavigationPopup.ID)).setPanelInfo(this, myRootView);
		((PopupPanel)myFBReaderApp.getPopupById(SelectionPopup.ID)).setPanelInfo(this, myRootView);
		((PopupPanel)myFBReaderApp.getPopupById(HighlightPopup.ID)).setPanelInfo(this, myRootView);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		switchWakeLock(hasFocus &&
			getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() <
			myFBReaderApp.getBatteryLevel()
		);
	}

	private void initPluginActions() {
		synchronized (myPluginActions) {
			int index = 0;
			while (index < myPluginActions.size()) {
				myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
			}
			myPluginActions.clear();
		}

		sendOrderedBroadcast(
			new Intent(PluginApi.ACTION_REGISTER),
			null,
			myPluginInfoReceiver,
			null,
			RESULT_OK,
			null,
			null
		);
	}

	private class TipRunner extends Thread {
		TipRunner() {
			setPriority(MIN_PRIORITY);
		}

		public void run() {
			final TipsManager manager = new TipsManager(Paths.systemInfo(FBReader.this));
			switch (manager.requiredAction()) {
				case Initialize:
					startActivity(new Intent(
						TipsActivity.INITIALIZE_ACTION, null, FBReader.this, TipsActivity.class
					));
					break;
				case Show:
					startActivity(new Intent(
						TipsActivity.SHOW_TIP_ACTION, null, FBReader.this, TipsActivity.class
					));
					break;
				case Download:
					manager.startDownloading();
					break;
				case None:
					break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (lifeCycle != null) {
			lifeCycle.onResume(this);
		}
		myStartTimer = true;
		Config.Instance().runOnConnect(new Runnable() {
			public void run() {
				SyncOperations.enableSync(FBReader.this, myFBReaderApp.SyncOptions);

				final int brightnessLevel =
					getZLibrary().ScreenBrightnessLevelOption.getValue();
				if (brightnessLevel != 0) {
					getViewWidget().setScreenBrightness(brightnessLevel);
				} else {
					setScreenBrightnessAuto();
				}
				if (getZLibrary().DisableButtonLightsOption.getValue()) {
					setButtonLight(false);
				}

				getCollection().bindToService(FBReader.this, new Runnable() {
					public void run() {
						final BookModel model = myFBReaderApp.Model;
						if (model == null || model.Book == null) {
							return;
						}
						onPreferencesUpdate(myFBReaderApp.Collection.getBookById(model.Book.getId()));
					}
				});
			}
		});

		registerReceiver(myBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		IsPaused = false;
		myResumeTimestamp = System.currentTimeMillis();
		if (OnResumeAction != null) {
			final Runnable action = OnResumeAction;
			OnResumeAction = null;
			action.run();
		}

		registerReceiver(mySyncUpdateReceiver, new IntentFilter(FBReaderIntents.Event.SYNC_UPDATED));

		SetScreenOrientationAction.setOrientation(this, getZLibrary().getOrientationOption().getValue());
		if (myCancelIntent != null) {
			final Intent intent = myCancelIntent;
			myCancelIntent = null;
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					runCancelAction(intent);
				}
			});
			return;
		} else if (myOpenBookIntent != null) {
			final Intent intent = myOpenBookIntent;
			myOpenBookIntent = null;
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					openBook(intent, null, true);
				}
			});
		} else if (myFBReaderApp.getCurrentServerBook(null) != null) {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					myFBReaderApp.useSyncInfo(true, myNotifier);
				}
			});
		} else if (myFBReaderApp.Model == null && myFBReaderApp.ExternalBook != null) {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					myFBReaderApp.openBook(myFBReaderApp.ExternalBook, null, null, myNotifier);
				}
			});
		} else {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					myFBReaderApp.useSyncInfo(true, myNotifier);
				}
			});
		}

		PopupPanel.restoreVisibilities(myFBReaderApp);
		ApiServerImplementation.sendEvent(this, ApiListener.EVENT_READ_MODE_OPENED);
		FBReaderReadTimeUtils.startRead();
		TurnPageJudgeUtil.getInstance().onResume();
	}

	@Override
	protected void onPause() {
		if (lifeCycle != null) {
			lifeCycle.onPause(this);
			if (isFinishing()) {
				lifeCycle.onFinishing(this);
				lifeCycle = null;
			}
		}
		SyncOperations.quickSync(this, myFBReaderApp.SyncOptions);

		IsPaused = true;
		try {
			unregisterReceiver(mySyncUpdateReceiver);
		} catch (IllegalArgumentException e) {
		}

		try {
			unregisterReceiver(myBatteryInfoReceiver);
		} catch (IllegalArgumentException e) {
			// do nothing, this exception means that myBatteryInfoReceiver was not registered
		}

		myFBReaderApp.stopTimer();
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(true);
		}
		myFBReaderApp.onWindowClosing();

		if(isFinishing()){
			TTSReadUtil.getInstance().stopSpeaking();
			FBReaderReadTimeUtils.stopRead();
			TurnPageJudgeUtil.getInstance().onFinish();
		}else {
			FBReaderReadTimeUtils.pauseRead();
			TurnPageJudgeUtil.getInstance().onPause();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		ApiServerImplementation.sendEvent(this, ApiListener.EVENT_READ_MODE_CLOSED);
		PopupPanel.removeAllWindows(myFBReaderApp, this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		unbindService(DataConnection);
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		myFBReaderApp.onWindowClosing();
		super.onLowMemory();
	}

	//内容查找
	@Override
	public boolean onSearchRequested() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		myFBReaderApp.hideActivePopup();
		if (DeviceType.Instance().hasStandardSearchDialog()) {
			final SearchManager manager = (SearchManager)getSystemService(SEARCH_SERVICE);
			manager.setOnCancelListener(new SearchManager.OnCancelListener() {
				public void onCancel() {
					if (popup != null) {
						myFBReaderApp.showPopup(popup.getId());
					}
					manager.setOnCancelListener(null);
				}
			});
			startSearch(myFBReaderApp.MiscOptions.TextSearchPattern.getValue(), true, null, false);
		} else {
			SearchDialogUtil.showDialog(
				this, FBReader.class, myFBReaderApp.MiscOptions.TextSearchPattern.getValue(), new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface di) {
						if (popup != null) {
							myFBReaderApp.showPopup(popup.getId());
						}
					}
				}
			);
		}
		return true;
	}

	public void showSelectionPanel() {
		final ZLTextView view = myFBReaderApp.getTextView();
		((SelectionPopup) myFBReaderApp.getPopupById(SelectionPopup.ID))
				.move(view.getSelectionStartY(), view.getSelectionEndY());
		myFBReaderApp.showPopup(SelectionPopup.ID);
	}

	public void hideSelectionPanel() {
		myFBReaderApp.hideActivePopup();
	}

	private void onPreferencesUpdate(Book book) {
		AndroidFontUtil.clearFontCache();
		myFBReaderApp.onBookUpdated(book);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
			case REQUEST_PREFERENCES:
				if (resultCode != RESULT_DO_NOTHING && data != null) {
					final Book book = FBReaderIntents.getBookExtra(data, myFBReaderApp.Collection);
					if (book != null) {
						getCollection().bindToService(this, new Runnable() {
							public void run() {
								onPreferencesUpdate(book);
							}
						});
					}
				}
				break;
			case REQUEST_CANCEL_MENU:
				runCancelAction(data);
				break;
		}
	}

	private void runCancelAction(Intent intent) {
		final CancelMenuHelper.ActionType type;
		try {
			type = CancelMenuHelper.ActionType.valueOf(
				intent.getStringExtra(FBReaderIntents.Key.TYPE)
			);
		} catch (Exception e) {
			// invalid (or null) type value
			return;
		}
		Bookmark bookmark = null;
		if (type == CancelMenuHelper.ActionType.returnTo) {
			bookmark = FBReaderIntents.getBookmarkExtra(intent);
			if (bookmark == null) {
				return;
			}
		}
		myFBReaderApp.runCancelAction(type, bookmark);
	}

	//region快速翻看
	public void navigate() {
		((NavigationPopup)myFBReaderApp.getPopupById(NavigationPopup.ID)).runNavigation();
	}
	//endregion

	//region 点击中间底部 弹出菜单 相关信息
	//只会调用一次，他只会在Menu第一显示之前去调用一次，之后就不会在去调用。
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		setupMenu(menu);
		return true;
	}

	//每次在显示 Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
	//先调用onCreateOptionsMenu方法，在调用此方法
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		setStatusBarVisibility(true);
		setupMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	//设置该显示的menu
	private void setupMenu(Menu menu) {
		//获取菜单语音，默认为system
		final String menuLanguage = ZLResource.getLanguageOption().getValue();
		if (menuLanguage.equals(myMenuLanguage)) {
			return;
		}
		myMenuLanguage = menuLanguage;

		menu.clear();
		fillMenu(menu, MenuData.topLevelNodes());
		synchronized (myPluginActions) {
			int index = 0;
			for (PluginApi.ActionInfo info : myPluginActions) {
				if (info instanceof PluginApi.MenuActionInfo) {
					addMenuItem(
							menu,
							PLUGIN_ACTION_PREFIX + index++,
							null,
							((PluginApi.MenuActionInfo) info).MenuItemName
					);
				}
			}
		}
		refresh();
	}

	private void fillMenu(Menu menu, List<MenuNode> nodes) {
		for (MenuNode n : nodes) {
			if (n instanceof MenuNode.Item) {
				final Integer iconId = ((MenuNode.Item)n).IconId;
				addMenuItem(menu, n.Code, iconId, null);
			} else /* if (n instanceof MenuNode.Submenu) */ {
				final Menu submenu = addSubmenu(menu, n.Code);
				fillMenu(submenu, ((MenuNode.Submenu)n).Children);
			}
		}
	}

	//添加子menu
	private Menu addSubmenu(Menu menu, String id) {
		return menu.addSubMenu(ZLResource.resource("menu").getResource(id).getValue());
	}

	//添加menu
	private void addMenuItem(Menu menu, String actionId, Integer iconId, String name) {
		if (name == null) {
			//assets/resources/resources/application/zh/<node name="menu">
			name = ZLResource.resource("menu").getResource(actionId).getValue();
		}
		final MenuItem menuItem = menu.add(name);
		if (iconId != null) {
			menuItem.setIcon(iconId);
		}
		menuItem.setOnMenuItemClickListener(myMenuListener);
		myMenuItemMap.put(menuItem, actionId);
	}

	//保存了所有的menu 和 action的对应关系
	private final HashMap<MenuItem,String> myMenuItemMap = new HashMap<MenuItem,String>();

	//所有的menu的点击事件回调
	private final MenuItem.OnMenuItemClickListener myMenuListener =
			new MenuItem.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					myFBReaderApp.runAction(myMenuItemMap.get(item));
					return true;
				}
			};

	//每次在关闭menu时回调
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		setStatusBarVisibility(false);
	}
	//endregion

	protected void onPluginNotFound(final Book book) {
		final BookCollectionShadow collection = getCollection();
		collection.bindToService(this, new Runnable() {
			public void run() {
				final Book recent = collection.getRecentBook(0);
				if (recent != null && !collection.sameBook(recent, book)) {
					myFBReaderApp.openBook(recent, null, null, null);
				} else {
					myFBReaderApp.openHelpBook();
				}
			}
		});
	}

	//region 设置状态栏显示或隐藏
	private void setStatusBarVisibility(boolean visible) {
		final ZLAndroidLibrary zlibrary = getZLibrary();
		if (DeviceType.Instance() != DeviceType.KINDLE_FIRE_1ST_GENERATION && !myShowStatusBarFlag) {
			if (visible) {
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			} else {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			}
		}
	}
	//endregion

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return (myMainView != null && myMainView.onKeyDown(keyCode, event)) || super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return (myMainView != null && myMainView.onKeyUp(keyCode, event)) || super.onKeyUp(keyCode, event);
	}

	private void setButtonLight(boolean enabled) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			setButtonLightInternal(enabled);
		}
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	private void setButtonLightInternal(boolean enabled) {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.buttonBrightness = enabled ? -1.0f : 0.0f;
		getWindow().setAttributes(attrs);
	}

	private PowerManager.WakeLock myWakeLock;
	private boolean myWakeLockToCreate;
	private boolean myStartTimer;

	public final void createWakeLock() {
		if (myWakeLockToCreate) {
			synchronized (this) {
				if (myWakeLockToCreate) {
					myWakeLockToCreate = false;
					myWakeLock =
						((PowerManager)getSystemService(POWER_SERVICE))
							.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "fte:mywakeFBReader");
					myWakeLock.acquire();
				}
			}
		}
		if (myStartTimer) {
			myFBReaderApp.startTimer();
			myStartTimer = false;
		}
	}

	private final void switchWakeLock(boolean on) {
		if (on) {
			if (myWakeLock == null) {
				myWakeLockToCreate = true;
			}
		} else {
			if (myWakeLock != null) {
				synchronized (this) {
					if (myWakeLock != null) {
						myWakeLock.release();
						myWakeLock = null;
					}
				}
			}
		}
	}

	private BroadcastReceiver myBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 100);
			final ZLAndroidApplication application = (ZLAndroidApplication)getApplication();
			setBatteryLevel(level);
			switchWakeLock(
				hasWindowFocus() &&
				getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() < level
			);
		}
	};

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow)myFBReaderApp.Collection;
	}

	// methods from ZLApplicationWindow interface
	@Override
	public void showErrorMessage(String key) {
		UIMessageUtil.showErrorMessage(this, key);
	}

	@Override
	public void showErrorMessage(String key, String parameter) {
		UIMessageUtil.showErrorMessage(this, key, parameter);
	}

	@Override
	public FBReaderApp.SynchronousExecutor createExecutor(String key) {
		if ("loadingBook".equals(key)) {
			final FBReader activity = FBReader.this;
			return new ZLApplication.SynchronousExecutor() {
				public void execute(final Runnable action, final Runnable uiPostAction) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							if (lifeCycle != null) {
								lifeCycle.onLoadStart(activity);
							}
							final Thread runner = new Thread() {
								public void run() {
									action.run();
									activity.runOnUiThread(new Runnable() {
										public void run() {
											if (lifeCycle != null) {
												lifeCycle.onLoadComplete(activity);
											}
											if (uiPostAction != null) {
												uiPostAction.run();
											}
										}
									});
								}
							};
							runner.setPriority(Thread.MAX_PRIORITY);
							runner.start();
						}
					});
				}

				public void executeAux(String key, Runnable runnable) {
					runnable.run();
				}
			};
		}
		return UIUtil.createExecutor(this, key);
	}

	private int myBatteryLevel;
	@Override
	public int getBatteryLevel() {
		return myBatteryLevel;
	}
	private void setBatteryLevel(int percent) {
		myBatteryLevel = percent;
	}

	@Override
	public void close() {
		finish();
	}

	@Override
	public ZLViewWidget getViewWidget() {
		return myMainView;
	}

	@Override
	public void refresh() {
		runOnUiThread(new Runnable() {
			public void run() {
				for (Map.Entry<MenuItem,String> entry : myMenuItemMap.entrySet()) {
					final String actionId = entry.getValue();
					final MenuItem menuItem = entry.getKey();
					menuItem.setVisible(myFBReaderApp.isActionVisible(actionId) && myFBReaderApp.isActionEnabled(actionId));
					switch (myFBReaderApp.isActionChecked(actionId)) {
						case TRUE:
							menuItem.setCheckable(true);
							menuItem.setChecked(true);
							break;
						case FALSE:
							menuItem.setCheckable(true);
							menuItem.setChecked(false);
							break;
						case UNDEFINED:
							menuItem.setCheckable(false);
							break;
					}
				}
			}
		});
	}

	@Override
	public void processException(Exception exception) {
		exception.printStackTrace();

		final Intent intent = new Intent(
			FBReaderIntents.Action.ERROR,
			new Uri.Builder().scheme(exception.getClass().getSimpleName()).build()
		);
		intent.setPackage(FBReaderIntents.DEFAULT_PACKAGE);
		intent.putExtra(ErrorKeys.MESSAGE, exception.getMessage());
		final StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		intent.putExtra(ErrorKeys.STACKTRACE, stackTrace.toString());
		/*
		if (exception instanceof BookReadingException) {
			final ZLFile file = ((BookReadingException)exception).File;
			if (file != null) {
				intent.putExtra("file", file.getPath());
			}
		}
		*/
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// ignore
			e.printStackTrace();
		}
	}

	@Override
	public void setWindowTitle(final String title) {
		runOnUiThread(new Runnable() {
			public void run() {
				setTitle(title);
			}
		});
	}

	private BroadcastReceiver mySyncUpdateReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			myFBReaderApp.useSyncInfo(myResumeTimestamp + 10 * 1000 > System.currentTimeMillis(), myNotifier);
		}
	};

	public void outlineRegion(ZLTextRegion.Soul soul) {
		myFBReaderApp.getTextView().outlineRegion(soul);
		myFBReaderApp.getViewWidget().repaint();
	}

	public void hideOutline() {
		myFBReaderApp.getTextView().hideOutline();
		myFBReaderApp.getViewWidget().repaint();
	}

	public void hideDictionarySelection() {
		myFBReaderApp.getTextView().hideOutline();
		myFBReaderApp.getTextView().removeHighlightings(DictionaryHighlighting.class);
		myFBReaderApp.getViewWidget().reset();
		myFBReaderApp.getViewWidget().repaint();
	}

    //region 显示menu操作

	MenuDialog menuDialog ;
	public void runAction(String actionId, Object... params) {
		myFBReaderApp.runAction(actionId, params);
	}
    //显示menu
	public void showMenu() {
		if (menuDialog == null) {
			menuDialog = new MenuDialog(this, myFBReaderApp);
		}
		if (!menuDialog.isShowing() && !isFinishing()) {
			menuDialog.show();
		}
	}
    //endregion

	//region显示设置界面
	public void setting() {
		((SettingPopup) myFBReaderApp.getPopupById(SettingPopup.ID)).show_();
	}
	//endregion

	public static void toast(String msg) {
		if (lifeCycle != null && ZLApplication.Instance().getReader() != null) {
			lifeCycle.toast(ZLApplication.Instance().getReader(), msg);
		} else {
			Toast.makeText(FBReaderConfig.getContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	public static interface ReaderLifeCycle {
		void onCreate(FBReader context);

		void onStart(FBReader context);

		void onResume(FBReader context);

		void onPause(FBReader context);

		void onFinishing(FBReader context);

		//返回true，直接关闭阅读页面
		boolean onBackClick(FBReader context);

		void onLoadStart(FBReader context);

		void onLoadComplete(FBReader context);

		void toast(FBReader context, String msg);

		void onTurnPage(TurnPageJudgeUtil.PageInfo info);

		void share(FBReader context);
	}

	public static class ReaderLifeCycleAdapter implements ReaderLifeCycle {

		@Override
		public void onCreate(FBReader context) {

		}

		@Override
		public void onStart(FBReader context) {

		}

		@Override
		public void onResume(FBReader context) {

		}

		@Override
		public void onPause(FBReader context) {

		}

		@Override
		public void onFinishing(FBReader context) {

		}

		@Override
		public boolean onBackClick(FBReader context) {
			return true;
		}

		@Override
		public void onLoadStart(FBReader context) {

		}

		@Override
		public void onLoadComplete(FBReader context) {

		}

		@Override
		public void toast(FBReader context, String msg) {

		}

		@Override
		public void onTurnPage(TurnPageJudgeUtil.PageInfo info) {

		}

		@Override
		public void share(FBReader context) {

		}
	}

	//region logger
	private static boolean isOpen = false;
	private static String logTAG = "FBReaderImpl";

	/**
	 * @param TAG 可传空 , 默认为FBReaderImpl
	 */
	public static void openLog(String TAG) {
		isOpen = true;
		if (!TextUtils.isEmpty(TAG)) {
			logTAG = TAG;
		}
	}

	public static void log(String msg) {
		if (isOpen) {
			Log.e(logTAG, msg);
		}
	}

	public static boolean isOpenLog() {
		return isOpen;
	}
	//endregion
}
