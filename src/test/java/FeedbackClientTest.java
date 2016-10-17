import feedback.web.api.FeedbackBuilder;
import feedback.web.api.ReviewResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class FeedbackClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackClientTest.class);

    @Test
    public void name() throws Exception {
        final FeedbackBuilder builder = FeedbackBuilder.fromApiKey("asdf-asdf-asdf-asdf");

        final ReviewResponse review = builder
                .forWebsite("default.feedback")
                .sendFeedback("I really enjoyed the eggs")
                .get(30, TimeUnit.SECONDS);

        LOGGER.debug("Our feedback is available at {}", review.getReviewUrl());
    }
}
