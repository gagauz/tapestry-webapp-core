package org.gagauz.utils;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketNode;
import org.apache.log4j.net.ZeroConfSupport;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.tapestry5.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ElasticSearchAppender extends AppenderSkeleton {

    /**
     * The default port number of remote logging server (4560).
     *
     * @since 1.2.15
     */
    static public final int DEFAULT_PORT = 920;

    /**
     * The default reconnection delay (30000 milliseconds or 30 seconds).
     */
    static final int DEFAULT_RECONNECTION_DELAY = 30000;

    /**
     * We remember host name as String in addition to the resolved InetAddress so that it can be returned via getOption().
     */
    String remoteHost;

    /**
     * The MulticastDNS zone advertised by a SocketAppender
     */
    public static final String ZONE = "_log4j_obj_tcpconnect_appender.local.";

    InetAddress address;
    int port = DEFAULT_PORT;
    BufferedOutputStream outputStream;
    int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
    boolean locationInfo = false;
    private String application;

    private Connector connector;

    int counter = 0;

    // reset the ObjectOutputStream every 70 calls
    //private static final int RESET_FREQUENCY = 70;
    private static final int RESET_FREQUENCY = 1;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;

    public ElasticSearchAppender() {
    }

    /**
         Connects to remote server at <code>address</code> and <code>port</code>.
      */
    public ElasticSearchAppender(InetAddress address, int port) {
        this.address = address;
        this.remoteHost = address.getHostName();
        this.port = port;
        connect(address, port);
      }

    /**
         Connects to remote server at <code>host</code> and <code>port</code>.
      */
    public ElasticSearchAppender(String host, int port) {
        this.port = port;
        this.address = getAddressByName(host);
        this.remoteHost = host;
        connect(address, port);
      }

    /**
     * Connect to the specified <b>RemoteHost</b> and <b>Port</b>.
     */
    @Override
    public void activateOptions() {
        if (advertiseViaMulticastDNS) {
            zeroConf = new ZeroConfSupport(ZONE, port, getName());
            zeroConf.advertise();
        }
        connect(address, port);
    }

    /**
     * Close this appender. <p>This will mark the appender as closed and call then {@link #cleanUp} method.
     */
    @Override
    synchronized public void close() {
        if (closed)
            return;

        this.closed = true;
        if (advertiseViaMulticastDNS) {
            zeroConf.unadvertise();
        }

        cleanUp();
    }

    /**
     * Drop the connection to the remote host and release the underlying connector thread if it has been created
     */
    public void cleanUp() {
        IOUtils.closeQuietly(outputStream);
        if (connector != null) {
            //LogLog.debug("Interrupting the connector.");
            connector.interrupted = true;
            connector = null;  // allow gc
        }
    }

    void connect(InetAddress address, int port) {
        if (this.address == null)
            return;
        try {
            // First, close the previous connection if any.
            cleanUp();
            Socket socket = new Socket(address, port);
            outputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            String msg = "Could not connect to remote log4j server at ["
                    + address.getHostName() + "].";
            if (reconnectionDelay > 0) {
                msg += " We will try again later.";
                fireConnector(); // fire the connector thread
            } else {
                msg += " We are not retrying.";
                errorHandler.error(msg, e, ErrorCode.GENERIC_FAILURE);
            }
            LogLog.error(msg);
        }
    }

    @Override
    public void append(LoggingEvent event) {
        if (event == null)
            return;

        if (address == null) {
            errorHandler.error("No remote host is set for SocketAppender named \"" +
                    this.name + "\".");
            return;
        }

        if (outputStream != null) {
            try {
                JSONObject json = new JSONObject();
                if (locationInfo) {
                    json.put("locationInformation", event.getLocationInformation());
                }
                if (application != null) {
                    event.setProperty("application", application);
                    json.put("application", application);
                }

                json.put("renderedMessage", event.getRenderedMessage());
                json.put("throwableStrRep", event.getThrowableStrRep());
                json.put("ndc", event.getNDC());
                json.put("thread", event.getThreadName());

                JSONObject mdc = new JSONObject();
                for (Object e : event.getPropertyKeySet()) {
                    mdc.put(e.toString(), event.getProperty(e.toString()));
                }
                json.put("mdc", mdc);




                json.put("level", event.getLevel().toString());
                json.put("message", event.getMessage());
                json.put("logger", event.getLoggerName());
                json.put("timestamp", event.getTimeStamp());
                String str = json.toCompactString();
                StringBuilder put = new StringBuilder("POST /test/log/_create HTTP/1.1\r\n");
                put.append("Host: " + address.getHostName() + ":" + port + "\r\n");
                put.append("Content-Type: application/json; charset=utf-8\r\n");
                //                put.append("Connection: keep-alive\r\n");
                put.append("Connection: close\r\n");
                put.append("Content-Length: ").append(str.length()).append("\r\n\r\n");
                put.append(str).append("\r\n");
                outputStream.write(put.toString().getBytes(StandardCharsets.UTF_8.name()));
                //LogLog.debug("=========Flushing.");
                outputStream.flush();
                if (++counter >= RESET_FREQUENCY) {
                    counter = 0;
                    // Failing to reset the object output stream every now and
                    // then creates a serious memory leak.
                    //System.err.println("Doing oos.reset()");
                    //                    outputStream.reset();
                    //                    outputStream.flush();
                }

            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                outputStream = null;
                LogLog.warn("Detected problem with connection: " + e);
                if (reconnectionDelay > 0) {
                    fireConnector();
                } else {
                    errorHandler.error("Detected problem with connection, not reconnecting.", e,
                            ErrorCode.GENERIC_FAILURE);
                }
            }
        }
    }

    public void setAdvertiseViaMulticastDNS(boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }

    public boolean isAdvertiseViaMulticastDNS() {
        return advertiseViaMulticastDNS;
    }

    void fireConnector() {
        if (connector == null) {
            LogLog.debug("Starting a new connector thread.");
            connector = new Connector();
            connector.setDaemon(true);
            connector.setPriority(Thread.MIN_PRIORITY);
            connector.start();
        }
    }

    static InetAddress getAddressByName(String host) {
        try {
            return InetAddress.getByName(host);
        } catch (Exception e) {
            if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("Could not find address of [" + host + "].", e);
            return null;
        }
    }

    /**
     * The SocketAppender does not use a layout. Hence, this method returns <code>false</code>.
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * The <b>RemoteHost</b> option takes a string value which should be the host name of the server where a {@link SocketNode} is running.
     */
    public void setRemoteHost(String host) {
        address = getAddressByName(host);
        remoteHost = host;
    }

    /**
     * Returns value of the <b>RemoteHost</b> option.
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * The <b>Port</b> option takes a positive integer representing the port where the server is waiting for connections.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns value of the <b>Port</b> option.
     */
    public int getPort() {
        return port;
    }

    /**
     * The <b>LocationInfo</b> option takes a boolean value. If true, the information sent to the remote host will include location information. By default no
     * location information is sent to the server.
     */
    public void setLocationInfo(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    /**
     * Returns value of the <b>LocationInfo</b> option.
     */
    public boolean getLocationInfo() {
        return locationInfo;
    }

    /**
     * The <b>App</b> option takes a string value which should be the name of the application getting logged. If property was already set (via system property),
     * don't set here.
     *
     * @since 1.2.15
     */
    public void setApplication(String lapp) {
        this.application = lapp;
    }

    /**
     * Returns value of the <b>Application</b> option.
     *
     * @since 1.2.15
     */
    public String getApplication() {
        return application;
    }

    /**
     * The <b>ReconnectionDelay</b> option takes a positive integer representing the number of milliseconds to wait between each failed connection attempt to
     * the server. The default value of this option is 30000 which corresponds to 30 seconds. <p>Setting this option to zero turns off reconnection capability.
     */
    public void setReconnectionDelay(int delay) {
        this.reconnectionDelay = delay;
    }

    /**
     * Returns value of the <b>ReconnectionDelay</b> option.
     */
    public int getReconnectionDelay() {
        return reconnectionDelay;
    }

    /**
     * The Connector will reconnect when the server becomes available again. It does this by attempting to open a new connection every
     * <code>reconnectionDelay</code> milliseconds. <p>It stops trying whenever a connection is established. It will restart to try reconnect to the server when
     * previously open connection is droppped.
     *
     * @author Ceki G&uuml;lc&uuml;
     * @since 0.8.4
     */
    class Connector extends Thread {

        boolean interrupted = false;

        @Override
        public void run() {
            Socket socket;
            while (!interrupted) {
                try {
                    sleep(reconnectionDelay);
                    LogLog.debug("Attempting connection to " + address.getHostName());
                    socket = new Socket(address, port);
                    synchronized (this) {
                        outputStream = new BufferedOutputStream(socket.getOutputStream());
                        connector = null;
                        LogLog.debug("Connection established. Exiting connector thread.");
                        break;
                    }
                } catch (InterruptedException e) {
                    LogLog.debug("Connector interrupted. Leaving loop.");
                    return;
                } catch (java.net.ConnectException e) {
                    LogLog.debug("Remote host " + address.getHostName()
                            + " refused connection.");
                } catch (IOException e) {
                    if (e instanceof InterruptedIOException) {
                        Thread.currentThread().interrupt();
                    }
                    LogLog.debug("Could not connect to " + address.getHostName() +
                            ". Exception is " + e);
                }
            }
            //LogLog.debug("Exiting Connector.run() method.");
        }

        /**
         * public void finalize() { LogLog.debug("Connector finalize() has been called."); }
         */
    }


    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        RequestSender.put("http://localhost:9200/test")
                .setHeader("Content-Type", "application/json;charset=utf-8").setRawData(
                        "{\"mappings\":{\"log\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}}")
                .execute(sb);

        System.out.println(sb);
    }
}
