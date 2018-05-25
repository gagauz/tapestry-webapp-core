package org.apache.tapestry5.web.services.annotation;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.FieldHandle;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticField;
import org.apache.tapestry5.services.ComponentEventHandler;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;

import com.xl0e.util.C;

public class PageContextTransformer implements ComponentClassTransformWorker2 {

    private static final Comparator<PlasticField> INDEX_COMPARATOR = (field1, field2) -> {
        int index1 = field1.getAnnotation(PageContext.class).index();
        int index2 = field2.getAnnotation(PageContext.class).index();

        int compare = index1 < index2 ? -1 : (index1 > index2 ? 1 : 0);
        if (compare == 0) {
            compare = field1.getName().compareTo(field2.getName());
        }
        return compare;
    };

    @Override
    public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model) {
        List<PlasticField> fields = plasticClass.getFieldsWithAnnotation(PageContext.class);

        if (!fields.isEmpty()) {
            transformFields(support, fields);
        }
    }

    private void transformFields(TransformationSupport support, List<PlasticField> fields) {
        List<PlasticField> sortedFields = C.arrayList(fields);
        Collections.sort(sortedFields, INDEX_COMPARATOR);
        validateSortedFields(sortedFields);

        PlasticField firstField = sortedFields.get(0);
        PageContext firstAnnotation = firstField.getAnnotation(PageContext.class);

        // these arrays reduce memory usage and allow the PlasticField instances to be garbage collected
        FieldHandle[] handles = new FieldHandle[sortedFields.size()];
        String[] typeNames = new String[sortedFields.size()];
        int[] indexes = new int[sortedFields.size()];

        int i = 0;
        for (PlasticField field : sortedFields) {
            handles[i] = field.getHandle();
            typeNames[i] = field.getTypeName();
            indexes[i] = field.getAnnotation(PageContext.class).index();
            ++i;
        }

        if (firstAnnotation.activate()) {
            support.addEventHandler(EventConstants.ACTIVATE, firstAnnotation.index() + 1,
                    "PageActivationContextWorker activate event handler", createActivationHandler(handles, typeNames, indexes));
        }

        if (firstAnnotation.passivate()) {
            support.addEventHandler(EventConstants.PASSIVATE, firstAnnotation.index(),
                    "PageActivationContextWorker passivate event handler", createPassivateHandler(handles, indexes));
        }

        // We don't claim the field, and other workers may even replace it with a FieldConduit.
    }

    private void validateSortedFields(List<PlasticField> sortedFields) {
        List<Integer> expectedIndexes = C.arrayList();
        List<Integer> actualIndexes = C.arrayList();
        Set<Boolean> activates = C.hashSet();
        Set<Boolean> passivates = C.hashSet();

        for (int i = 0; i < sortedFields.size(); ++i) {
            PlasticField field = sortedFields.get(i);
            PageContext annotation = field.getAnnotation(PageContext.class);
            expectedIndexes.add(i);
            actualIndexes.add(annotation.index());
            activates.add(annotation.activate());
            passivates.add(annotation.passivate());
        }

        List<String> errors = C.arrayList();
        if (activates.size() > 1) {
            errors.add("Illegal values for 'activate' (all fields must have the same value)");
        }
        if (passivates.size() > 1) {
            errors.add("Illegal values for 'passivate' (all fields must have the same value)");
        }
        if (!errors.isEmpty()) {
            throw new RuntimeException(String.format("Invalid values for @PageActivationContext: %s", InternalUtils.join(errors)));
        }
    }

    private static ComponentEventHandler createActivationHandler(FieldHandle[] handles, String[] fieldTypes, int[] indexes) {
        return (instance, event) -> {
            for (int i = 0; i < handles.length; ++i) {
                if (indexes[i] >= event.getEventContext().getCount()) {
                    break;
                }
                String fieldType = fieldTypes[i];
                FieldHandle handle = handles[i];
                Object value = event.coerceContext(indexes[i], fieldType);
                handle.set(instance, value);
            }
        };
    }

    private static ComponentEventHandler createPassivateHandler(final FieldHandle[] handles, int[] indexes) {
        return (instance, event) -> {
            Object result;
            if (handles.length == 1) {
                // simple / common case for a single @PageContext
                result = handles[0].get(instance);
            } else {
                LinkedList<Object> list = C.newLinkedList();

                // iterate backwards
                for (int i = 0; i < handles.length; i++) {
                    FieldHandle handle = handles[i];
                    Object value = handle.get(instance);

                    // ignore trailing nulls
                    if (value != null || !list.isEmpty()) {
                        list.add(indexes[i], value);
                    }
                }
                result = list.isEmpty() ? null : list;
            }

            event.storeResult(result);
        };
    }
}
