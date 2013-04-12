package tw.kin.android.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import tw.kin.android.KinView;

public class KinLable extends KinView {
  String mText;
  int mSize;
  Paint mPaint;
  static final int FILLHEIGHT = -1;
  static final int FILLWIDTH = -2;

  public KinLable(KinView old) {
    super( old );
  }

  public KinLable() {
    mPaint = new Paint( Paint.ANTI_ALIAS_FLAG );
    mPaint.setColor( Color.BLACK );
    mSize = FILLHEIGHT;
    mText = "";
  }

  public void SetText( String text ) {
    mText = text;
    RequireRedraw();
  }

  public void SetTextSize( int size ) {
    mSize = size;
    RequireRedraw();
  }

  public void SetTextColor( int color ) {
    mPaint.setColor( color );
    RequireRedraw();
  }

  public String GetText() {
    return mText;
  }

  public void SetTypeface( Typeface typeface ) {
    mPaint.setTypeface( typeface );
  }

  public void SetBold( boolean b ) {
    mPaint.setFakeBoldText( b );
  }

  public void Draw( Canvas canvas ) {
    if ( !mVisible ) return;
    super.Draw( canvas );
    if ( mSize == FILLHEIGHT ) mPaint.setTextSize( GetHeight()*2/3 );
    FontMetrics fontMetrics = mPaint.getFontMetrics();
    int textHeight = (int) ( fontMetrics.bottom - fontMetrics.top ) / 2;
    int textX = GetViewRect().left;
    int textY = GetViewRect().bottom - ( GetHeight() - textHeight ) / 2;
    canvas.drawText( mText, textX, textY, mPaint );

  }
}
