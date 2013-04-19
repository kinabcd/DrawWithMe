package tw.ome.drawwithme.ui;

import tw.kin.android.layout.KinAbsoluteLayout;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.ome.drawwithme.CConstant;
import tw.ome.drawwithme.DrawSurface;
import tw.ome.drawwithme.Main;
import tw.ome.drawwithme.R;
import tw.ome.drawwithme.protocal.CModeInternet;
import tw.ome.drawwithme.protocal.CModeSingle;
import tw.ome.drawwithme.protocal.IActionCotroller;
import tw.ome.drawwithme.widget.CCanvas;
import tw.ome.drawwithme.widget.CChat;
import tw.ome.drawwithme.widget.CSelectColor;
import tw.ome.drawwithme.widget.CSelectPen;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;

public class CDrawBoard extends KinAbsoluteLayout implements IUI {

  CSelectPen mUISelectPen;
  CSelectColor mUISelectColor;
  CChat mUIChat;
  CCanvas mUICanvas;
  KinButton mBSelectPen;
  KinButton mBSelectColor;
  KinButton mBCamera;
  KinButton mBSetting;
  KinButton mBChat;
  KinImage mTopbarBG;
  boolean mIsOnline;

  public CDrawBoard() {
  }

  public void NewCanvas( int width, int height, int bgColor, boolean internet ) {
    IActionCotroller mode;
    mIsOnline = internet;
    if ( internet )
      mode = CModeInternet.GetClient();
    else
      mode = new CModeSingle();
    mUICanvas.New( width, height, bgColor, mode );
  }

  public int GetPen() {
    return mUISelectPen.GetPen();
  }

  public int GetPenSize() {
    return mUISelectPen.GetSize();
  }

  public int GetPenColor() {
    return mUISelectColor.GetColor();
  }

  @Override
  public void LoadContent() {
    mUISelectPen = new CSelectPen();
    mUISelectColor = new CSelectColor();
    mUIChat = new CChat();
    mUICanvas = new CCanvas();

    mBSelectPen = new KinButton();
    mBSelectPen.AddImage( Main.lib.GetBitmap( R.drawable.board_selectpen ), -1 );
    mBSelectPen.AddImage( Main.lib.GetBitmap( R.drawable.board_selectpen2 ), -1 );
    mBSelectPen.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBSelectPen.SetFrame( 0 );
        if ( mUISelectPen.IsVisible() )
          mUISelectPen.Hide();
        else
          mUISelectPen.Show();
      }
    } );
    mBSelectPen.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBSelectPen.SetFrame( 1 );
      }
    } );

    mBSelectColor = new KinButton();
    mBSelectColor.AddImage( Main.lib.GetBitmap( R.drawable.board_selectcolor ), -1 );
    mBSelectColor.AddImage( Main.lib.GetBitmap( R.drawable.board_selectcolor2 ), -1 );
    mBSelectColor.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBSelectColor.SetFrame( 0 );
        if ( mUISelectColor.IsVisible() )
          mUISelectColor.Hide();
        else
          mUISelectColor.Show();
      }
    } );
    mBSelectColor.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBSelectColor.SetFrame( 1 );
      }
    } );

    mBCamera = new KinButton();
    mBCamera.AddImage( Main.lib.GetBitmap( R.drawable.board_camera ), -1 );
    mBCamera.AddImage( Main.lib.GetBitmap( R.drawable.board_camera2 ), -1 );
    mBCamera.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBCamera.SetFrame( 0 );
        mUICanvas.Save();
      }

    } );
    mBCamera.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBCamera.SetFrame( 1 );
      }
    } );

    mBSetting = new KinButton();
    mBSetting.AddImage( Main.lib.GetBitmap( R.drawable.board_setting ), -1 );
    mBSetting.AddImage( Main.lib.GetBitmap( R.drawable.board_setting2 ), -1 );
    mBSetting.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBSetting.SetFrame( 0 );
      }
    } );
    mBSetting.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBSetting.SetFrame( 1 );
      }
    } );

    mBChat = new KinButton();
    mBChat.AddImage( Main.lib.GetBitmap( R.drawable.board_dialogbox ), -1 );
    mBChat.AddImage( Main.lib.GetBitmap( R.drawable.board_dialogbox2 ), -1 );
    mBChat.SetOnUpRun( new Runnable() {
      @Override
      public void run() {
        mBChat.SetFrame( 0 );
        ToggleChat();
      }
    } );
    mBChat.SetOnDownRun( new Runnable() {
      @Override
      public void run() {
        mBChat.SetFrame( 1 );
      }
    } );

    mTopbarBG = new KinImage();
    mTopbarBG.AddImage( Main.lib.GetBitmap( R.drawable.board_topbar_bg ), -1 );
    SetAlignment( mTopbarBG, Alignment.LEFT, Alignment.TOP );

    AddChild( mTopbarBG );
    AddChild( mBChat );
    AddChild( mBSetting );
    AddChild( mBCamera );
    AddChild( mBSelectPen );
    AddChild( mBSelectColor );
    AddChild( mUICanvas );
    AddChild( mUISelectPen );
    AddChild( mUISelectColor );
    AddChild( mUIChat );
    RequireRedraw();

  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    SetPos( 0, 0, (int) windowWidth, (int) windowHeight );
    int bSize = (int) ( windowHeight * ( windowHeight * ( -1 / 28800.0 ) + ( 1 / 9.0 ) ) );
    mBSelectPen.SetPos( 0, 0, bSize, bSize );
    mBSelectColor.SetPos( bSize, 0, bSize * 2, bSize );
    mBCamera.SetPos( bSize * 2, 0, bSize * 3, bSize );
    mBSetting.SetPos( bSize * 3, 0, bSize * 4, bSize );
    mBChat.SetPos( bSize * 4, 0, bSize * 5, bSize );
    mTopbarBG.SetSize( (int) windowWidth, bSize );
    mUIChat.CompatibleWith( windowWidth, windowHeight );
    mUISelectColor.CompatibleWith( windowWidth, windowHeight );
    mUISelectPen.CompatibleWith( windowWidth, windowHeight );
    mUICanvas.CompatibleWith( windowWidth, windowHeight );
    RequireRedraw();

  }

  @Override
  public void onStart( IUI from ) {
    mUIChat.Hide();
    mUISelectColor.SetColor( Color.BLACK );
    mUISelectColor.Hide();
    mUISelectPen.SetPen( CConstant.PENNORMAL );
    mUISelectPen.SetSize( 10 );
    mUISelectPen.Hide();
    RequireRedraw();

  }

  @Override
  public void onQuit( IUI to ) {

  }

  @Override
  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( super.onKeyDown( keycode, event ) )
      return true;
    if ( keycode == KeyEvent.KEYCODE_BACK ) {
      if ( mUIChat.IsVisible() ) {
        ToggleChat();
        return true;
      }
      AlertDialog.Builder ad = new AlertDialog.Builder( Main.sInstance );
      ad.setTitle( "Quit" );
      ad.setMessage( "Do you want to leave?" );
      ad.setNegativeButton( "No", new DialogInterface.OnClickListener() {
        public void onClick( DialogInterface dialog, int which ) {
        }
      } );

      ad.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
        public void onClick( DialogInterface dialog, int whichButton ) {
          DrawSurface.GetInstance().SetPage( CConstant.PAGEMENU );
          if ( mIsOnline )
            CModeInternet.LeaveRoom();
        }
      } );
      ad.setCancelable( true );
      ad.show();
      return true;
    }
    return false;
  }

  public void ToggleChat() {
    if ( mUIChat.IsVisible() ) {
      mUIChat.Hide();
      mBSelectPen.SetVisible( true );
      mBSelectColor.SetVisible( true );
      mBCamera.SetVisible( true );
      mBSetting.SetVisible( true );
      mBChat.SetVisible( true );
    } else {
      mUIChat.Show();
      mBSelectPen.SetVisible( false );
      mBSelectColor.SetVisible( false );
      mBCamera.SetVisible( false );
      mBSetting.SetVisible( false );
      mBChat.SetVisible( false );
    }
  }
}
