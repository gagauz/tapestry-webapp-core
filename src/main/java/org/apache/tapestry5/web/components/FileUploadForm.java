package org.apache.tapestry5.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.FieldValidatorSource;
import org.apache.tapestry5.upload.services.UploadedFile;

public class FileUploadForm {

    @Parameter(defaultPrefix = BindingConstants.LITERAL, principal = true)
    private String extension;

    @Parameter(defaultPrefix = BindingConstants.BLOCK)
    @Property
    private Block label;

    @Component
    protected Form uploadForm;

    @Component(parameters = { "validate=prop:validators" })
    private FileInput file;

    @Property
    protected UploadedFile fileValue;

    @Inject
    private FieldValidatorSource fieldValidatorSource;

    @Inject
    protected AlertManager alertManager;

    @Inject
    protected Messages messages;

    public FieldValidator<?> getValidators() {
        return fieldValidatorSource.createValidators(file, "required,extension=" + extension);
    }

    public void onSuccessFromUploadForm() {
    }
}
