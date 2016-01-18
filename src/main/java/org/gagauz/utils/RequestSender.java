package org.gagauz.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

public class RequestSender {

    private static final Logger LOG = LoggerFactory.getLogger(RequestSender.class);
    private String method;
    private String url;
    private Map<String, String> headers;
    private Map<String, String> params;
    private int connectTimeout = 5000;
    private int readTimeout = 5000;

    private RequestSender(String method, String url) {
        this.method = method;
        this.url = url;
        if (null == method) {
            throw new IllegalStateException("Method cannot be null!");
        }
        if (null == url) {
            throw new IllegalStateException("URL cannot be null!");
        }
    }

    public static RequestSender head(String url) {
        RequestSender sender = new RequestSender("HEAD", url);
        return sender;
    }

    public static RequestSender get(String url) {
        RequestSender sender = new RequestSender("GET", url);
        return sender;
    }

    public static RequestSender post(String url) {
        RequestSender sender = new RequestSender("POST", url);
        return sender;
    }

    public static RequestSender put(String url) {
        RequestSender sender = new RequestSender("PUT", url);
        return sender;
    }

    public static RequestSender delete(String url) {
        RequestSender sender = new RequestSender("DELETE", url);
        return sender;
    }

    public RequestSender setParam(String key, String value) {
        if (null == params) {
            params = new HashMap<String, String>();
        }
        params.put(key, value);
        return this;
    }

    public RequestSender setParams(Map<String, String> data) {
        if (null == params) {
            params = new HashMap<String, String>();
        }
        params.putAll(data);
        return this;
    }

    public RequestSender setHeader(String key, String value) {
        if (null == headers) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
        return this;
    }

    public RequestSender setHeaders(Map<String, String> data) {
        if (null == headers) {
            headers = new HashMap<String, String>();
        }
        headers.putAll(data);
        return this;
    }

    public RequestSender setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    public RequestSender setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    public int execute() {
        return execute(null);
    }

    public int execute(StringBuilder responseBuff) {
        return execute(responseBuff, null);
    }

    public int execute(StringBuilder responseBuff, Map<String, String> responseHeaders) {

        int code = 0;
        URLConnection connection = null;
        try {
            LOG.info("Sending data to " + url + " with connection timeout = " + connectTimeout + " and read timeout = " + readTimeout);
            StringBuilder dataBuff = null;
            // Send the request
            if (null != params) {
                dataBuff = new StringBuilder();
                for (Entry<String, String> e : params.entrySet()) {
                    String key = URLEncoder.encode(e.getKey());
                    if (dataBuff.length() > 0) {
                        dataBuff.append('&');
                    }
                    dataBuff.append(key).append('=');
                    if (e.getValue() != null) {
                        String value = URLEncoder.encode(e.getValue());
                        dataBuff.append(value);
                    }
                }
                if (!method.equals("POST") || method.equals("PUT")) {
                    if (!url.contains("&")) {
                        url = url + '?';
                    } else if (!url.endsWith("&")) {
                        url = url + '&';
                    }
                    url += dataBuff.toString();
                }
            }

            URL url1 = new URL(url);
            connection = url1.openConnection();
            if (this.url.startsWith("https")) {
                HttpsURLConnection c = ((HttpsURLConnection) connection);
                c.setRequestMethod(method);
                c.setSSLSocketFactory(getSSLSocketFactory());
            } else {
                ((HttpURLConnection) connection).setRequestMethod(method);
            }
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if (null != headers) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    System.out.println("\t" + e.getKey() + ": " + e.getValue());
                    connection.setRequestProperty(e.getKey(), e.getValue());
                }
            }

            if (null != dataBuff && (method.equals("POST") || method.equals("PUT"))) {
                if (null != headers && !headers.containsKey("Content-Type")) {
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                }
                connection.setRequestProperty("Content-Length", "" + dataBuff.length());
                System.out.println("\tContentLength: " + dataBuff.length());
                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(dataBuff.toString());
                writer.flush();
                writer.close();
            }

            LOG.info("Reading data from " + url1);
            if (url.startsWith("https")) {
                code = ((HttpsURLConnection) connection).getResponseCode();
            } else {
                code = ((HttpURLConnection) connection).getResponseCode();
            }

            if (null != responseHeaders) {
                for (Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                    for (String value : entry.getValue()) {
                        System.out.println("\t" + entry.getKey() + ": " + value);
                        responseHeaders.put(entry.getKey(), value);
                    }
                }
            }

            if (null != responseBuff) {
                // Get the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuff.append(line);
                }

                reader.close();
            }

        } catch (Exception e) {
            LOG.error("Request Sending Error: ", e);
        } finally {
            if (null != connection) {
            }
        }

        return code;
    }

    /**
     * Workaround of JDK-6521495 : Lift 1024-bit long prime restriction on Diffie-Hellman
     * Sun's JCE implementation imposes an artificial restriction on Diffie-Hellman primes. 
     * When passing a DHParameterSpec generated with a 2048-bit long modulus, 
     * class DHKeyPairGenerator will throw an exception indicating that 
     * "Prime size must be multiple of 64, and can only range from 512 to 1024 (inclusive)."
     */
    private static SSLSocketFactory getSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        String[] exludedCipherSuites = {"_DHE_", "_DH_"};
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLParameters params = context.getSupportedSSLParameters();
        List<String> enabledCiphers = new ArrayList<String>();
        for (String cipher : params.getCipherSuites()) {
            boolean exclude = false;
            if (exludedCipherSuites != null) {
                for (int i = 0; i < exludedCipherSuites.length && !exclude; i++) {
                    exclude = cipher.indexOf(exludedCipherSuites[i]) >= 0;
                }
            }
            if (!exclude) {
                enabledCiphers.add(cipher);
            }
        }
        String[] cArray = new String[enabledCiphers.size()];
        enabledCiphers.toArray(cArray);
        SSLSocketFactory sf = context.getSocketFactory();
        return new SSLSocketFactoryWrapper(sf, cArray);
    }

    public static class SSLSocketFactoryWrapper extends SSLSocketFactory {

        private SSLSocketFactory socketFactory = null;
        private String[] enabledCiphers = null;

        private SSLSocketFactoryWrapper(SSLSocketFactory socketFactory, String[] enabledCiphers) {
            super();
            this.socketFactory = socketFactory;
            this.enabledCiphers = enabledCiphers;
        }

        private Socket getSocketWithEnabledCiphers(Socket socket) {
            if (enabledCiphers != null && socket != null && socket instanceof SSLSocket) {
                ((SSLSocket) socket).setEnabledCipherSuites(enabledCiphers);
            }

            return socket;
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return getSocketWithEnabledCiphers(socketFactory.createSocket(socket, host, port, autoClose));
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return socketFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            if (enabledCiphers == null)
                return socketFactory.getSupportedCipherSuites();
            else
                return enabledCiphers;
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return getSocketWithEnabledCiphers(socketFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port) throws IOException {
            return getSocketWithEnabledCiphers(socketFactory.createSocket(address, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
            return getSocketWithEnabledCiphers(socketFactory.createSocket(host, port, localAddress, localPort));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localaddress, int localport) throws IOException {
            return getSocketWithEnabledCiphers(socketFactory.createSocket(address, port, localaddress, localport));
        }
    }

}
