package org.apache.tapestry5.web.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Environment;

import java.util.ArrayDeque;
import java.util.Queue;

public class IncludeBlock {

    public static class IncludeBlockHolder {
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

    private IncludeBlockHolder holder;

    Object beginRender() {
        if (output) {
            holder = environment.peek(IncludeBlockHolder.class);
            if (null != holder) {
                System.out.println("render");
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
            IncludeBlockHolder holder = environment.peek(IncludeBlockHolder.class);
            if (null == holder) {
                holder = new IncludeBlockHolder();
                environment.push(IncludeBlockHolder.class, holder);
            }
            holder.getBodies().add(body);
        }
        return output;
    }
}
