package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.ioc.Messages;

public class ToolsService {

	private final String day;
	private final String hour;
	private final String minute;
	private final String second;
	private final Messages messages;

	public ToolsService(Messages messages) {
		this.messages = messages;
		this.day = messages.get("day");
		this.hour = messages.get("hour");
		this.minute = messages.get("minute");
		this.second = messages.get("second");
	}

	public String getTime(int time) {
		if (time == 0) {
			return "0";
		}
		StringBuffer sb = new StringBuffer();

		if (time > 86400) {
			sb.append(time / 86400).append(day);
			time = time % 86400;
		}

		if (time > 3600) {
			sb.append(time / 3600).append(hour);
			time = time % 3600;
		}
		if (time > 60) {
			sb.append(time / 60).append(minute);
			time = time % 60;
		}

		if (time > 0) {
			sb.append(time).append(second);
		}

		return sb.toString();
	}

	protected String addZero(int value) {
		if (value < 10) {
			return "0" + value;
		}
		return "" + value;
	}

	public String decline(final String key, final Number count) {
		long n = Math.abs(count.intValue()) % 100;
		long n1 = n % 10;
		if (n > 10 && n < 20) {
			return messages.format(key + "_0", count);
		} else if (n1 > 1 && n1 < 5) {
			return messages.format(key + "_2", count);
		} else if (n1 == 1) {
			return messages.format(key + "_1", count);
		}
		return messages.format(key + "_0", count);
	}
}
