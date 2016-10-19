package feedback.web.api;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feedback.web.api.executors.DefaultExecutorFactory;
import feedback.web.api.executors.ExecutorFactory;
import feedback.web.api.model.Website;
import feedback.web.api.util.MorePreconditions;
import feedback.web.api.util.ConversionUtils;
import feedback.web.api.util.UrlUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author msmyers
 * @since 10/17/16
 */
@SuppressWarnings("NullableProblems")
public class FeedbackClientConfiguration {

    @Nonnull
    private Authorizer authorizer;

    @Nonnull
    private URI versionUri = UrlUtils.getUri("/api/v1");

    @Nonnull
    private ExecutorFactory executorFactory = new DefaultExecutorFactory();

    @Nonnull
    private Gson gson = new GsonBuilder()
            .create();

    @Nullable
    private Website defaultWebsite;

    public FeedbackClientConfiguration() {

    }

    public FeedbackClientConfiguration(@Nonnull final FeedbackClientConfiguration configuration) {
        this.setDefaultWebsite(configuration.getDefaultWebsite());
        this.setExecutorFactory(configuration.getExecutorFactory());
        this.setGson(configuration.getGson());
        this.setAuthorizer(configuration.getAuthorizer());
        this.setDefaultWebsite(configuration.getDefaultWebsite());
    }

    @Nonnull
    public Authorizer getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(@Nonnull Authorizer authorizer) {
        this.authorizer = Preconditions.checkNotNull(authorizer, "authorizer");
    }

    public URL getUrl(@Nullable final Website website, @Nonnull URI relativeUri) {
        return toUrlFactory(website).withRelative(relativeUri);
    }

    @Nonnull
    public UrlFactory toUrlFactory(@Nullable final Website website) {
        return toUrlFactory(
                website,
                versionUri);
    }

    @Nonnull
    protected UrlFactory toUrlFactory(@Nullable Website website, @Nullable URI versionUri) {
        website = MorePreconditions.checkNotBlank(ConversionUtils.defaultObject(website, this.defaultWebsite));
        versionUri = MorePreconditions.checkNotBlank(versionUri);

        return new FeedbackUrlFactory(website, versionUri);
    }

    @Nonnull
    public ExecutorFactory getExecutorFactory() {
        return executorFactory;
    }

    public void setExecutorFactory(@Nonnull ExecutorFactory executorFactory) {
        this.executorFactory = MorePreconditions.checkNotNull(executorFactory, "executorFactory");
    }

    @Nonnull
    public Gson getGson() {
        return gson;
    }

    public void setGson(@Nonnull Gson gson) {
        this.gson = MorePreconditions.checkNotNull(gson);
    }

    public void setDefaultWebsite(@Nullable Website defaultWebsite) {
        this.defaultWebsite = defaultWebsite;
    }

    public Website getDefaultWebsite() {
        return defaultWebsite;
    }

    public void setVersionUri(@Nonnull final URI versionUri) {
        MorePreconditions.checkNotBlank(versionUri, "versionKey");

        this.versionUri = versionUri;
    }

    public void setVersionKey(@Nonnull final String versionKey) {
        MorePreconditions.checkNotBlank(versionKey, "versionKey");
        MorePreconditions.checkArgument(NumberUtils.isNumber(versionKey), "versionKey must be a number");

        this.versionUri = UrlUtils.getUri("/api/v" + versionKey);
    }
}
