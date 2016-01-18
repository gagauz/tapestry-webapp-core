package org.gagauz.tapestry.web.components;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.FieldValidationSupport;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Mixin;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.corelib.mixins.RenderDisabled;
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Predicate;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.util.EnumSelectModel;
import org.gagauz.tapestry.encoder.AbstractCollectionValueEncoder;
import org.gagauz.tapestry.encoder.CollectionValueEncoder;
import org.gagauz.utils.C;

/**
 *
 * @author mg
 */
@Import(library = "MultiTabSelect.js")
public class MultiTabSelect extends AbstractField {

	protected class Renderer implements SelectModelVisitor {

		protected boolean isSource;
		protected MarkupWriter writer;

		public Renderer(MarkupWriter writer, boolean source) {
			this.writer = writer;
			this.isSource = source;
		}

		@Override
		public void beginOptionGroup(OptionGroupModel groupModel) {
		}

		@Override
		public void endOptionGroup(OptionGroupModel groupModel) {
		}

		@Override
		public void option(OptionModel optionModel) {
			Object optionValue = optionModel.getValue();
			String clientValue = encoder.toClient(optionValue);

			if (isSource ^ isSelectedValue(optionValue)) {
				writer.element("option", "value", clientValue);
				writeDisabled(optionModel.isDisabled());
				writeAttributes(optionModel.getAttributes());
				writer.write(optionModel.getLabel());
				writer.end();
			}
		}

		protected void writeDisabled(boolean disabled) {
			if (disabled)
				writer.attributes("disabled", "disabled");
		}

		protected void writeAttributes(Map<String, String> attributes) {
			if (attributes == null)
				return;

			for (Map.Entry<String, String> e : attributes.entrySet())
				writer.attributes(e.getKey(), e.getValue());
		}

	}

	@Parameter
	protected CollectionValueEncoder encoder;

	@Inject
	private ComponentDefaultProvider defaultProvider;

	@Parameter(required = true, allowNull = false)
	protected SelectModel model;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "10")
	protected int size;

	@Inject
	protected Request request;

	@Inject
	protected ComponentResources resources;

	@Environmental
	protected ValidationTracker tracker;

	@Parameter(defaultPrefix = BindingConstants.VALIDATE)
	protected FieldValidator<Object> validate;

	@Parameter(required = true, principal = true, autoconnect = true)
	protected Collection<?> value;

	@Inject
	protected FieldValidationSupport fieldValidationSupport;

	@Inject
	protected JavaScriptSupport javaScriptSupport;

	@Mixin
	private RenderDisabled renderDisabled;

	protected boolean isSelectedValue(Object optionValue) {
		return value != null && value.contains(optionValue);
	}

	@Override
	protected void processSubmission(String controlName) {
		String[] params = request.getParameters(controlName);
		Collection<?> submittedValue = toValue(params);

		putPropertyNameIntoBeanValidationContext("value");

		try {
			fieldValidationSupport.validate(submittedValue, resources, validate);

			value = submittedValue;
		} catch (ValidationException ex) {
			tracker.recordError(this, ex.getMessage());
		}

		removePropertyNameFromBeanValidationContext();
	}

	protected void afterRender(MarkupWriter writer) {
		writer.end();
	}

	protected void createTab(MarkupWriter writer, boolean source, boolean destination2) {

		String id = source ? getClientId() + "-source" : destination2 ? getClientId() + "-destination2" : getClientId() + "-destination";
		String name = source ? getControlName() + "-source" : destination2 ? getControlName() + "-destination2" : getControlName()
				+ "-destination";

		writer.element("div", "class", "multitab-left-tab");
		writer.element("div", "class", "multitab-head", "id", getClientId());
		writer.writeRaw(source ? "Source" : "Selected");
		writer.end();// label
		writer.element("select", "name", name, "id", id, "multiple", true, "size", getSize());
		options(writer, source, destination2);
		writer.end();// select
		writer.end();// left-tab
	}

	protected void button(MarkupWriter writer, String label, String id) {
		writer.element("button", "id", id);
		writer.writeRaw(label);
		writer.end();
	}

	protected void beginRender(MarkupWriter writer) {

		String id = getClientId();

		writer.element("div", "class", "multitab-container", "id", getClientId());

		resources.renderInformalParameters(writer);

		createTab(writer, true, false);

		writer.element("div", "class", "multitab-button-tab");

		button(writer, ">>", id + "-selectall");
		button(writer, ">", id + "-select");
		button(writer, "<", id + "-deselect");
		button(writer, "<<", id + "-deselectall");
		// button(writer, "Up", id + "-up");
		// button(writer, "Down", id + "-down");

		writer.end();// button-tab

		createTab(writer, false, false);

		writer.element("div", "id", id + "-values", "style", "display:none");
		writer.end();

		putPropertyNameIntoBeanValidationContext("value");

		validate.render(writer);

		removePropertyNameFromBeanValidationContext();

		decorateInsideField();

		final List<String> valuesOrderList = C.newArrayList();
		final List<String> destinationList = C.newArrayList();

		F.flow(model.getOptions()).filter(new Predicate<OptionModel>() {
			@Override
			public boolean accept(OptionModel param) {
				if (isSelectedValue(param.getValue())) {
					destinationList.add(encoder.toClient(param.getValue()));
				} else {
					valuesOrderList.add(encoder.toClient(param.getValue()));
				}
				return false;
			}
		});

		JSONArray src = new JSONArray(valuesOrderList.toArray());
		JSONArray dst = new JSONArray(destinationList.toArray());

		javaScriptSupport.addScript("new Tapestry.MultiTabSelect('%s', %s, %s);", id, dst.toCompactString(), src.toCompactString());
		// Disabled is via a mixin
	}

	protected void options(MarkupWriter writer, boolean source, boolean destination2) {
		SelectModelVisitor renderer = new Renderer(writer, source);
		model.visit(renderer);
	}

	protected Collection<?> toValue(String[] submittedValues) {
		return encoder.toValues(submittedValues);
	}

	ValueEncoder<Object> defaultEncoder() {
		final ValueEncoder<?> defaultEncoder = defaultProvider.defaultValueEncoder("value", resources);
		return new AbstractCollectionValueEncoder<Object>() {
			@Override
			public Object toValue(String string) {
				return defaultEncoder.toValue(string);
			}

		};
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	SelectModel defaultModel() {
		Class valueType = resources.getBoundType("value");

		if (valueType == null) {
			return null;
		}

		if (Enum.class.isAssignableFrom(valueType)) {
			return new EnumSelectModel(valueType, resources.getContainerMessages());
		}

		return null;
	}

	Binding defaultValidate() {
		return defaultProvider.defaultValidatorBinding("value", resources);
	}

	@Override
	public boolean isRequired() {
		return validate.isRequired();
	}

	protected int getSize() {
		return 0 == size ? model.getOptions().size() : size;
	}
}
