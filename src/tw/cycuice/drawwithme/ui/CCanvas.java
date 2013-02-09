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
    Rect mViewRect;
    List<KinPoint> mPath;
    Rect mBitmapRect;
    Bitmap mBitmap;
    Paint mPaint;
    int mLastDrawPointIndex;

    Action(int op, Rect view, int pen, int color, int size) {
      mOpre = op;
      mPen = pen;
      mColor = color;
      mSize = size;
      mPath = new LinkedList<KinPoint>();
      mViewRect = new Rect( view );

      mLastDrawPointIndex = 0;
      mBitmapRect = new Rect();
      mBitmapRect.top = 0;
      mBitmapRect.left = 0;
      mBitmapRect.bottom = mViewRect.bottom - mViewRect.top;
      mBitmapRect.right = mViewRect.right - mViewRect.left;
      mPaint = new Paint();
      mPaint.setStyle( Style.FILL );
      mPaint.setAntiAlias( true );
      mPaint.setColor( color );
      mPaint.setStrokeWidth( mSize );
      if ( pen == CConstant.PENERASER )
        mPaint.setColor( mBackgroundColor );
      if ( pen == CConstant.PENHIGHLIGHTER )
        mPaint.setAlpha( 12 );
      if ( pen == CConstant.PENWATERCOLOR )
        mPaint.setAlpha( 3 );
    }

    void AddPoint( KinPoint p ) {
      KinPoint np = new KinPoint( p );
      np.x -= mViewRect.left;
      np.y -= mViewRect.top;
      mPath.add( np );
    }

    void Draw( Canvas canvas ) {
      Draw( canvas, mViewRect );
    }

    void Draw( Canvas canvas, Rect drawRect ) {
      if ( mBitmap == null )
        mBitmap = Bitmap.createBitmap( mBitmapRect.right, mBitmapRect.bottom, Bitmap.Config.ARGB_8888 );
      for ( int i = mLastDrawPointIndex; i < mPath.size(); i += 1 ) {
        if ( i == 0 )
          DrawPoint( mPath.get( i ) );
        else
          DrawLine( mPath.get( i - 1 ), mPath.get( i ) );
        mLastDrawPointIndex = i;
      }

      canvas.drawBitmap( mBitmap, mBitmapRect, drawRect, null );

    }

    void DrawPoint( KinPoint p1 ) {
      Canvas newCanvas = new Canvas( mBitmap );
      newCanvas.drawCircle( (float) p1.x, (float) p1.y, mSize / 2, mPaint );
    }

    void DrawLine( KinPoint p1, KinPoint p2 ) {
      Canvas newCanvas = new Canvas( mBitmap );
      if ( Math.abs( p1.x - p2.x ) > Math.abs( p1.y - p2.y ) ) {
        if ( p1.x > p2.x ) {
          KinPoint temp = p1;
          p1 = p2;
          p2 = temp;
        }
        for ( int i = (int) p1.x; i < p2.x; i += 1 ) {
          float myY = (float) ( ( i - p1.x ) / ( p2.x - p1.x ) * ( p2.y - p1.y ) + p1.y );
          newCanvas.drawCircle( i, myY, mSize / 2, mPaint );
        }
      } else {
        if ( p1.y > p2.y ) {
          KinPoint temp = p1;
          p1 = p2;
          p2 = temp;
        }
        for ( int i = (int) p1.y; i < p2.y; i += 1 ) {
          float myX = (float) ( ( i - p1.y ) / ( p2.y - p1.y ) * ( p2.x - p1.x ) + p1.x );
          newCanvas.drawCircle( myX, i, mSize / 2, mPaint );
        }
      }
    }
  }

  // TODO MiniMap
  Bitmap mBitmap;
  Canvas mCanvas;
  Rect WindowRect; // 螢幕上顯示的範圍
  Rect ViewRect; // 畫布要轉畫到螢幕的範圍
  KinPoint lastTouchPoint = null;
  int mBackgroundColor;
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
    mBackgroundColor = color;

  }

  @Override
  public void Draw( Canvas canvas ) {
    mHasUpdate = false;
    while ( !mActions.isEmpty() ) {
      Action exe = mActions.remove( 0 );
      exe.Draw( mCanvas );
    }
    canvas.drawBitmap( mBitmap, ViewRect, WindowRect, null );
    if ( newAction != null )
      newAction.Draw( canvas, WindowRect );
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
      newAction = new Action( 0, ViewRect, mUISelectPen.GetPen(), mUISelectColor.GetColor(), mUISelectPen.GetSize() );
      newAction.AddPoint( touchPoint );
    }
    if ( event.getAction() == MotionEvent.ACTION_MOVE ) {
      newAction.AddPoint( touchPoint );
    }
    if ( event.getAction() == MotionEvent.ACTION_UP ) {
      newAction.AddPoint( touchPoint );
      mActions.add( newAction );
      newAction = null;
    }
    return true;
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
