package org.dev.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Test;

public class TestLocalizable {

    @Test
    public void testLocalizable() {
	final Exception e = new TestException("The action '%s' is invalid", "foo");
	Locale.setDefault(Locale.GERMAN);

	assertThat(e.getMessage(), is("The action 'foo' is invalid"));
	assertThat(e.getLocalizedMessage(), is("Die Aktion 'foo' ist ungültig"));
    }

    static class TestException extends LocalizableException {
	private static final long serialVersionUID = 1L;

	public TestException(String format, Object... args) {
	    super(format, args);
	}
    }
}
