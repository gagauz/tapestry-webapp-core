package org.gagauz.tapestry.web.services.model;

import org.gagauz.hibernate.model.enums.MailStatus;
import org.gagauz.hibernate.model.enums.MessageType;

public class MailFilter {
	public MessageType type;
	public MailStatus status;
	public String to;
	public String subject;
}