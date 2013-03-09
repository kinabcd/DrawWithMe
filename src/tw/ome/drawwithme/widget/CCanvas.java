package tw.ome.drawwithme.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import tw.kin.android.KinPoint;
import tw.kin.android.layout.KinAbsoluteLayout;
import tw.ome.drawwithme.CConstant;
import tw.ome.drawwithme.DrawSurface;
import tw.ome.drawwithme.protocal.Action;
import tw.ome.drawwithme.protocal.IActionCotroller;
import tw.ome.drawwithme.ui.CDrawBoard;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.Toast;

public class CCanvas extends KinAbsoluteLayout {
  enum MODE {
    WAIT, DRAW, MOVE, SCALE
  };

  IActionCotroller mProtocal;
  Bitmap mBitmap;
  Canvas mCanvas;
  int mBackgroundColor;
  Action mNewAction;
  String pc;
  KinPoint mDownCenterPoint;
  double mDownLength;
  double mViewScaleRateOld;
  double mViewScaleRate;
  KinPoint mViewStart;
  KinPoint mViewSize;
  Rect mViewRect; // 畫布要轉畫到螢幕的範圍
  MODE mTouchMode;
  int mPointCount;

  public CCanvas() {
  }

  public void New( int width, int height, int bgColor, IActionCotroller mode ) {
    mBitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
    mCanvas = new Canvas( mBitmap );
    mCanvas.drawColor( bgColor );
    mBackgroundColor = bgColor;
    mViewScaleRate = 1;
    mViewStart = new KinPoint( 0, 0 );
    mViewSize = new KinPoint( GetWidth(), GetHeight() );
    mProtocal = mode;
    mTouchMode = MODE.WAIT;
  }

  public void ChangeMode( MODE mode, MotionEvent event ) {
    if ( mode == MODE.SCALE ) {
      mTouchMode = MODE.SCALE;
      mNewAction = null;
      KinPoint centerPointOnScreen = new KinPoint( ( event.getX( 0 ) + event.getX( 1 ) ) / 2, ( event.getY( 0 ) + event.getY( 1 ) ) / 2 );
      mDownCenterPoint = ToCanvasPoint( centerPointOnScreen );
      mDownLength = Math.sqrt( Math.pow( event.getX( 0 ) - event.getX( 1 ), 2 ) + Math.pow( event.getY( 0 ) - event.getY( 1 ), 2 ) );
      mViewScaleRateOld = mViewScaleRate;
    } else if ( mode == MODE.MOVE ) {
      mTouchMode = MODE.MOVE;
      mDownCenterPoint = null;
    } else if ( mode == MODE.DRAW ) {
      KinPoint screenPoint = new KinPoint( event.getX(), event.getY() );
      int pen = ( (CDrawBoard) GetParent() ).mUISelectPen.GetPen();
      int color = ( (CDrawBoard) GetParent() ).mUISelectColor.GetColor();
      int size = ( (CDrawBoard) GetParent() ).mUISelectPen.GetSize();
      if ( pen == CConstant.PENERASER )
        color = mBackgroundColor;
      mNewAction = new Action( pen, color, size );
      mNewAction.AddPoint( ToCanvasPoint( screenPoint ) );
      mPointCount = 0;
      mTouchMode = MODE.DRAW;
    } else if ( mode == MODE.WAIT ) {
      if ( mNewAction != null ) {
        mNewAction.SetCompleted();
        mProtocal.PushAction( mNewAction );
        mNewAction = null;
      }
      if ( mViewSize.x * mViewScaleRate > mBitmap.getWidth() && mViewSize.y * mViewScaleRate > mBitmap.getHeight() ) {
        double mViewScaleRateX = mBitmap.getWidth() / mViewSize.x;
        double mViewScaleRateY = mBitmap.getHeight() / mViewSize.y;
        if ( mViewScaleRateX > mViewScaleRateY )
          mViewScaleRate = mViewScaleRateX;
        else
          mViewScaleRate = mViewScaleRateY;
      }

      if ( mViewStart.x + mViewSize.x * mViewScaleRate > mBitmap.getWidth() )
        mViewStart.x = mBitmap.getWidth() - mViewSize.x * mViewScaleRate;
      if ( mViewStart.y + mViewSize.y * mViewScaleRate > mBitmap.getHeight() )
        mViewStart.y = mBitmap.getHeight() - mViewSize.y * mViewScaleRate;
      if ( mViewStart.x < 0 )
        mViewStart.x = 0;
      if ( mViewStart.y < 0 )
        mViewStart.y = 0;
      mTouchMode = MODE.WAIT;
      RequireRedraw();
    }
  }

  public KinPoint ToCanvasPoint( KinPoint screenPoint ) {
    int xOnStage = (int) ( ( screenPoint.x - GetX() ) * mViewScaleRate + mViewStart.x );
    int yOnStage = (int) ( ( screenPoint.y - GetY() ) * mViewScaleRate + mViewStart.y );
    return new KinPoint( xOnStage, yOnStage );
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( super.onTouchEvent( event ) )
      return true;
    if ( event.getPointerCount() > 2 )
      return false;
    pc = mTouchMode.toString() + " " + event.getPointerCount() + "\n";
    pc += event.getAction();
    for ( int i = 0; i < event.getPointerCount(); i += 1 )
      pc += "(" + event.getX( i ) + "," + event.getY( i ) + ")";

    if ( event.getAction() == MotionEvent.ACTION_CANCEL ) // 第三指下
      ChangeMode( MODE.WAIT, event );

    if ( mTouchMode == MODE.SCALE ) {
      if ( ( event.getAction() & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_POINTER_UP ) // 第二點起
        ChangeMode( MODE.MOVE, event );
      if ( event.getAction() == MotionEvent.ACTION_MOVE ) { // 雙指移動
        double x = ( event.getX( 0 ) + event.getX( 1 ) ) / 2;
        double y = ( event.getY( 0 ) + event.getY( 1 ) ) / 2;
        double length = Math.sqrt( Math.pow( event.getX( 0 ) - event.getX( 1 ), 2 ) + Math.pow( event.getY( 0 ) - event.getY( 1 ), 2 ) );
        mViewScaleRate = mViewScaleRateOld * mDownLength / length;
        if ( mViewScaleRate < 0.05 )
          mViewScaleRate = 0.05;
        KinPoint newCenterPoint = ToCanvasPoint( new KinPoint( x, y ) );
        mViewStart.x -= newCenterPoint.x - mDownCenterPoint.x;
        mViewStart.y -= newCenterPoint.y - mDownCenterPoint.y;
        RequireRedraw();
      }
    } else if ( mTouchMode == MODE.MOVE ) {
      if ( event.getAction() == MotionEvent.ACTION_UP ) // 第一點起
        ChangeMode( MODE.WAIT, event );
      if ( event.getAction() == MotionEvent.ACTION_MOVE ) { // 單指移動
        KinPoint newCenterPoint = ToCanvasPoint( new KinPoint( event.getX(), event.getY() ) );
        if ( mDownCenterPoint != null ) {
          mViewStart.x -= newCenterPoint.x - mDownCenterPoint.x;
          mViewStart.y -= newCenterPoint.y - mDownCenterPoint.y;
        } else
          mDownCenterPoint = newCenterPoint;
        RequireRedraw();
      }
      if ( ( event.getAction() & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_POINTER_DOWN ) // 第二點下
        ChangeMode( MODE.SCALE, event );
    } else if ( mTouchMode == MODE.DRAW ) {
      if ( ( event.getAction() & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_POINTER_DOWN ) // 第二點下
        ChangeMode( MODE.SCALE, event );
      KinPoint screenPoint = new KinPoint( event.getX(), event.getY() );

      if ( event.getAction() == MotionEvent.ACTION_MOVE ) {
        mNewAction.AddPoint( ToCanvasPoint( screenPoint ) );
        mPointCount += 1;
        if ( mPointCount > 5 )
          mProtocal.PushAction( mNewAction );
      }
      if ( event.getAction() == MotionEvent.ACTION_UP ) {
        mNewAction.AddPoint( ToCanvasPoint( screenPoint ) );
        ChangeMode( MODE.WAIT, event );
      }
    } else if ( mTouchMode == MODE.WAIT ) {
      KinPoint screenPoint = new KinPoint( event.getX(), event.getY() );
      if ( !screenPoint.In( GetViewRect() ) )
        return false;
      if ( event.getAction() == MotionEvent.ACTION_DOWN )
        ChangeMode( MODE.DRAW, event );

    }
    return true;
  }

  @Override
  public void Draw( Canvas canvas ) {
    canvas.save();
    canvas.clipRect( GetViewRect() );
    canvas.drawColor( 0xff888888 );
    int right = (int) ( mViewSize.x * mViewScaleRate + mViewStart.x );
    int bottom = (int) ( mViewSize.y * mViewScaleRate + mViewStart.y );
    mViewRect = new Rect( (int) mViewStart.x, (int) mViewStart.y, right, bottom );
    List<Action> actions = mProtocal.PullAction();
    for ( Action a : actions )
      a.Draw( mCanvas );
    canvas.drawBitmap( mBitmap, mViewRect, GetViewRect(), null );
    Paint m = new Paint();
    m.setColor( Color.BLACK );
    m.setStyle( Style.FILL );
    canvas.drawText( "" + pc, 0, 100, m );
    super.Draw( canvas );
    canvas.restore();

  }

  public void CompatibleWith( double windowWidth, double windowHeight ) {
    int myY = (int) ( windowHeight * ( windowHeight * ( -1 / 28800.0 ) + ( 1 / 9.0 ) ) );
    SetPos( 0, myY, (int) windowWidth, (int) windowHeight );
    RequireRedraw();
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
