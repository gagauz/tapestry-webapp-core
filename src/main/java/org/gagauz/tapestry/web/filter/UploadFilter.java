package org.gagauz.tapestry.web.filter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.gagauz.tapestry.web.config.Global;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class UploadFilter implements Filter {

    private static final ThreadLocal<MultiValueMap<String, FileItem>> FILES = new ThreadLocal<>();
    private static final ThreadLocal<FileItemFactory> factoryHolder = new ThreadLocal<>();
    private static String REQUEST_ENCODING = Charset.defaultCharset().name();

    public static List<FileItem> getFileItems(String name) {
        if (null != FILES.get() && null != FILES.get().get(name))
            return FILES.get().get(name);

        return Collections.<FileItem>emptyList();
    }

    private static String uploadedFilesDir;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        uploadedFilesDir = System.getProperty("java.io.tmpdir") + "/uploaded_files";
        Path path =Paths.get(uploadedFilesDir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (null != httpRequest.getContentType() && httpRequest.getContentType().contains("multipart")) {
            MultiValueMap<String, FileItem> value = new LinkedMultiValueMap<>();
            FILES.set(value);
            final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
            try {
                List<FileItem> items = new ServletFileUpload(getFactory(httpRequest)).parseRequest((HttpServletRequest) request);
                if (!items.isEmpty()) {
                    for (FileItem item : items) {
                        if (item.isFormField()) {
                            paramMap.add(item.getFieldName(), item.getString(REQUEST_ENCODING));
                        } else if (item.getSize() > 0) {
                            value.add(item.getFieldName(), item);
                        }
                    }
                }

                httpRequest = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public String getParameter(String name) {
                        return paramMap.getFirst(name);
                    }

                    @Override
                    public String[] getParameterValues(String name) {
                        List<String> param = paramMap.get(name);
                        if (null == param) {
                            return null;
                        }
                        return param.toArray(new String[param.size()]);
                    }
                };
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(httpRequest, response);

        FILES.remove(); // Reset

    }

    private FileItemFactory getFactory(HttpServletRequest request) {
        FileItemFactory factory = factoryHolder.get();
        if (null == factory) {
            factory = new FileItemFactory() {
                @Override
                public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {
                    DiskFileItem result = new DiskFileItem(fieldName, contentType,
                            isFormField, fileName, 0, getRepository(request));
                    return result;
                }
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
        REQUEST_ENCODING = requestEncoding;
    }

}
