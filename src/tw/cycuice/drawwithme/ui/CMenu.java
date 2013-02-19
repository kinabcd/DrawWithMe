package tw.cycuice.drawwithme.ui;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.Main;
import tw.cycuice.drawwithme.R;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinScroll;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CMenu extends KinView implements IUI {
  KinImage mBackground;
  KinImage mTitle;
  KinButton mBRefresh;
  KinButton mBSearch;
  KinButton mBSetting;
  KinButton mBCreate;
  KinButton mBLogin;
  KinScroll mScroll;

  public CMenu() {
    super();
  }

  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( R.drawable.menu_bg, -1 );
    mBackground.SetAlignment( Alignment.FILL, Alignment.FILL );
    mTitle = new KinImage();
    mTitle.AddImage( R.drawable.menu_title, -1 );
    mTitle.SetSizePercent( 0.95, 0.25 ); // 設定標題大小(百分比)
    mTitle.SetAlignment( Alignment.CENTER, Alignment.TOP );

    KinImage iRefresh = new KinImage();
    iRefresh.AddImage( R.drawable.menu_refresh, -1 );
    mBRefresh = new KinButton( iRefresh );
    mBRefresh.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO refresh menu
      }
    } );
    KinImage iSearch = new KinImage();
    iSearch.AddImage( R.drawable.menu_search, -1 );
    mBSearch = new KinButton( iSearch );
    mBSearch.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder( Main.sInstance );
        builder.setTitle( "Search" );
        final LinearLayout view = new LinearLayout( Main.sInstance );
        view.setOrientation( LinearLayout.VERTICAL );
        final EditText inputKeyword = new EditText( Main.sInstance );
        inputKeyword.setHint( "Keyword..." );
        inputKeyword.setInputType( InputType.TYPE_CLASS_TEXT );
        inputKeyword.setTypeface( Typeface.SERIF );
        final LinearLayout checkLayout = new LinearLayout( Main.sInstance );
        final CheckBox checkNo = new CheckBox( Main.sInstance );
        checkNo.setChecked( true );
        checkNo.setText( "No." );
        final CheckBox checkName = new CheckBox( Main.sInstance );
        checkName.setChecked( true );
        checkName.setText( "Name" );
        checkLayout.addView( checkNo );
        checkLayout.addView( checkName );
        view.addView( inputKeyword );
        view.addView( checkLayout );
        builder.setView( view );
        builder.setPositiveButton( "Search", new DialogInterface.OnClickListener() {
          @Override
          public void onClick( DialogInterface dialog, int which ) {
          }
        } );
        builder.show();
      }
    } );
    KinImage iSetting = new KinImage();
    iSetting.AddImage( R.drawable.menu_setting, -1 );
    mBSetting = new KinButton( iSetting );
    mBSetting.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        DrawSurface.GetInstance().SetPage( CConstant.PAGEMEMBER );
      }
    } );
    KinImage iCreate = new KinImage();
    iCreate.AddImage( R.drawable.menu_create, -1 );
    mBCreate = new KinButton( iCreate );
    mBCreate.SetAlignment( Alignment.LEFT, Alignment.TOP );
    mBCreate.SetSizePercent( 0.5, 0.125 ); // 設定Create大小(百分比)
    mBCreate.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        DrawSurface.GetInstance().SetPage( CConstant.PAGENEW );
      }
    } );
    KinImage iLogin = new KinImage();
    iLogin.AddImage( R.drawable.menu_login, -1 );
    mBLogin = new KinButton( iLogin );
    mBLogin.SetAlignment( Alignment.RIGHT, Alignment.TOP );
    mBLogin.SetSizePercent( 0.5, 0.125 ); // 設定Create大小(百分比)
    mBLogin.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder( Main.sInstance );
        builder.setTitle( "Login" );
        final LinearLayout view = new LinearLayout( Main.sInstance );
        view.setOrientation( LinearLayout.VERTICAL );
        final EditText inputAccount = new EditText( Main.sInstance );
        inputAccount.setHint( "Account..." );
        inputAccount.setInputType( InputType.TYPE_CLASS_TEXT );
        inputAccount.setTypeface( Typeface.SERIF );
        final EditText inputPasswd = new EditText( Main.sInstance );
        inputPasswd.setHint( "Password..." );
        inputPasswd.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        inputPasswd.setTypeface( Typeface.SERIF );

        view.addView( inputAccount );
        view.addView( inputPasswd );
        builder.setView( view );
        builder.setPositiveButton( "Login", new DialogInterface.OnClickListener() {
          @Override
          public void onClick( DialogInterface dialog, int which ) {
            Toast.makeText( Main.sInstance, "Login Failed!!", Toast.LENGTH_LONG ).show();
          }
        } );
        builder.setNeutralButton( "Register", new DialogInterface.OnClickListener() {
          @Override
          public void onClick( DialogInterface dialog, int which ) {
            LayoutInflater inflater = LayoutInflater.from( Main.sInstance );
            View register = inflater.inflate( R.layout.register, null );
            AlertDialog.Builder builder = new AlertDialog.Builder( Main.sInstance );
            builder.setTitle( "Register" );
            builder.setView( register );
            builder.setPositiveButton( "Register", new DialogInterface.OnClickListener() {
              @Override
              public void onClick( DialogInterface dialog, int which ) {
              }
            } );
            builder.show();
          }
        } );
        builder.show();
      }
    } );
    mScroll = new KinScroll();
    mScroll.SetBackground( Color.argb( 88, 255, 255, 255 ) );
    mScroll.GetLayout().AddChild( mBCreate );
    mScroll.GetLayout().AddChild( mBLogin );
    AddChild( mBackground );
    AddChild( mTitle );
    AddChild( mBRefresh );
    AddChild( mBSearch );
    AddChild( mBSetting );
    AddChild( mScroll );
  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    SetPos( 0, 0, (int) windowWidth, (int) windowHeight );
    int bHeight = (int) ( windowHeight * 0.25 );
    int bWidth = (int) ( bHeight / 300.0 * 680.0 );
    int bl = (int) ( ( windowWidth - bWidth ) / 2 );
    int br = (int) ( windowWidth - bl );
    mBRefresh.SetPos( bl, (int) ( bHeight * 0.7 ), (int) ( bl + bHeight * 0.25 ), (int) ( bHeight * 0.95 ) );
    mBSearch.SetPos( (int) ( bl + bHeight * 0.35 ), (int) ( bHeight * 0.70 ), (int) ( bl + bHeight * 0.6 ), (int) ( bHeight * 0.95 ) );
    mBSetting.SetPos( (int) ( br - bHeight * 0.25 ), (int) ( windowWidth - br ), br, (int) ( windowWidth - br + bHeight * 0.25 ) );
    mScroll.SetPos( (int) bl, (int) bHeight, (int) br, (int) ( windowHeight - bl ) );
  }

  @Override
  public void onStart( IUI from ) {
    mScroll.SetScroll( 0 );
    mHasUpdate = true;
  }

  @Override
  public void onQuit( IUI to ) {

  }

}
