import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * An implementation of a rudimentary web browser.
 */
public final class WebBrowser implements Closeable {

    Socket browserSocket;
    DataOutputStream outputToServer;
    DataInputStream inputFromServer;

    /**
     * Create a new web browser.
     * 
     * @param host The host running the web server
     * @param port The port the web server is listening on
     * @throws IOException if the IP address of the host could not be determined or
     *                     other I/O error occurs
     */
    public WebBrowser(String host, int port) throws IOException {
        browserSocket = new Socket(host, port);
        outputToServer = new DataOutputStream((browserSocket.getOutputStream()));
        inputFromServer = new DataInputStream((browserSocket.getInputStream()));
    }

    /**
     * Submit a GET method request to the web server.
     *
     * @param path The path to get
     * @throws IOException if an I/O error occurs
     */
    public void get(String path) throws IOException {
        String requestLine = "GET /" + path + " HTTP/1.1";
        outputToServer.writeUTF(requestLine);
        outputToServer.flush();

        System.out.println(inputFromServer.readUTF());
        System.out.println(inputFromServer.readUTF());
    }

    /**
     * Submit a HEAD method request to the web server.
     *
     * @param path The path to get
     * @throws IOException if an I/O error occurs
     */
    public void head(String path) throws IOException {
        String requestLine = "HEAD /" + path + " HTTP/1.1";
        outputToServer.writeUTF(requestLine);
        outputToServer.flush();

        System.out.println(inputFromServer.readUTF());
        System.out.println(inputFromServer.readUTF());
    }


    /**
     * Close the Web Browser and any socket connections to the web server.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        browserSocket.close();
    }

    /**
     * The main entry point of the application.
     */
    public static void main(String[] args) throws IOException {
        try (WebBrowser browser = new WebBrowser("localhost", 8000)) {
            browser.get("index.html");
        }
        try (WebBrowser browser = new WebBrowser("localhost", 8000)) {
            browser.head("index.html");
        }
        try (WebBrowser browser = new WebBrowser("localhost", 8000)) {
            browser.get("does-not-exist.html");
        }
    }

}
