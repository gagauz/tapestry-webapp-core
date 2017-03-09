package org.gagauz.tapestry.web.services.modules;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ValidationConstraintGenerator;
import org.gagauz.tapestry.hibernate.HibernateCommonRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateModule {

	private static final Logger LOG = LoggerFactory.getLogger(HibernateModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(HibernateCommonRequestFilter.class);
	}

	@Contribute(ComponentRequestHandler.class)
	public void contributeComponentRequestHandler(OrderedConfiguration<ComponentRequestFilter> configuration,
			HibernateCommonRequestFilter hibernateFilter) {
		configuration.add("HibernateFilter", hibernateFilter, "before:*");
	}

	@Contribute(ValidationConstraintGenerator.class)
	public void contributeValidationConstraintGenerator(
			OrderedConfiguration<ValidationConstraintGenerator> configuration) {
		configuration.addInstance("HibernateColumnsValidators", HibernateColumnValidationConstraintGenerator.class);
	}
	/*
	 * @Match("FieldValidatorDefaultSource")
	 *
	 * @org.apache.tapestry5.ioc.annotations.Order("after:*") public static
	 * FieldValidatorDefaultSource decorate(final FieldValidatorSource
	 * validationSource, final FieldValidatorDefaultSource defaultSource, final
	 * Environment environment) {
	 *
	 * final ListMultimap<Class, ValidatorFactory> validatorMap =
	 * Multimaps.newArrayListMultimap(); C.forEach(validators, v -> {
	 * validatorMap.put(v.getAnnotationClass(), v); });
	 *
	 * return new FieldValidatorDefaultSource() {
	 *
	 * private final Map<String, FieldValidator<?>> validatorCache = new
	 * HashMap<>();
	 *
	 * boolean isHibernateEntity(Class<?> clazz) { return
	 * clazz.getAnnotation(Entity.class) != null ||
	 * clazz.getAnnotation(Embeddable.class) != null; }
	 *
	 * @SuppressWarnings("rawtypes")
	 *
	 * @Override public FieldValidator createDefaultValidator(Field field,
	 * String overrideId, Messages overrideMessages, Locale locale, Class
	 * propertyType, AnnotationProvider propertyAnnotations) {
	 *
	 * FieldValidator defaultValidator =
	 * defaultSource.createDefaultValidator(field, overrideId, overrideMessages,
	 * locale, propertyType, propertyAnnotations); BeanValidationContext context
	 * = environment.peek(BeanValidationContext.class); if (null != context) {
	 * Class<?> beanClass = context.getBeanType(); final String lookUpKey =
	 * propertyType.getName() + ' ' + beanClass.getName() + '.' + overrideId;
	 * FieldValidator validator = validatorCache.get(lookUpKey); if (null !=
	 * validator) { LOG.debug("Found cached validator for {}", lookUpKey);
	 * return validator; } return createValidators(beanClass, overrideId, field,
	 * defaultValidator, lookUpKey); }
	 *
	 * return defaultValidator;
	 *
	 * }
	 *
	 * private FieldValidator<?> createValidators(Class<?> beanClass, String
	 * overrideId, Field field, FieldValidator<?> defaultValidator, final String
	 * lookUpKey) { if (isHibernateEntity(beanClass)) { List<FieldValidator>
	 * validators = new LinkedList<>();
	 * LOG.debug("Create validators for entity {}", beanClass);
	 *
	 * lookupAnnotatedFieldOrMethod(beanClass, overrideId).forEach(annotation ->
	 * { validatorMap.get(annotation.annotationType()).forEach(vf -> {
	 * FieldValidator validator = vf.createValidator(beanClass, field,
	 * annotation); if (null != validator) { validators.add(validator); } });
	 * });
	 *
	 * FieldValidator<?> validator = !validators.isEmpty() ? new
	 * CompositeFieldValidator(validators) : defaultValidator;
	 *
	 * validatorCache.put(lookUpKey, validator);
	 *
	 * return validator; } return defaultValidator; }
	 *
	 * private List<Annotation> lookupAnnotatedFieldOrMethod(Class<?> beanClass,
	 * String fieldName) { Annotation[] annotations = null; try {
	 * java.lang.reflect.Field field0 = beanClass.getField(fieldName);
	 * annotations = field0.getAnnotations(); } catch (Exception e) { } if (0 ==
	 * annotations.length) { try { Method method = beanClass.getMethod("get" +
	 * StringUtils.capitalize(fieldName)); annotations =
	 * method.getAnnotations(); } catch (Exception e) { } } return
	 * Arrays.asList(annotations); }
	 *
	 * @Override public FieldValidator createDefaultValidator(ComponentResources
	 * resources, String parameterName) { return
	 * defaultSource.createDefaultValidator(resources, parameterName); }
	 *
	 * }; }
	 */
}
