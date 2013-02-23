package tw.cycuice.drawwithme;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import tw.kin.android.KinPoint;

import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

public class Client {
  public static final int SERVERPORT = 55661;
  static Client sInstance;
  Socket mServer;
  HandlerThread mSentThread;
  Handler mSentHandler;
  DataInputStream mIn;
  DataOutputStream mOut;
  boolean mIsLogin;

  Client() {
    sInstance = this;
    mSentThread = new HandlerThread( "Connect" );
    mSentThread.start();
    mSentHandler = new Handler( mSentThread.getLooper() );
    mIsLogin = false;
  }

  static public Client GetClient() {
    if ( sInstance == null )
      sInstance = new Client();
    return sInstance;
  }

  public boolean IsConnect() {
    if ( mServer == null )
      return false;
    if ( mServer.isInputShutdown() )
      return false;
    if ( mServer.isOutputShutdown() )
      return false;
    try {
      mServer.sendUrgentData( 0xFF );
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  public boolean Connect() {
    try {
      mServer = new Socket( InetAddress.getByName( "lo.homedns.org" ), SERVERPORT );
      if ( mServer == null )
        Toast.makeText( Main.sInstance, "Connect fail:Server null", Toast.LENGTH_LONG ).show();
      if ( !mServer.isConnected() )
        Toast.makeText( Main.sInstance, "Connect fail:Not connect", Toast.LENGTH_LONG ).show();

      mIn = new DataInputStream( mServer.getInputStream() );
      mOut = new DataOutputStream( mServer.getOutputStream() );
    } catch (UnknownHostException e) {
      Toast.makeText( Main.sInstance, "Connect fail:UnknownHost", Toast.LENGTH_LONG ).show();
      return false;
    } catch (IOException e) {
      Toast.makeText( Main.sInstance, "Connect fail:IOException", Toast.LENGTH_LONG ).show();
      return false;
    }
    return true;
  }

  public void CloseSocket() {
    mSentHandler.post( new Runnable() {
      @Override
      public void run() {
        try {
          GetClient().mServer.close();
          GetClient().mServer = null;
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    } );

  }

  public void SendPacket( final byte content[] ) {
    mSentHandler.post( new Runnable() {
      @Override
      public void run() {
        if ( !IsConnect() )
          if ( !Connect() )
            return;

        try {
          mOut.write( content );
          mOut.flush();
          // Toast.makeText( Main.sInstance, mIn.readLine(), Toast.LENGTH_LONG ).show();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } );
  }

  public static boolean IsLogin() {
    return GetClient().mIsLogin;
  }

  public static void Acknowledgement( byte packetNumber, int part ) {
    // 00 手機確認收到packetNumber號封包
    // 00~00 封包自己的編號 (1 Byte)
    // 01~01 封包動作編號 (1 Byte)
    // 02~05 傳點或字的封包編號 (int)
    byte text[] = new byte[6];
    text[0] = 0x00;
    byte bytePart[] = BytesFrom( part ); // int to byte[]

    text[1] = packetNumber;
    ByteCopy( text, bytePart, 2, 4 );

    GetClient().SendPacket( text );
  }

  public static void Register( String account, String nickname, String password ) {
    // 01 註冊
    // 00~00 封包自己的編號 (1 Byte)
    // 01~16 帳號(16)
    // 17~32 密碼(16)
    // 33~64 暱稱(32)
    byte text[] = new byte[65];
    text[0] = 0x01;
    byte byteAc[] = account.getBytes();
    byte bytePw[] = password.getBytes();
    byte byteNi[] = nickname.getBytes();

    ByteCopy( text, byteAc, 1, 16 );
    ByteCopy( text, bytePw, 17, 16 );
    ByteCopy( text, byteNi, 33, 32 );

    GetClient().SendPacket( text );
  }

  public static void Login( String account, String password ) {
    // 02 登入
    // 00~00 封包自己的編號 (1 Byte)
    // 01~16 帳號(16)
    // 17~32 密碼(16)
    byte text[] = new byte[33];
    text[0] = 0x02;
    byte byteAc[] = account.getBytes();
    byte bytePw[] = password.getBytes();

    ByteCopy( text, byteAc, 1, 16 );
    ByteCopy( text, bytePw, 17, 16 );

    GetClient().SendPacket( text );
    GetClient().mIsLogin = true;
  }

  public static void Logout() {
    // 03 登出
    // 00~00 封包自己的編號 (1 Byte)
    byte text[] = new byte[1];
    text[0] = 0x03;

    if ( IsLogin() )
      GetClient().SendPacket( text );

    if ( GetClient().mServer != null ) {
      GetClient().mIsLogin = false;
      GetClient().CloseSocket();
    }
  }

  public static void Search( int searchField, String keyword ) {
    // 04 搜尋
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 搜尋number or name(int)
    // 05~36 keyword(32)
    byte text[] = new byte[37];
    text[0] = 0x04;
    byte byteSf[] = BytesFrom( searchField ); // int to byte[]
    byte byteKw[] = keyword.getBytes();

    ByteCopy( text, byteSf, 1, 4 );
    ByteCopy( text, byteKw, 5, 32 );

    GetClient().SendPacket( text );
  }

  public static void GetSearchResult( int start ) {
    // 05 得到搜尋結果
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 從哪裡開始(int)
    byte text[] = new byte[5];
    text[0] = 0x05;
    byte byteStart[] = BytesFrom( start ); // int to byte[]

    ByteCopy( text, byteStart, 1, 4 );

    GetClient().SendPacket( text );
  }

  public static void CreateRoom( int x, int y, int color, String roomName, String roomPassword ) {
    // 06 開房
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 畫布x (int)
    // 05~08 畫布y (int)
    // 09~12 畫布顏色 (int)
    // 13~44 房間名稱(32)
    // 45~60 房間密碼(16)
    byte text[] = new byte[61];
    text[0] = 0x06;
    byte byteX[] = BytesFrom( x ); // int to byte[]
    byte byteY[] = BytesFrom( y ); // int to byte[]
    byte byteCo[] = BytesFrom( color ); // int to byte[]
    byte byteRn[] = roomName.getBytes();
    byte byteRp[] = roomPassword.getBytes();

    ByteCopy( text, byteX, 1, 4 );
    ByteCopy( text, byteY, 5, 4 );
    ByteCopy( text, byteCo, 9, 4 );
    ByteCopy( text, byteRn, 13, 32 );
    ByteCopy( text, byteRp, 45, 16 );

    GetClient().SendPacket( text );
  }

  public static void JoinRoom( int roomNumber, String roomPassword ) {
    // 07 加入房
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 房間編號(int)
    // 05~20 房間密碼(16)
    byte text[] = new byte[21];
    text[0] = 0x07;
    byte byteRn[] = BytesFrom( roomNumber ); // int to byte[]
    byte byteRp[] = roomPassword.getBytes();

    ByteCopy( text, byteRn, 1, 4 );
    ByteCopy( text, byteRp, 5, 16 );

    GetClient().SendPacket( text );
  }

  public static void ChangeNickname( String nickname ) {
    // 08 修改暱稱
    // 00~00 封包自己的編號 (1 Byte)
    // 01~32 暱稱(32)
    byte text[] = new byte[33];
    text[0] = 0x08;
    byte byteNi[] = nickname.getBytes();

    ByteCopy( text, byteNi, 1, 32 );

    GetClient().SendPacket( text );
  }

  public static void ChangePassword( String oldPassword, String newPassword ) {
    // 09 改新密碼
    // 00~00 封包自己的編號 (1 Byte)
    // 01~16 舊密碼(16)
    // 17~32 新密碼(16)
    byte text[] = new byte[33];
    text[0] = 0x09;
    byte byteOp[] = oldPassword.getBytes();
    byte byteNp[] = newPassword.getBytes();

    ByteCopy( text, byteOp, 1, 16 );
    ByteCopy( text, byteNp, 17, 16 );

    GetClient().SendPacket( text );
  }

  public static void SendMessage( String message ) {
    // 10 送出對話訊息
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 這個封包有幾個Byte(int)
    // 05~?? 內容
    byte byteMe[] = message.getBytes();
    int size = byteMe.length;
    byte byteMSize[] = BytesFrom( size ); // int to byte[]

    byte text[] = new byte[size + 5];
    text[0] = 0x0A;

    ByteCopy( text, byteMSize, 1, 4 );
    ByteCopy( text, byteMe, 5, size );

    GetClient().SendPacket( text );
  }

  public static void LeaveRoom() {
    // 11 離開房間
    // 00~00 封包自己的編號 (1 Byte)
    byte text[] = new byte[1];
    text[0] = 0x0B;

    GetClient().SendPacket( text );
  }

  public static void ReConnect() {
    // 12 恢復連線(使用者資訊)
    // 00~00 封包自己的編號 (1 Byte)
    // byte text[] = new byte[1];
    // text[0] = 0x0C;

    // GetClient().SendPacket( text );
  }

  public static void StartSendPoint( int strokeNumber, int packetNum, int size, int style, int color ) {
    // 13 我要開始傳N個點囉
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 筆畫編號 (int)
    // 05~08 這一筆畫切成幾個封包 (int)
    // 09~12 筆的大小 (int)
    // 13~16 筆的樣式 (int)
    // 17~20 筆的顏色 (int)
    byte text[] = new byte[21];
    text[0] = 0x0D;
    byte byteSn[] = BytesFrom( strokeNumber ); // int to byte[]
    byte bytePn[] = BytesFrom( packetNum );
    byte byteSi[] = BytesFrom( size );
    byte byteSt[] = BytesFrom( style );
    byte byteCo[] = BytesFrom( color );

    ByteCopy( text, byteSn, 1, 4 );
    ByteCopy( text, bytePn, 5, 4 );
    ByteCopy( text, byteSi, 9, 4 );
    ByteCopy( text, byteSt, 13, 4 );
    ByteCopy( text, byteCo, 17, 4 );

    GetClient().SendPacket( text );
  }

  public static void SendPoint( int strokeNumber, int packetNumber, int pointNum, List<KinPoint> pointArray ) {
    // 14 傳M個點
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 筆畫編號 (int)
    // 05~08 這是一筆畫中第 i個封包(int)
    // 09~12 我這個封包有幾個點 (int)
    // 13~16 點點x (int) ┐
    // 17~20 點點y (int) ┴-循環1~126次
    int size = pointNum * 8 + 13;

    byte text[] = new byte[size];
    text[0] = 0x0E;
    byte byteSn[] = BytesFrom( strokeNumber ); // int to byte[]
    byte bytePn[] = BytesFrom( packetNumber );
    byte bytePo[] = BytesFrom( pointNum );

    ByteCopy( text, byteSn, 1, 4 );
    ByteCopy( text, bytePn, 5, 4 );
    ByteCopy( text, bytePo, 9, 4 );
    int i = 13;
    for ( KinPoint point : pointArray ) {
      byte byteX[] = BytesFrom( (int) point.x );
      byte byteY[] = BytesFrom( (int) point.y );
      ByteCopy( text, byteX, i, 4 );
      ByteCopy( text, byteY, i + 4, 4 );
      i += 8;
    }

    GetClient().SendPacket( text );
  }

  public static void CheckConnect() {
    // 16 確認連線
    // 00~00 封包自己的編號 (1 Byte)
    byte text[] = new byte[1];
    text[0] = 0x10;

    GetClient().SendPacket( text );
  }

  static void ByteCopy( byte text[], byte input[], int startIndex, int length ) {
    for ( int i = 0; i < length; i += 1 )
      if ( i < input.length )
        text[startIndex + i] = input[i];
      else
        text[startIndex + i] = 0x00;

  }

  static byte[] BytesFrom( int num ) {
    byte bytes[] = new byte[4];
    bytes[3] = (byte) ( ( num >> 24 ) & 0xff );
    bytes[2] = (byte) ( ( num >> 16 ) & 0xff );
    bytes[1] = (byte) ( ( num >> 8 ) & 0xff );
    bytes[0] = (byte) ( ( num ) & 0xff );
    return bytes;
  }
}
