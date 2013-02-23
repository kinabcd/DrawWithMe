package tw.cycuice.drawwithme.protocal;

import java.util.LinkedList;
import java.util.List;

import tw.cycuice.drawwithme.CConstant;
import tw.kin.android.KinPoint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class Action {
  int mOpre;
  int mPen;
  int mColor;
  int mSize;
  KinPoint mStartPoint;
  List<KinPoint> mPath;
  int mLastDrawPointIndex;
  Bitmap mTempBitmap;
  Bitmap mTempBitmapNeon;
  boolean mIsCompleted;

  public Action(int op, Rect view, int pen, int penColor, int penSize) {
    mOpre = op;
    mPen = pen;
    mColor = penColor;
    mSize = penSize;
    mPath = new LinkedList<KinPoint>();
    mStartPoint = new KinPoint( view.left, view.top );
    mLastDrawPointIndex = 0;
    Paint paint = new Paint();
    paint.setStyle( Style.FILL );
    paint.setAntiAlias( false );
    paint.setColor( penColor );
    if ( pen == CConstant.PENHIGHLIGHTER )
      paint.setAlpha( 12 );
    if ( pen == CConstant.PENWATERCOLOR )
      paint.setAlpha( 3 );
    if ( mPen == CConstant.PENNEON ) {
      paint.setAlpha( 6 );
      mTempBitmap = Bitmap.createBitmap( penSize * 3, penSize * 3, Bitmap.Config.ARGB_8888 );
      Canvas tempCanvas = new Canvas( mTempBitmap );
      // float iSizeRate = (float)(-0.05*mSize+3.15) ; // by black
      tempCanvas.drawCircle( mSize * 3 / 2, mSize * 3 / 2, mSize * 3 / 2, paint ); 
      Paint paintNeon = new Paint();
      paintNeon.setStyle( Style.FILL );
      paintNeon.setColor( Color.WHITE );
      mTempBitmapNeon = Bitmap.createBitmap( penSize, penSize, Bitmap.Config.ARGB_8888 );
      Canvas tempCanvasNeon = new Canvas( mTempBitmapNeon );
      tempCanvasNeon.drawCircle( mSize / 2f, mSize / 2f, mSize / 2f, paintNeon );
    } else {
      mTempBitmap = Bitmap.createBitmap( penSize, penSize, Bitmap.Config.ARGB_8888 );
      mTempBitmap.eraseColor( Color.TRANSPARENT );
      Canvas tempCanvas = new Canvas( mTempBitmap );
      tempCanvas.drawCircle( mSize / 2f, mSize / 2f, mSize / 2f, paint );
    }
  }

  public int GetPen() {
    return mPen;
  }

  public int GetColor() {
    return mColor;
  }

  public int GetSize() {
    return mSize;
  }

  public void AddPoint( KinPoint p ) {
    KinPoint np = new KinPoint( p );
    np.x -= mStartPoint.x;
    np.y -= mStartPoint.y;
    mPath.add( np );
  }

  public void Draw( Canvas canvas ) {
    int last = mLastDrawPointIndex;

    while ( mLastDrawPointIndex < mPath.size() ) {
      if ( mLastDrawPointIndex == 0 )
        DrawPoint( canvas, mStartPoint, mPath.get( mLastDrawPointIndex ) );
      else
        DrawLine( canvas, mStartPoint, mPath.get( mLastDrawPointIndex - 1 ), mPath.get( mLastDrawPointIndex ) );
      mLastDrawPointIndex += 1;
    }
    if ( mPen == CConstant.PENNEON ) {
      int neonStart = last - 1;
      if ( neonStart < 0 )
        neonStart = 0;
      for ( double length = 0; neonStart > 1 && length < mSize; neonStart -= 1 ) {
        KinPoint p1 = mPath.get( neonStart );
        KinPoint p2 = mPath.get( neonStart - 1 );
        length += Math.sqrt( Math.pow( p1.x - p2.x, 2 ) + Math.pow( p1.y - p2.y, 2 ) );
      }
      Bitmap sw = mTempBitmap;
      mTempBitmap = mTempBitmapNeon;
      for ( int i = neonStart; i < mPath.size(); i += 1 ) {
        if ( i == 0 )
          DrawPoint( canvas, mStartPoint, mPath.get( i ) );
        else
          DrawLine( canvas, mStartPoint, mPath.get( i - 1 ), mPath.get( i ) );
      }
      mTempBitmap = sw;
    }
  }

  void DrawPoint( Canvas canvas, KinPoint startpoint, KinPoint p1 ) {
    float x = (float) ( startpoint.x + p1.x );
    float y = (float) ( startpoint.y + p1.y );
    canvas.drawBitmap( mTempBitmap, x - mTempBitmap.getWidth() / 2f, y - mTempBitmap.getHeight() / 2f, null );
  }

  void DrawLine( Canvas canvas, KinPoint startpoint, KinPoint p1, KinPoint p2 ) {
    float x1 = (float) ( p1.x );
    float y1 = (float) ( p1.y );
    float x2 = (float) ( p2.x );
    float y2 = (float) ( p2.y );
    boolean modX = Math.abs( x1 - x2 ) > Math.abs( y1 - y2 );
    boolean inc = true;
    KinPoint drawPoint = new KinPoint();
    if ( modX ) {
      inc = x2 > x1;
      for ( int i = (int) x1; inc && i < x2 || !inc && i > x2; i += inc ? 1 : -1 ) {
        float myY = (float) ( ( i - x1 ) / ( x2 - x1 ) * ( y2 - y1 ) + y1 );
        drawPoint.x = i;
        drawPoint.y = myY;
        DrawPoint( canvas, startpoint, drawPoint );
      }
    } else {
      inc = y2 > y1;
      for ( int i = (int) y1; inc && i < y2 || !inc && i > y2; i += inc ? 1 : -1 ) {
        float myX = (float) ( ( i - y1 ) / ( y2 - y1 ) * ( x2 - x1 ) + x1 );
        drawPoint.x = myX;
        drawPoint.y = i;
        DrawPoint( canvas, startpoint, drawPoint );
      }

    }
  }
}
