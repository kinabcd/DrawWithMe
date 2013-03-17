package tw.ome.drawwithme.ui;

import tw.kin.android.layout.KinAbsoluteLayout;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinLable;
import tw.kin.android.widget.KinScroll;
import tw.ome.drawwithme.CConstant;
import tw.ome.drawwithme.DrawSurface;
import tw.ome.drawwithme.Main;
import tw.ome.drawwithme.R;
import tw.ome.drawwithme.protocal.CModeInternet;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CMenu extends KinAbsoluteLayout implements IUI {
  class Room extends KinAbsoluteLayout {
    boolean mLock;
    int mId;
    KinImage mLockImg = new KinImage();
    KinLable mLableNum = new KinLable();
    KinLable mLableId = new KinLable();
    KinLable mLableName = new KinLable();
    KinButton mButton = new KinButton();

    public Room(int id, String name, boolean lock, int peopleNum) {
      SetSizePercent( 1, 0.2 );
      mId = id;
      mLock = lock;
      if ( lock )
        mLockImg.AddImage( Main.lib.GetBitmap( R.drawable.chat_lock ), -1 );
      mLockImg.SetSizePercent( 0.1, 0.5 );
      mLockImg.SetFit( KinImage.SCALE );
      mLableId.SetText( "No." + id );
      mLableId.SetSizePercent( 0.9, 0.4444 );
      mLableId.SetTypeface( CConstant.TFShowFong );
      mLableId.SetTextColor( 0xff000088 );
      mLableId.SetBold( true );
      mLableName.SetText( name );
      mLableName.SetSizePercent( 0.95, 0.5 );
      mLableName.SetTypeface( Typeface.SERIF );
      mLableNum.SetText( peopleNum + "/" + 8 );
      mLableNum.SetSizePercent( 0.2, 0.25 );
      mLableNum.SetTextColor( 0xff000088 );
      mLableNum.SetBold( true );
      mButton.SetSizePercent( 1, 1 );
      mButton.SetOnClickRun( new Runnable() {
        @Override
        public void run() {
          AlertDialog.Builder builder = new AlertDialog.Builder( Main.sInstance );
          builder.setTitle( "Search" );
          final LinearLayout view = new LinearLayout( Main.sInstance );
          view.setOrientation( LinearLayout.VERTICAL );
          final EditText inputpass = new EditText( Main.sInstance );
          if ( mLock ) {
            inputpass.setHint( "Passwork..." );
            inputpass.setInputType( InputType.TYPE_TEXT_VARIATION_PASSWORD );
            inputpass.setTypeface( Typeface.SERIF );
            view.addView( inputpass );
          }
          builder.setView( view );
          builder.setPositiveButton( "Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
              String roomPassword = "";
              if ( mLock )
                roomPassword = inputpass.getText().toString();
              CModeInternet.JoinRoom( mId, roomPassword );
            }
          } );
          builder.show();
        }
      } );
      SetAlignment( mLableId, Alignment.RIGHT, Alignment.TOP );
      SetAlignment( mLockImg, Alignment.LEFT, Alignment.TOP );
      SetAlignment( mLableName, Alignment.RIGHT, Alignment.BOTTOM );
      SetAlignment( mLableNum, Alignment.RIGHT, Alignment.TOP );
      AddChild( mLableNum );
      AddChild( mLableId );
      AddChild( mLableName );
      AddChild( mLockImg );
      AddChild( mButton );
    }
  }

  boolean roomgroup;
  KinImage mBackground;
  KinImage mTitle;
  KinButton mBRefresh;
  KinButton mBSearch;
  KinButton mBSetting;
  KinButton mBCreate;
  KinButton mBLogin;
  KinScroll mScroll;
  KinAbsoluteLayout mButtonBar;
  int mLastSearch;
  String mLastSearchStr;

  public CMenu() {
    super();
  }

  public void AddRoom( int id, String name, boolean lock, int peopleNum ) {
    Room newRoom = new Room( id, name, lock, peopleNum );
    mScroll.GetLayout().AddChild( newRoom );
    if ( roomgroup ) {
      newRoom.SetBackground( 0x80eedddd );
    } else {
      newRoom.SetBackground( 0x80ffffee );
    }
    roomgroup = !roomgroup;
  }

  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( Main.lib.GetBitmap( R.drawable.menu_bg ), -1 );
    mBackground.SetSizePercent( 1, 1 );
    mTitle = new KinImage();
    mTitle.AddImage( Main.lib.GetBitmap( R.drawable.menu_title ), -1 );
    mTitle.SetSizePercent( 0.95, 0.25 ); // 設定標題大小(百分比)
    SetAlignment( mTitle, Alignment.CENTER, Alignment.TOP );

    KinImage iRefresh = new KinImage();
    iRefresh.AddImage( Main.lib.GetBitmap( R.drawable.menu_refresh ), -1 );
    mBRefresh = new KinButton( iRefresh );
    mBRefresh.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO refresh menu
        mScroll.GetLayout().CleanChild();
        mScroll.GetLayout().AddChild( mButtonBar );
        CModeInternet.Search( mLastSearch, mLastSearchStr );
      }
    } );
    KinImage iSearch = new KinImage();
    iSearch.AddImage( Main.lib.GetBitmap( R.drawable.menu_search ), -1 );
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
        checkNo.setText( "Room No." );
        final CheckBox checkName = new CheckBox( Main.sInstance );
        checkName.setChecked( true );
        checkName.setText( "Room Name" );
        checkLayout.addView( checkNo );
        checkLayout.addView( checkName );
        view.addView( inputKeyword );
        view.addView( checkLayout );
        builder.setView( view );
        builder.setPositiveButton( "Search", new DialogInterface.OnClickListener() {
          @Override
          public void onClick( DialogInterface dialog, int which ) {
            mScroll.GetLayout().CleanChild();
            mScroll.GetLayout().AddChild( mButtonBar );
            if ( checkNo.isChecked() && !checkName.isChecked() ) {
              CModeInternet.Search( 0x01, inputKeyword.getText().toString() );
              mLastSearch = 0x01;
            } else if ( !checkNo.isChecked() && checkName.isChecked() ) {
              CModeInternet.Search( 0x02, inputKeyword.getText().toString() );
              mLastSearch = 0x02;
            } else if ( checkNo.isChecked() && checkName.isChecked() ) {
              CModeInternet.Search( 0x03, inputKeyword.getText().toString() );
              mLastSearch = 0x03;
            }

            mLastSearchStr = inputKeyword.getText().toString();
          }
        } );
        builder.show();
      }
    } );
    KinImage iSetting = new KinImage();
    iSetting.AddImage( Main.lib.GetBitmap( R.drawable.menu_setting ), -1 );
    mBSetting = new KinButton( iSetting );
    mBSetting.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        if ( CModeInternet.IsLogin() )
          DrawSurface.GetInstance().SetPage( CConstant.PAGEMEMBER );
      }
    } );
    KinImage iCreate = new KinImage();
    iCreate.AddImage( Main.lib.GetBitmap( R.drawable.menu_create ), -1 );
    mBCreate = new KinButton( iCreate );
    mBCreate.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        DrawSurface.GetInstance().SetPage( CConstant.PAGENEW );
      }
    } );
    KinImage iLogin = new KinImage();
    iLogin.AddImage( Main.lib.GetBitmap( R.drawable.menu_login ), -1 );
    mBLogin = new KinButton( iLogin );
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
        inputAccount.setFilters( new InputFilter[] { CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter( 16 ) } );
        final EditText inputPasswd = new EditText( Main.sInstance );
        inputPasswd.setHint( "Password..." );
        inputPasswd.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        inputPasswd.setTypeface( Typeface.SERIF );
        inputPasswd.setFilters( new InputFilter[] { CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter( 16 ) } );
        final CheckBox checkRemember = new CheckBox( Main.sInstance );
        checkRemember.setChecked( true );
        checkRemember.setText( "Remember me" );

        view.addView( inputAccount );
        view.addView( inputPasswd );
        view.addView( checkRemember );
        builder.setView( view );
        builder.setPositiveButton( "Login", new DialogInterface.OnClickListener() {
          @Override
          public void onClick( DialogInterface dialog, int which ) {
            CModeInternet.Login( inputAccount.getText().toString(), inputPasswd.getText().toString() );
            mScroll.GetLayout().CleanChild();
            mScroll.GetLayout().AddChild( mButtonBar );
            CModeInternet.Search( 0x03, "" );
            mLastSearch = 0x03;
            mLastSearchStr = new String( "" );
          }
        } );
        builder.setNeutralButton( "Register", new DialogInterface.OnClickListener() {
          @Override
          public void onClick( DialogInterface dialog, int which ) {
            LayoutInflater inflater = LayoutInflater.from( Main.sInstance );
            final View register = inflater.inflate( R.layout.register, null );
            AlertDialog.Builder builder = new AlertDialog.Builder( Main.sInstance );
            builder.setTitle( "Register" );
            builder.setView( register );
            builder.setPositiveButton( "Register", new DialogInterface.OnClickListener() {
              @Override
              public void onClick( DialogInterface dialog, int which ) {
                EditText etAccount = (EditText) register.findViewById( R.id.editTextAccount );
                etAccount.setFilters( new InputFilter[] { CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter( 16 ) } );
                String account = new String( etAccount.getText().toString() );

                EditText etPassword = (EditText) register.findViewById( R.id.editTextPassword );
                etPassword.setFilters( new InputFilter[] { CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter( 16 ) } );
                String password = new String( etPassword.getText().toString() );

                EditText etConfirm = (EditText) register.findViewById( R.id.editTextConfirm );
                etConfirm.setFilters( new InputFilter[] { CConstant.ACCOUNTFILTER, new InputFilter.LengthFilter( 16 ) } );
                String confirm = new String( etConfirm.getText().toString() );

                EditText etNickname = (EditText) register.findViewById( R.id.editTextNickname );
                etNickname.setFilters( new InputFilter[] { CConstant.NICKNAMEFILTER, new InputFilter.LengthFilter( 16 ) } );
                String nickname = new String( etNickname.getText().toString() );

                if ( password.equals( confirm ) )
                  CModeInternet.Register( account, nickname, password );
                else
                  ;// TODO show confirm error
              }
            } );
            builder.show();
          }
        } );
        builder.show();
      }
    } );
    mButtonBar = new KinAbsoluteLayout();
    mButtonBar.AddChild( mBCreate );
    mButtonBar.AddChild( mBLogin );
    mButtonBar.SetAlignment( mBCreate, Alignment.LEFT, Alignment.TOP );
    mButtonBar.SetAlignment( mBLogin, Alignment.RIGHT, Alignment.TOP );
    mScroll = new KinScroll();
    mScroll.GetLayout().AddChild( mButtonBar );
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
    mBCreate.SetSizePercent( 0.5, 1 ); // 設定Create大小(百分比)
    mBLogin.SetSizePercent( 0.5, 1 ); // 設定Create大小(百分比)
    mButtonBar.SetSize( bWidth, windowHeight / 8 );
  }

  @Override
  public void onStart( IUI from ) {
    mScroll.SetScroll( 0 );
    RequireRedraw();
  }

  @Override
  public void onQuit( IUI to ) {

  }

  @Override
  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( keycode == KeyEvent.KEYCODE_BACK ) {
      Main.sInstance.finish();
      return true;
    }
    return super.onKeyDown( keycode, event );
  }
}
