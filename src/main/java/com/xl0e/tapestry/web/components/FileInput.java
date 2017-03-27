package com.xl0e.tapestry.web.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Mixin;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.corelib.mixins.RenderDisabled;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.FieldValidatorDefaultSource;
import org.apache.tapestry5.upload.internal.services.UploadedFileItem;
import org.apache.tapestry5.upload.services.MultipartDecoder;
import org.apache.tapestry5.upload.services.UploadedFile;

import com.xl0e.tapestry.web.filter.UploadFilter;

public class FileInput extends AbstractField {

    public static final String MULTIPART_ENCTYPE = "multipart/form-data";

    /**
     * The uploaded file. Note: This is only guaranteed to be valid while
     * processing the form submission. Subsequently the content may have been
     * cleaned up.
     */
    @Parameter( principal = true, autoconnect = true)
    private UploadedFile value;

    @Parameter(principal = true, autoconnect = true)
    private UploadedFile[] values;

    @Parameter(value = "false")
    private boolean multiple;


    /**
     * The object that will perform input validation. The "validate:" binding
     * prefix is generally used to provide this object in a declarative fashion.
     */
    @Parameter(defaultPrefix = BindingConstants.VALIDATE)
    private FieldValidator<Object> validate;

    @Inject
    private MultipartDecoder decoder;

    @Inject
    private Locale locale;

    @Inject
    private HttpServletRequest request;

    @Mixin
    private RenderDisabled renderDisabled;

    /**
     * Computes a default value for the "validate" parameter using
     * {@link FieldValidatorDefaultSource}.
     */
    final Binding defaultValidate() {
        return defaultProvider.defaultValidatorBinding("value", resources);
    }


    @Override
    protected void processSubmission(String controlName) {

        List<UploadedFile> result = new ArrayList<>();
        for (FileItem fileItem : UploadFilter.getFileItems(controlName)) {
            UploadedFile file = new UploadedFileItem(fileItem);
            try {
                validate.validate(file);
            } catch (ValidationException e) {
                validationTracker.recordError(this, e.getMessage());
            }
            result.add(file);
        }
        if (resources.isBound("value")) {
            value = result.size() > 0 ? result.get(0) : null;
        } else {
            values = result.toArray(new UploadedFile[result.size()]);
        }
    }

    /**
     * Render the upload tags.
     *
     * @param writer
     *            Writer to output markup
     */
    protected void beginRender(MarkupWriter writer) {
        formSupport.setEncodingType(MULTIPART_ENCTYPE);

        Element upload = writer.element("input", "type", "file", "name", getControlName(), "id", getClientId(), "class",
                cssClass);

        if (multiple) {
            upload.forceAttributes("multiple", "true");
        }

        validate.render(writer);

        resources.renderInformalParameters(writer);

        decorateInsideField();

        // TAPESTRY-2453
        if (super.request.isXHR()) {
            javaScriptSupport.require("t5/core/injected-upload").with(getClientId());
        }
    }

    /** @since 5.4 */
    @Override
    public boolean isRequired() {
        return validate.isRequired();
    }

    public void afterRender(MarkupWriter writer) {
        writer.end();
    }
}
