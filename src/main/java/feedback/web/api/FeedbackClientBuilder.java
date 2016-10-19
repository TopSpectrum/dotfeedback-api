package feedback.web.api;

import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpProvider;
import feedback.web.api.model.Website;
import feedback.web.api.util.MorePreconditions;
import feedback.web.api.util.AsyncHttpClientUtil;
import feedback.web.api.util.DestroyableUtils;
import feedback.web.api.util.UrlUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class FeedbackClientBuilder {

    @Nonnull
    private FeedbackClientConfiguration template = new FeedbackClientConfiguration();

    @Nullable
    private AsyncHttpClientConfig config;

    @Nullable
    private AsyncHttpProvider provider;

    @Nullable
    private AsyncHttpClient asyncHttpClient;

    //region forWebsite()
    public FeedbackClientBuilder forWebsite(Website website) {
        template.setDefaultWebsite(website);

        return this;
    }

    public FeedbackClientBuilder forWebsite(@Nonnull final URL fullDomainNameWithSlug) {
        return forWebsite(UrlUtils.parseFullDomainNameWithSlug(fullDomainNameWithSlug));
    }

    public FeedbackClientBuilder forWebsite(@Nonnull final String fullDomainNameWithSlug) {
        return forWebsite(UrlUtils.parseFullDomainNameWithSlug(fullDomainNameWithSlug));
    }
    //endregion

    @Nonnull
    public FeedbackClientBuilder withApiKey(@Nonnull final String apiKey) {
        template.setAuthorizer(new ApiKeyAuthorizer(apiKey));

        return this;
    }

    //region withAsyncHttpClient
    @Nonnull
    public FeedbackClientBuilder withAsyncHttpClient(@Nonnull final AsyncHttpClient client) {
        this.asyncHttpClient = MorePreconditions.checkNotNull(client, "asyncHttpClient");

        return this;
    }

    @Nonnull
    public FeedbackClientBuilder withAsyncHttpClient(@NotNull final AsyncHttpClientConfig config) {
        this.config = config;

        return this;
    }

    @Nonnull
    public FeedbackClientBuilder withAsyncHttpClient(@Nonnull final AsyncHttpProvider provider) {
        this.provider = MorePreconditions.checkNotNull(provider);

        return this;
    }

    @Nonnull
    public FeedbackClientBuilder withAsyncHttpClient(@Nonnull final AsyncHttpClientConfig config, @Nonnull AsyncHttpProvider provider) {
        this.withAsyncHttpClient(config);
        this.withAsyncHttpClient(provider);

        return this;
    }
    //endregion

    //region withGson()
    @Nonnull
    public FeedbackClientBuilder withGson(@Nonnull final GsonBuilder gson) {
        return withGson(gson.create());
    }

    @Nonnull
    public FeedbackClientBuilder withGson(@Nonnull final Gson gson) {
        template.setGson(gson);

        return this;
    }
    //endregion

    //region withApiVersion()
    @Nonnull
    public FeedbackClientBuilder withApiVersionUri(final URI versionUri) {
        template.setVersionUri(versionUri);

        return this;
    }

    @Nonnull
    public FeedbackClientBuilder withApiVersion(final double version) {
        return withApiVersion(String.valueOf(version));
    }

    @Nonnull
    public FeedbackClientBuilder withApiVersion(final int version) {
        return withApiVersion(String.valueOf(version));
    }

    @Nonnull
    public FeedbackClientBuilder withApiVersion(@Nonnull final String versionKey) {
        template.setVersionKey(versionKey);

        return this;
    }
    //endregion

    @Nonnull
    public FeedbackClientConfiguration toConfiguration() {
        return new FeedbackClientConfiguration(template);
    }

    @Nonnull
    public FeedbackClient toClient() {
        final AsyncHttpClient asyncHttpClient;
        final Closer closer = Closer.create();

        if (null == this.asyncHttpClient) {
            asyncHttpClient = closer.register(AsyncHttpClientUtil.newInstance(config, provider));
        } else {
            asyncHttpClient = this.asyncHttpClient;
        }

        final DefaultFeedbackClient client = new DefaultFeedbackClient(toConfiguration(), asyncHttpClient);

        // Because WE created the asyncHttpClient, WE must destroy it.
        return DestroyableUtils.link(client, closer);
    }

}
