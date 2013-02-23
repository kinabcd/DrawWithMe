package tw.cycuice.drawwithme.ui;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.Client;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.Main;
import tw.cycuice.drawwithme.R;
import tw.cycuice.drawwithme.widget.CSelectColor;
import tw.cycuice.drawwithme.widget.CSelectSize;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinLable;
import tw.kin.android.widget.KinSeekBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CNew extends KinView implements IUI {
  KinImage mBackground;
  KinButton mBOK;
  KinButton mBReset;
  KinButton mBSelectColor;
  KinButton mBCheckOnline;
  KinLable mRoomName;
  KinLable mRoomPassword;
  KinButton mBRoomName;
  KinButton mBRoomPassword;
  KinSeekBar mSizeBarX;
  KinSeekBar mSizeBarY;
  CSelectSize mUISelectSize;
  CSelectColor mUISelectColor;
  Paint mTextPaint;
  KinImage miOnline;
  KinImage miOffline;
  boolean mIsOnline;

  public CNew() {
  }

  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( R.drawable.menu_bg, -1 );
    mBackground.SetAlignment( Alignment.FILL, Alignment.FILL );
    KinImage iOK = new KinImage();
    iOK.AddImage( R.drawable.new_ok, -1 );
    mBOK = new KinButton( iOK );
    mBOK.SetAlignment( Alignment.RIGHT, Alignment.BOTTOM );
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
    KinImage iDefaultSize = new KinImage();
    iDefaultSize.AddImage( R.drawable.new_select_bg, -1 );
    mBSelectColor = new KinButton( iDefaultSize );
    mBSelectColor.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        mUISelectColor.Show();
      }
    } );
    KinImage iReset = new KinImage();
    iReset.AddImage( R.drawable.new_reset, -1 );
    mBReset = new KinButton( iReset );
    mBReset.SetAlignment( Alignment.LEFT, Alignment.BOTTOM );
    mBReset.SetOnClickRun( new Runnable() {
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

    miOnline = new KinImage();
    miOnline.AddImage( R.drawable.online, -1 );
    miOffline = new KinImage();
    miOffline.AddImage( R.drawable.offline, -1 );
    mBCheckOnline = new KinButton();
    mBCheckOnline.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        SetOnline( !mIsOnline );
      }
    } );
    mRoomName = new KinLable();
    //mRoomName.SetText( "NAME" );
    mBRoomName = new KinButton();
    mBRoomName.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder( Main.sInstance );
        builder.setTitle( "RoomName" );
        final LinearLayout view = new LinearLayout( Main.sInstance );
        view.setOrientation( LinearLayout.VERTICAL );
        final EditText inputRoomName = new EditText( Main.sInstance );
        inputRoomName.setText( mRoomName.GetText() );
        inputRoomName.setHint( "Room Name" );
        inputRoomName.setInputType( InputType.TYPE_CLASS_TEXT );
        inputRoomName.setTypeface( Typeface.SERIF );
        view.addView( inputRoomName );
        builder.setView( view );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick( DialogInterface dialog, int which ) {
            mRoomName.SetText( inputRoomName.getText().toString() );
            DrawSurface.GetInstance().UpdateView();
          }
        } );
        builder.show();
      }
    } );

    mRoomPassword = new KinLable();
    //mRoomPassword.SetText( "PASSWORD" );
    mBRoomPassword = new KinButton();
    mBRoomPassword.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder( Main.sInstance );
        builder.setTitle( "RoomPassword" );
        final LinearLayout view = new LinearLayout( Main.sInstance );
        view.setOrientation( LinearLayout.VERTICAL );
        final EditText inputRoomPassword = new EditText( Main.sInstance );
        inputRoomPassword.setText( mRoomPassword.GetText() );
        inputRoomPassword.setHint( "Number Only" );
        inputRoomPassword.setInputType( InputType.TYPE_CLASS_NUMBER );
        inputRoomPassword.setTypeface( Typeface.SERIF );
        inputRoomPassword.setFilters(new InputFilter[]{CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter(16)});
        view.addView( inputRoomPassword );
        builder.setView( view );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick( DialogInterface dialog, int which ) {
            mRoomPassword.SetText( inputRoomPassword.getText().toString() );
            DrawSurface.GetInstance().UpdateView();
          }
        } );
        builder.show();
      }
    } );

    AddChild( mBackground );
    AddChild( mSizeBarX );
    AddChild( mSizeBarY );
    AddChild( mBOK );
    AddChild( mBReset );
    AddChild( mBSelectColor );
    AddChild( mBCheckOnline );
    AddChild( mRoomName );
    AddChild( mRoomPassword );
    AddChild( mBRoomName );
    AddChild( mBRoomPassword );
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
    mBRoomName.SetSize( windowWidth * 0.65, windowWidth * 0.125 );
    mBRoomName.SetPos( (int) ( windowWidth * 0.3 ), (int) ( windowWidth ) );
    mBRoomPassword.SetSize( windowWidth * 0.65, windowWidth * 0.125 );
    mBRoomPassword.SetPos( (int) ( windowWidth * 0.3 ), (int) ( windowWidth * 1.15 ) );

    mUISelectColor.CompatibleWith( windowWidth, windowHeight );
  }

  @Override
  public void onStart( IUI from ) {
    SetOnline( Client.IsLogin() );
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

  void SetOnline( boolean online ) {
    if ( !Client.IsLogin() )
      online = false;
    mIsOnline = online;
    if ( mIsOnline ) {
      mBCheckOnline.SetImage( miOnline );
      mRoomName.SetVisible( true );
      mRoomPassword.SetVisible( true );
      mBRoomName.SetVisible( true );
      mBRoomPassword.SetVisible( true );
    } else {
      mBCheckOnline.SetImage( miOffline );
      mRoomName.SetVisible( false );
      mRoomPassword.SetVisible( false );
      mBRoomName.SetVisible( false );
      mBRoomPassword.SetVisible( false );
    }
  }
}
