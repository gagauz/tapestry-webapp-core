package org.gagauz.tapestry.web.components;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRenderBody;
import org.apache.tapestry5.annotations.BeforeRenderBody;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.gagauz.hibernate.model.base.Parent;

@SupportsInformalParameters
public class TreeIterator<T extends Parent<T>> {
	@Parameter(required = true)
	private Iterable<T> source;

	@Parameter
	private T value;

	@Parameter(cache = false, value = "true")
	private boolean expand;

	@Parameter(value = "div", defaultPrefix = BindingConstants.LITERAL)
	private String element;

	@Parameter(cache = false)
	private int hierarchy = 0;

	private Deque<Iterator<T>> lastIterator;
	private Iterator<T> iterator;

	@SetupRender
	boolean setupRender() {
		if (null != source) {
			iterator = source.iterator();
			return iterator.hasNext();
		}
		return false;
	}

	@BeforeRenderBody
	void beforeRenderBody(MarkupWriter writer) {
		value = iterator.next();
	}

	@AfterRenderBody
	boolean afterRenderBody(MarkupWriter writer) {
		if (expand && enter(writer)) {
			;
		} else if (!iterator.hasNext()) {
			exit(writer);
		}
		return !iterator.hasNext();
	}

	private boolean enter(MarkupWriter writer) {
		if (!value.getChildren().isEmpty()) {
			if (null == lastIterator) {
				lastIterator = new ArrayDeque<>();
			}
			lastIterator.add(iterator);
			iterator = value.getChildren().iterator();
			hierarchy++;
			writer.element(element);
			return true;
		}
		return false;
	}

	private void exit(MarkupWriter writer) {
		if (null != lastIterator && !lastIterator.isEmpty()) {
			iterator = lastIterator.removeLast();
			hierarchy--;
			writer.end();
			if (!iterator.hasNext()) {
				exit(writer);
			}
		}
	}

	@CleanupRender
	void cleanupRender(MarkupWriter writer) {
		while (hierarchy > 0) {
			writer.end();
			hierarchy--;
		}
	}
}
