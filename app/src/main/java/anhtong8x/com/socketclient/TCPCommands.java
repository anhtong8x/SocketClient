package anhtong8x.com.socketclient;

/**
 * Created by Administrator on 11/28/2018.
 */

public class TCPCommands {
    public static int TYPE_CMD = 1;
    public static int TYPE_FILE_CONTENT = 2;

    public static String CMD_REQUEST_FILES = "server_get_files";
    public static String CMD_REQUEST_FILES_RESPONSE = "server_get_files_response";
    public static String CMD_REQUEST_FILE_DOWNLOAD = "server_download_file";
}
