package org.gagauz.tapestry.web.services.modules;

import java.lang.annotation.Annotation;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.FieldValidator;

public interface ValidatorFactory<X extends Annotation> {

	Class<X> getAnnotationClass();

	FieldValidator createValidator(Class<?> beanClass, Field field, X annotation);

}
