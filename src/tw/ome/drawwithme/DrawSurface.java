package tw.ome.drawwithme;

import tw.kin.android.KinDrawTable;
import tw.kin.android.KinView;
import tw.kin.android.widget.KinImage;
import tw.ome.drawwithme.protocal.CModeInternet;
import tw.ome.drawwithme.ui.CDrawBoard;
import tw.ome.drawwithme.ui.CMemberCenter;
import tw.ome.drawwithme.ui.CMenu;
import tw.ome.drawwithme.ui.CNew;
import tw.ome.drawwithme.ui.IUI;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Looper;
import android.view.SurfaceHolder;

public class DrawSurface extends KinDrawTable {
  int mPageStatus;
  private int mWindowWidth;
  private int mWindowHeight;
  int ViewWidth;
  int ViewHeight;
  IUI mUIs;
  CMemberCenter mUIMemberCenter;
  public CDrawBoard mUICanvas;
  public CMenu mUIMenu;
  public CNew mUINew;
  static DrawSurface sInstance = null;
  CModeInternet mClient;

  public static DrawSurface GetInstance() {
    if ( sInstance == null )
      sInstance = new DrawSurface( Main.sInstance );
    return sInstance;
  }

  public DrawSurface(Context context) {
    super( context );
    mPageStatus = CConstant.NOTLOADING;

  }

  public void SetPage( int i ) {
    if ( mPageStatus == i )
      return;
    IUI oldui = mUIs;
    mPageStatus = i;
    if ( mPageStatus == CConstant.PAGEMENU ) {
      mUIs = mUIMenu;
    } else if ( mPageStatus == CConstant.PAGENEW ) {
      mUIs = mUINew;
    } else if ( mPageStatus == CConstant.PAGECANVAS ) {
      mUIs = mUICanvas;
    } else if ( mPageStatus == CConstant.PAGEMEMBER ) {
      mUIs = mUIMemberCenter;
    }
    if ( oldui != null )
      oldui.onQuit( mUIs );
    mUIs.onStart( oldui );
    CleanChild();
    AddChild( (KinView) mUIs );
  }

  @Override
  public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
    super.surfaceChanged( holder, format, width, height );
    System.out.println( width + "x" +height);
    mWindowWidth = width;
    mWindowHeight = height;
    if ( LoadContent != null )
      LoadContent.start();
    else
      Resize( mWindowWidth, mWindowHeight );

  }

  Thread LoadContent = new Thread() {
    public void run() {
      Looper.prepare();
      CConstant.TFShowFong = Typeface.createFromAsset( Main.sInstance.getAssets(), "showfong.ttc" );
      KinImage mBackground = new KinImage();
      mBackground.AddImage( Main.lib.GetBitmap( R.drawable.menu_bg ), -1 );
      mBackground.SetPos( 0, 0, mWindowWidth, mWindowHeight );
      KinImage mTitle = new KinImage();
      mTitle.AddImage( Main.lib.GetBitmap( R.drawable.menu_title ), -1 );
      mTitle.SetSize( mWindowWidth * 0.95, mWindowHeight * 0.25 );
      mTitle.SetPos( ( mWindowWidth - mTitle.GetWidth() ) / 2, ( mWindowHeight - mTitle.GetHeight() ) / 2 );

      AddChild( mBackground );
      AddChild( mTitle );
      mUIMenu = new CMenu();
      mUINew = new CNew();
      mUICanvas = new CDrawBoard();
      mUIMemberCenter = new CMemberCenter();
      mUIMenu.LoadContent();
      mUINew.LoadContent();
      mUICanvas.LoadContent();
      mUIMemberCenter.LoadContent();
      Resize( mWindowWidth, mWindowHeight );
      try {
        Thread.sleep( 500 );
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      SetPage( CConstant.PAGEMENU );
      LoadContent = null; // 讀取後自動銷毀
    }
  };

  public void Resize( double windowWidth, double windowHeight ) {
    mUIMenu.CompatibleWith( windowWidth, windowHeight );
    mUINew.CompatibleWith( windowWidth, windowHeight );
    mUICanvas.CompatibleWith( windowWidth, windowHeight );
    mUIMemberCenter.CompatibleWith( windowWidth, windowHeight );

  }

}
