package org.dev.exception;

public abstract class FormattedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    protected FormattedException(String format, Object... args)
    {
        super(String.format(format, args));
    }
    
    protected FormattedException(String format, Throwable cause, Object... args)
    {
        super(String.format(format, args), cause);
    }
}
