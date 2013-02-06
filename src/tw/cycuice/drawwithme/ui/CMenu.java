package tw.cycuice.drawwithme.ui;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.R;
import tw.kin.android.KinImage;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import android.graphics.Canvas;

public class CMenu extends KinView implements IUI {
  KinImage mBackground;
  KinImage mTitle;
  KinImage mInternet;
  KinImage mSingle;
  KinImage mBluetooth;
  KinButton mBSingle;
  KinButton mBInternet;
  KinButton mBBluetooth;

  public CMenu() {
    super();
  }

  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( R.drawable.menu_bg, -1 );
    mTitle = new KinImage();
    mTitle.AddImage( R.drawable.title, -1 );

    mInternet = new KinImage();
    mInternet.AddImage( R.drawable.internet, -1 );
    mBInternet = new KinButton( mInternet );
    mSingle = new KinImage();
    mSingle.AddImage( R.drawable.single, -1 );
    mBSingle = new KinButton( mSingle );
    mBSingle.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        DrawSurface.GetInstance().SetPage( CConstant.PAGENEW );
      }
    } );
    mBluetooth = new KinImage();
    mBluetooth.AddImage( R.drawable.bluetooth, -1 );
    mBBluetooth = new KinButton( mBluetooth );
    AddChild( mBInternet );
    AddChild( mBSingle );
    AddChild( mBBluetooth );
  }

  @Override
  public void Draw( Canvas canvas ) {
    mBackground.Draw( canvas, 0, 0 );
    mTitle.Draw( canvas, 0, 0 );
    super.Draw( canvas );

  }

  @Override
  public void CompatibleWith( double windowWidth, double windowHeight ) {
    int bHeight = (int) ( windowHeight * 0.25 );
    int bWidth = (int) ( bHeight / 300.0 * 680.0 );
    int bl = (int) ( ( windowWidth - bWidth ) / 2 );
    int br = (int) ( windowWidth - bl );
    mBackground.SetSize( windowWidth, windowHeight ); // 設定背景大小
    mTitle.SetSize( windowWidth * 0.95, windowHeight * 0.25 ); // 設定背景大小
    mBSingle.SetPos( bl, bHeight, br, bHeight * 2 );
    mBBluetooth.SetPos( bl, bHeight * 2, br, bHeight * 3 );
    mBInternet.SetPos( bl, bHeight * 3, br, bHeight * 4 );

  }

  @Override
  public void onStart( IUI from ) {
    mHasUpdate = true;
    
  }

  @Override
  public void onQuit( IUI to ) {
    
  }

}
