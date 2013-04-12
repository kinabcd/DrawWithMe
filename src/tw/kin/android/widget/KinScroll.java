package tw.kin.android.widget;

import tw.kin.android.KinLayout;
import tw.kin.android.KinPoint;
import tw.kin.android.KinView;
import tw.kin.android.layout.KinLinearLayout;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class KinScroll extends KinView {
  private int mScroll;
  private int mScrollOld;
  protected KinLayout mScrollLayout;
  private boolean mIsTouchDown;
  KinPoint mDownPos;

  public KinScroll() {
    mIsTouchDown = false;
    mScrollLayout = new KinLinearLayout();
  }

  public KinLayout GetLayout() {
    return mScrollLayout;
  }

  public void SetScroll( int scroll ) {
    mScroll = scroll;
    int maxScroll = mScrollLayout.GetChildHeight() - GetHeight();
    if ( mScroll > maxScroll ) mScroll = maxScroll;
    if ( mScroll < 0 ) mScroll = 0;
    mScrollLayout.SetPos( GetX(), GetY() - mScroll, GetX() + GetWidth(), GetY() + GetHeight() - mScroll );
    RequireRedraw();
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( !mVisible ) return false;
    KinPoint tp = new KinPoint( event.getX(), event.getY() );
    if ( !mIsTouchDown && !tp.In( GetViewRect() ) ) return false;
    if ( event.getAction() == MotionEvent.ACTION_MOVE ) {
      if ( Math.abs( tp.y - mDownPos.y ) > 10 ) mIsTouchDown = true;
      SetScroll( mScrollOld - (int) ( tp.y - mDownPos.y ) );
      return true;
    }
    if ( !mIsTouchDown ) {
      mScrollLayout.onTouchEvent( event );
    }
    if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
      mDownPos = tp;
      mScrollOld = mScroll;
      return true;
    }

    if ( event.getAction() == MotionEvent.ACTION_UP ) {
      SetScroll( mScrollOld - (int) ( tp.y - mDownPos.y ) );
      mIsTouchDown = false;
      return true;
    }
    if ( event.getAction() == MotionEvent.ACTION_CANCEL ) {
      SetScroll( mScrollOld );
      mIsTouchDown = false;
      return true;
    }
    return false;
  }

  @Override
  public void Draw( Canvas canvas ) {
    if ( !mVisible ) return;
    super.Draw( canvas );
    canvas.save();
    canvas.clipRect( GetViewRect() );
    mScrollLayout.Draw( canvas );
    canvas.restore();
  }
}
