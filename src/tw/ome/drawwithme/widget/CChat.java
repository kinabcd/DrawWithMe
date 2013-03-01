package tw.ome.drawwithme.widget;

import tw.kin.android.layout.KinAbsoluteLayout;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinEditText;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinLable;
import tw.ome.drawwithme.R;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class CChat extends KinAbsoluteLayout {
  KinImage mBackground;

  KinLable mRoomNumber;
  KinImage mRoomOnlock;
  KinLable mRoomName;

  KinButton mBPageRoommate;
  KinButton mBPageFriend;
  KinButton mBPageHide;
  KinImage mListRoommate;
  KinImage mListFriend;

  KinImage mListMessage;

  KinButton mBEmoticon;// 表情符號
  KinButton mBWordColor;
  KinEditText mInputMessage;
  KinButton mBSend;

  public CChat() {
    mBackground = new KinImage();
    mRoomNumber = new KinLable();
    mRoomNumber.SetText( "0" );

    mRoomOnlock = new KinImage();
    mRoomOnlock.AddImage( R.drawable.chat_lock, -1 );
    mRoomName = new KinLable();
    mRoomName.SetText( "RoomName" );

    KinImage iPageRoommate = new KinImage();
    iPageRoommate.AddImage( R.drawable.new_ok, -1 );
    mBPageRoommate = new KinButton( iPageRoommate );
    mBPageRoommate.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO show roommate list
        SetPage( 1 );
      }
    } );

    KinImage iPageFriend = new KinImage();
    iPageFriend.AddImage( R.drawable.new_ok, -1 );
    mBPageFriend = new KinButton( iPageFriend );
    mBPageFriend.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO show friend list
        SetPage( 2 );
      }
    } );

    KinImage iPageHide = new KinImage();
    iPageHide.AddImage( R.drawable.new_ok, -1 );
    mBPageHide = new KinButton( iPageHide );
    mBPageHide.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO hide roommate/friend list
        SetPage( 3 );
      }
    } );

    mListRoommate = new KinImage();
    mListRoommate.AddImage( R.drawable.new_ok, -1 );
    mListFriend = new KinImage();
    mListFriend.AddImage( R.drawable.new_reset, -1 );

    mListMessage = new KinImage();
    mListMessage.AddImage( R.drawable.menu_bg, -1 );

    KinImage iEmoticon = new KinImage();
    iEmoticon.AddImage( R.drawable.new_ok, -1 );
    mBEmoticon = new KinButton( iEmoticon );

    KinImage iWordColor = new KinImage();
    iWordColor.AddImage( R.drawable.new_ok, -1 );
    mBWordColor = new KinButton( iWordColor );

    mInputMessage = new KinEditText();

    KinImage iSend = new KinImage();
    iSend.AddImage( R.drawable.new_ok, -1 );
    mBSend = new KinButton( iSend );

    AddChild( mRoomNumber );
    AddChild( mRoomOnlock );
    AddChild( mRoomName );
    AddChild( mBPageRoommate );
    AddChild( mBPageFriend );
    AddChild( mBPageHide );
    AddChild( mListMessage );
    AddChild( mListRoommate );
    AddChild( mListFriend );
    AddChild( mBEmoticon );
    AddChild( mBWordColor );
    AddChild( mInputMessage );
    AddChild( mBSend );

  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( !mVisible )
      return false;
    if ( super.onTouchEvent( event ) )
      return true;

    return true;
  }

  public boolean IsVisible() {
    return mVisible;
  }

  public void Hide() {
    mVisible = false;
    RequireRedraw();
  }

  public void Show() {
    mVisible = true;
    SetPage( 3 );
    RequireRedraw();
  }

  @Override
  public void Draw( Canvas canvas ) {
    if ( !mVisible )
      return;

    canvas.drawColor( 0xAA000000 );

    Paint paintSTROKE = new Paint();
    paintSTROKE.setColor( Color.WHITE );
    paintSTROKE.setStyle( Style.STROKE );

    super.Draw( canvas );
  }

  public void CompatibleWith( double windowWidth, double windowHeight ) {
    mBackground.SetSize( windowWidth, windowHeight ); // 設定背景大小

    mRoomNumber.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.1 ) );
    mRoomNumber.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowWidth * 0.05 ) );
    mRoomOnlock.SetSize( (int) ( windowWidth / 12 ), (int) ( windowWidth * 0.1 ) );
    mRoomOnlock.SetPos( (int) ( windowWidth * 0.175 ), (int) ( windowWidth * 0.05 ) );
    mRoomName.SetSize( (int) ( windowWidth * 0.7 ), (int) ( windowWidth * 0.1 ) );
    mRoomName.SetPos( (int) ( windowWidth * 0.25 ), (int) ( windowWidth * 0.05 ) );

    mBPageHide.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mBPageHide.SetPos( (int) ( windowWidth * 0.75 ), (int) ( windowWidth * 0.175 ) );
    mBPageFriend.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mBPageFriend.SetPos( (int) ( windowWidth * 0.55 ), (int) ( windowWidth * 0.175 ) );
    mBPageRoommate.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mBPageRoommate.SetPos( (int) ( windowWidth * 0.35 ), (int) ( windowWidth * 0.175 ) );

    mListRoommate.SetSize( (int) ( windowWidth * 0.6 ), (int) ( windowWidth * 1 ) );
    mListRoommate.SetPos( (int) ( windowWidth * 0.35 ), (int) ( windowWidth * 0.3 ) );
    mListFriend.SetSize( (int) ( windowWidth * 0.6 ), (int) ( windowWidth * 1 ) );
    mListFriend.SetPos( (int) ( windowWidth * 0.35 ), (int) ( windowWidth * 0.3 ) );

    mListMessage.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowWidth * 0.3 ), (int) ( windowWidth * 0.95 ),
        (int) ( windowHeight - windowWidth * 0.15 ) );

    mBEmoticon.SetSize( (int) ( windowWidth * 0.1 ), (int) ( windowWidth * 0.125 ) );
    mBEmoticon.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowHeight - windowWidth * 0.15 ) );
    // mBWordColor.SetSize( (int) ( windowWidth * 0.1 ), (int) ( windowWidth * 0.125 ) );
    // mBWordColor.SetPos( (int) ( windowWidth * 0.15 ), (int) ( windowHeight - windowWidth * 0.15 ) );
    mInputMessage.SetSize( (int) ( windowWidth * 0.6 ), (int) ( windowWidth * 0.125 ) );
    mInputMessage.SetPos( (int) ( windowWidth * 0.15 ), (int) ( windowHeight - windowWidth * 0.15 ) );
    mBSend.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mBSend.SetPos( (int) ( windowWidth * 0.75 ), (int) ( windowHeight - windowWidth * 0.15 ) );

    RequireRedraw();

  }

  void SetPage( int page ) {
    if ( page == 1 ) {
      mListFriend.SetVisible( false );
      mListRoommate.SetVisible( true );
    } else if ( page == 2 ) {
      mListRoommate.SetVisible( false );
      mListFriend.SetVisible( true );
    } else {
      mListRoommate.SetVisible( false );
      mListFriend.SetVisible( false );
    }
    RequireRedraw();
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
