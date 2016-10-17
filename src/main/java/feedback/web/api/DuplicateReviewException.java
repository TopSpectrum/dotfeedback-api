package feedback.web.api;

import javax.annotation.Nullable;

/**
 * {discussion here}
 *
 * @author msmyers
 * @version 1.0.0
 * @since 5/18/15
 */
public class DuplicateReviewException extends RuntimeException {

    private static final long serialVersionUID = -7368635227885091448L;

    public DuplicateReviewException() {

    }

    public DuplicateReviewException(@Nullable final Object message) {
        super(String.valueOf(message));
    }

    public DuplicateReviewException(@Nullable final String message) {
        super(message);
    }

    public DuplicateReviewException(@Nullable final String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateReviewException(@Nullable final Throwable cause) {
        super(cause);
    }

    public DuplicateReviewException(@Nullable final String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
