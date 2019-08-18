package cn.kanyun.calc.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import cn.kanyun.calc.ImagePiece;

/**
 * 图片切割工具类
 */
public class ImageSplitter {

    /**
     * 九宫格
     */
    public static int[][] NINE = {{3}, {3}};

    /**
     * 十六宫格
     */
    public static int[][] SIXTEEN = {{4}, {4}};

    /**
     * 图片切割
     * 传入的参数是要切割的Bitmap对象，和横向和竖向的切割片数
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
}