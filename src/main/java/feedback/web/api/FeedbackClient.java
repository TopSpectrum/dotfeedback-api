package feedback.web.api;

import com.zipwhip.concurrent.ObservableFuture;

import javax.annotation.Nonnull;

/**
 * @author msmyers
 * @since 10/17/16
 */
public interface FeedbackClient {

    @Nonnull
    ObservableFuture<ReviewResponse> review(@Nonnull final Review review);

    @Nonnull
    ObservableFuture<ReviewResponse> review(@Nonnull final ReviewFeedbackBuilder review);


}
