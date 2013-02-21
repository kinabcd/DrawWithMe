package tw.cycuice.drawwithme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

public class Client {
  public static final int SERVERPORT = 55661;
  static Client sInstance;
  Socket mServer;
  HandlerThread mSentThread;
  Handler mSentHandler;
  BufferedReader mIn;
  PrintWriter mOut;

  Client() {
    sInstance = this;
    mSentThread = new HandlerThread( "Connect" );
    mSentThread.start();
    mSentHandler = new Handler( mSentThread.getLooper() );
  }

  static public Client GetClient() {
    if ( sInstance == null )
      sInstance = new Client();
    return sInstance;
  }

  public boolean Connect() {
    if ( mServer != null ) {
      try {
        mServer.sendUrgentData(0xFF);
        if ( mServer.isClosed() )
          mServer = null;
        else if ( mServer.isInputShutdown() )
          mServer = null;
        else if ( mServer.isOutputShutdown() )
          mServer = null;
        else if ( mServer.isConnected() )
          return true;
      } catch (IOException e) {
        mServer = null;
      }
    }
    try {
      mServer = new Socket( InetAddress.getByName( "lo.homedns.org" ), SERVERPORT );
      if ( mServer == null )
        Toast.makeText( Main.sInstance, "Connect fail:Server null", Toast.LENGTH_LONG ).show();
      if ( !mServer.isConnected() )
        Toast.makeText( Main.sInstance, "Connect fail:Not connect", Toast.LENGTH_LONG ).show();

      mIn = new BufferedReader( new InputStreamReader( mServer.getInputStream() ) );
      mOut = new PrintWriter( new OutputStreamWriter( mServer.getOutputStream() ), true );
    } catch (UnknownHostException e) {
      Toast.makeText( Main.sInstance, "Connect fail:UnknownHost", Toast.LENGTH_LONG ).show();
      return false;
    } catch (IOException e) {
      Toast.makeText( Main.sInstance, "Connect fail:IOException", Toast.LENGTH_LONG ).show();
      return false;
    }
    return true;
  }

  public void SendPacket( final char content[] ) {
    mSentHandler.post( new Runnable() {
      @Override
      public void run() {
        if ( !Connect() )
          return;

        try {
          mOut.println( content );
          Toast.makeText( Main.sInstance, mIn.readLine(), Toast.LENGTH_LONG ).show();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    } );
  }

  public static void Login( String account, String password ) {
    GetClient().SendPacket( ( "login " + account + " " + password ).toCharArray() );

  }
}
