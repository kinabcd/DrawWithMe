package tw.cycuice.drawwithme.ui;

import tw.cycuice.drawwithme.CConstant;
import tw.cycuice.drawwithme.DrawSurface;
import tw.cycuice.drawwithme.R;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinButton;
import tw.kin.android.widget.KinImage;
import tw.kin.android.widget.KinScroll;
import android.graphics.Color;

public class CMenu extends KinView implements IUI {
  KinImage mBackground;
  KinImage mTitle;
  KinButton mBRefresh;
  KinButton mBSearch;
  KinButton mBSetting;
  KinButton mBCreate;
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
        // TODO pop searching view
      }
    } );
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
    iNew.AddImage( R.drawable.menu_create, -1 );
    mBCreate = new KinButton( iNew );
    mBCreate.SetAlignment( Alignment.LEFT, Alignment.TOP );
    mBCreate.SetSizePercent( 0.5, 0.125 ); // 設定Create大小(百分比)
    mBCreate.SetOnClickRun( new Runnable() {
      @Override
      public void run() {
        DrawSurface.GetInstance().SetPage( CConstant.PAGENEW );
      }
    } );
    mScroll = new KinScroll();
    mScroll.SetBackground( Color.argb( 88, 255, 255, 255 ) );
    mScroll.GetLayout().AddChild( mBCreate );
    mScroll.SetAlignment( Alignment.DEPENDENT_PARENT, Alignment.DEPENDENT_PARENT );
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
    mHasUpdate = true;
  }

  @Override
  public void onQuit( IUI to ) {

  }

}
