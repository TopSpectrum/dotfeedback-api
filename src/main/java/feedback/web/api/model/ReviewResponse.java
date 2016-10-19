package feedback.web.api.model;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.net.URL;

/**
 * @author msmyers
 * @since 10/17/16
 */
@SuppressWarnings("NullableProblems")
public class ReviewResponse implements Serializable {

    private static final long serialVersionUID = -6606171966552012996L;

    @Nonnull
    private URL reviewUrl;

    @Nonnull
    private URL authorUrl;

    @Nonnull
    public URL getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(@Nonnull URL reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    @Nonnull
    public URL getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(@Nonnull URL authorUrl) {
        this.authorUrl = authorUrl;
    }
}
