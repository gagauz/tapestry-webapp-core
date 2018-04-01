package org.apache.tapestry5.web.components;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.dom.Element;

public class BigDecimalField extends TextField {

    @Override
    protected void writeFieldTag(MarkupWriter writer, String value) {
        Element el = writer.element("input",
                "type", "text",
                "name", getControlName(),
                "class", cssClass,
                "id", getClientId(),
                "value", value,
                "size", getWidth());
        System.out.println(el);
    }

    final void afterRender(MarkupWriter writer) {
        writer.end(); // input
    }
}
