package org.dev.exception;

import java.util.Locale;

public abstract class LocalizableException extends FormattedException {

    private static final long serialVersionUID = 1L;

    private final String format;
    private final Object[] args;

    protected LocalizableException(String format, Object... args) {
	super(format, args);
	this.format = format;
	this.args = args;
    }

    protected LocalizableException(String format, Throwable cause, Object... args) {
	super(format, cause, args);
	this.format = format;
	this.args = args;
    }

    @Override
    public String getLocalizedMessage() {
	return getLocalizedMessage(Locale.getDefault());
    }

    public String getLocalizedMessage(Locale locale) {
	return LocalizationManager.localize(locale, format, args);
    }
}
