package tw.ome.drawwithme;

import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.Spanned;

public class CConstant {
  public final static int MaxHeight = 2048;
  public final static int MaxWidth = 2048;
  public final static int MaxPenSize = 30;
  public final static int NOTLOADING = 0;
  public final static int PAGEMENU = 1;
  public final static int PAGECANVAS = 2;
  public final static int PAGENEW = 3;
  public final static int PAGEMEMBER = 4;

  public final static int PENNORMAL = 0;
  public final static int PENERASER = 1;
  public final static int PENHIGHLIGHTER = 2;
  public final static int PENWATERCOLOR = 3;
  public final static int PENNEON = 4;
  public static Typeface TFShowFong;

  public final static InputFilter NICKNAMEFILTER = new InputFilter() {
    // only Chinese,English,Number
    public CharSequence filter( CharSequence source, int start, int end, Spanned dest, int dstart, int dend ) {
      for ( int i = start; i < end; i++ ) {
        if ( !Character.isLetterOrDigit( source.charAt( i ) ) ) {
          return "";
        }
      }
      return null;
    }
  };
  public final static InputFilter ACCOUNTFILTER = new InputFilter() {
    // only English,Number
    public CharSequence filter( CharSequence source, int start, int end, Spanned dest, int dstart, int dend ) {
      for ( int i = start; i < end; i++ ) {
        if ( source.charAt( i ) >= 48 && source.charAt( i ) <= 57 )
          ;
        else if ( source.charAt( i ) >= 65 && source.charAt( i ) <= 90 )
          ;
        else if ( source.charAt( i ) >= 97 && source.charAt( i ) <= 122 )
          ;
        else
          return "";
      }
      return null;
    }
  };
}
