package feedback.web.api;

import javax.annotation.Nullable;

/**
 * {discussion here}
 *
 * @author msmyers
 * @version 1.0.0
 * @since 5/18/15
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7368635227885091448L;

    public NotFoundException() {

    }

    public NotFoundException(@Nullable final Object message) {
        super(String.valueOf(message));
    }

    public NotFoundException(@Nullable final String message) {
        super(message);
    }

    public NotFoundException(@Nullable final String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(@Nullable final Throwable cause) {
        super(cause);
    }

    public NotFoundException(@Nullable final String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
