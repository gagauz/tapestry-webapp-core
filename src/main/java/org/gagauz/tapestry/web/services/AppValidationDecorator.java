package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.BaseValidationDecorator;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.services.Environment;

public class AppValidationDecorator extends BaseValidationDecorator {
    private MarkupWriter markupWriter;
    private final Environment environment;

    public AppValidationDecorator(MarkupWriter markupWriter, Environment environment) {
        this.markupWriter = markupWriter;
        this.environment = environment;
    }

    @Override
    public void afterField(Field field) {
        if (field == null)
            return;

        if (inError(field)) {
            Element group = markupWriter.getElement().getContainer();
            String clazz = group.getAttribute("class");
            if (clazz != null && clazz.contains("form-group")) {
                group.addClassName("has-error");
            }
        }
    }

    private boolean inError(Field field) {
        ValidationTracker tracker = environment.peekRequired(ValidationTracker.class);
        return tracker.inError(field);
    }
}
