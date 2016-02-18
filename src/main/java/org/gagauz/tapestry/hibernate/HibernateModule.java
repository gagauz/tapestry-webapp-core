package org.gagauz.tapestry.hibernate;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.internal.BeanValidationContext;
import org.apache.tapestry5.internal.services.CompositeFieldValidator;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.FieldValidatorDefaultSource;
import org.apache.tapestry5.services.FieldValidatorSource;

public class HibernateModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(HibernateCommonRequestFilter.class);
    }

    @Contribute(ComponentRequestHandler.class)
    public void contributeComponentRequestHandler(OrderedConfiguration<ComponentRequestFilter> configuration, HibernateCommonRequestFilter hibernateFilter) {
        configuration.add("HibernateFilter", hibernateFilter, "before:*");
    }

    @Match("FieldValidatorDefaultSource")
    @org.apache.tapestry5.ioc.annotations.Order("after:*")
    public static FieldValidatorDefaultSource decorate(final FieldValidatorSource validationSource, final FieldValidatorDefaultSource defaultSource, final Environment environment) {

        return new FieldValidatorDefaultSource() {

            private final Map<String, FieldValidator<?>> validatorCache = new HashMap<>();

            boolean isHibernateEntity(Class<?> clazz) {
                return clazz.getAnnotation(Entity.class) != null;
            }

            <A extends Annotation> A getAnnotation(AccessibleObject clazz, Class<A> a) {
                return clazz.getAnnotation(a);
            }

            @SuppressWarnings("rawtypes")
            @Override
            public FieldValidator createDefaultValidator(Field field, String overrideId, Messages overrideMessages, Locale locale, Class propertyType, AnnotationProvider propertyAnnotations) {

                FieldValidator defaultValidator = defaultSource.createDefaultValidator(field, overrideId, overrideMessages, locale,
                        propertyType, propertyAnnotations);
                BeanValidationContext context = environment.peek(BeanValidationContext.class);
                if (null != context) {
                    Class<?> beanClass = context.getBeanType();
                    final String lookUpKey = propertyType.getName() + ' ' + beanClass.getName() + '.' + overrideId;
                    FieldValidator validator = validatorCache.get(lookUpKey);
                    if (null != validator) {
                        System.out.println("***** Found cached validator for " + lookUpKey);
                        return validator;
                    }

                    List<FieldValidator> validators = new LinkedList<FieldValidator>();
                    if (isHibernateEntity(beanClass)) {

                        System.out.println("---------------------------------------------------------------------------");
                        System.out.println("Create validators for entity " + beanClass.getSimpleName());

                        Column column = null;
                        JoinColumn joinColumn = null;
                        try {
                            java.lang.reflect.Field field0 = beanClass.getField(overrideId);
                            column = getAnnotation(field0, Column.class);
                            joinColumn = getAnnotation(field0, JoinColumn.class);
                        } catch (Exception e) {
                        }
                        if (null == column && null == joinColumn) {
                            try {
                                Method method = beanClass.getMethod("get" + StringUtils.capitalize(overrideId));
                                column = getAnnotation(method, Column.class);
                                joinColumn = getAnnotation(method, JoinColumn.class);
                            } catch (Exception e) {
                            }
                        }
                        if (null != column) {
                            if (!column.nullable()) {
                                System.out.println("\t add required validator for " + field.getControlName());
                                validators.add(validationSource.createValidator(field, "required", null));
                            }
                            if (column.length() > 0) {
                                System.out.println("\t add maxlength validator for " + field.getControlName());
                                validators.add(validationSource.createValidator(field, "maxlength", String.valueOf(column.length())));
                            }
                        }
                        if (null != joinColumn) {
                            if (!joinColumn.nullable()) {
                                System.out.println("\t add required validator for " + field.getControlName());
                                validators.add(validationSource.createValidator(field, "required", null));
                            }
                        }

                        validator = !validators.isEmpty()
                                ? new CompositeFieldValidator(validators)
                                : defaultValidator;

                        validatorCache.put(lookUpKey, validator);

                        System.out.println("---------------------------------------------------------------------------");

                        return validator;
                    }
                }

                return defaultValidator;

            }

            @Override
            public FieldValidator createDefaultValidator(ComponentResources resources, String parameterName) {
                return defaultSource.createDefaultValidator(resources, parameterName);
            }
        };
    }
}
