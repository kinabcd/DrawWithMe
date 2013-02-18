package tw.cycuice.drawwithme.widget;

import tw.cycuice.drawwithme.R;
import tw.kin.android.KinImage;
import tw.kin.android.KinView;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;

public class CSelectColor extends KinView {
  boolean visible;
  KinImage mShadow;
  KinImage mSelectors[];
  int mBaseColor[];
  int mColor;
  int mCenterX;
  int mCenterY;
  int mOutR;
  int mInR;

  public boolean IsVisible() {
    return visible;
  }

  public void Hide() {
    visible = false;
    mHasUpdate = true;
  }

  public void Show() {
    visible = true;
    mHasUpdate = true;
  }

  @Override
  public void Draw( Canvas canvas ) {
    mHasUpdate = false;
    if ( !visible )
      return;
    for ( int i = 0; i < 8; i += 1 ) {
      canvas.save();
      canvas.translate( 3, 3 );
      canvas.rotate( 45 * i, mCenterX, mCenterY );
      mShadow.Draw( canvas, mCenterX - mShadow.GetWidth() / 2, mCenterY - mOutR );
      canvas.restore();
    }
    for ( int i = 0; i < 8; i += 1 ) {
      mSelectors[i].Draw( canvas, mCenterX - mSelectors[i].GetWidth() / 2, mCenterY - mOutR );
      canvas.rotate( 45, mCenterX, mCenterY );
    }
  }

  public void LoadContent() {
    mBaseColor = new int[8];
    mSelectors = new KinImage[8];
    mShadow = new KinImage();
    mShadow.AddImage( R.drawable.selectcolor_fan_shadow, -1 );
    for ( int i = 0; i < 8; i += 1 ) {
      mSelectors[i] = new KinImage();
      mSelectors[i].AddImage( R.drawable.selectcolor_fan, -1 );
    }
    SetSelectorColor( 0, 0xffff0000 );
    SetSelectorColor( 1, 0xffffff00 );
    SetSelectorColor( 2, 0xffffffff );
    SetSelectorColor( 3, 0xff00ff00 );
    SetSelectorColor( 4, 0xff00ffff );
    SetSelectorColor( 5, 0xff0000ff );
    SetSelectorColor( 6, 0xff000000 );
    SetSelectorColor( 7, 0xffff00ff );
  }

  public int GetColor() {
    return mColor | 0xff000000;
  }

  public void SetColor( int color ) {
    mColor = color | 0xff000000;
  }

  void SetSelectorColor( int index, int color ) {
    mBaseColor[index] = color;
    mSelectors[index].SetColorFilter( ColorFilter( mBaseColor[index] ) );
  }

  ColorMatrixColorFilter ColorFilter( int color ) {
    int r = ( color & 0x00ff0000 ) >> 16;
    int g = ( color & 0x0000ff00 ) >> 8;
    int b = ( color & 0x000000ff );
    return RGBFilter( r, g, b );

  }

  ColorMatrixColorFilter RGBFilter( int r, int g, int b ) {
    ColorMatrix cm = new ColorMatrix();
    cm.setScale( r / 255f, g / 255f, b / 255f, 1 );
    return new ColorMatrixColorFilter( cm );

  }

  public void CompatibleWith( double windowWidth, double windowHeight ) {
    mHasUpdate = true;
    int rWidth = (int) ( windowWidth / 3 );
    int rHeight = (int) ( rWidth / 250.0 * 150.0 );
    for ( int i = 0; i < 8; i += 1 )
      mSelectors[i].SetSize( rWidth, rHeight );
    mShadow.SetSize( rWidth, rHeight );
    mCenterX = (int) windowWidth / 2;
    mCenterY = (int) windowHeight / 2;
    mOutR = mCenterX;
    mInR = mOutR - rHeight;
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( !visible )
      return false;
    float x = event.getX();
    float y = event.getY();
    double d = Math.sqrt( ( x - mCenterX ) * ( x - mCenterX ) + ( y - mCenterY ) * ( y - mCenterY ) );
    double radian = Math.atan2( mCenterY - y, mCenterX - x ) + Math.PI;
    int area = ( (int) ( ( Math.toDegrees( radian ) + 22.5 ) / 45 ) + 2 ) % 8;
    if ( d < mOutR && d > mInR )
      mColor = mBaseColor[area];

    if ( event.getAction() == MotionEvent.ACTION_UP ) {
      Hide();
      return true;
    }

    return true;
  }
}
