package tw.cycuice.drawwithme.ui;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.Main;
import tw.cycuice.drawwithme.R;
import tw.cycuice.drawwithme.widget.CSelectColor;
import tw.kin.android.KinImage;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinSeekBar;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.view.KeyEvent;
import android.widget.Toast;

public class CNew extends KinView implements IUI {
  KinImage mBackground;
  int mDefaultHeight;
  int mDefaultWidth;
  KinButton mBOK;
  KinImage mOK;
  KinButton mBReset;
  KinImage mReset;
  KinButton mBSelectColor;
  KinImage mDefaultSize;
  Rect mMaxC;
  Paint mMaxCPaint;
  Rect mC;
  Paint mCPaint;
  Paint mCPaintStroke;
  Paint mTextPaint;
  KinSeekBar mSizeBarX;
  KinSeekBar mSizeBarY;
  CSelectColor mUISelectColor;

  public CNew() {
  }

  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( R.drawable.menu_bg, -1 );
    mOK = new KinImage();
    mOK.AddImage( R.drawable.ok, -1 );
    mBOK = new KinButton( mOK );
    mBOK.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        if ( mSizeBarX.GetSeekValue() < 1 || mSizeBarY.GetSeekValue() < 1 ) {
          Toast.makeText( Main.sInstance, "Create Fail!!", Toast.LENGTH_SHORT ).show();
          return;
        }
        DrawSurface.GetInstance().SetPage( CConstant.PAGECANVAS );
      }
    } );
    mDefaultSize = new KinImage();
    mDefaultSize.AddImage( R.drawable.new_select_bg, -1 );
    mBSelectColor = new KinButton( mDefaultSize );
    mBSelectColor.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        mUISelectColor.Show();
      }
    } );
    mReset = new KinImage();
    mReset.AddImage( R.drawable.reset, -1 );
    mBReset = new KinButton( mReset );
    mBReset.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        mSizeBarX.SetSeekValue( mDefaultWidth );
        mSizeBarY.SetSeekValue( mDefaultHeight );
        mUISelectColor.SetColor( Color.WHITE );
      }
    } );
    mMaxCPaint = new Paint();
    mMaxCPaint.setColor( Color.WHITE );
    mMaxCPaint.setStyle( Style.FILL );
    mCPaint = new Paint();
    mCPaint.setStyle( Style.FILL );
    mCPaintStroke = new Paint();
    mCPaintStroke.setColor( Color.BLACK );
    mCPaintStroke.setStyle( Style.STROKE );
    mTextPaint = new Paint();
    mTextPaint.setStyle( Style.FILL_AND_STROKE );
    mTextPaint.setTextSize( 20 );
    mTextPaint.setARGB( 0xff, 220, 220, 255 );
    mSizeBarX = new KinSeekBar();
    mSizeBarX.SetMinValue( 0 );
    mSizeBarX.SetMaxValue( CConstant.MaxWidth );
    mSizeBarY = new KinSeekBar();
    mSizeBarY.SetVertical( true );
    mSizeBarY.SetReverse( true );
    mSizeBarY.SetMinValue( 0 );
    mSizeBarY.SetMaxValue( CConstant.MaxHeight );
    mUISelectColor = new CSelectColor();
    mUISelectColor.LoadContent();
    AddChild( mSizeBarX );
    AddChild( mSizeBarY );
    AddChild( mBOK );
    AddChild( mBReset );
    AddChild( mBSelectColor );
    AddChild( mUISelectColor );

  }

  @Override
  public void Draw( Canvas canvas ) {
    int width = mSizeBarX.GetSeekValue();
    int height = mSizeBarY.GetSeekValue();
    mBackground.Draw( canvas, 0, 0 );
    canvas.drawRect( mMaxC, mMaxCPaint );
    mC.right = mC.left + ( width * ( mMaxC.right - mMaxC.left ) / CConstant.MaxWidth );
    mC.top = mC.bottom - ( height * ( mMaxC.bottom - mMaxC.top ) / CConstant.MaxHeight );
    mCPaint.setColor( mUISelectColor.GetColor() );
    canvas.drawRect( mC, mCPaint );
    canvas.drawRect( mC, mCPaintStroke );
    canvas.drawText( height + " x " + width, mMaxC.left, mMaxC.top - 3, mTextPaint );
    super.Draw( canvas );
  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    int bWidth = (int) ( windowWidth * 0.5 );
    int bHeight = (int) ( bWidth / 200.0 * 100.0 );
    mBackground.SetSize( windowWidth, windowHeight ); // 設定背景大小
    mBOK.SetPos( bWidth, (int) windowHeight - bHeight, (int) windowWidth, (int) windowHeight );
    mBReset.SetPos( 0, (int) windowHeight - bHeight, bWidth, (int) windowHeight );
    mBSelectColor.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowWidth * 0.825 ), (int) ( windowWidth * 0.175 ), (int) ( windowWidth * 0.95 ) );
    mMaxC = new Rect( (int) ( windowWidth * 0.175 ), (int) ( windowWidth * 0.05 ), (int) ( windowWidth * 0.95 ), (int) ( windowWidth * 0.825 ) );
    mC = new Rect( (int) ( windowWidth * 0.175 ), 0, 0, (int) ( windowWidth * 0.825 ) );
    mDefaultHeight = (int) windowHeight;
    mDefaultWidth = (int) windowWidth;
    mSizeBarX.SetSeekValue( mDefaultWidth );
    mSizeBarY.SetSeekValue( mDefaultHeight );
    mSizeBarX.SetPos( mMaxC.left, (int) ( windowWidth * 0.825 ), mMaxC.right, (int) ( windowWidth * 0.95 ) );
    mSizeBarY.SetPos( (int) ( windowWidth * 0.05 ), mMaxC.top, (int) ( windowWidth * 0.175 ), mMaxC.bottom );

    mUISelectColor.CompatibleWith( windowWidth, windowHeight );
  }

  @Override
  public void onStart( IUI from ) {
    mUISelectColor.SetColor( Color.WHITE );
    mUISelectColor.Hide();
    mHasUpdate = true;

  }

  @Override
  public void onQuit( IUI to ) {
    if ( to instanceof CDrawBoard ) {
      CDrawBoard canvas = (CDrawBoard) to;
      int width = mSizeBarX.GetSeekValue();
      int height = mSizeBarY.GetSeekValue();
      int color = mUISelectColor.GetColor();
      canvas.mUICanvas.New( width, height, color );
    }

  }

  @Override
  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( keycode == KeyEvent.KEYCODE_BACK ) {
      DrawSurface.GetInstance().SetPage( CConstant.PAGEMENU );
      return true;
    }
    return false;
  }
}
