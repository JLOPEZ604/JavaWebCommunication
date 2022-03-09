import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A rudimentary HTTP Web Server.
 */
public final class WebServer {

    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final File root;

    /**
     * Construct a new web server.
     *
     * @param root The root directory to serve pages from
     * @param port The port to listen to
     */
    public WebServer(File root, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.root = root;
        pool = Executors.newCachedThreadPool();

    }

    /**
     * Start listening to the socket and accept new browser connections. The server
     * should forever listen for new requests and therefore this method do not
     * return.
     *
     */
    public void start() {
        while (true) {
            try {
                pool.execute(new Handler(serverSocket.accept(), root));
            } catch (IOException ex) {
                pool.shutdown();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        WebServer webServer = new WebServer(new File("/tmp"), 8000);
        webServer.start();
    }

}
class Handler implements Runnable {
    private final Socket socket;
    private final File root;

    Handler(Socket socket, File root) {
        this.socket = socket;
        this.root = root;

    }

    public void run() {
        try {
            // Create data input and output streams
            DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
            DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

            InetAddress clientIP = socket.getLocalAddress(); // Get client IP
            String requestLine = inputFromClient.readUTF(); // read in te new request line and split it by whitespace
            System.out.println(clientIP + " " + requestLine); // Log IP and request

            String[] splitLine = requestLine.split(" ");
            String method = splitLine[0];

            switch (method) {
                case "GET":
                    final File requestedFile = new File(root, splitLine[1]);

                    if (requestedFile.exists()) {
                        outputToClient.writeUTF("HTTP/1.1 200 OK");
                        outputToClient.writeUTF(Files.readString(requestedFile.toPath()));
                        outputToClient.flush();
                    } else {
                        outputToClient.writeUTF("HTTP/1.1 404 Not Found");
                        outputToClient.writeUTF("");
                        outputToClient.flush();
                    }
                    break;
                default:
                    outputToClient.writeUTF("HTTP/1.1 405 Method Not Allowed");
                    outputToClient.writeUTF("");
                    outputToClient.flush();
                    break;

            }

        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
        // read and service request on socket
    }



