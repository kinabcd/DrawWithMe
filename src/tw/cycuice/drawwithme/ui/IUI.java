package tw.cycuice.drawwithme.ui;

import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public interface IUI {

  public void onStart( IUI from );

  public void onQuit( IUI to );

  public void Draw( Canvas canvas );

  public void LoadContent();

  public void CompatibleWith( double windowWidth, double windowHeight );

  public boolean onTouchEvent( MotionEvent event );

  public boolean onKeyDown( int keycode, KeyEvent event );

  public boolean HasUpdate();
}
