package tw.kin.android.connection;

import android.view.KeyEvent;

public interface KinInputable {
  public void onTextInput( CharSequence text );

  public void onDeleteText( int leftLength, int rightLength );

  public void onKeyEvent( KeyEvent event );
}
