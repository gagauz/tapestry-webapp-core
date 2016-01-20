package org.gagauz.tapestry.web.components;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.HeartbeatDeferred;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;

@SupportsInformalParameters
public class MyLabel {
    /**
     * The for parameter is used to identify the {@link Field} linked to this label (it is named this way because it
     * results in the for attribute of the label element).
     */
    @Parameter(name = "for", required = true, allowNull = false, defaultPrefix = BindingConstants.COMPONENT)
    private Field field;

    @Environmental
    private ValidationDecorator decorator;

    @Inject
    private ComponentResources resources;

    private Element labelElement;

    void beginRender(MarkupWriter writer) {
        decorator.beforeLabel(field);

        labelElement = writer.element("label");

        resources.renderInformalParameters(writer);

        // Since we don't know if the field has rendered yet, we need to defer writing the for and id
        // attributes until we know the field has rendered (and set its clientId property). That's
        // exactly what Heartbeat is for.

        updateAttributes();
    }

    @HeartbeatDeferred
    private void updateAttributes() {
        String fieldId = field.getClientId();

        labelElement.forceAttributes("for", fieldId);

        decorator.insideLabel(field, labelElement);
    }

    void afterRender(MarkupWriter writer) {
        // If the Label element has a body that renders some non-blank output, that takes precedence
        // over the label string provided by the field.

        writer.write(field.getLabel());

        writer.end(); // label

        decorator.afterLabel(field);
    }
}
