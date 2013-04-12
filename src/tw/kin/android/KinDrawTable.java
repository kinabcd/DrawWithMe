package tw.kin.android;

import tw.kin.android.connection.KinInputConnection;
import tw.kin.android.layout.KinAbsoluteLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class KinDrawTable extends SurfaceView implements SurfaceHolder.Callback {
  int mPageStatus;
  int ViewWidth;
  int ViewHeight;
  SurfaceHolder msfh;
  KinLayout mView;
  KinRepeat mDrawThread;

  public KinDrawTable(Context context) {
    super( context );
    msfh = this.getHolder();
    msfh.addCallback( this );
    this.setFocusable( true );
    this.setFocusableInTouchMode( true );
    mView = new KinAbsoluteLayout();

  }

  public void AddChild( KinView newChild ) {
    mView.AddChild( newChild );
  }

  public void RemoveChild( KinView newChild ) {
    mView.RemoveChild( newChild );
  }

  public void CleanChild() {
    mView.CleanChild();
  }

  @Override
  public void surfaceCreated( SurfaceHolder holder ) {
    mDrawThread = new KinRepeat( mDrawMethod, "Draw" );
    mDrawThread.start();
  }

  @Override
  public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
    mView.SetSize( width, height );

  }

  @Override
  public void surfaceDestroyed( SurfaceHolder holder ) {
    mDrawThread.Finish();
    mDrawThread = null;
  }

  @Override
  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( mView.onKeyDown( keycode, event ) ) return true;
    return super.onKeyDown( keycode, event );
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( mView.onTouchEvent( event ) ) return true;
    return super.onTouchEvent( event );
  }

  @Override
  public InputConnection onCreateInputConnection( EditorInfo outAttrs ) {
    return new KinInputConnection( this, false );// super.onCreateInputConnection(outAttrs);
  }

  public Canvas LockCanvas( Rect rect ) {
    return msfh.lockCanvas( rect );
  }

  public void UnlockCanvas( Canvas canvas ) {
    msfh.unlockCanvasAndPost( canvas );
  }

  public void RequireRedraw() {
    mView.RequireRedraw();
  }

  Runnable mDrawMethod = new Runnable() {
    @Override
    public void run() {
      if ( mView.HasUpdate() ) {
        Canvas canvas = LockCanvas( null );
        if ( canvas != null ) { // canvas可能因為surfaceView未準備好而為null
          mView.Draw( canvas );
          UnlockCanvas( canvas );// 更新螢幕顯示
        }
      }
      try {
        Thread.sleep( 33 );
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  };
}
