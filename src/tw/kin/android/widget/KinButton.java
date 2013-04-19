package tw.kin.android.widget;

import tw.kin.android.KinPoint;
import android.view.MotionEvent;

public class KinButton extends KinImage {
  KinImage mImage;
  Runnable mClickCallback;
  Runnable mDownCallback;

  public KinButton() {
    super();
  }

  public KinButton(KinImage image) {
    super( image );
  }

  public void SetOnUpRun( Runnable run ) {
    mClickCallback = run;
  }

  public void SetOnDownRun( Runnable run ) {
    mDownCallback = run;
  }

  public boolean onTouchEvent( MotionEvent event ) {
    if ( !mVisible )
      return false;
    if ( super.onTouchEvent( event ) )
      return true;
    if ( !( new KinPoint( event.getX(), event.getY() ) ).In( GetViewRect() ) )
      return false;
    if ( event.getAction() == MotionEvent.ACTION_UP ) {
      if ( mClickCallback != null )
        mClickCallback.run();
    }

    if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
      if ( mDownCallback != null )
        mDownCallback.run();
    }
    RequireRedraw();
    return true;
  }

}
