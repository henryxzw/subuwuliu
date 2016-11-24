package com.johan.subuwuliu.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.qiniu.Myupdate;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

public class ImageUtil {

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static String compressPhoto(String oldPath) throws IOException {
		if (!hasSDCard()) {
			throw new IOException("没有SD卡");
		}
		String didiDirPath = Environment.getExternalStorageDirectory() + "/"
				+ DiDiApplication.PROJECT_DIR + "/";
		File didiDir = new File(didiDirPath);
		if (!didiDir.exists() || !didiDir.isDirectory()) {
			didiDir.mkdir();
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(oldPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(oldPath, newOpts);
		String saveFileName = didiDirPath + System.currentTimeMillis() + ".jpg";
		File saveFile = new File(saveFileName);
		int quality = 100;
		boolean isOK = false;
		FileOutputStream outputStream = new FileOutputStream(saveFile);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();
			quality -= 10;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		}
		try {
			FileOutputStream fos = new FileOutputStream(saveFile);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// while (!isOK) {
		// try {
		// if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality,
		// outputStream)) {
		// outputStream.flush();
		// outputStream.close();
		// }
		// System.out.println("压缩图片成功：" + saveFileName);
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// System.out.println("压缩图片失败：" + e.getMessage());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// System.out.println("压缩图片失败：" + e.getMessage());
		// }
		// System.out.println("------image---KB------------------->>"
		// + saveFile.length());
		// if (saveFile.length() < 60 * 1024 * 2) { // 不能大于120K
		// isOK = true;
		// } else {
		// quality -= 10;
		// }
		//
		// }
		//
		// System.out.println("------image---KB------------------->>"
		// + saveFile.length());
		// System.out.println("压缩图片的路径：" + saveFileName);
		return saveFileName;
	}

	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static final int SAVE_PNG = 1;
	public static final int SAVE_JPG = 2;

	public static String saveBitmap(Bitmap bitmap, int saveMode) {
		String takePicturePath = Environment.getExternalStorageDirectory()
				+ "/" + DiDiApplication.PROJECT_DIR + "/";
		File takePictureDir = new File(takePicturePath);
		if (!takePictureDir.isDirectory()) {
			takePictureDir.mkdir();
		}
		if (saveMode == SAVE_JPG) {
			takePicturePath = takePicturePath + System.currentTimeMillis()
					+ ".jpg";
		} else if (saveMode == SAVE_PNG) {
			takePicturePath = takePicturePath + System.currentTimeMillis()
					+ ".png";
		} else {
			return null;
		}
		boolean saveResult = saveBitmap(bitmap, takePicturePath);
		if (saveResult) {
			return takePicturePath;
		}
		return null;
	}

	public static boolean saveBitmap(Bitmap bitmap, String imgPath) {
		CompressFormat format;
		if (imgPath.contains(".jpg") || imgPath.contains(".jpeg")) {
			format = Bitmap.CompressFormat.JPEG;
		} else if (imgPath.contains(".png")) {
			format = Bitmap.CompressFormat.PNG;
		} else {
			return false;
		}
		try {
			FileOutputStream op = new FileOutputStream(imgPath);
			bitmap.compress(format, 100, op);
			op.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Bitmap getBitmapFromPath(Context context, String imgPath,
			int width) {
		// 设置BitmapFactory的option
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 为了可以得到真实图片的大小
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, options);
		// 取得像素的图片
		options.inSampleSize = options.outWidth / 256;
		// 不进行图片抖动处理，也可以优化内存
		options.inDither = false;
		// 设置设置Bitmap之前要变回false
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imgPath, options);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
	 */
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {
		// 文件路径，直接返回
		File file = new File(uri.getPath());
		if (file.exists()) {
			return uri.getPath();
		}
		// 下面是Uri
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	public static int a = 0;

	public static void getBitmab(final String name, final Bitmap bmp) {

		UploadManager uploadManager = new UploadManager();
		String token = Myupdate.Token();
		String key = name;
		uploadManager.put(Myupdate.Bitmap2Bytes(bmp), key, token,
				new UpCompletionHandler() {
					@Override
					public void complete(String arg0, ResponseInfo arg1,
							JSONObject arg3) {
						// res 包含hash、key等信息，具体字段取决于上传策略的设置。
						if (arg1.statusCode == 200) {

						} else if (arg1.statusCode == 401) {
						
							if (a > 3) {
								return;
							}
							a++;
							getBitmab(name, bmp);
							return;
						} else {
							Toast.makeText(DiDiApplication.applicationContext,
									"上传失败", 0).show();
							return;
						}
					}
				}, null);

	}
}
