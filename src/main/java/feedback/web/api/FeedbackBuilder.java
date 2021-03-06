package feedback.web.api;

import com.zipwhip.concurrent.ObservableFuture;
import feedback.web.api.model.Author;
import feedback.web.api.util.MorePreconditions;
import feedback.web.api.util.UrlUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class FeedbackBuilder {

    @Nullable
    private FeedbackClient feedbackClient;

    @Nullable
    private FeedbackClientBuilder feedbackClientBuilder;

    //region constructors()
    public FeedbackBuilder(@Nullable FeedbackClient client) {
        this.feedbackClient = client;
    }

    public FeedbackBuilder(@Nullable FeedbackClientBuilder builder) {
        this.feedbackClientBuilder = builder;
    }

//    public FeedbackBuilder(@Nonnull final String apiKey) {
//        getOrCreateClientBuilder().withApiKey(apiKey);
//    }

    public FeedbackBuilder() {

    }
    //endregion

    //region client/builder/apiKey
    @Nonnull
    public FeedbackBuilder withClient(@Nullable FeedbackClient client) {
        MorePreconditions.checkState(null == this.feedbackClientBuilder, "Builder was already set.");
        MorePreconditions.checkState(null == this.feedbackClient, "Client was already set.");

        this.feedbackClient = client;

        return this;
    }

    @Nonnull
    public FeedbackBuilder withBuilder(@Nonnull FeedbackClientBuilder builder) {
        MorePreconditions.checkState(null == feedbackClient, "Client was already created.");
        MorePreconditions.checkState(null == this.feedbackClientBuilder, "Builder was already set.");

        this.feedbackClientBuilder = builder;

        return this;
    }

    @Nonnull
    public static FeedbackBuilder fromApiKey(@Nonnull final String apiKey) {
        return new FeedbackBuilder()
                .withApiKey(apiKey);
    }

    @Nonnull
    public FeedbackBuilder withApiKey(@Nonnull final String apiKey) {
        MorePreconditions.checkState(null == feedbackClient, "client is already created and cannot be changed.");

        getOrCreateClientBuilder().withApiKey(apiKey);

        return this;
    }
    //endregion

    @Nonnull
    public FeedbackBuilder forWebsite(@Nonnull final String fullDomainNameWithSlug) {
        getOrCreateClientBuilder().forWebsite(fullDomainNameWithSlug);

        return this;
    }

    @Nonnull
    public ReviewFeedbackBuilder importedFrom(@Nonnull final String url) {
        return importedFrom(UrlUtils.getUrl(url));
    }

    @Nonnull
    public ReviewFeedbackBuilder importedFrom(@Nonnull final URL url) {
        return review().importedFrom(url);
    }

    //region review
    @Nonnull
    public ObservableFuture<ReviewResponse> sendFeedback(@Nonnull final String content) {
        MorePreconditions.checkNotBlank(content, "content");

        return review()
                .withContent(content)
                .send();
    }

    @Nonnull
    public ObservableFuture<ReviewResponse> sendFeedback(@Nonnull Author author, @Nonnull String content) {
        return review()
                .writtenBy(author)
                .withContent(content)
                .send();
    }

    @Nonnull
    public ReviewFeedbackBuilder review() {
        return new ReviewFeedbackBuilder(this);
    }
    //endregion

    //region private utilities
    @Nullable
    protected FeedbackClient getFeedbackClient() {
        return feedbackClient;
    }

    @Nullable
    protected FeedbackClientBuilder getFeedbackClientBuilder() {
        return feedbackClientBuilder;
    }

    @Nonnull
    protected FeedbackClientBuilder getOrCreateClientBuilder() {
        return feedbackClientBuilder = FeedbackBuilder.getOrCreateClientBuilder(feedbackClientBuilder);
    }

    @Nonnull
    protected FeedbackClient getOrCreateClientOrFail() {
        return this.feedbackClient = FeedbackBuilder.getOrCreateClientOrFail(feedbackClient, feedbackClientBuilder);
    }

    @Nonnull
    public static FeedbackClient getOrCreateClientOrFail(@Nullable final FeedbackClient client, @Nullable final FeedbackClientBuilder feedbackClientBuilder) {
        if (null != client) {
            return client;
        } else if (null != feedbackClientBuilder) {
            return feedbackClientBuilder.toClient();
        }

        throw new IllegalStateException("We are asked to create a FeedbackClient, but we do not have enough information to do so.");
    }

    @Nonnull
    public static FeedbackClientBuilder getOrCreateClientBuilder(@Nullable final FeedbackClientBuilder feedbackClientBuilder) {
        if (null != feedbackClientBuilder) {
            return feedbackClientBuilder;
        } else {
            return new FeedbackClientBuilder();
        }
    }
    //endregion
}
