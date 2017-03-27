package com.xl0e.tapestry.web.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Environment;

import java.util.ArrayDeque;
import java.util.Queue;

public class AppendBlock {

    public static class AppendBlockHolder {
        private final Queue<Block> bodies = new ArrayDeque<Block>();

        public Queue<Block> getBodies() {
            return bodies;
        }
    }

    @Parameter
    private boolean output;

    @Inject
    private Environment environment;

    @Inject
    private ComponentResources resources;

    private AppendBlockHolder holder;

    Object beginRender() {
        if (output) {
            holder = environment.peek(AppendBlockHolder.class);
            if (null != holder) {
                return holder.getBodies().poll();
            }
        }
        return null;
    }

    boolean afterRender() {
        return !(output && null != holder && !holder.getBodies().isEmpty());
    }

    boolean beforeRenderBody() {
        if (!output) {
            Block body = resources.getBody();
            AppendBlockHolder holder = environment.peek(AppendBlockHolder.class);
            if (null == holder) {
                holder = new AppendBlockHolder();
                environment.push(AppendBlockHolder.class, holder);
            }
            holder.getBodies().add(body);
        }
        return output;
    }
}
