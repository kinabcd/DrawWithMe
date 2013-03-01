package tw.ome.drawwithme.protocal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tw.kin.android.KinPoint;
import tw.ome.drawwithme.DrawSurface;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class CModeInternet implements IActionCotroller {
  public static final int SERVERPORT = 55661;
  static CModeInternet sInstance;
  SocketChannel mServer;
  HandlerThread mSentThread;
  Handler mSentHandler;
  DataInputStream mIn;
  DataOutputStream mOut;
  boolean mIsLogin;
  List<Action> mActions;
  int mSendingId;
  int mSendingLast;
  int mSendingPart;
  Selector selector = null;
  SelectionKey key = null;

  CModeInternet() {
    sInstance = this;
    mSentThread = new HandlerThread( "Connect" );
    mSentThread.start();
    mSentHandler = new Handler( mSentThread.getLooper() );
    mIsLogin = false;
    mActions = Collections.synchronizedList( new LinkedList<Action>() );
    mSendingId = -1;
  }

  static public CModeInternet GetClient() {
    if ( sInstance == null )
      sInstance = new CModeInternet();
    return sInstance;
  }

  public boolean IsConnect() {
    if ( mServer == null )
      return false;
    if ( mServer.isConnected() )
      return true;
    return true;
  }

  public boolean Connect() {
    try {
      InetSocketAddress addr = new InetSocketAddress( InetAddress.getByName( "lo.homedns.org" ), SERVERPORT );
      mServer = SocketChannel.open( addr );
      mServer.configureBlocking( false );
      selector = Selector.open();
      key = mServer.register( selector, SelectionKey.OP_READ );
      mSentHandler.postAtTime( checkpack, 2000 );
    } catch (IOException e) {
      System.out.println( e.getMessage() );
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
    mSentHandler.postAtFrontOfQueue( new Runnable() {
      @Override
      public void run() {
        if ( !IsConnect() )
          if ( !Connect() )
            return;

        try {
          ByteBuffer serverBuf = ByteBuffer.wrap( content );
          mServer.write( serverBuf );
          serverBuf.clear();
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

  public void PushAction( Action newAction ) {
    mActions.add( newAction );
    if ( mSendingId != newAction.mId ) {
      mSendingId = newAction.mId;
      mSendingLast = 0;
      mSendingPart = 0;
      StartSendPoint( newAction.mId, 0, newAction.mSize, newAction.mPen, newAction.mColor );
    }
    int unsendnum = newAction.mPath.size() - mSendingLast;
    if ( unsendnum > 20 || newAction.mIsCompleted ) {
      List<KinPoint> path = newAction.mPath.subList( mSendingLast, newAction.mPath.size() );
      SendPoint( newAction.mId, mSendingPart, unsendnum, path );
      mSendingLast = newAction.mPath.size();
      mSendingPart += 1;
    }
    DrawSurface.GetInstance().RequireRedraw();

  }

  public boolean HasNewAction() {
    if ( !mActions.isEmpty() )
      return true;
    return false;
  }

  public List<Action> PullAction() {
    List<Action> relist = new LinkedList<Action>();
    while ( !mActions.isEmpty() ) {
      Action exe = mActions.remove( 0 );
      relist.add( exe );
    }

    return relist;
  }

  // ======================================================================================================
  void PackageParser( byte head ) {
    if ( head == 0x20 ) { // 伺服器確認收到K號封包
      try {
        int b = ReadByte();
        ReadInt();
        Log.i( "0x20", "Receive" + Integer.toString( b ) );
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if ( head == 0x21 ) { // 註冊結果
      try {
        int regResult = ReadInt();
        if ( regResult == 0 )
          Log.i( "0x21", "Register Success!" );
        else if ( regResult == 1 )
          Log.i( "0x21", "Account Repeat!" );
        else if ( regResult == 2 )
          Log.i( "0x21", "Account Error!" );
        else if ( regResult == 3 )
          Log.i( "0x21", "Password Error!" );
        else if ( regResult == 3 )
          Log.i( "0x21", "Nickname Error!" );

      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if ( head == 0x22 ) { // 登入結果
      try {
        int loginResult = ReadInt();
        int accountNumber = ReadInt();
        String nickname = ReadString( 32 );
        if ( loginResult == 0 ) {
          Log.i( "0x22", "Login Success!" );
          Log.i( "0x22", "accountNumber: " + Integer.toString( accountNumber ) );
          Log.i( "0x22", "Nickname: " + nickname );
        } else if ( loginResult == 1 )
          Log.i( "0x22", "Login Failed!" );
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  int ReadByte() throws IOException {
    return mIn.read();
  }

  int ReadInt() throws IOException {
    byte bType[] = new byte[4];
    mIn.read( bType, 0, 4 );
    return ( bType[0] & 0xff ) | ( ( bType[1] << 8 ) & 0xff00 ) | ( ( bType[2] << 24 ) >>> 8 ) | ( bType[3] << 24 );

  }

  String ReadString( int length ) throws IOException {
    byte bs[] = new byte[length];
    mIn.read( bs, 0, length );
    String s = new String( bs );
    int last = s.indexOf( '\0' );
    if ( last == -1 )
      return s;
    if ( last == 0 )
      return "";
    return s.substring( 0, last );
  }

  Runnable checkpack = new Runnable() {

    @Override
    public void run() {
      if ( IsConnect() )
        mSentHandler.postDelayed( checkpack, 2000 );
      try {
        if ( selector == null )
          return;
        if ( selector.select( 500 ) > 0 ) {
          System.out.println( "3" );
          Set<SelectionKey> set = selector.selectedKeys();
          Iterator<SelectionKey> it = set.iterator();

          while ( it.hasNext() ) {
            key = (SelectionKey) it.next();
            it.remove();
            if ( key.isReadable() ) {
              SocketChannel sc = (SocketChannel) key.channel();
              ByteBuffer buffer = ByteBuffer.allocate( 1024 );
              int read;
              while ( ( read = sc.read( buffer ) ) != -1 ) {
                if ( read == 0 ) {
                  break;
                }
                buffer.flip();
                byte[] array = new byte[read];
                buffer.get( array );
                String s = new String( array );
                System.out.print( s );
                buffer.clear();

              }
              System.out.println();
              if ( read == -1 )
                CloseSocket();
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  };
}
