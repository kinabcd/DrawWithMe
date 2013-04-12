package tw.kin.android.widget;

import java.util.ArrayList;

import tw.kin.android.KinFrame;
import tw.kin.android.KinView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

public class KinImage extends KinView {
  public final static int FILL = 0;
  public final static int SCALE = 1;
  ArrayList<KinFrame> mFrames;
  long mLastChangeTime;
  boolean mLoop;
  boolean mRunning;
  int mNowView;
  boolean mReverse;
  Paint mPaint;
  int mFit;

  public KinImage(KinImage old) {
    super( old );
    mFrames = new ArrayList<KinFrame>( old.mFrames );
    mLoop = old.mLoop;
    mNowView = old.mNowView;
    mRunning = old.mRunning;
    mReverse = old.mReverse;
    if ( old.mPaint != null ) mPaint = new Paint( old.mPaint );
  }

  public KinImage() {
    mFrames = new ArrayList<KinFrame>();
    mLoop = false;
    mNowView = 0;
    mRunning = false;
    mReverse = false;
  }

  public void Start( boolean reverse ) {
    Start();
    mReverse = reverse;
    if ( reverse ) mNowView = mFrames.size() - 1;
  }

  public void Start() {
    mLastChangeTime = System.currentTimeMillis();
    mRunning = true;
    mNowView = 0;
    mReverse = false;
  }

  public void Stop() {
    mRunning = false;

  }

  public void SetFrame( int i ) {
    mNowView = i;
  }

  public void SetFit( int fit ) {
    mFit = fit;
  }

  public void AddColor( int color, int time ) {
    mFrames.add( new KinFrame( color, time ) );
  }

  public void AddImage( Bitmap image, int time ) {
    mFrames.add( new KinFrame( image, time ) );
  }

  public void SetColorFilter( ColorFilter cmcf ) {
    if ( mPaint == null ) mPaint = new Paint();
    mPaint.setColorFilter( cmcf );
  }

  public void SetAlpha( int a ) {
    if ( mPaint == null ) mPaint = new Paint();
    mPaint.setAlpha( a );
  }

  public void Update() {
    if ( !mRunning ) return;
    if ( mFrames == null || mFrames.size() == 0 ) return;
    if ( mFrames.get( mNowView ).mKeepTime == -1 ) return;
    while ( mFrames.size() > 0 && mLastChangeTime + mFrames.get( mNowView ).mKeepTime < System.currentTimeMillis() ) {
      mLastChangeTime = System.currentTimeMillis();
      if ( !mReverse ) {
        mNowView += 1;
        if ( mNowView == mFrames.size() ) {
          if ( mLoop ) {
            mNowView = 0;
          } else {
            mNowView = mFrames.size() - 1;
            Stop();
          }
        }
      } else {
        mNowView -= 1;
        if ( mNowView == -1 ) {
          if ( mLoop ) {
            mNowView = mFrames.size() - 1;
          } else {
            mNowView = 0;
            Stop();
          }
        }
      }
    }
  }

  public void Draw( Canvas canvas ) {
    if ( !mVisible ) return;
    Update();
    if ( mFrames == null || mFrames.size() == 0 ) return;
    KinFrame frame = mFrames.get( mNowView );
    Rect view = GetViewRect();
    if ( mFit == SCALE ) {
      double viewRate = (double) GetWidth() / GetHeight();
      double frameRate = (double) ( frame.mRect.right - frame.mRect.left ) / ( frame.mRect.bottom - frame.mRect.top );
      if ( frameRate < viewRate ) view.right = view.left + (int) ( GetHeight() * frameRate );
      if ( frameRate > viewRate ) view.bottom = view.top + (int) ( GetWidth() / frameRate );
    }
    if ( frame.mImage != null ) canvas.drawBitmap( frame.mImage, frame.mRect, view, mPaint );
    else {
      int alpha = mPaint.getAlpha();
      mPaint.setColor( frame.mColor );
      mPaint.setAlpha( alpha );
      canvas.drawRect( GetViewRect(), mPaint );
    }
  }
}
