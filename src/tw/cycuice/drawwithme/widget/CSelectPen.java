package tw.cycuice.drawwithme.widget;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.R;
import tw.kin.android.KinPoint;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinSeekBar;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;

public class CSelectPen extends KinView {
  boolean visible;
  int mSize;
  int mPen;
  KinImage mBackground;
  KinButton mBCancel;
  KinImage mCancel;
  KinImage mPenType[];
  KinButton mB_Normal;
  KinButton mB_Eraser;
  KinButton mB_Highlighter; // 螢光筆
  KinButton mB_Watercolor; // 水彩
  KinButton mB_Neon; // 霓虹
  KinButton mB_Onlock;
  KinSeekBar mSizeBar;
  KinPoint mSizeDemo;

  public CSelectPen() {
    mBackground = new KinImage();
    mCancel = new KinImage();
    mCancel.AddImage( R.drawable.selectpen_cancel, -1 );
    mBCancel = new KinButton( mCancel );
    mBCancel.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        Hide();
      }
    } );

    mPenType = new KinImage[6];
    for ( int i = 0; i < 6; i += 1 )
      mPenType[i] = new KinImage();

    mPenType[0].AddImage( R.drawable.selectpen_normal, -1 );
    mPenType[1].AddImage( R.drawable.selectpen_eraser, -1 );
    mPenType[2].AddImage( R.drawable.selectpen_highlighter, -1 );
    mPenType[3].AddImage( R.drawable.selectpen_watercolor, -1 );
    mPenType[4].AddImage( R.drawable.selectpen_neon, -1 );
    mPenType[5].AddImage( R.drawable.selectpen_lock, -1 );

    mB_Normal = new KinButton( mPenType[0] ); // 一般畫筆
    mB_Normal.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        mPen = CConstant.PENNORMAL;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Eraser = new KinButton( mPenType[1] ); // 橡皮擦
    mB_Eraser.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        mPen = CConstant.PENERASER;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Highlighter = new KinButton( mPenType[2] ); // 螢光筆
    mB_Highlighter.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        mPen = CConstant.PENHIGHLIGHTER;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Watercolor = new KinButton( mPenType[3] ); // 水彩
    mB_Watercolor.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        mPen = CConstant.PENWATERCOLOR;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Neon = new KinButton( mPenType[4] ); // 霓虹
    mB_Neon.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        mPen = CConstant.PENNEON;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Onlock = new KinButton( mPenType[5] ); // 鎖
    mSizeBar = new KinSeekBar();
    mSizeBar.SetMaxValue( CConstant.MaxPenSize );
    mSizeBar.SetMinValue( 1 );
    mSizeBar.SetStyle( KinSeekBar.BarStyle.VOLUME );
    AddChild( mBCancel );
    AddChild( mB_Normal );
    AddChild( mB_Eraser );
    AddChild( mB_Highlighter ); // 螢光筆
    AddChild( mB_Watercolor ); // 水彩
    AddChild( mB_Neon ); // 霓虹
    AddChild( mB_Onlock );
    AddChild( mSizeBar );
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( !visible )
      return false;
    if ( super.onTouchEvent( event ) )
      return true;

    return true;
  }

  public boolean IsVisible() {
    return visible;
  }

  public void Hide() {
    visible = false;
    mHasUpdate = true;
  }

  public void Show() {
    mSizeBar.SetSeekValue( mSize );
    visible = true;
    mHasUpdate = true;
  }

  @Override
  public void Draw( Canvas canvas ) {
    if ( !visible )
      return;

    canvas.drawColor( 0xAA000000 );

    Paint paintSTROKE = new Paint();
    paintSTROKE.setColor( Color.WHITE );
    paintSTROKE.setStyle( Style.STROKE );
    int mTempSize = mSizeBar.GetSeekValue();
    canvas.drawCircle( (int) mSizeDemo.x, (int) mSizeDemo.y, (float) ( mTempSize > 2 ? mTempSize / 2.0 : 1 ), paintSTROKE );
    super.Draw( canvas );
  }

  public void CompatibleWith( double windowWidth, double windowHeight ) {
    SetAlignment( Alignment.FILL, Alignment.FILL );
    mHasUpdate = true;
    mBackground.SetSize( windowWidth, windowHeight ); // 設定背景大小
    mBCancel.SetPos( (int) ( windowWidth * 0.25 ), (int) ( windowHeight - ( windowWidth * 0.25 ) ), (int) ( windowWidth * 0.75 ), (int) windowHeight );

    mSizeBar.SetPos( (int) ( windowWidth * 0.3 ), (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.95 ), (int) ( windowWidth * 0.4 ) );
    mSizeDemo = new KinPoint( windowWidth * 0.15, windowWidth * 0.3 );
    // 0.25:0.5 ver
    int bleft = 0;
    int bright = (int) windowWidth;
    int bcenter = (int) ( windowWidth * 0.5 );
    int brow1 = (int) ( windowWidth * 0.50 );
    int brow2 = (int) ( windowWidth * 0.75 );
    int brow3 = (int) ( windowWidth * 1.00 );
    int brow4 = (int) ( windowWidth * 1.25 );
    // left button
    mB_Normal.SetPos( bleft, brow1, bcenter, brow2 );
    mB_Highlighter.SetPos( bleft, brow2, bcenter, brow3 );
    mB_Neon.SetPos( bleft, brow3, bcenter, brow4 );
    // right button
    mB_Eraser.SetPos( bcenter, brow1, bright, brow2 );
    mB_Watercolor.SetPos( bcenter, brow2, bright, brow3 );
    mB_Onlock.SetPos( bcenter, brow3, bright, brow4 );

  }

  public int GetSize() {
    if ( mSize < 2 )
      return 2;
    return mSize;
  }

  public int GetPen() {
    return mPen;
  }

  public void SetPen( int pen ) {
    mPen = pen;
  }

  public void SetSize( int size ) {
    mSize = size;
  }
}
