package tw.ome.drawwithme.widget;

import tw.kin.android.KinPoint;
import tw.kin.android.layout.KinAbsoluteLayout;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinSeekBar;
import tw.ome.drawwithme.CConstant;
import tw.ome.drawwithme.Main;
import tw.ome.drawwithme.R;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class CSelectPen extends KinAbsoluteLayout {
  int mSize;
  int mPen;
  KinImage mBackground;
  KinButton mBCancel;
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

    mBCancel = new KinButton();
    mBCancel.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_cancel ), -1 );
    mBCancel.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_cancel2 ), -1 );
    mBCancel.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBCancel.SetFrame( 0 );
        Hide();
      }
    } );
    mBCancel.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBCancel.SetFrame( 1 );
      }
    } );

    mB_Normal = new KinButton(); // 一般畫筆
    mB_Normal.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_normal ), -1 );
    mB_Normal.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_normal2 ), -1 );
    mB_Normal.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mB_Normal.SetFrame( 0 );
        mPen = CConstant.PENNORMAL;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Normal.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mB_Normal.SetFrame( 1 );
      }
    } );

    mB_Eraser = new KinButton(); // 橡皮擦
    mB_Eraser.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_eraser ), -1 );
    mB_Eraser.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_eraser2 ), -1 );
    mB_Eraser.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mB_Eraser.SetFrame( 0 );
        mPen = CConstant.PENERASER;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Eraser.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mB_Eraser.SetFrame( 1 );
      }
    } );

    mB_Highlighter = new KinButton(); // 螢光筆
    mB_Highlighter.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_highlighter ), -1 );
    mB_Highlighter.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_highlighter2 ), -1 );
    mB_Highlighter.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mB_Highlighter.SetFrame( 0 );
        mPen = CConstant.PENHIGHLIGHTER;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Highlighter.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mB_Highlighter.SetFrame( 1 );
      }
    } );

    mB_Watercolor = new KinButton(); // 水彩
    mB_Watercolor.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_watercolor ), -1 );
    mB_Watercolor.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_watercolor2 ), -1 );
    mB_Watercolor.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mB_Watercolor.SetFrame( 0 );
        mPen = CConstant.PENWATERCOLOR;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Watercolor.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mB_Watercolor.SetFrame( 1 );
      }
    } );

    mB_Neon = new KinButton(); // 霓虹
    mB_Neon.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_neon ), -1 );
    mB_Neon.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_neon2 ), -1 );
    mB_Neon.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mB_Neon.SetFrame( 0 );
        mPen = CConstant.PENNEON;
        mSize = mSizeBar.GetSeekValue();
        Hide();
      }
    } );
    mB_Neon.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mB_Neon.SetFrame( 1 );
      }
    } );

    mB_Onlock = new KinButton(); // 鎖
    mB_Onlock.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_lock ), -1 );
    mB_Onlock.AddImage( Main.lib.GetBitmap( R.drawable.selectpen_lock ), -1 );
    mB_Onlock.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mB_Onlock.SetFrame( 0 );
      }
    } );
    mB_Onlock.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mB_Onlock.SetFrame( 1 );
      }
    } );

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
    if ( !IsVisible() )
      return false;
    if ( super.onTouchEvent( event ) )
      return true;

    return true;
  }

  public void Hide() {
    mVisible = false;
    RequireRedraw();
  }

  public void Show() {
    mSizeBar.SetSeekValue( mSize );
    mVisible = true;
    RequireRedraw();
  }

  @Override
  public void Draw( Canvas canvas ) {
    if ( !IsVisible() )
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
    RequireRedraw();

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
