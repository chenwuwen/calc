package cn.kanyun.calc;

import android.graphics.drawable.Drawable;


public class Reward {

    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片路径(其包含图片名称)
     */
    private String path;
    private Drawable drawable;
    private boolean lock = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
