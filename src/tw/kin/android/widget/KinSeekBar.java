package tw.kin.android.widget;

import tw.kin.android.KinPoint;
import tw.kin.android.KinView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

public class KinSeekBar extends KinView {
  int mMinValue;
  int mMaxValue;
  int mSeekValue;
  BarStyle mStyle;
  boolean mVertical;
  boolean mReverse;
  Paint mPaint;
  boolean mIsTouchDown;
  Runnable mChangeCallback;

  public enum BarStyle {
    NORMAL, VOLUME
  };

  public KinSeekBar() {
    super();
    mPaint = new Paint();
    mPaint.setColor( Color.BLACK );
    mPaint.setStrokeWidth( 3 );
    mPaint.setStyle( Style.STROKE );
    mIsTouchDown = false;
    mReverse = false;
    mStyle = BarStyle.NORMAL;
  }

  public void SetReverse( boolean value ) {
    mReverse = value;

  }

  public void SetVertical( boolean value ) {
    mVertical = value;
  }

  public void SetMaxValue( int value ) {
    RequireRedraw();
    mMaxValue = value;
  }

  public int GetMaxValue() {
    return mMaxValue;
  }

  public void SetMinValue( int value ) {
    RequireRedraw();
    mMinValue = value;
  }

  public int GetMinValue() {
    return mMinValue;
  }

  public void SetSeekValue( int value ) {
    if ( mReverse ) value = mMaxValue - value;
    RequireRedraw();
    mSeekValue = value;
    if ( mSeekValue < mMinValue ) mSeekValue = mMinValue;
    if ( mSeekValue > mMaxValue ) mSeekValue = mMaxValue;
    if ( mChangeCallback != null ) mChangeCallback.run();

  }

  public int GetSeekValue() {
    if ( mReverse ) return mMaxValue - mSeekValue;
    return mSeekValue;
  }

  public void SetStyle( BarStyle style ) {
    mStyle = style;
    RequireRedraw();
  }

  public void SetOnSeekChange( Runnable run ) {
    mChangeCallback = run;

  }

  public boolean onTouchEvent( MotionEvent event ) {
    if ( !mVisible ) return false;
    if ( super.onTouchEvent( event ) ) return true;
    if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
      if ( !( new KinPoint( event.getX(), event.getY() ) ).In( GetViewRect() ) ) return false;
      mIsTouchDown = true;
    }
    if ( mIsTouchDown ) {
      double seekRate = 1;
      if ( mVertical ) {
        seekRate = (double) ( event.getY() - GetViewRect().top ) / (double) ( GetHeight() );
      } else {
        seekRate = (double) ( event.getX() - GetViewRect().left ) / (double) ( GetWidth() );
      }

      int valueCount = mMaxValue - mMinValue;
      int value = (int) ( mMinValue + ( valueCount * seekRate ) );
      if ( mReverse ) value = mMaxValue - value;
      SetSeekValue( value );
      RequireRedraw();

      if ( event.getAction() == MotionEvent.ACTION_UP ) mIsTouchDown = false;
      return true;

    }

    return false;
  }

  public void Draw( Canvas canvas ) {
    if ( !mVisible ) return;
    super.Draw( canvas );
    KinPoint SeekPoint;
    int valueCount = mMaxValue - mMinValue;
    int seekCount = mSeekValue - mMinValue;
    if ( mVertical ) {
      int seeky = GetHeight() * seekCount / valueCount + GetViewRect().top;
      SeekPoint = new KinPoint( GetViewRect().left + GetWidth() / 2, seeky );
    } else {
      int seekx = GetWidth() * seekCount / valueCount + GetViewRect().left;
      SeekPoint = new KinPoint( seekx, GetViewRect().top + GetHeight() / 2 );
    }
    if ( mStyle == BarStyle.NORMAL ) {
      KinPoint startPoint;
      KinPoint endPoint;

      if ( mVertical ) {
        startPoint = new KinPoint( GetViewRect().left + GetWidth() / 2, GetViewRect().top );
        endPoint = new KinPoint( GetViewRect().left + GetWidth() / 2, GetViewRect().bottom );
      } else {
        startPoint = new KinPoint( GetViewRect().left, ( GetViewRect().bottom + GetViewRect().top ) / 2 );
        endPoint = new KinPoint( GetViewRect().right, ( GetViewRect().bottom + GetViewRect().top ) / 2 );
      }
      canvas.drawLine( (int) startPoint.x, (int) startPoint.y, (int) endPoint.x, (int) endPoint.y, mPaint );
      canvas.drawCircle( (float) SeekPoint.x, (float) SeekPoint.y, 10, mPaint );
    } else if ( mStyle == BarStyle.VOLUME ) {

      Paint paintSTROKE = new Paint();
      paintSTROKE.setColor( Color.WHITE );
      paintSTROKE.setStyle( Style.STROKE );
      Paint paintGRAY = new Paint();
      paintGRAY.setARGB( 0xff, 230, 255, 230 );
      paintGRAY.setStyle( Style.FILL );
      Path triangle = new Path();
      triangle.moveTo( GetViewRect().left, GetViewRect().bottom );
      triangle.lineTo( GetViewRect().right, GetViewRect().bottom );
      triangle.lineTo( GetViewRect().right, GetViewRect().top );
      triangle.close();
      Path triangleTouch = new Path();
      int sizeWidth = ( GetViewRect().right - GetViewRect().left ) * seekCount / valueCount;
      int sizeHeight = ( GetViewRect().bottom - GetViewRect().top ) * seekCount / valueCount;
      triangleTouch.moveTo( GetViewRect().left, GetViewRect().bottom );
      triangleTouch.lineTo( GetViewRect().left + sizeWidth, GetViewRect().bottom );
      triangleTouch.lineTo( GetViewRect().left + sizeWidth, GetViewRect().bottom - sizeHeight );
      canvas.drawPath( triangleTouch, paintGRAY );
      canvas.drawPath( triangle, paintSTROKE );
    }
  }
}
