package org.gagauz.tapestry.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BeanForm extends BeanEditForm {
    @Parameter
    @Property(write = false)
    private boolean showErrors;

    @Parameter(value = "literal:fa fa-floppy-o", defaultPrefix = BindingConstants.LITERAL)
    @Property(write = false)
    private String submitIcon;

    @Parameter(autoconnect = true, defaultPrefix = BindingConstants.BLOCK)
    @Property
    private Block buttons;

    @Inject
    private ComponentResources resources;

    @Environmental
    private ValidationTracker tracker;

    public boolean isValid(final String fieldName) {
        return !tracker.inError(getField(fieldName));
    }

    private Field getField(String fieldName) {
        return new Field() {

            @Override
            public String getClientId() {
                return fieldName;
            }

            @Override
            public boolean isRequired() {
                return false;
            }

            @Override
            public boolean isDisabled() {
                return false;
            }

            @Override
            public String getLabel() {
                return null;
            }

            @Override
            public String getControlName() {
                return fieldName;
            }
        };
    }

    public void recordErrorCode(final String code, Object... args) {
        String message = args.length > 0
                ? resources.getContainerMessages().format(code, args)
                : resources.getContainerMessages().get(code);
    }

    public void recordErrorCode(final String fieldName, final String code, Object... args) {
        String message = args.length > 0
                ? resources.getContainerMessages().format(code, args)
                : resources.getContainerMessages().get(code);
        try {
            recordError(getField(fieldName), message);
        } catch (Exception e) {
        }
    }
}
