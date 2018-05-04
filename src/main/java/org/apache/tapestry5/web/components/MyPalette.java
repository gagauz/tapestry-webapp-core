package org.apache.tapestry5.web.components;

import org.apache.tapestry5.corelib.components.Palette;

public class MyPalette extends Palette {

    @Override
    public String getInitialJSON() {
        try {
            return super.getInitialJSON();
        } catch (Exception e) {
            return "[]";
        }
    }
}
