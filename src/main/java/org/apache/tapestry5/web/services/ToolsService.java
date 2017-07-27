package org.apache.tapestry5.web.services;

import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ToolsService {

    private String day;
    private String hour;
    private String minute;
    private String second;

    @Inject
    private Messages messages;

    String getDay() {
        if (null == day) {
            day = messages.get("day");
        }
        return day;
    }

    String getHour()
    {
        if (null == hour) {
            hour = messages.get("hour");
        }
        return hour;
    }

    String getMinute() {
        if (null == minute) {
            minute = messages.get("minute");
        }
        return minute;
    }

    String getSecond() {
        if (null == second) {
            second = messages.get("second");
        }
        return second;
    }


    public String getTime(int time) {
        if (time == 0) {
            return "0";
        }
        StringBuffer sb = new StringBuffer();

        if (time > 86400) {
            sb.append(time / 86400).append(getDay());
            time = time % 86400;
        }

        if (time > 3600) {
            sb.append(time / 3600).append(getHour());
            time = time % 3600;
        }
        if (time > 60) {
            sb.append(time / 60).append(getMinute());
            time = time % 60;
        }

        if (time > 0) {
            sb.append(time).append(getSecond());
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
