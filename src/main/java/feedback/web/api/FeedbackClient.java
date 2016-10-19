package feedback.web.api;

import com.zipwhip.concurrent.ObservableFuture;
import feedback.web.api.model.Review;
import feedback.web.api.model.ReviewFeedbackBuilder;
import feedback.web.api.model.ReviewResponse;

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
