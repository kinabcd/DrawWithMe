package tw.kin.android.widget;

import tw.kin.android.KinPoint;
import tw.kin.android.connection.KinInputConnection;
import tw.kin.android.connection.KinInputable;
import tw.ome.drawwithme.Main;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class KinEditText extends KinLable implements KinInputable {

  public KinEditText() {
  }

  /*
   * public boolean onTouchEvent( MotionEvent event ) { if ( !mVisible ) return false; if ( super.onTouchEvent( event ) ) return true; if ( !( new
   * KinPoint( event.getX(), event.getY() ) ).In( GetViewRect() ) ) {
   * 
   * return false; } if ( event.getAction() == MotionEvent.ACTION_UP ) { KinInputConnection.ShowSoft( this ); } return true; }
   */
  public boolean onKeyDown( int keycode, KeyEvent event ) {
    if ( !mVisible )
      return false;
    if ( super.onKeyDown( keycode, event ) )
      return true;
    if ( event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL ) {
      SetText( GetText().substring( 0, GetText().length() - 1 ) );
    }
    return super.onKeyDown( keycode, event );
  }

  @Override
  public void onTextInput( CharSequence text ) {
    SetText( GetText() + text );
    RequireRedraw();
  }

  @Override
  public void onDeleteText( int leftLength, int rightLength ) {
    SetText( GetText().substring( 0, GetText().length() - leftLength ) );
  }

  @Override
  public void onKeyEvent( KeyEvent event ) {
    if ( event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL ) {
      SetText( GetText().substring( 0, GetText().length() - 1 ) );
      RequireRedraw();
    }

  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( !( new KinPoint( event.getX(), event.getY() ) ).In( GetViewRect() ) )
      return false;
    if ( event.getAction() == MotionEvent.ACTION_UP ) {
      AlertDialog.Builder builder = new AlertDialog.Builder( Main.sInstance );
      builder.setTitle( "RoomName" );
      final LinearLayout view = new LinearLayout( Main.sInstance );
      view.setOrientation( LinearLayout.VERTICAL );
      final EditText inputRoomName = new EditText( Main.sInstance );
      inputRoomName.setText( KinEditText.this.GetText() );
      inputRoomName.setHint( "Room Name" );
      inputRoomName.setInputType( InputType.TYPE_CLASS_TEXT );
      inputRoomName.setTypeface( Typeface.SERIF );
      view.addView( inputRoomName );
      builder.setView( view );
      builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick( DialogInterface dialog, int which ) {
          KinEditText.this.SetText( inputRoomName.getText().toString() );
          InputMethodManager imm = (InputMethodManager) Main.sInstance.getSystemService( Context.INPUT_METHOD_SERVICE );
          imm.toggleSoftInput( InputMethodManager.SHOW_IMPLICIT, 0 );
        }
      } );
      InputMethodManager imm = (InputMethodManager) Main.sInstance.getSystemService( Context.INPUT_METHOD_SERVICE );
      imm.toggleSoftInput( InputMethodManager.SHOW_FORCED, 0 );
      builder.show();
    }
    return true;
  }
}
