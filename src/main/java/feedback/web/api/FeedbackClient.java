package feedback.web.api;

import com.zipwhip.concurrent.ObservableFuture;
import feedback.web.api.model.Review;

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
