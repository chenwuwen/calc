package cn.kanyun.calc;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


/**
 * 图片切割实体类
 * 此类保存了一个Bitmap对象和一个标识图片的顺序索引的int变量
 */
public class ImagePiece implements Serializable, Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeParcelable(this.bitmap, flags);
    }

    public ImagePiece() {
    }

    protected ImagePiece(Parcel in) {
        this.index = in.readInt();
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<ImagePiece> CREATOR = new Parcelable.Creator<ImagePiece>() {
        @Override
        public ImagePiece createFromParcel(Parcel source) {
            return new ImagePiece(source);
        }

        @Override
        public ImagePiece[] newArray(int size) {
            return new ImagePiece[size];
        }
    };
}