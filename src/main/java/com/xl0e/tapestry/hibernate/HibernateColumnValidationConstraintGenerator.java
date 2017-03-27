package com.xl0e.tapestry.hibernate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.services.ValidationConstraintGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xl0e.hibernate.annotations.Email;
import com.xl0e.util.C;

public class HibernateColumnValidationConstraintGenerator implements ValidationConstraintGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(HibernateColumnValidationConstraintGenerator.class);

	@Override
	public List<String> buildConstraints(Class propertyType, AnnotationProvider annotationProvider) {

		Set<String> validators = C.hashSet();
		getAnnotation(annotationProvider, Email.class).ifPresent(annotation -> {
			validators.add("emailhost");
		});
		getAnnotation(annotationProvider, Column.class).ifPresent(annotation -> {
			if (!annotation.nullable()) {
				validators.add("required");
			}
			if (annotation.length() > 0) {
				validators.add("maxlength=" + annotation.length());
			}
		});
		getAnnotation(annotationProvider, JoinColumn.class).ifPresent(annotation -> {
			if (!annotation.nullable()) {
				validators.add("required");
			}
		});
		getAnnotation(annotationProvider, ManyToOne.class).ifPresent(annotation -> {
			if (!annotation.optional()) {
				validators.add("required");
			}
		});
		getAnnotation(annotationProvider, OneToOne.class).ifPresent(annotation -> {
			if (!annotation.optional()) {
				validators.add("required");
			}
		});
		return new ArrayList<>(validators);
	}

	private <A extends Annotation> Optional<A> getAnnotation(AnnotationProvider annotationProvider,
			Class<A> annotationClass) {
		return Optional.ofNullable(annotationProvider.getAnnotation(annotationClass));
	}

}
