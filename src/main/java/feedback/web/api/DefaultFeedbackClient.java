package feedback.web.api;

import com.google.common.base.Preconditions;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.zipwhip.concurrent.DefaultObservableFuture;
import com.zipwhip.concurrent.MutableObservableFuture;
import com.zipwhip.concurrent.ObservableFuture;
import com.zipwhip.lifecycle.CascadingDestroyableBase;
import com.zipwhip.lifecycle.DestroyableBase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class DefaultFeedbackClient extends CascadingDestroyableBase implements FeedbackClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeedbackClient.class);

    @Nonnull
    private final AsyncHttpClient client;

    @Nonnull
    private final FeedbackClientConfiguration configuration;

    @Nonnull
    private final ObservableExecutorAdapter eventExecutor;

    @Nonnull
    private final ObservableExecutorAdapter workerExecutor;

    @Nonnull
    private final ObservableExecutorAdapter bossExecutor;

    public DefaultFeedbackClient(@Nonnull final FeedbackClientConfiguration configuration) {
        this(configuration, new AsyncHttpClient());

        //region client.destroy()
        this.link(new DestroyableBase() {
            @Override
            protected void onDestroy() {
                client.closeAsynchronously();
            }
        });
        //endregion
    }

    public DefaultFeedbackClient(@Nonnull final FeedbackClientConfiguration configuration, @Nonnull final AsyncHttpClient client) {
        this.configuration = Preconditions.checkNotNull(configuration, "configuration");
        this.client = Preconditions.checkNotNull(client, "client");

        Preconditions.checkState(!this.client.isClosed(), "client cannot be closed");

        this.eventExecutor = new ObservableExecutorAdapter(configuration.getExecutorFactory().create(ExecutorType.Events, configuration.toString()));
        this.workerExecutor = new ObservableExecutorAdapter(configuration.getExecutorFactory().create(ExecutorType.Worker, configuration.toString()));
        this.bossExecutor = new ObservableExecutorAdapter(configuration.getExecutorFactory().create(ExecutorType.Boss, configuration.toString()));
    }

    @Nonnull
    @Override
    public ObservableFuture<ReviewResponse> createReview(@Nonnull final Review review) {
        final AsyncHttpClient.BoundRequestBuilder builder = preparePost(review.getFullDomainNameWithSlug(), "/reviews");

        {
            setPostBody(builder, "review", review);
        }

        return execute(builder, ReviewResponse.class);
    }

    @Nonnull
    @Override
    public ObservableFuture<ReviewResponse> createReview(@Nonnull final ReviewFeedbackBuilder builder) {
        MorePreconditions.checkNotNull(builder, "builder");

        return createReview(builder.toReview());
    }

    @Nonnull
    protected AsyncHttpClient.BoundRequestBuilder preparePost(@Nullable final String fullDomainNameWithSlug, @Nonnull final String relativeUri) {
        MorePreconditions.checkNotBlank(relativeUri, "relativeUri");

        Website website = (StringUtils.isBlank(fullDomainNameWithSlug)) ? (null) : (UrlUtils.parseFullDomainNameWithSlug(fullDomainNameWithSlug));

        return preparePost(website, relativeUri);
    }

    @Nonnull
    protected AsyncHttpClient.BoundRequestBuilder preparePost(@Nullable final Website website, @Nonnull final String relativeUri) {
        MorePreconditions.checkNotBlank(relativeUri, "relativeUri");

        @Nonnull
        final Authorizer authorizer = Preconditions.checkNotNull(configuration.getAuthorizer(), "authorizer");

        @Nonnull
        final String url = MorePreconditions.checkNotBlank(getUrl(website, relativeUri), "url");

        LOGGER.debug("Requesting URL {}", url);

        @Nonnull
        final AsyncHttpClient.BoundRequestBuilder builder = Preconditions.checkNotNull(client.preparePost(url), "builder");

        return authorizer.attach(builder);
    }

    protected void setPostBody(@Nonnull final AsyncHttpClient.BoundRequestBuilder builder, @Nullable final String name, @Nullable final Object bodyPart) {
//        , "application/json"));
//        StringEntity entity = new StringEntity("jsonLongStringParsingByGson");
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.post(context,uri, entity, "application/json", myAsyncHttpResponseHandler);`

        AsyncHttpClientUtil.setPostBody(
                configuration.getGson(),
                builder,
                bodyPart);
    }

    @Nonnull
    protected <T> ObservableFuture<T> execute(@Nonnull final AsyncHttpClient.BoundRequestBuilder builder, @Nonnull Class<T> clazz) {
        return execute(builder, AsyncHttpClientUtil.toJsonConverter(configuration.getGson(), clazz));
    }

    @Nonnull
    protected  <T> ObservableFuture<T> execute(@Nonnull final AsyncHttpClient.BoundRequestBuilder builder, @Nonnull Function<Response, T> converter) {
        final MutableObservableFuture<T> result = future();

        builder.execute(new AsyncHttpClientUtil.SimpleCompletionHandler<>(result, converter));

        return result;
    }

    @Nonnull
    protected <T> MutableObservableFuture<T> future() {
        return new DefaultObservableFuture<>(this, eventExecutor);
    }

    @Nonnull
    protected String getUrl(@Nullable final Website website, @Nonnull final String relativeUri) {
        MorePreconditions.checkNotBlank(relativeUri);

        return MorePreconditions.checkNotBlank(
                configuration.getUrl(website, UrlUtils.getUri(relativeUri)).toString(),
                String.format("website:%s,relativeUri:%s", website, relativeUri));
    }

    @Override
    protected void onDestroy() {
        eventExecutor.shutdownNow();
        workerExecutor.shutdownNow();
        bossExecutor.shutdownNow();
    }
}

