package feedback.web.api;

import com.google.common.base.MoreObjects;

/**
 * @author msmyers
 * @since 10/18/16
 */
public class HttpRequestException extends RuntimeException {

    private static final long serialVersionUID = -8675578045304938107L;

    private final HttpStatus status;

    public HttpRequestException(HttpStatus status) {
        this.status = status;
    }

    public HttpRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpRequestException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpRequestException(Throwable cause, HttpStatus status) {
        super(cause);
        this.status = status;
    }

    public HttpRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .toString();
    }
}
