package org.apache.tapestry5.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

@Import(module = "bootstrap/modal")
public class Modal {
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String id;

	@Parameter(defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block header;

}
