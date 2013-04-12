package tw.kin.android;

public class KinRepeat extends Thread {
  boolean mPaused = false;
  boolean mFinished = false;
  Runnable mRunnable;

  public KinRepeat(Runnable runnable, String name) {
    super( runnable, name );
    mPaused = false;
    mFinished = false;
    mRunnable = runnable;
  }

  public KinRepeat(Runnable runnable) {
    mPaused = false;
    mFinished = false;
    mRunnable = runnable;
  }

  public void Finish() {
    mFinished = true;
  }

  public void Pause() {
    mPaused = true;
  }

  public void Resume() {
    mPaused = false;
  }

  public void run() {
    while ( !mFinished ) {
      while ( mPaused ) {
        try {
          Thread.sleep( 1000 );
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      mRunnable.run();
    }
  }
}
