package tw.cycuice.drawwithme.protocal;

import java.util.LinkedList;
import java.util.List;

import tw.cycuice.drawwithme.CConstant;
import tw.kin.android.KinPoint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class Action {

  Bitmap mBitmap;
  int mOpre;
  int mPen;
  int mColor;
  int mSize;
  Rect mViewRect;
  List<KinPoint> mPath;
  Rect mBitmapRect;
  Paint mPaint;
  int mLastDrawPointIndex;

  public Action(int op, Rect view, int pen, int penColor, int penSize) {
    mOpre = op;
    mPen = pen;
    mColor = penColor;
    mSize = penSize;
    mPath = new LinkedList<KinPoint>();
    mViewRect = new Rect( view );

    mLastDrawPointIndex = 0;
    mBitmapRect = new Rect();
    mBitmapRect.top = 0;
    mBitmapRect.left = 0;
    mBitmapRect.bottom = mViewRect.bottom - mViewRect.top;
    mBitmapRect.right = mViewRect.right - mViewRect.left;
    mPaint = new Paint();
    mPaint.setStyle( Style.FILL );
    mPaint.setAntiAlias( true );
    mPaint.setColor( penColor );
    mPaint.setStrokeWidth( mSize );
    if ( pen == CConstant.PENHIGHLIGHTER )
      mPaint.setAlpha( 12 );
    if ( pen == CConstant.PENWATERCOLOR )
      mPaint.setAlpha( 3 );
  }

  public void AddPoint( KinPoint p ) {
    KinPoint np = new KinPoint( p );
    np.x -= mViewRect.left;
    np.y -= mViewRect.top;
    mPath.add( np );
  }

  public void Draw( Canvas canvas ) {
    Draw( canvas, mViewRect );
  }

  public void Draw( Canvas canvas, Rect drawRect ) {
    if ( mBitmap == null )
      mBitmap = Bitmap.createBitmap( mBitmapRect.right, mBitmapRect.bottom, Bitmap.Config.ARGB_8888 );
    for ( int i = mLastDrawPointIndex; i < mPath.size(); i += 1 ) {
      if ( i == 0 )
        DrawPoint( mPath.get( i ) );
      else
        DrawLine( mPath.get( i - 1 ), mPath.get( i ) );
      mLastDrawPointIndex = i;
    }
    
    canvas.drawBitmap( mBitmap, mBitmapRect, drawRect, null );

  }

  void DrawPoint( KinPoint p1 ) {
    Canvas newCanvas = new Canvas( mBitmap );
    newCanvas.drawCircle( (float) p1.x, (float) p1.y, mSize / 2f, mPaint );
  }

  void DrawLine( KinPoint p1, KinPoint p2 ) {
    Canvas newCanvas = new Canvas( mBitmap );
    if ( Math.abs( p1.x - p2.x ) > Math.abs( p1.y - p2.y ) ) {
      if ( p1.x > p2.x ) {
        KinPoint temp = p1;
        p1 = p2;
        p2 = temp;
      }
      for ( int i = (int) p1.x; i < p2.x; i += 1 ) {
        float myY = (float) ( ( i - p1.x ) / ( p2.x - p1.x ) * ( p2.y - p1.y ) + p1.y );
        newCanvas.drawCircle( i, myY, mSize / 2f, mPaint );
      }
    } else {
      if ( p1.y > p2.y ) {
        KinPoint temp = p1;
        p1 = p2;
        p2 = temp;
      }
      for ( int i = (int) p1.y; i < p2.y; i += 1 ) {
        float myX = (float) ( ( i - p1.y ) / ( p2.y - p1.y ) * ( p2.x - p1.x ) + p1.x );
        newCanvas.drawCircle( myX, i, mSize / 2f, mPaint );
      }
    }
  }
}
