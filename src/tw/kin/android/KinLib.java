package tw.kin.android;

import java.io.InputStream;

import tw.kin.android.connection.KinInputConnection;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

public class KinLib {

  Context sContext;
  KinInputConnection mInputConnection;
  public static SparseArray<Bitmap> sCache = new SparseArray<Bitmap>(); // 相同的圖片只需讀取一次

  public void SetContext( Context context ) {
    sContext = context;
  }

  public Context GetContext() {
    return sContext;
  }

  public Bitmap GetBitmap( int resId ) {
    Bitmap readBitmap = KinLib.sCache.get( resId );
    if ( KinLib.sCache.get( resId ) == null ) {
      BitmapFactory.Options opt = new BitmapFactory.Options();
      // opt.inPreferredConfig = Bitmap.Config.RGB_565;
      opt.inPurgeable = true;
      opt.inInputShareable = true;
      // 獲取資源圖片
      InputStream is = sContext.getResources().openRawResource( resId );
      readBitmap = BitmapFactory.decodeStream( is, null, opt );
      KinLib.sCache.put( resId, readBitmap );
    }
    return readBitmap;
  }

}
