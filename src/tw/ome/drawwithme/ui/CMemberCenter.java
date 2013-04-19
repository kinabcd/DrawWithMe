package tw.ome.drawwithme.ui;

import tw.kin.android.layout.KinAbsoluteLayout;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinLabel;
import tw.ome.drawwithme.CConstant;
import tw.ome.drawwithme.DrawSurface;
import tw.ome.drawwithme.Main;
import tw.ome.drawwithme.R;
import tw.ome.drawwithme.protocal.CModeInternet;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;

public class CMemberCenter extends KinAbsoluteLayout implements IUI {

  KinImage mBackground;
  KinImage mTitle;
  KinLabel mAccount;
  KinLabel mNickname;
  KinLabel mPassword;

  KinButton mBLogout;
  KinButton mBChangeNickname;
  KinButton mBChangePassword;
  KinButton mBOK;

  public CMemberCenter() {
  }

  @Override
  public void onStart( IUI from ) {
    SharedPreferences mSp = Main.sInstance.getSharedPreferences( "Options", android.content.Context.MODE_PRIVATE );
    if ( DrawSurface.GetInstance().mUIMenu.mRememberAccount!=null && !DrawSurface.GetInstance().mUIMenu.mRememberAccount.equals( "" ) )
      mAccount.SetText( DrawSurface.GetInstance().mUIMenu.mRememberAccount );
    else if ( !mSp.getString( "UserAccount", "" ).equals( "" ) )
      mAccount.SetText( mSp.getString( "UserAccount", "" ) );
  }

  @Override
  public void onQuit( IUI to ) {

  }

  @Override
  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( Main.lib.GetBitmap( R.drawable.menu_bg ), -1 );
    mBackground.SetSizePercent( 1, 1 );
    mTitle = new KinImage();
    mTitle.AddImage( Main.lib.GetBitmap( R.drawable.menu_title ), -1 );
    mTitle.SetSizePercent( 0.95, 0.25 ); // 設定標題大小(百分比)
    SetAlignment( mTitle, Alignment.CENTER, Alignment.TOP );

    mAccount = new KinLabel();
    mAccount.SetSizePercent( 0.75, 0.1 );
    mNickname = new KinLabel();
    mNickname.SetSizePercent( 0.75, 0.1 );
    mPassword = new KinLabel();
    mPassword.SetSizePercent( 0.75, 0.1 );

    mBLogout = new KinButton();
    mBLogout.AddImage( Main.lib.GetBitmap( R.drawable.logout ), -1 );
    mBLogout.AddImage( Main.lib.GetBitmap( R.drawable.logout2 ), -1 );
    mBLogout.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBLogout.SetFrame( 0 );
        CModeInternet.Logout();
        SharedPreferences mSp = Main.sInstance.getSharedPreferences( "Options", android.content.Context.MODE_PRIVATE );
        Editor editor = mSp.edit(); // 清空設定檔
        editor.putString( "UserAccount", "" );
        editor.putString( "UserPassword", "" );
        editor.commit();
        DrawSurface.GetInstance().mUIMenu.mScroll.GetLayout().CleanChild();
        DrawSurface.GetInstance().mUIMenu.mScroll.GetLayout().AddChild( DrawSurface.GetInstance().mUIMenu.mButtonBar );
        DrawSurface.GetInstance().SetPage( CConstant.PAGEMENU );
      }
    } );
    mBLogout.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBLogout.SetFrame( 1 );
      }
    } );

    mBChangeNickname = new KinButton();
    mBChangeNickname.AddImage( Main.lib.GetBitmap( R.drawable.edit ), -1 );
    mBChangeNickname.AddImage( Main.lib.GetBitmap( R.drawable.edit2 ), -1 );
    mBChangeNickname.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBChangeNickname.SetFrame( 0 );
        // etConfirm.setFilters(new InputFilter[]{CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter(16)});
      }
    } );
    mBChangeNickname.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBChangeNickname.SetFrame( 1 );
      }
    } );

    mBChangePassword = new KinButton();
    mBChangePassword.AddImage( Main.lib.GetBitmap( R.drawable.edit ), -1 );
    mBChangePassword.AddImage( Main.lib.GetBitmap( R.drawable.edit2 ), -1 );
    mBChangePassword.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBChangePassword.SetFrame( 0 );
        // etConfirm.setFilters(new InputFilter[]{CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter(16)});
      }
    } );
    mBChangePassword.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBChangePassword.SetFrame( 1 );
      }
    } );

    mBOK = new KinButton();
    mBOK.AddImage( Main.lib.GetBitmap( R.drawable.new_ok ), -1 );
    mBOK.AddImage( Main.lib.GetBitmap( R.drawable.new_ok2 ), -1 );
    SetAlignment( mBOK, Alignment.CENTER, Alignment.BOTTOM );
    mBOK.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBOK.SetFrame( 0 );
        DrawSurface.GetInstance().SetPage( CConstant.PAGEMENU );
      }
    } );
    mBOK.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBOK.SetFrame( 1 );
      }
    } );

    AddChild( mBackground );
    AddChild( mTitle );
    AddChild( mAccount );
    AddChild( mNickname );
    AddChild( mPassword );
    AddChild( mBLogout );
    AddChild( mBChangeNickname );
    AddChild( mBChangePassword );
    AddChild( mBOK );

  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    SetPos( 0, 0, (int) windowWidth, (int) windowHeight );
    int iHeight = (int) ( windowHeight * 0.12 ); // 圖片高度+間隔高度
    int iWidth = (int) ( windowWidth * 0.75 );
    int itop = (int) ( windowHeight * 0.33 );
    int ileft = (int) ( windowWidth * 0.05 );
    int bWidth = (int) ( windowWidth * 0.5 );
    int bHeight = (int) ( bWidth / 200.0 * 80.0 );
    mAccount.SetPos( ileft, itop );
    mNickname.SetPos( ileft, itop + iHeight );
    mPassword.SetPos( ileft, itop + iHeight + iHeight );
    mBLogout.SetSize( (int) ( windowHeight * 0.1 ), (int) ( windowHeight * 0.1 ) );
    mBLogout.SetPos( ileft + iWidth, itop );
    mBChangeNickname.SetSize( (int) ( windowHeight * 0.1 ), (int) ( windowHeight * 0.1 ) );
    mBChangeNickname.SetPos( ileft + iWidth, itop + iHeight );
    mBChangePassword.SetSize( (int) ( windowHeight * 0.1 ), (int) ( windowHeight * 0.1 ) );
    mBChangePassword.SetPos( ileft + iWidth, itop + iHeight + iHeight );
    mBOK.SetSize( bWidth, bHeight );

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
