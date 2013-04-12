package tw.kin.android.layout;

import tw.kin.android.KinLayout;
import tw.kin.android.KinView;

public class KinLinearLayout extends KinLayout {
  int mChildHeight;

  public KinLinearLayout() {
  }

  public int GetChildHeight() {
    return mChildHeight;
  }

  @Override
  public void PosUpdate() {
    super.PosUpdate();
    if ( mChild == null ) return;
    int x = GetX();
    int y = GetY();
    mChildHeight = 0;
    for ( KinView v : mChild ) {
      v.SetPos( x, y, x + v.GetWidth(), y + v.GetHeight() );
      y += v.GetHeight();
      mChildHeight += v.GetHeight();
    }
  }
}
