package tw.ome.drawwithme;

import tw.kin.android.KinLib;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity {
  public static Main sInstance = null;
  public static KinLib lib = null;

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    lib = new KinLib();
    //getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
    requestWindowFeature( Window.FEATURE_NO_TITLE ); // 設定成No title
    lib.SetContext( this );
    getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN );
    if ( sInstance != null ) {
      ViewGroup vg = (ViewGroup) ( DrawSurface.GetInstance().getParent() );
      if ( vg != null )
        vg.removeView( DrawSurface.GetInstance() );
    }
    sInstance = this;
    setContentView( DrawSurface.GetInstance() );
    // StrictMode.setThreadPolicy( new
    // StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()
    // .penaltyLog().build() );
    // StrictMode.setVmPolicy( new
    // StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath()
    // .build() );
    if ( android.os.Build.VERSION.SDK_INT > 9 ) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy( policy );
    }
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
    // getMenuInflater().inflate( R.menu.activity_menu, menu );
    return true;
  }

  @Override
  public boolean onKeyDown( int keycode, KeyEvent event ) {
    return DrawSurface.GetInstance().onKeyDown( keycode, event );
  }

}
