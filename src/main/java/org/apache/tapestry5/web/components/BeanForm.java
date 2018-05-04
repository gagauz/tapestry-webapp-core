package org.apache.tapestry5.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.xl0e.tapestry.util.FormHelper;

public class BeanForm extends BeanEditForm {

    @Parameter(defaultPrefix = BindingConstants.BLOCK)
    @Property
    private Block title;

    @Parameter
    @Property(write = false)
    private boolean showErrors;

    @Parameter(value = "literal:fa fa-floppy-o", defaultPrefix = BindingConstants.LITERAL)
    @Property(write = false)
    private String submitIcon;

    @Parameter(defaultPrefix = BindingConstants.BLOCK)
    @Property
    private Block buttons;

    @Inject
    private ComponentResources resources;

    @Environmental
    private ValidationTracker tracker;

    public boolean isValid(final String fieldName) {
        return FormHelper.isValid(tracker, fieldName);
    }

    public void recordErrorCode(final String code, Object... args) {
        FormHelper.recordErrorCode(tracker, resources, code, args);
    }

    public void recordErrorCode(final String fieldName, final String code, Object... args) {
        FormHelper.recordErrorCodeForField(tracker, resources, fieldName, code, args);
    }
}
