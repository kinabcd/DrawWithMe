package tw.cycuice.drawwithme.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import tw.cycuice.drawwithme.CConstant;
import tw.kin.android.KinView;

public class CSelectSize extends KinView {
  Paint mMaxCPaint;
  Rect mC;
  Paint mCPaint;
  Paint mCPaintStroke;
  int mSeekX;
  int mSeekY;

  public CSelectSize() {
    mMaxCPaint = new Paint();
    mMaxCPaint.setColor( Color.WHITE );
    mMaxCPaint.setStyle( Style.FILL );
    mCPaint = new Paint();
    mCPaint.setStyle( Style.FILL );
    mCPaintStroke = new Paint();
    mCPaintStroke.setColor( Color.BLACK );
    mCPaintStroke.setStyle( Style.STROKE );
    mC = new Rect();
  }

  public void SetSeekValueX( int x ) {
    mSeekX = x;
  }

  public void SetSeekValueY( int y ) {
    mSeekY = y;
  }

  public void SetColor( int color ) {
    mCPaint.setColor( color );
  }

  @Override
  public void Draw( Canvas canvas ) {
    canvas.drawRect( mViewPos, mMaxCPaint );
    mC.left = GetX();
    mC.bottom = GetY() + GetHeight();
    mC.right = mC.left + ( mSeekX * GetWidth() / CConstant.MaxWidth );
    mC.top = mC.bottom - ( mSeekY * GetHeight() / CConstant.MaxHeight );
    canvas.drawRect( mC, mCPaint );
    canvas.drawRect( mC, mCPaintStroke );

  }
}
