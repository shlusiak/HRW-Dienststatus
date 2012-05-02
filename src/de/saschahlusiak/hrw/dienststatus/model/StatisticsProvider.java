package de.saschahlusiak.hrw.dienststatus.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class StatisticsProvider {
	private static final String tag = StatisticsProvider.class.getSimpleName();
	private static final int TIMEOUT = 30; /* minutes */
	
	public static BitmapDrawable getImage(Context context, String url, boolean force) {
		File file = getCacheFile(context, url);

		/* use cached file if younger than TIMEOUT minutes */
		if (force || (file.lastModified() < System.currentTimeMillis() - TIMEOUT * 60 * 1000)) {
			try {
				InputStream myInput = (InputStream) new URL("http://static.hs-weingarten.de/portvis/" + url + ".png").getContent();
				OutputStream myOutput = new FileOutputStream(file);
				byte[] buffer = new byte[4096];
				int length;
				while ((length = myInput.read(buffer)) > 0) {
					myOutput.write(buffer, 0, length);
					if (Thread.interrupted()) {
						myOutput.flush();
						myInput.close();
						myOutput.close();
						file.delete();
						Log.d(tag, "removed partly downloaded file "
								+ file.getPath());
						return null;
					}
				}
				myOutput.flush();
				myInput.close();
				myOutput.close();
				file.setReadable(true, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.d(tag, "using cached file " + file.getPath());
		}
				
		BitmapDrawable b = (BitmapDrawable) BitmapDrawable.createFromPath(file.getPath());
		return b;
	}

	public static File getCacheFile(Context context, String url) {
		File cache_dir = context.getCacheDir();
		return new File(cache_dir, url + ".png");
	}
}
