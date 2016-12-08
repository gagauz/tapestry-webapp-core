package org.gagauz.tapestry.web.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;
import org.gagauz.tapestry.web.config.Global;
import org.gagauz.tapestry.web.filter.UploadFilter;

public class UploadedFiles {


    Object onActivate(final EventContext ctx) {
        return new StreamResponse() {

            private File file;

            @Override
            public void prepareResponse(Response response) {
                file = new File(UploadFilter.getRepository(Global.getRequest()), ctx.get(String.class, 0));
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
