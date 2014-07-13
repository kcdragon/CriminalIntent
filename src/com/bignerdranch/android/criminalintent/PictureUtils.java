package com.bignerdranch.android.criminalintent;

import java.io.IOException;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

public class PictureUtils {

    public static BitmapDrawable getRotatedDrawable(BitmapDrawable drawable) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bitmap = drawable.getBitmap();
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return new BitmapDrawable(bitmap);
    }
    
    public static BitmapDrawable getScaledDrawable(Activity activity, String path) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            }
            else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap drawable = BitmapFactory.decodeFile(path, options);

        return new BitmapDrawable(activity.getResources(), drawable);
    }

    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            return;
        }
        
        BitmapDrawable bitmap = (BitmapDrawable) imageView.getDrawable();
        bitmap.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}
