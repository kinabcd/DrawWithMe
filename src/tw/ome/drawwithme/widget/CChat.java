package tw.ome.drawwithme.widget;

import tw.kin.android.layout.KinAbsoluteLayout;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinEditText;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinLable;
import tw.ome.drawwithme.Main;
import tw.ome.drawwithme.R;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class CChat extends KinAbsoluteLayout {
  KinImage mBackground;

  KinLable mRoomNumber;
  KinImage mRoomOnlock;
  KinLable mRoomName;

  KinButton mBPageRoommate;
  KinImage mPageRoommateUp;
  KinButton mBPageFriend;
  KinImage mPageFriendUp;
  KinButton mBPageHide;
  KinImage mPageHideUp;

  KinImage mListRoommate;
  KinImage mListFriend;
  KinImage mListMessage;

  KinImage mBottomBackground;
  KinButton mBSmile;// 表情符號
  KinImage mInputBg;
  KinEditText mInputMessage;
  KinButton mBSend;

  public CChat() {
    mBackground = new KinImage();
    mBackground.AddImage( Main.lib.GetBitmap( R.drawable.menu_bg ), -1 );

    mRoomNumber = new KinLable();
    mRoomNumber.SetText( "0" );
    mRoomOnlock = new KinImage();
    mRoomOnlock.AddImage( Main.lib.GetBitmap( R.drawable.chat_lock ), -1 );
    mRoomName = new KinLable();
    mRoomName.SetText( "RoomName" );

    mPageRoommateUp = new KinImage();
    mPageRoommateUp.AddImage( Main.lib.GetBitmap( R.drawable.chat_roommate_up ), -1 );
    KinImage iPageRoommateDown = new KinImage();
    iPageRoommateDown.AddImage( Main.lib.GetBitmap( R.drawable.chat_roommate_down ), -1 );
    mBPageRoommate = new KinButton( iPageRoommateDown );
    mBPageRoommate.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO show roommate list
        SetPage( 1 );
      }
    } );

    mPageFriendUp = new KinImage();
    mPageFriendUp.AddImage( Main.lib.GetBitmap( R.drawable.chat_friend_up ), -1 );
    KinImage iPageFriendDown = new KinImage();
    iPageFriendDown.AddImage( Main.lib.GetBitmap( R.drawable.chat_friend_down ), -1 );
    mBPageFriend = new KinButton( iPageFriendDown );
    mBPageFriend.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO show friend list
        SetPage( 2 );
      }
    } );
    mPageHideUp = new KinImage();
    mPageHideUp.AddImage( Main.lib.GetBitmap( R.drawable.chat_hide_up ), -1 );
    KinImage iPageHide = new KinImage();
    iPageHide.AddImage( Main.lib.GetBitmap( R.drawable.chat_hide_down ), -1 );
    mBPageHide = new KinButton( iPageHide );
    mBPageHide.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO hide roommate/friend list
        SetPage( 3 );
      }
    } );

    mListRoommate = new KinImage();
    mListRoommate.AddImage( Main.lib.GetBitmap( R.drawable.chat_roommate_page ), -1 );
    mListFriend = new KinImage();
    mListFriend.AddImage( Main.lib.GetBitmap( R.drawable.chat_friend_page ), -1 );

    mListMessage = new KinImage();
    mListMessage.AddImage( Main.lib.GetBitmap( R.drawable.menu_bg ), -1 );
    mBottomBackground = new KinImage();
    mBottomBackground.AddImage( Main.lib.GetBitmap( R.drawable.chat_bottombg ), -1 );

    KinImage iSmile = new KinImage();
    iSmile.AddImage( Main.lib.GetBitmap( R.drawable.chat_smile ), -1 );
    mBSmile = new KinButton( iSmile );

    mInputBg = new KinImage();
    mInputBg.AddImage( Main.lib.GetBitmap( R.drawable.chat_inputbg ), -1 );
    mInputMessage = new KinEditText();

    KinImage iSend = new KinImage();
    iSend.AddImage( Main.lib.GetBitmap( R.drawable.chat_send_disable ), -1 );
    mBSend = new KinButton( iSend );

    AddChild( mBackground );
    AddChild( mRoomNumber );
    AddChild( mRoomOnlock );
    AddChild( mRoomName );
    AddChild( mPageRoommateUp );
    AddChild( mPageFriendUp );
    AddChild( mPageHideUp );
    AddChild( mBPageRoommate );
    AddChild( mBPageFriend );
    AddChild( mBPageHide );
    AddChild( mListMessage );
    AddChild( mListRoommate );
    AddChild( mListFriend );
    AddChild( mBottomBackground );
    AddChild( mBSmile );
    AddChild( mInputBg );
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

    // canvas.drawColor( 0xAA000000 );

    super.Draw( canvas );
  }

  public void CompatibleWith( double windowWidth, double windowHeight ) {
    mBackground.SetSize( windowWidth, windowHeight ); // 設定背景大小
    mBackground.SetPos( 0, 0 );

    mRoomNumber.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.1 ) );
    mRoomNumber.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowWidth * 0.05 ) );
    mRoomOnlock.SetSize( (int) ( windowWidth / 12 ), (int) ( windowWidth * 0.1 ) );
    mRoomOnlock.SetPos( (int) ( windowWidth * 0.175 ), (int) ( windowWidth * 0.05 ) );
    mRoomName.SetSize( (int) ( windowWidth * 0.7 ), (int) ( windowWidth * 0.1 ) );
    mRoomName.SetPos( (int) ( windowWidth * 0.25 ), (int) ( windowWidth * 0.05 ) );

    mBPageRoommate.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mBPageRoommate.SetPos( (int) ( windowWidth * 0.35 ), (int) ( windowWidth * 0.175 ) );
    mPageRoommateUp.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mPageRoommateUp.SetPos( (int) ( windowWidth * 0.35 ), (int) ( windowWidth * 0.175 ) );
    mBPageFriend.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mBPageFriend.SetPos( (int) ( windowWidth * 0.55 ), (int) ( windowWidth * 0.175 ) );
    mPageFriendUp.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mPageFriendUp.SetPos( (int) ( windowWidth * 0.55 ), (int) ( windowWidth * 0.175 ) );
    mBPageHide.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mBPageHide.SetPos( (int) ( windowWidth * 0.75 ), (int) ( windowWidth * 0.175 ) );
    mPageHideUp.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mPageHideUp.SetPos( (int) ( windowWidth * 0.75 ), (int) ( windowWidth * 0.175 ) );

    mListRoommate.SetSize( (int) ( windowWidth * 0.6 ), (int) ( windowWidth * 1 ) );
    mListRoommate.SetPos( (int) ( windowWidth * 0.35 ), (int) ( windowWidth * 0.3 ) );
    mListFriend.SetSize( (int) ( windowWidth * 0.6 ), (int) ( windowWidth * 1 ) );
    mListFriend.SetPos( (int) ( windowWidth * 0.35 ), (int) ( windowWidth * 0.3 ) );

    mListMessage.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowWidth * 0.3 ), (int) ( windowWidth * 0.95 ),
        (int) ( windowHeight - windowWidth * 0.15 ) );

    mBottomBackground.SetSize( (int) ( windowWidth * 0.9 ), (int) ( windowWidth * 0.125 ) );
    mBottomBackground.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowHeight - windowWidth * 0.15 ) );
    mBSmile.SetSize( (int) ( windowWidth * 0.1 ), (int) ( windowWidth * 0.125 ) );
    mBSmile.SetPos( (int) ( windowWidth * 0.05 ), (int) ( windowHeight - windowWidth * 0.15 ) );
    mInputBg.SetSize( (int) ( windowWidth * 0.6 ), (int) ( windowWidth * 0.125 ) );
    mInputBg.SetPos( (int) ( windowWidth * 0.15 ), (int) ( windowHeight - windowWidth * 0.15 ) );
    mInputMessage.SetSize( (int) ( windowWidth * 0.6 ), (int) ( windowWidth * 0.125 ) );
    mInputMessage.SetPos( (int) ( windowWidth * 0.15 ), (int) ( windowHeight - windowWidth * 0.15 ) );
    mBSend.SetSize( (int) ( windowWidth * 0.2 ), (int) ( windowWidth * 0.125 ) );
    mBSend.SetPos( (int) ( windowWidth * 0.75 ), (int) ( windowHeight - windowWidth * 0.15 ) );

    RequireRedraw();

  }

  void SetPage( int page ) {
    if ( page == 1 ) {
      mBPageRoommate.SetVisible( false );
      mPageRoommateUp.SetVisible( true );
      mBPageFriend.SetVisible( true );
      mPageFriendUp.SetVisible( false );
      mBPageHide.SetVisible( true );
      mPageHideUp.SetVisible( false );
      mListFriend.SetVisible( false );
      mListRoommate.SetVisible( true );
    } else if ( page == 2 ) {
      mBPageRoommate.SetVisible( true );
      mPageRoommateUp.SetVisible( false );
      mBPageFriend.SetVisible( false );
      mPageFriendUp.SetVisible( true );
      mBPageHide.SetVisible( true );
      mPageHideUp.SetVisible( false );
      mListRoommate.SetVisible( false );
      mListFriend.SetVisible( true );
    } else {
      mBPageRoommate.SetVisible( true );
      mPageRoommateUp.SetVisible( false );
      mBPageFriend.SetVisible( true );
      mPageFriendUp.SetVisible( false );
      mBPageHide.SetVisible( false );
      mPageHideUp.SetVisible( true );
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
