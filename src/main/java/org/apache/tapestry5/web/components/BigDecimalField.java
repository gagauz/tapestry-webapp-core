package org.apache.tapestry5.web.components;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.corelib.base.AbstractTextField;

public class BigDecimalField extends AbstractTextField {

    @Override
    protected void writeFieldTag(MarkupWriter writer, String value) {
        writer.element("input",
                "type", "text",
                "name", getControlName(),
                "class", cssClass,
                "id", getClientId(),
                "value", value,
                "size", getWidth());
    }

    final void afterRender(MarkupWriter writer) {
        writer.end(); // input
    }
}
