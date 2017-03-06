package org.gagauz.tapestry.web.components;

import org.apache.tapestry5.corelib.components.Zone;

public class EmptyZone extends Zone {
	boolean beforeRenderBody() {
		return false;
	}
}
