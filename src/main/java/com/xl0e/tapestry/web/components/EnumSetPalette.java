package com.xl0e.tapestry.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.util.EnumSelectModel;
import org.apache.tapestry5.util.EnumValueEncoder;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class EnumSetPalette<E extends Enum<E>> {

    @Parameter(name = "set", required = true, allowNull = false)
    private EnumSet<E> setToModify;

    @Parameter(name = "enum", required = true, allowNull = false)
    private Class<E> enumClass;

    @Parameter
    private E[] enumConsts;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    @Property
    private String label;

    @Inject
    private TypeCoercer typeCoercer;

    @Inject
    private Messages messages;

    public List<E> getSetToModify() {
        return new ArrayList<E>(setToModify);
    }

    public void setSetToModify(List<E> selected) {
        this.setToModify.clear();
        if (!selected.isEmpty()) {
            this.setToModify.addAll(selected);
        }
    }

    public ValueEncoder<E> getEncoder() {
        return new EnumValueEncoder<E>(typeCoercer, enumClass);
    }

    public SelectModel getModel() {
        if (enumConsts != null) {
            return new EnumSelectModel(enumClass, messages, enumConsts);
        } else {
            return new EnumSelectModel(enumClass, messages);
        }
    }
}
