package org.apache.tapestry5.security.api;

import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;

public interface AccessAttributesExtractor<A extends AccessAttributes> {

    /**
     * Extract access attributes from page
     *
     * @param plasticClass
     * @return
     */
    default A extract(PlasticClass plasticClass) {
        return extract(plasticClass, null);
    }

    /**
     * Extract access attributes from page method
     *
     * @param plasticClass
     * @param plasticMethod
     * @return
     */
    A extract(PlasticClass plasticClass, PlasticMethod plasticMethod);

}
