package ru.tulupov.nsuconnect.helper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import ru.tulupov.nsuconnect.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public final class BitmapHelper {

    private static final String TAG = "BitmapHelper";
    public static final int PICTURE_MAX_SIZE = 600;

    private BitmapHelper() {
        throw new UnsupportedOperationException();
    }

    public static String getPicturePath(final Context context, final Intent data) {
        if (data == null) {
            return null;
        }
        final Uri selectedImage = data.getData();
        if (selectedImage == null) {
            return null;
        }
        final String[] filePathColumn = {MediaStore.Images.Media.DATA};
        final Cursor cursor = context.getContentResolver()
                .query(selectedImage, filePathColumn, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        final String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    public static Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    public static int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }


        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
        return rotate;
    }

    public static Bitmap getScaledBitmap(Bitmap photo) {
        final int bitmapWidth = photo.getWidth();
        final int bitmapHeight = photo.getHeight();
        float ratio;
        int targetWidth;
        int targetHeight;
        if (bitmapWidth > bitmapHeight) {
            ratio = (float) bitmapHeight / bitmapWidth;
            targetWidth = PICTURE_MAX_SIZE;
            targetHeight = Math.round(targetWidth * ratio);
        } else {
            ratio = (float) bitmapWidth / bitmapHeight;

            targetHeight = PICTURE_MAX_SIZE;
            targetWidth = Math.round(targetHeight * ratio);
        }


        return Bitmap.createScaledBitmap(photo, targetWidth, targetHeight, true);
    }

    public static Bitmap getNormalPhoto(String picturePath) {
        Bitmap bitmap = BitmapHelper.getScaledBitmap(picturePath, PICTURE_MAX_SIZE, PICTURE_MAX_SIZE);

        int orientation = getCameraPhotoOrientation(picturePath);

        if (orientation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            Bitmap rotateImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            bitmap = rotateImage;
        }
        return bitmap;
    }

    public static File getTempFile() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/tmp");
        dir.mkdirs();
        return new File(dir, String.format("%d.jpg", System.currentTimeMillis()));
    }

    public static File saveBitmapToTmpFile(Bitmap bitmap) {
        File file = null;
        try {

            file = getTempFile();
            file.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (Exception e) {
            android.util.Log.e(TAG, "Error", e);
        }
        return file;
    }
}
