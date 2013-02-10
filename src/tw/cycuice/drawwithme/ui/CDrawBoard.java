package tw.cycuice.drawwithme.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.Main;
import tw.cycuice.drawwithme.R;
import tw.cycuice.drawwithme.protocal.Action;
import tw.cycuice.drawwithme.widget.CCanvas;
import tw.cycuice.drawwithme.widget.CSelectColor;
import tw.cycuice.drawwithme.widget.CSelectPen;
import tw.kin.android.KinImage;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.KeyEvent;

public class CDrawBoard extends KinView implements IUI {

  // TODO MiniMap
  List<Action> mActions;
  public CSelectPen mUISelectPen;
  public CSelectColor mUISelectColor;
  CCanvas mUICanvas;
  KinButton mBSetting;
  KinButton mBSelectColor;
  KinButton mBCamera;
  KinImage mTopbarBG;

  public CDrawBoard() {
  }

  @Override
  public void Draw( Canvas canvas ) {
    mHasUpdate = false;
    while ( !mActions.isEmpty() ) {
      Action exe = mActions.remove( 0 );
      mUICanvas.Apply( exe );
    }
    mTopbarBG.Draw( canvas, 0, 0 );
    super.Draw( canvas );
  }

  @Override
  public void LoadContent() {
    mActions = Collections.synchronizedList( new LinkedList<Action>() );
    mUISelectPen = new CSelectPen();
    mUISelectPen.LoadContent();
    mUISelectColor = new CSelectColor();
    mUISelectColor.LoadContent();
    mUICanvas = new CCanvas();
    KinImage imgSetting = new KinImage();
    imgSetting.AddImage( R.drawable.setting, -1 );
    mBSetting = new KinButton( imgSetting );
    mBSetting.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        if ( mUISelectPen.IsVisible() )
          mUISelectPen.Hide();
        else
          mUISelectPen.Show();
      }
    } );

    KinImage imgSelectColor = new KinImage();
    imgSelectColor.AddImage( R.drawable.select_color, -1 );
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
    imgCamera.AddImage( R.drawable.camera, -1 );
    mBCamera = new KinButton( imgCamera );
    mBCamera.SetOnClickRun( new Runnable() {

      @Override
      public void run() {
        mUICanvas.Save();
      }

    } );

    mTopbarBG = new KinImage();
    mTopbarBG.AddImage( R.drawable.topbar_bg, -1 );

    AddChild( mBCamera );
    AddChild( mBSetting );
    AddChild( mBSelectColor );
    AddChild( mUICanvas );
    AddChild( mUISelectPen );
    AddChild( mUISelectColor );
    mHasUpdate = true;

  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    int bSize = (int) ( windowHeight * ( windowHeight * ( -1 / 28800.0 ) + ( 1 / 9.0 ) ) );
    mBSetting.SetPos( 0, 0, bSize, bSize );
    mBSelectColor.SetPos( bSize, 0, bSize * 2, bSize );
    mBCamera.SetPos( bSize * 2, 0, bSize * 3, bSize );
    mTopbarBG.SetSize( windowWidth, bSize );
    mUISelectColor.CompatibleWith( windowWidth, windowHeight );
    mUISelectPen.CompatibleWith( windowWidth, windowHeight );
    mUICanvas.CompatibleWith( windowWidth, windowHeight );
    mHasUpdate = true;

  }

  @Override
  public boolean HasUpdate() {
    if ( !mActions.isEmpty() )
      return true;
    return super.HasUpdate();
  }

  public void Submit( Action newAction ) {
    mActions.add( newAction );

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
    }
    return false;

  }
}
