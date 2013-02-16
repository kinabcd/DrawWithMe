package tw.cycuice.drawwithme.protocal;

import java.util.LinkedList;
import java.util.List;

import tw.cycuice.drawwithme.CConstant;
import tw.kin.android.KinPoint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class Action {
  int mOpre;
  int mPen;
  int mColor;
  int mSize;
  KinPoint mStartPoint;
  List<KinPoint> mPath;
  Paint mPaint;
  int mLastDrawPointIndex;

  public Action(int op, Rect view, int pen, int penColor, int penSize) {
    mOpre = op;
    mPen = pen;
    mColor = penColor;
    mSize = penSize;
    mPath = new LinkedList<KinPoint>();
    mStartPoint = new KinPoint( view.left, view.top );
    mLastDrawPointIndex = 0;
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
    np.x -= mStartPoint.x;
    np.y -= mStartPoint.y;
    mPath.add( np );
  }

  public void Draw( Canvas canvas ) {
    for ( int i = 0; i < mPath.size(); i += 1 ) {
      if ( i == 0 )
        DrawPoint( canvas, mStartPoint, mPath.get( i ) );
      else
        DrawLine( canvas, mStartPoint, mPath.get( i - 1 ), mPath.get( i ) );
    }
  }

  public void Preview( Canvas canvas, double scaleRete ) {
    KinPoint sp = new KinPoint( 0, 0 );
    canvas.save();
    canvas.scale( (float) scaleRete, (float) scaleRete );
    for ( int i = mLastDrawPointIndex; i < mPath.size(); i += 1 ) {
      if ( i == 0 )
        DrawPoint( canvas, sp, mPath.get( i ) );
      else
        DrawLine( canvas, sp, mPath.get( i - 1 ), mPath.get( i ) );
      mLastDrawPointIndex = i;
    }
    canvas.restore();
  }

  void DrawPoint( Canvas canvas, KinPoint startpoint, KinPoint p1 ) {
    float x = (float) ( startpoint.x + p1.x );
    float y = (float) ( startpoint.y + p1.y );
    canvas.drawCircle( x, y, mSize / 2f, mPaint );
  }

  void DrawLine( Canvas canvas, KinPoint startpoint, KinPoint p1, KinPoint p2 ) {
    float x1 = (float) ( startpoint.x + p1.x );
    float y1 = (float) ( startpoint.y + p1.y );
    float x2 = (float) ( startpoint.x + p2.x );
    float y2 = (float) ( startpoint.y + p2.y );
    if ( Math.abs( x1 - x2 ) > Math.abs( y1 - y2 ) ) {
      if ( x1 > x2 ) {
        float xtemp = x1;
        float ytemp = y1;
        x1 = x2;
        y1 = y2;
        x2 = xtemp;
        y2 = ytemp;
      }
      for ( int i = (int) x1; i < x2; i += 1 ) {
        float myY = (float) ( ( i - x1 ) / ( x2 - x1 ) * ( y2 - y1 ) + y1 );
        canvas.drawCircle( i, myY, mSize / 2f, mPaint );
      }
    } else {
      if ( y1 > y2 ) {
        float xtemp = x1;
        float ytemp = y1;
        x1 = x2;
        y1 = y2;
        x2 = xtemp;
        y2 = ytemp;
      }
      for ( int i = (int) y1; i < y2; i += 1 ) {
        float myX = (float) ( ( i - y1 ) / ( y2 - y1 ) * ( x2 - x1 ) + x1 );
        canvas.drawCircle( myX, i, mSize / 2f, mPaint );
      }
    }
  }
}
