package tw.ome.drawwithme.widget;

import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.ome.drawwithme.DrawSurface;
import tw.ome.drawwithme.Main;
import tw.ome.drawwithme.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

public class CSelectColor extends KinView {
  KinImage mShadow;
  KinImage mSelectors[];
  int mBaseColor[];
  int mColor;
  int mCenterX;
  int mCenterY;
  int mOutR;
  int mInR;
  KinButton mBAdvColor;
  long mTouchDownTime ;

  public void Hide() {
    mVisible = false;
    RequireRedraw();
  }

  public void Show() {
    mVisible = true;
    SharedPreferences mSp = Main.sInstance.getSharedPreferences( "Options", android.content.Context.MODE_PRIVATE );
    if ( mSp.getString( "FirstUse", "false" ).equals( "false" ) ) { // 第一次開選色
      Editor editor = mSp.edit();
      editor.putString( "FirstUse", "True" );
      editor.commit();
      Toast.makeText( DrawSurface.GetInstance().getContext(), "Long press to edit the fan color", Toast.LENGTH_LONG ).show();
    }
    RequireRedraw();
  }

  @Override
  public void Draw( Canvas canvas ) {
    super.Draw( canvas );
    if ( !IsVisible() )
      return;
    for ( int i = 0; i < 8; i += 1 ) {
      canvas.save();
      canvas.translate( 3, 3 );
      canvas.rotate( 45 * i, mCenterX, mCenterY );
      mShadow.Draw( canvas );
      canvas.restore();
    }
    for ( int i = 0; i < 8; i += 1 ) {
      mSelectors[i].Draw( canvas );
      canvas.rotate( 45, mCenterX, mCenterY );
    }
    mBAdvColor.Draw( canvas );
  }

  public CSelectColor() {
    mBaseColor = new int[8];
    mSelectors = new KinImage[8];
    mShadow = new KinImage();
    mShadow.AddImage( Main.lib.GetBitmap( R.drawable.selectcolor_fan_shadow ), -1 );
    for ( int i = 0; i < 8; i += 1 ) {
      mSelectors[i] = new KinImage();
      mSelectors[i].AddImage( Main.lib.GetBitmap( R.drawable.selectcolor_fan ), -1 );
    }
    SetSelectorColor( 0, 0xffff0000 );
    SetSelectorColor( 1, 0xffffff00 );
    SetSelectorColor( 2, 0xffffffff );
    SetSelectorColor( 3, 0xff00ff00 );
    SetSelectorColor( 4, 0xff00ffff );
    SetSelectorColor( 5, 0xff0000ff );
    SetSelectorColor( 6, 0xff000000 );
    SetSelectorColor( 7, 0xffff00ff );

    mBAdvColor = new KinButton();
    mBAdvColor.AddImage( Main.lib.GetBitmap( R.drawable.board_more_colors ), -1 );

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
    int rWidth = (int) ( windowWidth / 3 );
    int rHeight = (int) ( rWidth / 250.0 * 150.0 );
    mCenterX = (int) windowWidth / 2;
    mCenterY = (int) windowHeight / 2;
    mOutR = mCenterX;
    mInR = mOutR - rHeight;
    int rX = mCenterX - rWidth / 2;
    int rY = mCenterY - mOutR;
    for ( int i = 0; i < 8; i += 1 ) {
      mSelectors[i].SetPos( rX, rY, rX + rWidth, rY + rHeight );
    }
    mShadow.SetPos( rX, rY, rX + rWidth, rY + rHeight );

    int sideLength = (int) ( mInR / 1.414 );
    mBAdvColor.SetPos( mCenterX - sideLength, mCenterY - sideLength, mCenterX + sideLength, mCenterY + sideLength );
    RequireRedraw();
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( !IsVisible() )
      return false;
    float x = event.getX();
    float y = event.getY();
    double d = Math.sqrt( ( x - mCenterX ) * ( x - mCenterX ) + ( y - mCenterY ) * ( y - mCenterY ) );
    double radian = Math.atan2( mCenterY - y, mCenterX - x ) + Math.PI;
    int area = ( (int) ( ( Math.toDegrees( radian ) + 22.5 ) / 45 ) + 2 ) % 8;
    if ( d < mOutR && d > mInR ) { // 按到扇形
      if ( event.getAction() == MotionEvent.ACTION_DOWN ) // DOWN時先記錄時間
        mTouchDownTime = event.getEventTime();
      else if (event.getAction() == MotionEvent.ACTION_UP ) { // UP時判斷長or短按
        long pressTime = event.getEventTime() - mTouchDownTime ;
        if ( pressTime >= 500 ) { // long press
          final int myarea = area;
          ColorPickerDialog dialog = new ColorPickerDialog( Main.sInstance, mColor, "Advance Color", new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void colorChanged( int color ) {
              mColor = color;
              SetSelectorColor( myarea, color );
            }
          } );
          dialog.setSize( mCenterX * 2, mCenterY * 2 );
          Hide();
          dialog.show();
        } else { // short press
          mColor = mBaseColor[area];
          Hide();
        }
      }
    } else if ( d < mInR ) {
      ColorPickerDialog dialog = new ColorPickerDialog( Main.sInstance, mColor, "Advance Color", new ColorPickerDialog.OnColorChangedListener() {
        @Override
        public void colorChanged( int color ) {
          mColor = color;
        }
      } );
      dialog.setSize( mCenterX * 2, mCenterY * 2 );
      Hide();
      dialog.show();
    } else
      Hide();

    return true;
  }

  @Override
  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( !IsVisible() )
      return false;
    if ( keycode == KeyEvent.KEYCODE_BACK ) {
      Hide();
      return true;
    }
    return false;
  }

}
