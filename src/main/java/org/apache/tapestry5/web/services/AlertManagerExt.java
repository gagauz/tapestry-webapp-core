package org.apache.tapestry5.web.services;

import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.ioc.annotations.IncompatibleChange;

public interface AlertManagerExt {

	void successCode(String messageCode, Object... args);

	void infoCode(String messageCode, Object... args);

	void warnCode(String messageCode, Object... args);

	void errorCode(String messageCode, Object... args);

	/**
	 * Adds an {@link Severity#SUCCESS} alert with the default duration,
	 * {@link Duration#SINGLE}.
	 *
	 * @param message
	 *            to present to the user
	 * @since 5.3.6
	 */
	void success(String message);

	/**
	 * Adds an {@link Severity#INFO} alert with the default duration,
	 * {@link Duration#SINGLE}.
	 *
	 * @param message
	 *            to present to the user
	 */
	void info(String message);

	/**
	 * Adds an {@link Severity#WARN} alert with the default duration,
	 * {@link Duration#SINGLE}.
	 *
	 * @param message
	 *            to present to the user
	 */
	void warn(String message);

	/**
	 * Adds an {@link Severity#ERROR} alert with the default duration,
	 * {@link Duration#SINGLE}.
	 *
	 * @param message
	 *            to present to the user
	 */
	void error(String message);

	/**
	 * Adds an alert with configurable severity and duration. Message isn't
	 * treated as HTML, being HTML-escaped.
	 *
	 * @param duration
	 *            controls how long the alert is presented to the user
	 * @param severity
	 *            controls how the alert is presented to the user
	 * @param message
	 *            to present to the user
	 */
	void alert(Duration duration, Severity severity, String message);

	/**
	 * Adds an alert with configurable severity and duration.
	 *
	 * @param duration
	 *            controls how long the alert is presented to the user
	 * @param severity
	 *            controls how the alert is presented to the user
	 * @param message
	 *            to present to the user
	 * @param markup
	 *            whether to treat the message as raw HTML (true) or escape it
	 *            (false).
	 */
	@IncompatibleChange(details = "Added in 5.4 in order to support HTML in alerts", release = "5.4")
	void alert(Duration duration, Severity severity, String message, boolean markup);
}
