package tw.ome.drawwithme.protocal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
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
import tw.ome.drawwithme.CConstant;
import tw.ome.drawwithme.DrawSurface;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

public class CModeInternet implements IActionCotroller {
  public static final int SERVERPORT = 55661;
  static CModeInternet sInstance;
  SocketChannel mServer;
  HandlerThread mSentThread;
  Handler mSentHandler;
  boolean mIsLogin;
  List<Action> mActions;
  int mSendingId;
  int mSendingLast;
  int mSendingPart;
  Selector selector = null;
  SelectionKey key = null;
  ByteBuffer mBufferIn;
  SparseArray<Action> mInActions;

  CModeInternet() {
    sInstance = this;
    mSentThread = new HandlerThread( "Connect" );
    mSentThread.start();
    mSentHandler = new Handler( mSentThread.getLooper() );
    mIsLogin = false;
    mActions = Collections.synchronizedList( new LinkedList<Action>() );
    mSendingId = -1;
    mBufferIn = ByteBuffer.allocate( 10240 );
    mInActions = new SparseArray<Action>();
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
    mSentHandler.post( new Runnable() {
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

  public static void StartSendPoint( int strokeNumber, int size, int style, int color ) {
    // 13 我要開始傳N個點囉
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 筆畫編號 (int)
    // 05~08 這一筆畫切成幾個封包 (int)
    // 09~12 筆的大小 (int)
    // 13~16 筆的樣式 (int)
    // 17~20 筆的顏色 (int)
    byte text[] = new byte[17];
    text[0] = 0x0D;
    byte byteSn[] = BytesFrom( strokeNumber ); // int to byte[]
    byte byteSi[] = BytesFrom( size );
    byte byteSt[] = BytesFrom( style );
    byte byteCo[] = BytesFrom( color );

    ByteCopy( text, byteSn, 1, 4 );
    ByteCopy( text, byteSi, 5, 4 );
    ByteCopy( text, byteSt, 9, 4 );
    ByteCopy( text, byteCo, 13, 4 );

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

  public static void EndSendPoint( int strokeNumber ) {
    // 00~00 封包自己的編號 (1 Byte)
    // 01~04 筆畫編號 (int)
    byte text[] = new byte[5];
    text[0] = 0x0F;
    byte byteSn[] = BytesFrom( strokeNumber ); // int to byte[]

    ByteCopy( text, byteSn, 1, 4 );

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
    if ( mSendingId != newAction.mId ) {
      mSendingId = newAction.mId;
      mSendingLast = 0;
      mSendingPart = 0;
      StartSendPoint( newAction.mId, newAction.mSize, newAction.mPen, newAction.mColor );
    }
    int unsendnum = newAction.mPath.size() - mSendingLast;
    if ( unsendnum > 20 || newAction.mIsCompleted ) {
      List<KinPoint> path = newAction.mPath.subList( mSendingLast, newAction.mPath.size() );
      SendPoint( newAction.mId, mSendingPart, unsendnum, path );
      mSendingLast = newAction.mPath.size();
      mSendingPart += 1;
      if ( newAction.mIsCompleted )
        EndSendPoint( newAction.mId );
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
  boolean PackageParser() {
    // 回傳是否成功處理一個封包
    int bufferLen = mBufferIn.limit() - mBufferIn.position();
    if ( bufferLen == 0 )
      return false; // 沒有新訊息
    try {
      byte head = mBufferIn.get();
      if ( head == 0x20 ) { // 伺服器確認收到K號封包
        if ( bufferLen < 6 ) {
          Log.i( "0x20", "BufferLen Error" );
          return false;
        }
        int b = ReadByte();
        int packetNum = ReadInt();
        Log.i( "0x20", "Server Receive: " + Integer.toString( b ) );
        return true;
      } else if ( head == 0x21 ) { // 註冊結果
        if ( bufferLen < 5 ) {
          Log.i( "0x21", "BufferLen Error" );
          return false;
        }
        int regResult = ReadInt();
        if ( regResult == 0 )
          Log.i( "0x21_0", "Register Success!" );
        else if ( regResult == 1 )
          Log.i( "0x21_1", "Account Repeat!" );
        else if ( regResult == 2 )
          Log.i( "0x21_2", "Account Error!" );
        else if ( regResult == 3 )
          Log.i( "0x21_3", "Password Error!" );
        else if ( regResult == 4 )
          Log.i( "0x21_4", "Nickname Error!" );

        return true;
      } else if ( head == 0x22 ) { // 登入結果
        if ( bufferLen < 73 ) {
          Log.i( "0x22", "BufferLen Error" );
          return false;
        }
        int result = ReadInt();
        int accountNumber = ReadInt();
        String nickname = ReadString( 32 );
        String accessToken = ReadString( 32 );
        if ( result == 0 ) {
          Log.i( "0x22_0", "Login Success!" );
          Log.i( "0x22_0", "accountNumber: " + Integer.toString( accountNumber ) );
          Log.i( "0x22_0", "Nickname: " + nickname );
          Log.i( "0x22_0", "accessToken: " + accessToken );
          GetClient().mIsLogin = true;
          DrawSurface.GetInstance().mUIMenu.AfterLogin();
          Toast.makeText( DrawSurface.GetInstance().getContext(), "Login Success!!", Toast.LENGTH_LONG ).show();
        } else if ( result == 1 ) {
          Log.i( "0x22_1", "Login Failed!" );
          Toast.makeText( DrawSurface.GetInstance().getContext(), "Login Failed!!", Toast.LENGTH_LONG ).show();
        }
        return true;
      } else if ( head == 0x23 ) { // 密碼更改結果
        if ( bufferLen < 5 ) {
          Log.i( "0x23", "BufferLen Error" );
          return false;
        }
        int result = ReadInt();
        if ( result == 0 )
          Log.i( "0x23_0", "Password Modify Success!" );
        else if ( result == 1 )
          Log.i( "0x23_1", "Password Error!" );
        else if ( result == 9 )
          Log.i( "0x23_9", "Not Login!" );

        return true;
      } else if ( head == 0x24 ) { // 暱稱更改結果
        if ( bufferLen < 5 ) {
          Log.i( "0x24", "BufferLen Error" );
          return false;
        }
        int result = ReadInt();
        if ( result == 0 )
          Log.i( "0x24_0", "Nickname Modify Success!" );
        else if ( result == 1 )
          Log.i( "0x24_1", "Nickname Error!" );
        else if ( result == 9 )
          Log.i( "0x24_9", "Not Login!" );

        return true;
      } else if ( head == 0x25 ) { // 開房結果
        if ( bufferLen < 9 ) {
          Log.i( "0x25", "BufferLen Error" );
          return false;
        }
        int result = ReadInt();
        int id = ReadInt();
        if ( result == 0 ) {
          Log.i( "0x25_0", "Create Success!" );
          JoinRoom( id, DrawSurface.GetInstance().mUINew.GetPassword() );
        } else if ( result == 1 )
          Log.i( "0x25_1", "Room Name Error!" );
        else if ( result == 2 )
          Log.i( "0x25_2", "Room Password Error!" );
        else if ( result == 9 )
          Log.i( "0x25_9", "Not Login!" );

        return true;
      } else if ( head == 0x26 ) { // 加入房間結果
        if ( bufferLen < 17 ) {
          Log.i( "0x26", "BufferLen Error" );
          return false;
        }
        int result = ReadInt();
        int width = ReadInt();
        int height = ReadInt();
        int color = ReadInt();
        if ( result == 0 ) {
          Log.i( "0x26_0", "Join Success!" );
          DrawSurface.GetInstance().mUICanvas.NewCanvas( width, height, color, true );
          DrawSurface.GetInstance().SetPage( CConstant.PAGECANVAS );
        } else if ( result == 1 ) {
          Log.i( "0x26_1", "Password Error!" );
          Toast.makeText( DrawSurface.GetInstance().getContext(), "Password Error!", Toast.LENGTH_LONG ).show();
        }
        else if ( result == 2 ) {
          Log.i( "0x26_2", "Room is Full!" );
          Toast.makeText( DrawSurface.GetInstance().getContext(), "Room is Full!", Toast.LENGTH_LONG ).show();
        }
        else if ( result == 3 ) {
          Log.i( "0x26_3", "You were Banned!" );
          Toast.makeText( DrawSurface.GetInstance().getContext(), "You were Banned!", Toast.LENGTH_LONG ).show();
        }
        else if ( result == 4 ) {
          Log.i( "0x26_4", "Without Room!" );
          Toast.makeText( DrawSurface.GetInstance().getContext(), "Room does not exit!", Toast.LENGTH_LONG ).show();
        }
        else if ( result == 9 )
          Log.i( "0x26_9", "Not Login!" );

        return true;
      } else if ( head == 0x27 ) { // 搜尋結果
        if ( bufferLen < 5 ) {
          Log.i( "0x27", "BufferLen Error" );
          return false;
        }
        int num = ReadInt();
        if ( bufferLen < ( 5 + num * 38 ) ) {
          Log.i( "0x27", "BufferLen Error2" );
          return false;
        }
        for ( int i = 0; i < num; i += 1 ) {
          int roomNum = ReadInt();
          String roomName = ReadString( 32 );
          int peopleNum = ReadInt();
          int locked = ReadByte();
          DrawSurface.GetInstance().mUIMenu.AddRoom( roomNum, roomName, locked != 0, peopleNum );
          // 儲存 ?
        }
        // 顯示?
        // DrawSurface.GetInstance().RequireRedraw();
        Log.i( "0x27", "Receive " + Integer.toString( num ) + " search results!" );
        DrawSurface.GetInstance().RequireRedraw();
        return true;
      } else if ( head == 0x28 ) { // 筆劃開始
        if ( bufferLen < 17 ) {
          Log.i( "0x28", "BufferLen Error" );
          return false;
        }
        int drawIndex = ReadInt();
        int size = ReadInt();
        int style = ReadInt();
        int color = ReadInt();
        Action newAction = new Action( style, color, size );
        mInActions.append( drawIndex, newAction );
        Log.i( "0x28", "Draw number: " + Integer.toString( drawIndex ) + " start!" );
        return true;
      } else if ( head == 0x29 ) { // 筆劃封包
        if ( bufferLen < 13 ) {
          Log.i( "0x29", "BufferLen Error" );
          return false;
        }
        int drawIndex = ReadInt();
        int part = ReadInt();
        int num = ReadInt();
        if ( bufferLen < ( 13 + num * 8 ) ) {
          Log.i( "0x29", "BufferLen Error2" );
          return false;
        }
        Action newAction = mInActions.get( drawIndex );
        for ( int i = 0; i < num; i += 1 ) {
          int x = ReadInt();
          int y = ReadInt();
          newAction.AddPoint( new KinPoint( x, y ) );
        }
        mActions.add( newAction );
        DrawSurface.GetInstance().RequireRedraw();
        Log.i( "0x29", "Draw number: " + Integer.toString( drawIndex ) + " part " + Integer.toString( part ) + " received!" );
        return true;
      } else if ( head == 0x2A ) { // 筆劃結束
        if ( bufferLen < 5 ) {
          Log.i( "0x2A", "BufferLen Error" );
          return false;
        }
        int drawIndex = ReadInt();
        mInActions.delete( drawIndex );
        Log.i( "0x2A", "Draw number: " + Integer.toString( drawIndex ) + " end!" );
        return true;
      } else if ( head == 0x2B ) { // 對話訊息
        if ( bufferLen < 37 ) {
          Log.i( "0x2B", "BufferLen Error" );
          return false;
        }
        int length = ReadInt();
        String name = ReadString( 32 );
        if ( bufferLen < ( 37 + length * 2 ) ) {
          Log.i( "0x2B", "BufferLen Error2" );
          return false;
        }
        String message = ReadString( length * 2 );
        // 印出訊息?
        Log.i( "0x2B", "Receive Message!" );
        return true;
      } else {
        Log.i( "Package", "Unknowed:" + head );
        return true;
      }

    } catch (BufferUnderflowException e) { // 封包長度不足
      return false;
    }

  }

  byte ReadByte() throws BufferUnderflowException {
    return mBufferIn.get();
  }

  int ReadInt() throws BufferUnderflowException {
    byte bType[] = new byte[4];
    mBufferIn.get( bType );
    return ( bType[0] & 0xff ) | ( ( bType[1] << 8 ) & 0xff00 ) | ( ( bType[2] << 24 ) >>> 8 ) | ( bType[3] << 24 );

  }

  String ReadString( int length ) throws BufferUnderflowException {
    byte bs[] = new byte[length];
    mBufferIn.get( bs );
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
        mSentHandler.post( checkpack );
      try {
        if ( selector == null )
          return;
        if ( selector.select( 33 ) > 0 ) {
          Set<SelectionKey> set = selector.selectedKeys();
          Iterator<SelectionKey> it = set.iterator();

          while ( it.hasNext() ) {
            key = (SelectionKey) it.next();
            it.remove();
            if ( key.isReadable() ) {
              SocketChannel sc = (SocketChannel) key.channel();
              int read;
              while ( ( read = sc.read( mBufferIn ) ) > 0 ) {
                mBufferIn.flip(); // 準備處理
                int pos = 0;
                while ( PackageParser() )
                  pos = mBufferIn.position();
                mBufferIn.position( pos );
                mBufferIn.compact(); // 回存未處理的訊息
              }
              if ( read == -1 ) // EOF (意外中斷連線)
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
