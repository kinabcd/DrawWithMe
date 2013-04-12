package tw.ome.drawwithme.ui;

import tw.kin.android.layout.KinAbsoluteLayout;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinEditText;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinSeekBar;
import tw.ome.drawwithme.CConstant;
import tw.ome.drawwithme.DrawSurface;
import tw.ome.drawwithme.Main;
import tw.ome.drawwithme.R;
import tw.ome.drawwithme.protocal.CModeInternet;
import tw.ome.drawwithme.widget.CSelectColor;
import tw.ome.drawwithme.widget.CSelectSize;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.KeyEvent;
import android.widget.Toast;

public class CNew extends KinAbsoluteLayout implements IUI {
  KinImage mBackground;
  KinButton mBOK;
  KinButton mBReset;
  KinButton mBSelectColor;
  KinButton mBCheckOnline;
  KinEditText mRoomName;
  KinEditText mRoomPassword;
  KinImage mIRoomName;
  KinImage mIRoomPassword;
  KinSeekBar mSizeBarX;
  KinSeekBar mSizeBarY;
  CSelectSize mUISelectSize;
  CSelectColor mUISelectColor;
  Paint mTextPaint;
  boolean mIsOnline;

  public CNew() {
  }

  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( Main.lib.GetBitmap( R.drawable.menu_bg ), -1 );
    mBackground.SetSizePercent( 1, 1 );
    KinImage iOK = new KinImage();
    iOK.AddImage( Main.lib.GetBitmap( R.drawable.new_ok ), -1 );
    mBOK = new KinButton( iOK );
    SetAlignment( mBOK, Alignment.RIGHT, Alignment.BOTTOM );
    mBOK.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        if ( mSizeBarX.GetSeekValue() < 1 || mSizeBarY.GetSeekValue() < 1 ) {
          Toast.makeText( Main.sInstance, "Create Fail!!", Toast.LENGTH_SHORT ).show();
          return;
        }
        int width = mSizeBarX.GetSeekValue();
        int height = mSizeBarY.GetSeekValue();
        int color = mUISelectColor.GetColor();
        if ( !mIsOnline ) {
          DrawSurface.GetInstance().mUICanvas.NewCanvas( width, height, color, false );
          DrawSurface.GetInstance().SetPage( CConstant.PAGECANVAS );
        } else {
          CModeInternet.CreateRoom( width, height, color, mRoomName.GetText(), mRoomPassword.GetText() );
        }
      }
    } );
    KinImage iDefaultSize = new KinImage();
    iDefaultSize.AddImage( Main.lib.GetBitmap( R.drawable.new_select_bg ), -1 );
    mBSelectColor = new KinButton( iDefaultSize );
    mBSelectColor.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mUISelectColor.Show();
      }
    } );
    KinImage iReset = new KinImage();
    iReset.AddImage( Main.lib.GetBitmap( R.drawable.new_reset ), -1 );
    mBReset = new KinButton( iReset );
    SetAlignment( mBReset, Alignment.LEFT, Alignment.BOTTOM );
    mBReset.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mSizeBarX.SetSeekValue( GetWidth() );
        mSizeBarY.SetSeekValue( GetHeight() );
        mUISelectColor.SetColor( Color.WHITE );
      }
    } );
    mSizeBarX = new KinSeekBar();
    mSizeBarX.SetMinValue( 0 );
    mSizeBarX.SetMaxValue( CConstant.MaxWidth );
    mSizeBarY = new KinSeekBar();
    mSizeBarY.SetVertical( true );
    mSizeBarY.SetReverse( true );
    mSizeBarY.SetMinValue( 0 );
    mSizeBarY.SetMaxValue( CConstant.MaxHeight );
    mUISelectSize = new CSelectSize();
    mUISelectColor = new CSelectColor();
    mTextPaint = new Paint();
    mTextPaint.setStyle( Style.FILL_AND_STROKE );
    mTextPaint.setTextSize( 20 );
    mTextPaint.setARGB( 0xff, 220, 220, 255 );

    mBCheckOnline = new KinButton();
    mBCheckOnline.AddImage( Main.lib.GetBitmap( R.drawable.online ), -1 );
    mBCheckOnline.AddImage( Main.lib.GetBitmap( R.drawable.offline ), -1 );
    mBCheckOnline.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        SetOnline( !mIsOnline );
        mBCheckOnline.SetFrame( 0 );
      }
    } );
    mBCheckOnline.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBCheckOnline.SetFrame( 1 );

      }
    } );
    mRoomName = new KinEditText();
    // mRoomName.SetText( "NAME" );
    mIRoomName = new KinImage();
    mIRoomName.AddImage( Main.lib.GetBitmap( R.drawable.chat_inputbg ), -1 );

    mRoomPassword = new KinEditText();
    mIRoomPassword = new KinImage();
    mIRoomPassword.AddImage( Main.lib.GetBitmap( R.drawable.chat_inputbg ), -1 );
    // mRoomPassword.SetText( "PASSWORD" );

    AddChild( mBackground );
    AddChild( mSizeBarX );
    AddChild( mSizeBarY );
    AddChild( mBOK );
    AddChild( mBReset );
    AddChild( mBSelectColor );
    AddChild( mBCheckOnline );
    AddChild( mIRoomName );
    AddChild( mIRoomPassword );
    AddChild( mRoomName );
    AddChild( mRoomPassword );
    AddChild( mUISelectSize );
    AddChild( mUISelectColor );
  }

  @Override
  public void Draw( Canvas canvas ) {
    int height = mSizeBarY.GetSeekValue();
    int width = mSizeBarX.GetSeekValue();

    mUISelectSize.SetSeekValueX( mSizeBarX.GetSeekValue() );
    mUISelectSize.SetSeekValueY( mSizeBarY.GetSeekValue() );
    mUISelectSize.SetColor( mUISelectColor.GetColor() );
    super.Draw( canvas );
    canvas.drawText( height + " x " + width, mUISelectSize.GetX(), mUISelectSize.GetY() - 3, mTextPaint );
  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    SetPos( 0, 0, (int) windowWidth, (int) windowHeight );
    int bWidth = (int) ( windowWidth * 0.5 );
    int bHeight = (int) ( bWidth / 200.0 * 80.0 );
    mBOK.SetSize( bWidth, bHeight );
    mBReset.SetSize( bWidth, bHeight );
    mBSelectColor.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowWidth * 0.825 ), (int) ( windowWidth * 0.175 ), (int) ( windowWidth * 0.95 ) );
    mBCheckOnline.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowWidth ), (int) ( windowWidth * 0.28 ), (int) ( windowWidth * 1.275 ) );
    mUISelectSize.SetPos( (int) ( windowWidth * 0.175 ), (int) ( windowWidth * 0.05 ), (int) ( windowWidth * 0.95 ), (int) ( windowWidth * 0.825 ) );
    mSizeBarX.SetSeekValue( GetWidth() );
    mSizeBarY.SetSeekValue( GetHeight() );
    mSizeBarX.SetPos( mUISelectSize.GetX(), (int) ( windowWidth * 0.825 ), mUISelectSize.GetX() + mUISelectSize.GetWidth(),
        (int) ( windowWidth * 0.95 ) );
    mSizeBarY.SetPos( (int) ( windowWidth * 0.05 ), mUISelectSize.GetY(), (int) ( windowWidth * 0.175 ),
        mUISelectSize.GetY() + mUISelectSize.GetHeight() );
    mRoomName.SetSize( windowWidth * 0.65, windowWidth * 0.125 );
    mRoomName.SetPos( (int) ( windowWidth * 0.3 ), (int) ( windowWidth ) );
    mRoomPassword.SetSize( windowWidth * 0.65, windowWidth * 0.125 );
    mRoomPassword.SetPos( (int) ( windowWidth * 0.3 ), (int) ( windowWidth * 1.15 ) );
    mIRoomName.SetSize( windowWidth * 0.7, windowWidth * 0.125 );
    mIRoomName.SetPos( (int) ( windowWidth * 0.28 ), (int) ( windowWidth ) );
    mIRoomPassword.SetSize( windowWidth * 0.7, windowWidth * 0.125 );
    mIRoomPassword.SetPos( (int) ( windowWidth * 0.28 ), (int) ( windowWidth * 1.15 ) );

    mUISelectColor.CompatibleWith( windowWidth, windowHeight );
  }

  @Override
  public void onStart( IUI from ) {
    SetOnline( CModeInternet.IsLogin() );
    mUISelectColor.SetColor( Color.WHITE );
    mUISelectColor.Hide();
    RequireRedraw();

  }

  @Override
  public void onQuit( IUI to ) {
    if ( to instanceof CDrawBoard && !mIsOnline ) {
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

  void SetOnline( boolean online ) {
    if ( !CModeInternet.IsLogin() )
      online = false;
    mIsOnline = online;
    if ( mIsOnline ) {
      mBCheckOnline.SetFrame( 0 );
      mRoomName.SetVisible( true );
      mRoomPassword.SetVisible( true );
      mIRoomName.SetVisible( true );
      mIRoomPassword.SetVisible( true );
    } else {
      mBCheckOnline.SetFrame( 1 );
      mRoomName.SetVisible( false );
      mRoomPassword.SetVisible( false );
      mIRoomName.SetVisible( false );
      mIRoomPassword.SetVisible( false );
    }
    RequireRedraw();
  }
}
