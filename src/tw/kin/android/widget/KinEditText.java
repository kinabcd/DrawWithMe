package tw.kin.android.widget;

import tw.kin.android.KinPoint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class KinEditText extends KinLabel {
  Context mContext;
  public String mTitle = "";
  public String mHint = "";

  public KinEditText(Context context) {
    mContext = context;
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    if ( !( new KinPoint( event.getX(), event.getY() ) ).In( GetViewRect() ) ) return false;
    if ( event.getAction() == MotionEvent.ACTION_UP ) {
      AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
      builder.setTitle( mTitle );
      final LinearLayout view = new LinearLayout( mContext );
      view.setOrientation( LinearLayout.VERTICAL );
      final EditText inputRoomName = new EditText( mContext );
      inputRoomName.setText( KinEditText.this.GetText() );
      inputRoomName.setHint( mHint );
      inputRoomName.setInputType( InputType.TYPE_CLASS_TEXT );
      inputRoomName.setTypeface( Typeface.SERIF );
      view.addView( inputRoomName );
      builder.setView( view );
      builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
        public void onClick( DialogInterface dialog, int which ) {
          KinEditText.this.SetText( inputRoomName.getText().toString() );
          InputMethodManager imm = (InputMethodManager) mContext.getSystemService( Context.INPUT_METHOD_SERVICE );
          imm.toggleSoftInput( InputMethodManager.SHOW_IMPLICIT, 0 );
        }
      } );
      InputMethodManager imm = (InputMethodManager) mContext.getSystemService( Context.INPUT_METHOD_SERVICE );
      imm.toggleSoftInput( InputMethodManager.SHOW_FORCED, 0 );
      builder.show();
    }
    return true;
  }
}
