package anhtong8x.com.socketclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView messageTv;
    Button btnConnect, btnSend;

    private static String TAG = "TCPClient";
    private String SERVER_IP =   "192.168.1.112";
    private int SERVERPORT = 3000;

    private Socket connectionSocket;

    private SendRunnable sendRunnable;
    private Thread sendThread;

    private ReceiveRunnable receiveRunnable;
    private Thread receiveThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTv = (TextView) findViewById(R.id.messageTv);
        btnConnect = (Button) findViewById(R.id.connect_server);
        btnSend = (Button) findViewById(R.id.send_data);
        btnConnect.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnSend.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connect_server) {
            messageTv.setText("");

            new Thread(new ConnectRunnable()).start();

            btnConnect.setEnabled(false);
            btnSend.setEnabled(true);
            return;
        }

        if (view.getId() == R.id.send_data) {
            startSending();
            sendRunnable.Send("Client: Toi la client");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionSocket != null ){
            try {
                connectionSocket.close();
                if (receiveThread != null)
                    receiveThread.interrupt();

                if (sendThread != null)
                    sendThread.interrupt();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    public void updateMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageTv.append(message + "\n");
            }
        });
    }

    private void startSending() {
        sendRunnable = new SendRunnable(connectionSocket);
        sendThread = new Thread(sendRunnable);
        sendThread.start();
    }

    private void startReceiving() {
        receiveRunnable = new ReceiveRunnable(connectionSocket);
        receiveThread = new Thread(receiveRunnable);
        receiveThread.start();
    }

    // 1.
    public class ConnectRunnable implements Runnable {

        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                connectionSocket = new Socket();
                connectionSocket.connect(new InetSocketAddress(serverAddr, SERVERPORT), 5000);

                startSending();
                sendRunnable.Send("Toi la client");
                startReceiving();

            }catch (Exception e){
            }
        }
    }

    // 2.
    public class SendRunnable implements Runnable {

        private OutputStream out;
        private boolean hasMessage = false;
        private String msg;

        public SendRunnable(Socket server) {
            try {
                this.out = server.getOutputStream();
            } catch (IOException e) {
            }
        }

        public void Send(String str) {
            this.msg = str;
            this.hasMessage = true;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (this.hasMessage) {
                    PrintWriter printWriter = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(out)),true);
                    printWriter.println(msg);
                    this.hasMessage = false;
                    msg = "";
                }
            }
        }
    }

    // 3.
    class ReceiveRunnable implements Runnable {
        private Socket sock;
        private InputStream input;
        private BufferedReader bufferedReader;

        public ReceiveRunnable(Socket server) {
            sock = server;
            try {
                input = sock.getInputStream();
            } catch (Exception e) { }
        }

        @Override
        public void run() {

            try {
                while (!Thread.currentThread().isInterrupted()) {

                    this.bufferedReader = new BufferedReader(new InputStreamReader(input));
                    String message = bufferedReader.readLine();

                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected.";
                        updateMessage(getTime() + " | Server : " + message);
                        break;
                    }

                    updateMessage(getTime() + " | Server : " + message);

                }

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
