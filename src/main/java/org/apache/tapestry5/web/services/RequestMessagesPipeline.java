package org.apache.tapestry5.web.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.ApplicationStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestMessagesPipeline {

    protected static class MessagesHolder extends CopyOnWriteArrayList<String> {
        private static final long serialVersionUID = -3242954989592225882L;

        public List<String> truncate() {
            List<String> result = new ArrayList<String>(this);
            super.clear();
            return result;
        }
    }

    protected final ApplicationStateManager stateManager;
    protected final Messages messages;
    protected static final MessagesHolder EMPTY = new MessagesHolder();

    public RequestMessagesPipeline(ApplicationStateManager stateManager, Messages messages) {
        this.stateManager = stateManager;
        this.messages = messages;
    }

    public void add(String message) {
        if (!StringUtils.isBlank(message)) {
            getHolder(true).add(message);
        }
    }

    public void addAll(List<String> errors) {
        getHolder(true).addAll(errors);
    }

    public List<String> getMessages() {
        return getHolder(false).truncate();
    }

    public boolean isEmpty() {
        return getHolder(false).isEmpty();
    }

    public void addByKey(String format, Object... formatParameters) {
        if (formatParameters.length > 0) {
            add(messages.format(format, formatParameters));
        } else {
            add(messages.get(format));
        }
    }

    public void clear() {
        getHolder(false).clear();
    }

    protected MessagesHolder getHolder(boolean create) {
        MessagesHolder holder = stateManager.getIfExists(MessagesHolder.class);
        if (holder == null) {
            if (create) {
                holder = new MessagesHolder();
                stateManager.set(MessagesHolder.class, holder);
            } else {
                holder = EMPTY;
            }
        }
        return holder;
    }
}
