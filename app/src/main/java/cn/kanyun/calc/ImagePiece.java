package cn.kanyun.calc;

import android.graphics.Bitmap;

/**
 * 图片切割实体类
 * 此类保存了一个Bitmap对象和一个标识图片的顺序索引的int变量
 */
public class ImagePiece {
    public int index = 0;
    public Bitmap bitmap = null;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}