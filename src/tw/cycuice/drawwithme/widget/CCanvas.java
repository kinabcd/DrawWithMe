package tw.cycuice.drawwithme.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.protocal.Action;
import tw.cycuice.drawwithme.ui.CDrawBoard;
import tw.kin.android.KinPoint;
import tw.kin.android.KinView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.Toast;

public class CCanvas extends KinView {
  Bitmap mBitmap;
  Canvas mCanvas;
  Rect WindowRect; // 螢幕上顯示的範圍
  Rect ViewRect; // 畫布要轉畫到螢幕的範圍
  int mBackgroundColor;
  Action newAction;

  public CCanvas() {
  }

  public void Apply( Action action ) {
    if ( action == null )
      return;
    action.Draw( mCanvas );
  }

  public void New( int width, int height, int bgColor ) {
    mBitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
    mCanvas = new Canvas( mBitmap );
    mCanvas.drawColor( bgColor );
    mBackgroundColor = bgColor;
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( super.onTouchEvent( event ) )
      return true;
    float x = event.getX();
    float y = event.getY();
    double yrate = (double) ( ViewRect.bottom - ViewRect.top ) / (double) ( WindowRect.bottom - WindowRect.top );
    double xrate = (double) ( ViewRect.right - ViewRect.left ) / (double) ( WindowRect.right - WindowRect.left );
    int xOnStage = (int) ( ( x - WindowRect.left ) * xrate ) + ViewRect.left;
    int yOnStage = (int) ( ( y - WindowRect.top ) * yrate ) + ViewRect.top;
    KinPoint touchPoint = new KinPoint( xOnStage, yOnStage );
    if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
      if ( x < WindowRect.left || x > WindowRect.right )
        return false;
      if ( y < WindowRect.top || y > WindowRect.bottom )
        return false;
      int pen = ( (CDrawBoard) GetParent() ).mUISelectPen.GetPen();
      int color = ( (CDrawBoard) GetParent() ).mUISelectColor.GetColor();
      int size = ( (CDrawBoard) GetParent() ).mUISelectPen.GetSize();
      if ( pen == CConstant.PENERASER )
        color = mBackgroundColor;
      newAction = new Action( 0, ViewRect, pen, color, size );
      newAction.AddPoint( touchPoint );
    }
    if ( event.getAction() == MotionEvent.ACTION_MOVE ) {
      if ( newAction == null )
        return false;
      newAction.AddPoint( touchPoint );
    }
    if ( event.getAction() == MotionEvent.ACTION_UP ) {
      if ( newAction == null )
        return false;
      newAction.AddPoint( touchPoint );
      ( (CDrawBoard) GetParent() ).Submit( newAction );
      newAction = null;
    }
    return true;
  }

  @Override
  public void Draw( Canvas canvas ) {
    mHasUpdate = false;
    canvas.drawBitmap( mBitmap, ViewRect, WindowRect, null );
    if ( newAction != null )
      newAction.Draw( canvas, WindowRect );

  }

  public void CompatibleWith( double windowWidth, double windowHeight ) {
    int myY = (int) ( windowHeight * ( windowHeight * ( -1 / 28800.0 ) + ( 1 / 9.0 ) ) );
    WindowRect = new Rect( 0, myY, (int) windowWidth, (int) windowHeight );
    ViewRect = new Rect( 0, 0, (int) windowWidth, (int) windowHeight - myY );
    mHasUpdate = true;
  }

  public void Save() {
    try {
      // 輸出的圖檔位置
      File sdFile;
      if ( !Environment.getExternalStorageState().equals( Environment.MEDIA_REMOVED ) ) {
        // 從系統取得外部存取位置
        sdFile = Environment.getExternalStorageDirectory();
      } else {
        Toast.makeText( DrawSurface.GetInstance().getContext(), "Save Fail!!", Toast.LENGTH_LONG ).show();
        return;
      }
      // Draw with me 資料夾位置
      String appPath = sdFile.getPath() + "/drawwithme/";
      File vPath = new File( appPath );
      if ( !vPath.exists() )
        vPath.mkdirs();

      // 本次儲存檔案位置
      String filename = appPath + System.currentTimeMillis() + ".png";
      FileOutputStream fos = new FileOutputStream( filename );

      // 將 Bitmap 儲存成 PNG / JPEG 檔案格式
      mBitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );
      fos.close();
      Toast.makeText( DrawSurface.GetInstance().getContext(), "Save to " + filename, Toast.LENGTH_LONG ).show();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
