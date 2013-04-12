package tw.kin.android.layout;

import java.util.HashMap;

import tw.kin.android.KinLayout;
import tw.kin.android.KinView;
import android.graphics.Rect;

public class KinAbsoluteLayout extends KinLayout {

  public enum Alignment {
    DEPENDENT_PARENT, LEFT, TOP, CENTER, RIGHT, BOTTOM
  };

  HashMap<KinView, Alignment> mAlignmentX;
  HashMap<KinView, Alignment> mAlignmentY;

  public KinAbsoluteLayout() {
    mAlignmentX = new HashMap<KinView, Alignment>();
    mAlignmentY = new HashMap<KinView, Alignment>();
  }

  public void SetAlignment( KinView child, Alignment x, Alignment y ) {
    mAlignmentX.put( child, x );
    mAlignmentY.put( child, y );
  }

  @Override
  public void PosUpdate() {
    super.PosUpdate();
    if ( mChild == null ) return;
    Rect myPos = GetViewRect();
    for ( KinView child : mChild ) {
      int width = child.GetWidth();
      int height = child.GetHeight();
      Alignment alignmentX = mAlignmentX.get( child );
      Alignment alignmentY = mAlignmentY.get( child );
      Rect chPos = child.GetViewRect();
      if ( alignmentX == null ) chPos.left = myPos.left + child.GetX();
      else if ( alignmentX == Alignment.DEPENDENT_PARENT ) chPos.left = myPos.left + child.GetX();
      else if ( alignmentX == Alignment.LEFT ) chPos.left = myPos.left;
      else if ( alignmentX == Alignment.CENTER ) chPos.left = myPos.left + ( GetWidth() - width ) / 2;
      else if ( alignmentX == Alignment.RIGHT ) chPos.left = myPos.right - width;

      if ( alignmentY == null ) chPos.top = myPos.top + child.GetY();
      else if ( alignmentY == Alignment.DEPENDENT_PARENT ) chPos.top = myPos.top + child.GetY();
      else if ( alignmentY == Alignment.TOP ) chPos.top = myPos.top;
      else if ( alignmentY == Alignment.CENTER ) chPos.top = myPos.top + ( GetHeight() - height ) / 2;
      else if ( alignmentY == Alignment.BOTTOM ) chPos.top = myPos.bottom - height;

      chPos.right = chPos.left + width;
      chPos.bottom = chPos.top + height;
      child.PosUpdate();
    }
  }

  @Override
  public int GetChildHeight() {
    return 0;
  }

}
