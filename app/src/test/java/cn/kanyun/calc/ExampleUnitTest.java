package cn.kanyun.calc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.kanyun.calc.util.ImageSplitter;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSplit() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/timg.jfif",options);
        int x, y;
        x = ImageSplitter.NINE_GRID[0][0];
        y = ImageSplitter.NINE_GRID[0][0];
        List<ImagePiece> imagePieces = ImageSplitter.split(bitmap, x, y);
        for (ImagePiece imagePiece : imagePieces) {
            int index = imagePiece.getIndex();
            Bitmap bm = imagePiece.getBitmap();
            String savePath = "/storage/emulated/0/DCIM/" + index + ".jpg";
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