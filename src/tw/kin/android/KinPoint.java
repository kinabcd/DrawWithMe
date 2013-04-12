package tw.kin.android;

import android.graphics.Rect;

public class KinPoint {
  public final static KinPoint Origin = new KinPoint( 0, 0 );

  public double x = 0;
  public double y = 0;

  public KinPoint() {

  }

  public void Move( int direction, int b ) {

    if ( direction == 0 ) {
      y -= b;
    } else if ( direction == 1 ) {
      x += b;

    } else if ( direction == 2 ) {
      y += b;

    } else if ( direction == 3 ) {
      x -= b;

    }
  }

  public KinPoint(double x, double y) {
    super();
    this.x = x;
    this.y = y;
  }

  public KinPoint(KinPoint oldPoint) {
    super();
    this.x = oldPoint.x;
    this.y = oldPoint.y;
  }

  public boolean In( Rect rect ) {
    return x > rect.left && x < rect.right && y > rect.top && y < rect.bottom;
  }

}
