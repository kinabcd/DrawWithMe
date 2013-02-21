package tw.cycuice.drawwithme;

import tw.cycuice.drawwithme.ui.CDrawBoard;
import tw.cycuice.drawwithme.ui.CMemberCenter;
import tw.cycuice.drawwithme.ui.CMenu;
import tw.cycuice.drawwithme.ui.CNew;
import tw.cycuice.drawwithme.ui.IUI;
import tw.kin.android.KinView.Alignment;
import tw.kin.android.widget.KinImage;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class DrawSurface extends SurfaceView implements Callback {
  final Handler handler = new Handler();
  int mPageStatus;
  int mWindowWidth;
  int mWindowHeight;
  int ViewWidth;
  int ViewHeight;
  SurfaceHolder sfh;
  IUI mUIs;
  CMemberCenter mUIMemberCenter;
  CDrawBoard mUICanvas;
  CMenu mUIMenu;
  CNew mUINew;
  static DrawSurface sInstance = null;
  Handler mDrawHandler;
  Client mClient;

  public static DrawSurface GetInstance() {
    if ( sInstance == null )
      sInstance = new DrawSurface( Main.sInstance );
    return sInstance;
  }

  public DrawSurface(Context context) {
    super( context );
    sfh = this.getHolder();
    sfh.addCallback( this );
    mPageStatus = CConstant.NOTLOADING;
    HandlerThread drawThread = new HandlerThread( "DrawThread" );
    drawThread.start();
    mDrawHandler = new Handler( drawThread.getLooper() );
  }

  public void SetPage( int i ) {
    if ( mPageStatus == i )
      return;
    IUI oldui = mUIs;
    mPageStatus = i;
    if ( mPageStatus == CConstant.PAGEMENU ) {
      mUIs = mUIMenu;
    } else if ( mPageStatus == CConstant.PAGENEW ) {
      mUIs = mUINew;
    } else if ( mPageStatus == CConstant.PAGECANVAS ) {
      mUIs = mUICanvas;
    } else if ( mPageStatus == CConstant.PAGEMEMBER ) {
      mUIs = mUIMemberCenter;
    }
    if ( oldui != null )
      oldui.onQuit( mUIs );
    mUIs.onStart( oldui );
    UpdateView();
  }

  public void surfaceCreated( SurfaceHolder holder ) {
  }

  public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
    Log.d( "surfaceChanged", "f:" + format + " w:" + width + " h:" + height );
    mWindowWidth = width;
    mWindowHeight = height;
    if ( LoadContent != null )
      LoadContent.start();
    else
      Resize( mWindowWidth, mWindowHeight );
    UpdateView();
  }

  public void surfaceDestroyed( SurfaceHolder holder ) {
  }

  public void onPause() {
  }

  public void onResume() {
    UpdateView();

  }

  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( mUIs.onKeyDown( keycode, event ) )
      return true;
    else {
      if ( keycode == KeyEvent.KEYCODE_BACK ) {
        Main.sInstance.finish();
        return true;
      }
    }

    return super.onKeyDown( keycode, event );
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( mUIs.onTouchEvent( event ) ) {
      UpdateView();
      return true;
    }
    return false;

  }

  Thread LoadContent = new Thread() {
    public void run() {
      Looper.prepare();

      KinImage mBackground = new KinImage();
      mBackground.AddImage( R.drawable.menu_bg, -1 );
      mBackground.SetPos( 0, 0, mWindowWidth, mWindowHeight );

      KinImage mTitle = new KinImage();
      mTitle.AddImage( R.drawable.menu_title, -1 );
      mTitle.SetSize( mWindowWidth * 0.95, mWindowHeight * 0.25 );
      mTitle.SetAlignment( Alignment.ABSOLUTE, Alignment.ABSOLUTE );
      mTitle.SetPos( ( mWindowWidth - mTitle.GetWidth() ) / 2, ( mWindowHeight - mTitle.GetHeight() ) / 2 );
      if ( sfh != null ) {
        Canvas canvas = sfh.lockCanvas( null );
        if ( canvas != null ) {
          mBackground.Draw( canvas );
          mTitle.Draw( canvas );
          sfh.unlockCanvasAndPost( canvas );// 更新螢幕顯示
        }
      }
      mUIMenu = new CMenu();
      mUINew = new CNew();
      mUICanvas = new CDrawBoard();
      mUIMemberCenter = new CMemberCenter();
      mUIMenu.LoadContent();
      mUINew.LoadContent();
      mUICanvas.LoadContent();
      mUIMemberCenter.LoadContent();
      Resize( mWindowWidth, mWindowHeight );
      try {
        Thread.sleep( 500 );
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      SetPage( CConstant.PAGEMENU );
      LoadContent = null; // 讀取後自動銷毀
    }
  };

  public void Resize( double windowWidth, double windowHeight ) {
    mUIMenu.CompatibleWith( windowWidth, windowHeight );
    mUINew.CompatibleWith( windowWidth, windowHeight );
    mUICanvas.CompatibleWith( windowWidth, windowHeight );
    mUIMemberCenter.CompatibleWith( windowWidth, windowHeight );

  }

  public void UpdateView() {
    mDrawHandler.post( new Runnable() {
      public void run() {
        if ( sfh != null && mUIs != null && mUIs.HasUpdate() ) {
          Canvas canvas = sfh.lockCanvas( null );
          if ( canvas != null ) {
            canvas.drawColor( 0xff888888 );
            // canvas可能因為surfaceView未準備好而為null
            mUIs.Draw( canvas );
            sfh.unlockCanvasAndPost( canvas );// 更新螢幕顯示
          }
        }
      }
    } );
  }
}
