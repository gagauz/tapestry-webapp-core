package org.apache.tapestry5.web.mixins;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ValidationDecorator;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.HeartbeatDeferred;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.web.MySymbolConstants;
import org.apache.tapestry5.services.ComponentDefaultProvider;

/**
 * Applied to a {@link org.apache.tapestry5.Field}, this provides the outer
 * layers of markup to correctly render text fields, selects, and textareas
 * using Bootstrap: an outer {@code <div class="field-group">} containing a
 * {@code <label class="control-label">} and the field itself. Actually, the
 * class attribute of the div is defined by the
 * {@link SymbolConstants#FORM_GROUP_WRAPPER_CSS_CLASS} and the class attribute
 * of label is defined by the
 * {@link SymbolConstants#FORM_GROUP_LABEL_CSS_CLASS}. <code>field-group</code>
 * and <code>control-label</code> are the default values. As with the
 * {@link org.apache.tapestry5.corelib.components.Label} component, the
 * {@code for} attribute is set (after the field itself renders).
 *
 *
 * You can also use the
 * {@link SymbolConstants#FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_NAME} symbol to
 * optionally wrap the input field in an element and
 * {@link SymbolConstants#FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_CSS_CLASS} to
 * give it a CSS class. This is useful for Bootstrap form-horizontal forms.
 * Setting {@link SymbolConstants#FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_NAME} to
 * <code>div</code>,
 * {@link SymbolConstants#FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_CSS_CLASS} to
 * <code>col-sm-10</code> and {@link SymbolConstants#FORM_GROUP_LABEL_CSS_CLASS}
 * to <code>col-sm-2</code> will generate labels 2 columns wide and form fields
 * 10 columns wide.
 *
 *
 * This component is not appropriate for radio buttons or checkboxes as they use
 * a different class on the outermost element ("radio" or "checkbox") and next
 * the element inside the {@code <label>}.
 *
 *
 * @tapestrydoc
 * @since 5.4
 * @see SymbolConstants#FORM_GROUP_WRAPPER_CSS_CLASS
 * @see SymbolConstants#FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_NAME
 * @see SymbolConstants#FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_CSS_CLASS
 * @see SymbolConstants#FORM_GROUP_LABEL_CSS_CLASS
 * @see SymbolConstants#FORM_FIELD_CSS_CLASS
 */
public class MyFormGroup {
    @InjectContainer
    private Component container;

    private Field field;

    @Inject
    @Symbol(SymbolConstants.FORM_GROUP_LABEL_CSS_CLASS)
    private String labelCssClass;

    @Inject
    @Symbol(MySymbolConstants.FORM_CHECKBOX_LABEL_CSS_CLASS)
    private String checkboxLabelCssClass;

    @Inject
    @Symbol(SymbolConstants.FORM_GROUP_WRAPPER_CSS_CLASS)
    private String divCssClass;

    @Inject
    @Symbol(SymbolConstants.FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_NAME)
    private String fieldWrapperElementName;

    @Inject
    @Symbol(SymbolConstants.FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_CSS_CLASS)
    private String fieldWrapperElementCssClass;

    @Inject
    protected ComponentDefaultProvider defaultProvider;

    private Element label;

    private Element fieldWrapper;

    private boolean checkbox;

    @Environmental
    private ValidationDecorator decorator;

    void beginRender(MarkupWriter writer) {
        writer.element("div", "class", divCssClass);

        if (container instanceof Field) {
            field = (Field) container;
        decorator.beforeLabel(field);

        label = writer.element("label");

        checkbox = field instanceof Checkbox;

        if (!checkbox) {
            writer.end();
        }

        fillInLabelAttributes();

        decorator.afterLabel(field);

            if (!checkbox) {
                wrapContainer(writer);
            }
        } else {
            label = writer.element("label");
            label.attribute("for", container.getComponentResources().getId());
            label.attribute("class", labelCssClass);
            label.text(defaultLabel());
            writer.end();

            wrapContainer(writer);
        }
    }

    private void wrapContainer(MarkupWriter writer) {
        if (fieldWrapperElementName.length() > 0) {
            fieldWrapper = writer.element(fieldWrapperElementName);
            if (fieldWrapperElementCssClass.length() > 0) {
                fieldWrapper.attribute("class", fieldWrapperElementCssClass);
            }
        }
    }

    @HeartbeatDeferred
    void fillInLabelAttributes() {
        if (null != field) {
        label.attribute("for", field.getClientId());
        label.attribute("class", checkbox
                ? checkboxLabelCssClass
                : labelCssClass);
        label.text(field.getLabel());
    }
    }

    void afterRender(MarkupWriter writer) {
        if (fieldWrapper != null) {
            writer.end(); // field wrapper
        }
        if (checkbox) {
            writer.end(); // label end
        }
        writer.end(); // div.form-group
    }

    final String defaultLabel() {
        return defaultProvider.defaultLabel(container.getComponentResources());
    }
}
