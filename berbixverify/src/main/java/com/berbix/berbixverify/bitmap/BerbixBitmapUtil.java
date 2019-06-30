package com.berbix.berbixverify.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

import com.google.android.gms.vision.face.Face;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BerbixBitmapUtil {
    private static final String TAG = "CameraExif";

    // Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
    static int getOrientation(byte[] jpeg) {
        if (jpeg == null) {
            return 0;
        }

        int offset = 0;
        int length = 0;

        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
            int marker = jpeg[offset] & 0xFF;

            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue;
            }
            offset++;

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                break;
            }

            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false);
            if (length < 2 || offset + length > jpeg.length) {
                Log.e(TAG, "Invalid length");
                return 0;
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 &&
                    pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                    pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8;
                length -= 8;
                break;
            }

            // Skip other markers.
            offset += length;
            length = 0;
        }

        // JEITA CP-3451 BerbixBitmapUtil Version 2.2
        if (length > 8) {
            // Identify the byte order.
            int tag = pack(jpeg, offset, 4, false);
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                Log.e(TAG, "Invalid byte order");
                return 0;
            }
            boolean littleEndian = (tag == 0x49492A00);

            // Get the offset and check if it is reasonable.
            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count < 10 || count > length) {
                Log.e(TAG, "Invalid offset");
                return 0;
            }
            offset += count;
            length -= count;

            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian);
            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian);
                if (tag == 0x0112) {
                    // We do not really care about type and count, do we?
                    int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                    switch (orientation) {
                        case 1:
                            return 0;
                        case 3:
                            return 180;
                        case 6:
                            return 90;
                        case 8:
                            return 270;
                    }
                    Log.i(TAG, "Unsupported orientation");
                    return 0;
                }
                offset += 12;
                length -= 12;
            }
        }

        Log.i(TAG, "Orientation not found");
        return 0;
    }

    private static int pack(byte[] bytes, int offset, int length,
                            boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }

        int value = 0;
        while (length-- > 0) {
            value = (value << 8) | (bytes[offset] & 0xFF);
            offset += step;
        }
        return value;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),   source.getHeight(), matrix,
                true);
    }

    public static Bitmap fixOrientation(byte[] bytes) {
        int orientation = getOrientation(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes ,0, bytes.length);
        Bitmap bitmapPicture = null;

        switch(orientation) {
            case 90:
                bitmapPicture = rotateImage(bitmap, 90);

                break;
            case 180:
                bitmapPicture = rotateImage(bitmap, 180);

                break;
            case 270:
                bitmapPicture = rotateImage(bitmap, 270);

                break;
            case 0:
                bitmapPicture = bitmap;

            default:
                break;
        }

        return bitmapPicture;
    }

    public static Bitmap scaleBitmap(Bitmap bmp, int targetWidth) {
        double scale = (double) bmp.getWidth() / targetWidth;

        return Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() / scale), (int) (bmp.getHeight() / scale), false);
    }

    public static Bitmap cropBitmap(Bitmap bmp, double ratio) {
        int width = bmp.getWidth();
        int height = (int) (width / ratio);
        int x = ((bmp.getWidth() - width) / 2);
        int y = ((bmp.getHeight() - height) / 2);

        Bitmap cropped = Bitmap.createBitmap(bmp, x, y, width, height);

        return scaleBitmap(cropped, 800);
    }

    public static Bitmap cropDetected(Bitmap bmp, Rect bound, int screenWidth, int screenHeight, double density) {

        double xScale = (double) bmp.getWidth() / screenWidth;
        double yScale = (double) bmp.getHeight() / screenHeight;

        double scaledX = bound.left * xScale;
        double scaledY = bound.top * yScale;
        double scaledWidth = bound.width() * xScale;
        double scaledHeight = bound.height() * yScale;

        int cropMinX = Math.max(0, (int) Math.round(scaledX - (scaledWidth * 0.2)));
        int cropMaxX = Math.min(bmp.getWidth(), (int) Math.round(scaledX + (scaledWidth * 1.2)));

        int cropMinY = Math.max(0, (int) Math.round(scaledY - (scaledHeight * 0.2)));
        int cropMaxY = Math.min(bmp.getHeight(), (int) Math.round(scaledY + (scaledHeight * 1.2)));

        Bitmap cropped = Bitmap.createBitmap(bmp, cropMinX, cropMinY, cropMaxX - cropMinX, cropMaxY - cropMinY);

        return scaleBitmap(cropped, 800);
    }

    public static File saveToJpg(Context context, Bitmap bmp, String filename) {
        File f = new File(context.getCacheDir(), filename);
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            return null;
        }

        return f;
    }

    public static File saveToPng(Context context, Bitmap bmp, String filename) {
        File f = new File(context.getCacheDir(), filename);
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            return null;
        }

        return f;
    }
}