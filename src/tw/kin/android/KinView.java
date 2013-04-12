package tw.kin.android;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class KinView {
  private KinView mParent;
  private boolean mHasUpdate;
  protected boolean mVisible;
  private Rect mViewPos;
  protected KinPoint mSize;
  protected KinPoint mOffset;
  protected int mBackground;

  public KinView(KinView old) {
    mSize = new KinPoint( old.mSize );
    mOffset = new KinPoint( old.mOffset );
    this.SetPos( new Rect( old.GetViewRect() ) );
    mVisible = true;
    RequireRedraw();
  }

  public KinView() {
    mOffset = new KinPoint( 0, 0 );
    mSize = new KinPoint( -1, -1 );
    this.SetPos( new Rect() );
    mVisible = true;
    mViewPos = new Rect();
    RequireRedraw();
  }

  public KinView GetParent() {
    return mParent;
  }

  public int GetWidth() {
    if ( mSize.x > 0 ) return (int) mSize.x;
    if ( this.GetParent() == null ) return 0;
    if ( mSize.x < 0 ) return (int) ( -mSize.x * GetParent().GetWidth() );
    return 0;
  }

  public int GetHeight() {
    if ( mSize.y > 0 ) return (int) mSize.y;
    if ( this.GetParent() == null ) return 0;
    if ( mSize.y < 0 ) return (int) ( -mSize.y * GetParent().GetHeight() );
    return 0;
  }

  public Rect GetViewRect() {
    mViewPos.right = mViewPos.left + GetWidth();
    mViewPos.bottom = mViewPos.top + GetHeight();
    return mViewPos;
  }

  public void SetVisible( boolean visible ) {
    mVisible = visible;
  }

  public boolean IsVisible() {
    return mVisible;
  }

  public void SetSizePercent( double width, double height ) {
    mSize = new KinPoint( -width, -height );
    this.PosUpdate();
  }

  public void SetSize( double width, double height ) {
    this.SetSize( (int) width, (int) height );
  }

  public void SetSize( int width, int height ) {
    mSize = new KinPoint( width, height );
    this.PosUpdate();
  }

  public void SetPos( Rect viewPos ) {
    this.SetPos( viewPos.left, viewPos.top, viewPos.right, viewPos.bottom );
  }

  public void SetPos( KinPoint pos ) {
    this.SetPos( (int) pos.x, (int) pos.y );
  }

  public void SetPos( int x, int y ) {
    mOffset = new KinPoint( x, y );
    this.PosUpdate();
  }

  public void SetPos( int left, int top, int right, int bottom ) {
    mOffset = new KinPoint( left, top );
    mSize = new KinPoint( right - left, bottom - top );
    mViewPos = new Rect( left, top, right, bottom );
    this.PosUpdate();

  }

  public void PosUpdate() {
    RequireRedraw();
  }

  public int GetX() {
    return (int) mOffset.x;
  }

  public int GetY() {
    return (int) mOffset.y;
  }

  public boolean onTouchEvent( MotionEvent event ) {
    return false;
  }

  public boolean HasUpdate() {
    return mHasUpdate;
  }

  public void SetBackground( int color ) {
    mBackground = color;
  }

  public void Draw( Canvas canvas ) {
    if ( !mVisible ) return;
    if ( mBackground != 0 ) {
      canvas.save();
      canvas.clipRect( mViewPos );
      canvas.drawColor( mBackground );
      canvas.restore();
    }
    mHasUpdate = false;

  }

  public boolean onKeyDown( int keycode, KeyEvent event ) {
    return false;
  }

  public void RequireRedraw() {
    mHasUpdate = true;
    if ( GetParent() != null ) GetParent().RequireRedraw();
  }

  public void SetParent( KinView mParent ) {
    this.mParent = mParent;
  }
}
