package anhtong8x.com.socketclient;

/**
 * Created by Administrator on 11/28/2018.
 */

public interface MessageReceivedListener {
    /**
     * Called when new command/message received
     * @param msg Command/message as String
     */
    public void OnMessageReceived(String msg);

    /**
     * Called when new file is incoming.
     * @param length Total length of data expected to be received
     * @param //downloaded Total length of data actually received so far
     */
    public void OnFileIncoming(int length);

    /**
     * Called when more data of a file has been transfered
     * @param data Byte array of data received
     * @param read The lenght of the data received as int
     * @param length Total length of data expected to be received
     * @param downloaded Total length of data actually received so far
     */
    public void OnFileDataReceived(byte[] data,int read, int length, int downloaded);

    /**
     * Called when file transfer is complete
     * @param //got
     * @param //expected
     */
    public void OnFileComplete();

    /**
     * Called when an error occur
     */
    public void OnConnectionError();

    /**
     * Called when socket has connected to the server
     */
    public void OnConnectSuccess();
}
