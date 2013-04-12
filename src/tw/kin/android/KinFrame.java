package tw.kin.android;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class KinFrame {

  public Bitmap mImage;
  public int mKeepTime;
  public Rect mRect;
  public int mColor;

  public KinFrame(int color, int keeptime) {
    mColor = color;
  }

  public KinFrame(Bitmap image, int keeptime) {
    mImage = image;
    mKeepTime = keeptime;
    mRect = new Rect( 0, 0, image.getWidth(), image.getHeight() );
  }
}
