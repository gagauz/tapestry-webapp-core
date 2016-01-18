package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.*;
import org.apache.tapestry5.services.FormSupport;

public class EncoderTranslator<T> implements Translator<T> {

    private final ValueEncoder<T> encoder;
    private final Class<T> clazz;

    public EncoderTranslator(ValueEncoder<T> encoder, Class<T> clazz) {
        this.encoder = encoder;
        this.clazz = clazz;
    }

    @Override
    public String getMessageKey() {
        return null;
    }

    @Override
    public Class<T> getType() {
        return clazz;
    }

    @Override
    public String toClient(T value) {
        return encoder.toClient(value);
    };

    @Override
    public T parseClient(Field field, String clientValue, String message) throws ValidationException {
        T value = encoder.toValue(clientValue);
        if (field.isRequired() && null == value) {
            throw new ValidationException(message);
        }
        return value;
    }

    @Override
    public void render(Field field, String message, MarkupWriter writer, FormSupport formSupport) {

    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }
}
