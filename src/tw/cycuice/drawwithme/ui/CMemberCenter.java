package tw.cycuice.drawwithme.ui;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.Client;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.R;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinLable;
import android.view.KeyEvent;

public class CMemberCenter extends KinView implements IUI {

  KinImage mBackground;
  KinImage mTitle;
  KinLable mAccount;
  KinLable mNickname;
  KinLable mPassword;

  KinButton mBLogout;
  KinButton mBChangeNickname;
  KinButton mBChangePassword;
  KinButton mBOK;

  public CMemberCenter() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public void onStart( IUI from ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onQuit( IUI to ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( R.drawable.menu_bg, -1 );
    mBackground.SetAlignment( Alignment.FILL, Alignment.FILL );
    mTitle = new KinImage();
    mTitle.AddImage( R.drawable.menu_title, -1 );
    mTitle.SetSizePercent( 0.95, 0.25 ); // 設定標題大小(百分比)
    mTitle.SetAlignment( Alignment.CENTER, Alignment.TOP );

    mAccount = new KinLable();
    mAccount.SetSizePercent( 0.75, 0.1 );
    mNickname = new KinLable();
    mNickname.SetSizePercent( 0.75, 0.1 );
    mPassword = new KinLable();
    mPassword.SetSizePercent( 0.75, 0.1 );

    KinImage iLogout = new KinImage();
    iLogout.AddImage( R.drawable.logout, -1 );
    mBLogout = new KinButton( iLogout );
    mBLogout.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        Client.Logout();
        DrawSurface.GetInstance().SetPage( CConstant.PAGEMENU );
      }
    } );
    KinImage iChangeNickname = new KinImage();
    iChangeNickname.AddImage( R.drawable.edit, -1 );
    mBChangeNickname = new KinButton( iChangeNickname );
    // etConfirm.setFilters(new InputFilter[]{CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter(16)});
    KinImage iChangePassword = new KinImage();
    iChangePassword.AddImage( R.drawable.edit, -1 );
    mBChangePassword = new KinButton( iChangePassword );
    // etConfirm.setFilters(new InputFilter[]{CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter(16)});
    KinImage iOK = new KinImage();
    iOK.AddImage( R.drawable.new_ok, -1 );
    mBOK = new KinButton( iOK );
    mBOK.SetAlignment( Alignment.CENTER, Alignment.BOTTOM );
    mBOK.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        DrawSurface.GetInstance().SetPage( CConstant.PAGEMENU );
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
