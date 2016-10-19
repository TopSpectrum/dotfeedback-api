package feedback.web.api;

import com.zipwhip.concurrent.ObservableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class ReviewFeedbackBuilder {

    @Nullable
    private FeedbackClient client;

    @Nullable
    private FeedbackBuilder feedbackBuilder;

    @Nullable
    private FeedbackClientBuilder feedbackClientBuilder;

    @Nullable
    private AuthorFeedbackBuilder authorFeedbackBuilder;

    private Review template = new Review();

    //region constructors()
    public ReviewFeedbackBuilder(@Nullable final FeedbackBuilder feedbackBuilder) {
        this.feedbackBuilder = feedbackBuilder;
    }

    public ReviewFeedbackBuilder(@Nullable final FeedbackClientBuilder feedbackClientBuilder) {
        this.feedbackClientBuilder = feedbackClientBuilder;
    }

    public ReviewFeedbackBuilder(@Nullable final FeedbackClient client) {
        this.client = client;
    }

    public ReviewFeedbackBuilder() {

    }
    //endregion

    @Nonnull
    public Review toReview() {
        return new Review(template);
    }

    @Nonnull
    public FeedbackClient toFeedbackClient() {
        return getOrCreateClientOrFail();
    }

    @Nonnull
    public ObservableFuture<ReviewResponse> send() {
        return send(getOrCreateClientOrFail());
    }

    @Nonnull
    public ObservableFuture<ReviewResponse> send(@Nonnull final FeedbackClient client) {
        MorePreconditions.checkNotNull(client, "client is null, cannot send yet.");

        return client.createReview(this);
    }

    @Nonnull
    public ReviewFeedbackBuilder importedFrom(@Nonnull final URL importedSourceUrl, @Nullable final Author author) {
        return importedFrom(importedSourceUrl).writtenBy(author);
    }

    @Nonnull
    public ReviewFeedbackBuilder importedFrom(@Nonnull final URL importedSource, @Nullable final Author author, @Nonnull final String content) {
        return importedFrom(importedSource, author).withContent(MorePreconditions.checkNotBlank(content, "content"));
    }

    @Nonnull
    public ReviewFeedbackBuilder importedFrom(@Nullable final URL importedSourceUrl) {
        return withAddition(Review::setImportedSource, importedSourceUrl);
    }

    @Nonnull
    public ReviewFeedbackBuilder rated(int rating) {
        template.setRating(rating);
        return this;
    }

    @Nonnull
    public ReviewFeedbackBuilder writtenBy(@Nullable final Author author) {
        return withAddition(Review::setAuthor, author);
    }

    @Nonnull
    public AuthorFeedbackBuilder writtenBy(@Nullable final String authorName) {
        if (null == authorFeedbackBuilder) {
            authorFeedbackBuilder = new AuthorFeedbackBuilder(this);
        }

        return authorFeedbackBuilder.named(authorName);
    }

    @Nonnull
    public <T> ReviewFeedbackBuilder withAddition(@NotNull final BiConsumer<Review, T> addition, @Nullable final T value) {
        addition.accept(template, value);

        return this;
    }

    @Nonnull
    public ReviewFeedbackBuilder withAddition(@Nonnull Consumer<Review> consumer) {
        MorePreconditions.checkNotNull(consumer, "consumer");

        consumer.accept(template);

        return this;
    }

    @Nonnull
    public ReviewFeedbackBuilder withContent(@Nonnull final String content) {
        template.setContent(content);

        return this;
    }

    @Nonnull
    protected FeedbackClient getOrCreateClientOrFail() {
        client = ConversionUtils.defaultObjectOrNull(client, ConversionUtils.optValue(feedbackBuilder, FeedbackBuilder::getFeedbackClient));

        if (null != client) {
            return client;
        }

        feedbackClientBuilder = ConversionUtils.defaultObjectOrNull(feedbackClientBuilder, ConversionUtils.optValue(feedbackBuilder, FeedbackBuilder::getFeedbackClientBuilder));

        if (null != feedbackClientBuilder) {
            return client = feedbackClientBuilder.toClient();
        }

        throw new IllegalStateException("I am asked to make a FeedbackClient, but I do not have enough information to do so.");
    }
}
