package tw.cycuice.drawwithme.ui;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.Main;
import tw.cycuice.drawwithme.R;
import tw.cycuice.drawwithme.widget.CCanvas;
import tw.cycuice.drawwithme.widget.CSelectColor;
import tw.cycuice.drawwithme.widget.CSelectPen;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;

public class CDrawBoard extends KinView implements IUI {

  public CSelectPen mUISelectPen;
  public CSelectColor mUISelectColor;
  CCanvas mUICanvas;
  KinButton mBSelectPen;
  KinButton mBSelectColor;
  KinButton mBCamera;
  KinButton mBSetting;
  KinButton mBDialogBox;
  KinImage mTopbarBG;

  public CDrawBoard() {
  }

  @Override
  public void LoadContent() {
    mUISelectPen = new CSelectPen();
    mUISelectColor = new CSelectColor();
    mUICanvas = new CCanvas();
    KinImage imgSelectPen = new KinImage();
    imgSelectPen.AddImage( R.drawable.board_selectpen, -1 );
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
    imgSelectColor.AddImage( R.drawable.board_selectcolor, -1 );
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
    imgCamera.AddImage( R.drawable.board_camera, -1 );
    mBCamera = new KinButton( imgCamera );
    mBCamera.SetOnClickRun( new Runnable() {

      @Override
      public void run() {
        mUICanvas.Save();
      }

    } );

    KinImage imgSetting = new KinImage();
    imgSetting.AddImage( R.drawable.board_setting, -1 );
    mBSetting = new KinButton( imgSetting );
    mBSetting.SetOnClickRun( new Runnable() {

      @Override
      public void run() {
      }
    } );
    
    KinImage imgDialogBox = new KinImage();
    imgDialogBox.AddImage( R.drawable.board_dialogbox, -1 );
    mBDialogBox = new KinButton( imgDialogBox );
    mBDialogBox.SetOnClickRun( new Runnable() {

      @Override
      public void run() {
      }
    } );
    mTopbarBG = new KinImage();
    mTopbarBG.AddImage( R.drawable.board_topbar_bg, -1 );
    mTopbarBG.SetAlignment( Alignment.FILL, Alignment.TOP );

    AddChild( mTopbarBG );
    AddChild( mBDialogBox );
    AddChild( mBSetting );
    AddChild( mBCamera );
    AddChild( mBSelectPen );
    AddChild( mBSelectColor );
    AddChild( mUICanvas );
    AddChild( mUISelectPen );
    AddChild( mUISelectColor );
    mHasUpdate = true;

  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    SetPos( 0, 0, (int) windowWidth, (int) windowHeight );
    int bSize = (int) ( windowHeight * ( windowHeight * ( -1 / 28800.0 ) + ( 1 / 9.0 ) ) );
    mBSelectPen.SetPos( 0, 0, bSize, bSize );
    mBSelectColor.SetPos( bSize, 0, bSize * 2, bSize );
    mBCamera.SetPos( bSize * 2, 0, bSize * 3, bSize );
    mBSetting.SetPos( bSize * 3, 0, bSize * 4, bSize );
    mBDialogBox.SetPos( bSize * 4, 0, bSize * 5, bSize );
    mTopbarBG.SetSize( (int) windowWidth, bSize );
    mUISelectColor.CompatibleWith( windowWidth, windowHeight );
    mUISelectPen.CompatibleWith( windowWidth, windowHeight );
    mUICanvas.CompatibleWith( windowWidth, windowHeight );
    mHasUpdate = true;

  }

  @Override
  public void onStart( IUI from ) {
    mUISelectColor.SetColor( Color.BLACK );
    mUISelectColor.Hide();
    mUISelectPen.SetPen( CConstant.PENNORMAL );
    mUISelectPen.SetSize( 10 );
    mUISelectPen.Hide();
    mHasUpdate = true;

  }

  @Override
  public void onQuit( IUI to ) {

  }

  @Override
  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( super.onKeyDown( keycode, event ) )
      return true;
    if ( keycode == KeyEvent.KEYCODE_BACK ) {
      AlertDialog.Builder ad = new AlertDialog.Builder( Main.sInstance );
      ad.setTitle( "Quit" );
      ad.setMessage( "Do you want to save it?" );
      ad.setNegativeButton( "No", new DialogInterface.OnClickListener() {
        public void onClick( DialogInterface dialog, int which ) {
          DrawSurface.GetInstance().SetPage( CConstant.PAGEMENU );
        }
      } );

      ad.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
        public void onClick( DialogInterface dialog, int whichButton ) {
          mUICanvas.Save();
          DrawSurface.GetInstance().SetPage( CConstant.PAGEMENU );
        }
      } );
      ad.setCancelable( true );
      ad.show();
      return true;
    }
    return false;

  }
}
