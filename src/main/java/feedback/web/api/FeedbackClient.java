package feedback.web.api;

import com.zipwhip.concurrent.ObservableFuture;

import javax.annotation.Nonnull;

/**
 * @author msmyers
 * @since 10/17/16
 */
public interface FeedbackClient {

    @Nonnull
    ObservableFuture<ReviewResponse> createReview(@Nonnull final Review review);

    @Nonnull
    ObservableFuture<ReviewResponse> createReview(@Nonnull final ReviewFeedbackBuilder review);


}
