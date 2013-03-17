package tw.ome.drawwithme.ui;

import tw.kin.android.layout.KinAbsoluteLayout;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.ome.drawwithme.CConstant;
import tw.ome.drawwithme.DrawSurface;
import tw.ome.drawwithme.Main;
import tw.ome.drawwithme.R;
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

  public CDrawBoard() {
  }

  public void NewCanvas( int width, int height, int bgColor, IActionCotroller mode ) {
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
    KinImage imgSelectPen = new KinImage();
    imgSelectPen.AddImage( Main.lib.GetBitmap( R.drawable.board_selectpen ), -1 );
    mBSelectPen = new KinButton( imgSelectPen );
    mBSelectPen.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        if ( mUISelectPen.IsVisible() )
          mUISelectPen.Hide();
        else
          mUISelectPen.Show();
      }
    } );

    KinImage imgSelectColor = new KinImage();
    imgSelectColor.AddImage( Main.lib.GetBitmap( R.drawable.board_selectcolor ), -1 );
    mBSelectColor = new KinButton( imgSelectColor );
    mBSelectColor.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        if ( mUISelectColor.IsVisible() )
          mUISelectColor.Hide();
        else
          mUISelectColor.Show();
      }
    } );

    KinImage imgCamera = new KinImage();
    imgCamera.AddImage( Main.lib.GetBitmap( R.drawable.board_camera ), -1 );
    mBCamera = new KinButton( imgCamera );
    mBCamera.SetOnClickRun( new Runnable() {

      @Override
      public void run() {
        mUICanvas.Save();
      }

    } );

    KinImage imgSetting = new KinImage();
    imgSetting.AddImage( Main.lib.GetBitmap( R.drawable.board_setting ), -1 );
    mBSetting = new KinButton( imgSetting );
    mBSetting.SetOnClickRun( new Runnable() {

      @Override
      public void run() {
      }
    } );

    KinImage imgDialogBox = new KinImage();
    imgDialogBox.AddImage( Main.lib.GetBitmap( R.drawable.board_dialogbox ), -1 );
    mBChat = new KinButton( imgDialogBox );
    mBChat.SetOnClickRun( new Runnable() {

      @Override
      public void run() {
        ToggleChat();
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
