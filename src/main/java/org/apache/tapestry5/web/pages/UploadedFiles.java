package org.apache.tapestry5.web.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.web.config.Global;
import org.apache.tapestry5.web.filter.UploadFilter;
import org.apache.tapestry5.web.services.CustomHttpResponse;

public class UploadedFiles {

    Object onActivate(final EventContext ctx) {
        final File file = new File(UploadFilter.getRepository(Global.getRequest()), ctx.get(String.class, 0));
        if (!file.exists()) {
            return new CustomHttpResponse(404, null);
        }

        return new StreamResponse() {

            @Override
            public void prepareResponse(Response response) {
            }

            @Override
            public InputStream getStream() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public String getContentType() {
                return URLConnection.guessContentTypeFromName(file.getName());
            }
        };
    }
}
