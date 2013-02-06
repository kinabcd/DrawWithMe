package tw.cycuice.drawwithme.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.R;
import tw.cycuice.drawwithme.widget.CSelectColor;
import tw.cycuice.drawwithme.widget.CSelectPen;
import tw.kin.android.KinImage;
import tw.kin.android.KinPoint;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.Toast;

public class CCanvas extends KinView implements IUI {
  class Action {
    int mOpre;
    int mPen;
    int mColor;
    int mSize;
    Object mPares[];

    Action(int op, Object[] pares, int pen, int color, int size) {
      mOpre = op;
      mPen = pen;
      mColor = color;
      mSize = size;
      mPares = pares;
    }

  }

  // TODO MiniMap
  Bitmap mBitmap;
  Canvas mCanvas;
  Rect WindowRect; // 螢幕上顯示的範圍
  Rect ViewRect; // 畫布要轉畫到螢幕的範圍
  Paint mPaint[];
  KinPoint lastTouchPoint = null;
  int mBGColor;
  Action newAction;
  List<Action> mActions;
  CSelectPen mUISelectPen;
  CSelectColor mUISelectColor;
  KinButton mBSetting;
  KinButton mBSelectColor;
  KinButton mBCamera;
  KinImage mTopbarBG;

  public CCanvas() {
  }

  public void New( int width, int height, int color ) {
    mBitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
    mCanvas = new Canvas( mBitmap );
    mCanvas.drawColor( color );
    mBGColor = color;

  }

  @Override
  public void Draw( Canvas canvas ) {
    mHasUpdate = false;
    while ( !mActions.isEmpty() ) {
      Action exe = mActions.remove( 0 );
      if ( exe.mPen == CConstant.PENNORMAL || exe.mPen == CConstant.PENERASER ) {
        Paint paint = new Paint();
        paint.setColor( exe.mPen == CConstant.PENNORMAL ? exe.mColor : mBGColor );
        paint.setStrokeWidth( exe.mSize );
        paint.setStyle( Style.FILL );
        paint.setAntiAlias( true );
        if ( exe.mOpre == 1 )
          DrawPoint( (KinPoint) exe.mPares[0], paint );
        else if ( exe.mOpre == 2 )
          DrawLine( (KinPoint) exe.mPares[0], (KinPoint) exe.mPares[1], paint );
      } else if ( exe.mPen == CConstant.PENHIGHLIGHTER || exe.mPen == CConstant.PENWATERCOLOR ) {
        Paint paint = new Paint();
        paint.setColor( exe.mColor );
        paint.setAlpha( exe.mPen == CConstant.PENHIGHLIGHTER ? 12 : 3 );
        paint.setStrokeWidth( exe.mSize );
        paint.setStyle( Style.FILL );
        paint.setAntiAlias( true );
        if ( exe.mOpre == 1 )
          DrawPoint( (KinPoint) exe.mPares[0], paint );
        else if ( exe.mOpre == 2 )
          DrawLine( (KinPoint) exe.mPares[0], (KinPoint) exe.mPares[1], paint );
      } else if ( exe.mPen == CConstant.PENNEON ) {
        Paint paint = new Paint();
        paint.setColor( Color.WHITE );
        paint.setStrokeWidth( (int) ( exe.mSize / 0.7 ) );
        paint.setStyle( Style.FILL );
        paint.setAntiAlias( true );
        if ( exe.mOpre == 1 )
          DrawPoint( (KinPoint) exe.mPares[0], paint );
        else if ( exe.mOpre == 2 )
          DrawLine( (KinPoint) exe.mPares[0], (KinPoint) exe.mPares[1], paint );

      }

    }
    canvas.drawBitmap( mBitmap, ViewRect, WindowRect, null );
    mTopbarBG.Draw( canvas, 0, 0 );
    super.Draw( canvas );
  }

  @Override
  public void LoadContent() {
    New( CConstant.MaxWidth, CConstant.MaxHeight, Color.WHITE );
    mActions = Collections.synchronizedList( new LinkedList<Action>() );
    mUISelectPen = new CSelectPen();
    mUISelectPen.LoadContent();
    mUISelectColor = new CSelectColor();
    mUISelectColor.LoadContent();
    KinImage imgSetting = new KinImage();
    imgSetting.AddImage( R.drawable.setting, -1 );
    mBSetting = new KinButton( imgSetting );
    mBSetting.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        if ( mUISelectPen.IsVisible() )
          mUISelectPen.Hide();
        else
          mUISelectPen.Show();
      }
    } );

    KinImage imgSelectColor = new KinImage();
    imgSelectColor.AddImage( R.drawable.select_color, -1 );
    mBSelectColor = new KinButton( imgSelectColor );
    mBSelectColor.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        if ( mUISelectColor.IsVisible() )
          mUISelectColor.Hide();
        else
          mUISelectColor.Show();
      }
    } );

    KinImage imgCamera = new KinImage();
    imgCamera.AddImage( R.drawable.camera, -1 );
    mBCamera = new KinButton( imgCamera );
    mBCamera.SetOnClickRun( new Runnable() {

      @Override
      public void run() {
        Save();
      }

    } );

    mTopbarBG = new KinImage();
    mTopbarBG.AddImage( R.drawable.topbar_bg, -1 );

    AddChild( mBSetting );
    AddChild( mBSelectColor );
    AddChild( mBCamera );
    AddChild( mUISelectPen );
    AddChild( mUISelectColor );
    mHasUpdate = true;

  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    int myY = (int) ( windowHeight * ( windowHeight * ( -1 / 28800.0 ) + ( 1 / 9.0 ) ) );
    WindowRect = new Rect( 0, myY, (int) windowWidth, (int) windowHeight );
    ViewRect = new Rect( 0, 0, (int) windowWidth, (int) windowHeight - myY );
    int bSize = (int) ( windowHeight * ( windowHeight * ( -1 / 28800.0 ) + ( 1 / 9.0 ) ) );
    mBSetting.SetPos( 0, 0, bSize, bSize );
    mBSelectColor.SetPos( bSize, 0, bSize * 2, bSize );
    mBCamera.SetPos( bSize * 2, 0, bSize * 3, bSize );
    mTopbarBG.SetSize( windowWidth, bSize );
    mUISelectColor.CompatibleWith( windowWidth, windowHeight );
    mUISelectPen.CompatibleWith( windowWidth, windowHeight );
    mHasUpdate = true;

  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( super.onTouchEvent( event ) )
      return true;
    if ( event.getX() < WindowRect.left )
      return false;
    if ( event.getX() > WindowRect.right )
      return false;
    if ( event.getY() < WindowRect.top )
      return false;
    if ( event.getY() > WindowRect.bottom )
      return false;
    float x = event.getX();
    float y = event.getY();
    double yrate = (double) ( ViewRect.bottom - ViewRect.top ) / (double) ( WindowRect.bottom - WindowRect.top );
    double xrate = (double) ( ViewRect.right - ViewRect.left ) / (double) ( WindowRect.right - WindowRect.left );
    int xOnStage = (int) ( ( x - WindowRect.left ) * xrate ) + ViewRect.left;
    int yOnStage = (int) ( ( y - WindowRect.top ) * yrate ) + ViewRect.top;
    KinPoint touchPoint = new KinPoint( xOnStage, yOnStage );
    Object temp[];
    switch ( event.getAction() ) {
    case MotionEvent.ACTION_DOWN:
      lastTouchPoint = touchPoint;
      break;
    case MotionEvent.ACTION_MOVE:
      temp = new Object[3];
      temp[0] = lastTouchPoint;
      temp[1] = touchPoint;
      mActions.add( new Action( 2, temp, mUISelectPen.GetPen(), mUISelectColor.GetColor(), mUISelectPen.GetSize() ) );
      lastTouchPoint = touchPoint;
      break;
    case MotionEvent.ACTION_UP:
      temp = new Object[2];
      temp[0] = touchPoint;
      mActions.add( new Action( 1, temp, mUISelectPen.GetPen(), mUISelectColor.GetColor(), mUISelectPen.GetSize() ) );
      break;
    }
    return true;
  }

  void DrawPoint( KinPoint p1, Paint paint ) {
    mCanvas.drawCircle( (float) p1.x, (float) p1.y, paint.getStrokeWidth() / 2, paint );
    mHasUpdate = true;
  }

  void DrawLine( KinPoint p1, KinPoint p2, Paint paint ) {
    if ( Math.abs( p1.x - p2.x ) > Math.abs( p1.y - p2.y ) ) {
      if ( p1.x > p2.x ) {
        KinPoint temp = p1;
        p1 = p2;
        p2 = temp;
      }
      for ( int i = (int) p1.x; i < p2.x; i += 1 ) {
        float myY = (float) ( ( i - p1.x ) / ( p2.x - p1.x ) * ( p2.y - p1.y ) + p1.y );
        mCanvas.drawCircle( i, myY, paint.getStrokeWidth() / 2, paint );
      }
    } else {
      if ( p1.y > p2.y ) {
        KinPoint temp = p1;
        p1 = p2;
        p2 = temp;
      }
      for ( int i = (int) p1.y; i < p2.y; i += 1 ) {
        float myX = (float) ( ( i - p1.y ) / ( p2.y - p1.y ) * ( p2.x - p1.x ) + p1.x );
        mCanvas.drawCircle( myX, i, paint.getStrokeWidth() / 2, paint );
      }
    }
    mHasUpdate = true;
  }

  @Override
  public boolean HasUpdate() {
    if ( !mActions.isEmpty() )
      return true;
    return super.HasUpdate();
  }

  public void Save() {
    try {
      // 輸出的圖檔位置
      File sdFile = new File( "/sdcard/" );
      if ( !Environment.getExternalStorageState().equals( Environment.MEDIA_REMOVED ) ) {
        sdFile = Environment.getExternalStorageDirectory();
      } else if ( sdFile.exists() ) {

        // 取得 SD Card 位置
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

  @Override
  public void onStart( IUI from ) {
    mUISelectColor.SetColor( Color.BLACK );
    mUISelectColor.Hide();
    mUISelectPen.SetPen( CConstant.PENNORMAL );
    mUISelectPen.SetSize( 10 );
    mUISelectPen.Hide();
    mHasUpdate = true;

  }

  @Override
  public void onQuit( IUI to ) {

  }
}
