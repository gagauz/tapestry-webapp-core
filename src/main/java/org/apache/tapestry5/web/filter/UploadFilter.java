package org.apache.tapestry5.web.filter;

import static com.xl0e.util.C.ifNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tapestry5.web.config.Global;

import com.xl0e.util.C;

public class UploadFilter extends AbstractHttpFilter {

    private static final String MULTIPART_CONTENT_TYPE = "multipart/";
    private static final String UPLOADED_FILES_PATH_PARAM = "uploadedFilesPath";
    private static final String JAVA_IO_TMP_DIR = "java.io.tmpdir";
    private static final String REQUEST_ENCODING = Charset.defaultCharset().name();

    private static final ThreadLocal<Map<String, FileItem[]>> requestFiles = new ThreadLocal<>();
    private static final ThreadLocal<FileItemFactory> factoryHolder = new ThreadLocal<>();

    public static List<FileItem> getFileItems(String name) {

        if (null != requestFiles.get() && null != requestFiles.get().get(name)) {
            return Arrays.asList(requestFiles.get().get(name));
        }

        return Collections.<FileItem>emptyList();
    }

    private static String uploadedFilesDir;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        uploadedFilesDir = ifNull(filterConfig.getInitParameter(UPLOADED_FILES_PATH_PARAM),
                System.getProperty(JAVA_IO_TMP_DIR));
        final Path path = Paths.get(uploadedFilesDir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new ServletException("Failed to create upload directory for files", e);
            }
        }
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (null != request.getContentType()
                && request.getContentType().contains(MULTIPART_CONTENT_TYPE)) {

            try {

                final List<FileItem> items = new ServletFileUpload(getFactory(request)).parseRequest(request);

                Map<String, String[]> parametersMap = Collections.emptyMap();
                Map<String, FileItem[]> requestFileItems = Collections.emptyMap();

                if (!items.isEmpty()) {
                    parametersMap = new HashMap<>(items.size());
                    requestFileItems = new HashMap<>(items.size());
                    for (final FileItem item : items) {
                        if (item.isFormField()) {
                            final String[] values = C.addToArray(parametersMap.get(item.getFieldName()), item.getString(REQUEST_ENCODING));
                            parametersMap.put(item.getFieldName(), values);
                        } else if (item.getSize() > 0) {
                            final FileItem[] values = C.addToArray(requestFileItems.get(item.getFieldName()), item);
                            requestFileItems.put(item.getFieldName(), values);
                        }
                    }
                }

                requestFiles.set(requestFileItems);

                final Map<String, String[]> finalParametersMap = parametersMap;

                request = new HttpServletRequestWrapper(request) {
                    @Override
                    public Map<String, String[]> getParameterMap() {
                        return finalParametersMap;
                    }

                    @Override
                    public Enumeration<String> getParameterNames() {
                        if (null == getParameterMap()) {
                            return Collections.emptyEnumeration();
                        }
                        final Iterator<String> it = getParameterMap().keySet().iterator();
                        return new Enumeration<String>() {

                            @Override
                            public boolean hasMoreElements() {
                                return it.hasNext();
                            }

                            @Override
                            public String nextElement() {
                                return it.next();
                            }
                        };
                    }

                    @Override
                    public String getParameter(final String name) {
                        final String[] parameters = getParameterMap().get(name);
                        return null == parameters || 0 == parameters.length ? null : parameters[0];
                    }

                    @Override
                    public String[] getParameterValues(final String name) {
                        return finalParametersMap.get(name);
                    }
                };
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);

        requestFiles.remove(); // Reset

    }

    private FileItemFactory getFactory(final HttpServletRequest request) {
        FileItemFactory factory = factoryHolder.get();
        if (null == factory) {
            factory = (fieldName, contentType, isFormField, fileName) -> {
                DiskFileItem result = new DiskFileItem(fieldName, contentType,
                        isFormField, fileName, 0, getRepository(request));
                return result;
            };
        }
        return factory;
    }

    public static File getRepository(HttpServletRequest request) {
        File repo = new File(uploadedFilesDir, request.getSession().getId());
        if (!repo.exists()) {
            repo.mkdir();
        }
        return repo;
    }

    public static File getRepository() {
        return getRepository(Global.getRequest());
    }

    @Override
    public void destroy() {
        cleanupFolder(new File(uploadedFilesDir));
    }

    public static void cleanupFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    cleanupFolder(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public static void setRequestCharset(String requestEncoding) {
    }

}
