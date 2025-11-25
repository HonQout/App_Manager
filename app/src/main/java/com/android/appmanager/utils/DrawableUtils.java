package com.android.appmanager.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

public class DrawableUtils {

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static byte[] drawableToBytes(Drawable drawable) {
        Bitmap bitmap = drawableToBitmap(drawable);
        return bitmapToBytes(bitmap);
    }

    public static Bitmap bytesToBitmap(byte[] bytes) {
        if (bytes.length != 0) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(Resources.getSystem(), bitmap);
        return (Drawable) bd;
    }

    public static Drawable bytesToDrawable(byte[] bytes) {
        Bitmap bitmap = bytesToBitmap(bytes);
        return bitmapToDrawable(bitmap);
    }

    public static Drawable resizeDrawable(Drawable drawable, int width, int height, Resources resources) {
        Bitmap bitmap = drawableToBitmap(drawable);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return new BitmapDrawable(resources, resizedBitmap);
    }
}