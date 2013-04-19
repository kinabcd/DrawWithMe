package tw.kin.android;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public abstract class KinLayout extends KinView {
  protected ArrayList<KinView> mChild;

  public KinLayout() {
    mChild = new ArrayList<KinView>();
  }

  public void AddChild( KinView newChild ) {
    mChild = new ArrayList<KinView>( mChild );
    mChild.add( newChild );
    newChild.SetParent( this );
    PosUpdate();
  }

  public void RemoveChild( KinView removeChild ) {
    mChild = new ArrayList<KinView>( mChild );
    if ( mChild.remove( removeChild ) ) removeChild.SetParent( null );

  }

  public void CleanChild() {
    for ( KinView child : mChild )
      child.SetParent( null );
    mChild = new ArrayList<KinView>();
  }

  @Override
  public void Draw( Canvas canvas ) {
    if ( !mVisible ) return;
    super.Draw( canvas );
    for ( KinView child : mChild ) {
      child.Draw( canvas );
    }
  }

  public boolean onTouchEvent( MotionEvent event ) {
    if ( !mVisible ) return false;
    if ( mChild.isEmpty() ) return false;
    for ( int i = mChild.size() - 1; i >= 0; i -= 1 ) {
      KinView child = mChild.get( i );
      if ( child.onTouchEvent( event ) ) {
        return true;
      }
    }
    return false;
  }

  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( !mVisible ) return false;
    if ( mChild.isEmpty() ) return false;
    for ( int i = mChild.size() - 1; i >= 0; i -= 1 ) {
      KinView child = mChild.get( i );
      if ( child.onKeyDown( keycode, event ) ) return true;
    }
    return false;
  }

  @Override
  public void PosUpdate() {

  }

  public abstract int GetChildHeight();
}
