package org.geometerplus.android.fbreader.util.fileprovider;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import java.io.File;

/**
 * desc:<br>
 * author : yuanbin<br>
 * date : 2018/8/24 18:24
 */
public class FileProviderUtil {
    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static Uri getUriForFile24(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".fileProvider", file);
        return fileUri;
    }
}
