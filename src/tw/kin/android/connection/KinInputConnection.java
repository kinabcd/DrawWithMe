package tw.kin.android.connection;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputMethodManager;

public class KinInputConnection extends BaseInputConnection {
  static KinInputable mInputFocus;
  static InputMethodManager mImm;

  public KinInputConnection(View targetView, boolean fullEditor) {
    super( targetView, fullEditor );
    if ( mImm == null ) {
      mImm = (InputMethodManager) targetView.getContext().getSystemService( Activity.INPUT_METHOD_SERVICE );
    }
  }

  @Override
  public boolean commitText( CharSequence text, int newCursorPosition ) {
    if ( mInputFocus == null ) return false;
    mInputFocus.onTextInput( text );
    return true;
  }

  @Override
  public boolean deleteSurroundingText( int leftLength, int rightLength ) {
    if ( mInputFocus == null ) return false;
    mInputFocus.onDeleteText( leftLength, rightLength );
    return true;

  }

  static public void ShowSoft( KinInputable edit ) {
    mInputFocus = edit;
    mImm.toggleSoftInput( 0, InputMethodManager.HIDE_NOT_ALWAYS );

  }

  static public void HideSoft() {
    mInputFocus = null;
    mImm.toggleSoftInput( 0, InputMethodManager.HIDE_NOT_ALWAYS );
  }

  @Override
  public boolean sendKeyEvent( KeyEvent event ) {
    if ( mInputFocus == null ) return false;
    mInputFocus.onKeyEvent( event );
    return true;
  }
}
