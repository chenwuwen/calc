package cn.kanyun.calc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.blankj.utilcode.util.FileUtils;

import cn.kanyun.calc.ImagePiece;

/**
 * 图片切割工具类
 */
public class ImageSplitter {

    /**
     * 九宫格
     */
    public static int[][] NINE_GRID = {{3}, {3}};

    /**
     * 十六宫格
     */
    public static int[][] SIXTEEN_GRID = {{4}, {4}};

    /**
     * 图片切割
     * 传入的参数是要切割的Bitmap对象，和横向和竖向的切割片数
     *
     * @param bitmap 导入图片
     * @param x      x轴切割
     * @param y      y轴切割
     * @return
     */
    public static List<ImagePiece> split(Bitmap bitmap, int x, int y) {
        List<ImagePiece> pieces = new ArrayList<ImagePiece>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / x;
        int pieceHeight = height / y;
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                ImagePiece image = new ImagePiece();
                image.index = j + i * x;
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                image.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
                        pieceWidth, pieceHeight);
                pieces.add(image);
            }
        }
        return pieces;
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap, String fileName) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = "";
        } else {
            savePath = context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
        filePic = new File(savePath + fileName + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(filePic)) {
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
//            bitmap压缩的方法 第一个参数：压缩格式 第二个参数：压缩率,如果是20 表示压缩80% 如果不压缩则设置为100
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    public static void test(Bitmap bitmap) throws IOException {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        options.inBitmap = null;
//        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Download/未命名文件 (1).jpg", options);
        int x, y;
        x = NINE_GRID[0][0];
        y = NINE_GRID[0][0];
        List<ImagePiece> imagePieces = split(bitmap, x, y);
        for (ImagePiece imagePiece : imagePieces) {
            int index = imagePiece.getIndex();
            Bitmap bm = imagePiece.getBitmap();
            String savePath = "/storage/emulated/0/DCIM/" + index + ".jpg";
            File file = new File(savePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (FileOutputStream fos = new FileOutputStream(savePath)){
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}