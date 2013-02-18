package tw.cycuice.drawwithme.ui;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.R;
import tw.kin.android.KinImage;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

public class CMenu extends KinView implements IUI {
  KinImage mBackground;
  KinImage mTitle;
  KinButton mBRefresh;
  KinButton mBSearch;
  KinButton mBSetting;
  KinButton mBNew;
  ScrollView mScroll;

  public CMenu() {
    super();
  }

  public void LoadContent() {
    mBackground = new KinImage();
    mBackground.AddImage( R.drawable.menu_bg, -1 );
    mTitle = new KinImage();
    mTitle.AddImage( R.drawable.menu_title, -1 );

    KinImage iRefresh = new KinImage();
    iRefresh.AddImage( R.drawable.menu_refresh, -1 );
    mBRefresh = new KinButton( iRefresh );
    KinImage iSearch = new KinImage();
    iSearch.AddImage( R.drawable.menu_search, -1 );
    mBSearch = new KinButton( iSearch );
    KinImage iSetting = new KinImage();
    iSetting.AddImage( R.drawable.menu_setting, -1 );
    mBSetting = new KinButton( iSetting );
    mBSetting.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        // TODO pop setting view
      }
    } );
    KinImage iNew = new KinImage();
    iNew.AddImage( R.drawable.menu_setting, -1 );
    mBNew = new KinButton( iNew );
    mBNew.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        DrawSurface.GetInstance().SetPage( CConstant.PAGENEW );
      }
    } );
    mScroll = new ScrollView();
    mScroll.SetBackground( Color.argb( 88, 255, 255, 255 ) );
    mScroll.AddChild( mBNew );
    AddChild( mBRefresh );
    AddChild( mBSearch );
    AddChild( mBSetting );
    AddChild( mScroll );
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
    mBRefresh.SetPos( bl, (int) ( bHeight * 0.75 ), (int) ( bl + bHeight * 0.25 ), bHeight );
    mBSearch.SetPos( (int) ( bl + bHeight * 0.35 ), (int) ( bHeight * 0.75 ), (int) ( bl + bHeight * 0.6 ), bHeight );
    mBSetting.SetPos( (int) ( br - bHeight * 0.25 ), (int) ( windowWidth - br ), br, (int) ( windowWidth - br + bHeight * 0.25 ) );
    mScroll.SetPos( (int) bl, (int) bHeight, (int) br, (int) ( windowHeight - bl ) );
    mBNew.SetPos( 0, 0, (int) ( windowWidth * 0.9 ), (int) ( windowWidth * 0.125 ) );
  }

  @Override
  public void onStart( IUI from ) {
    mHasUpdate = true;

  }

  @Override
  public void onQuit( IUI to ) {

  }

}
