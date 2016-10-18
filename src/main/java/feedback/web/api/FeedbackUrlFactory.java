package feedback.web.api;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URL;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class FeedbackUrlFactory implements UrlFactory {

    private static final URI VERSION_1 = UrlUtils.getUri("/api/v1");

    @Nonnull
    private final Website fullDomainNameWithSlug;

    @Nonnull
    private final URI versionUri;

    public FeedbackUrlFactory(@Nonnull final Website fullDomainNameWithSlug, @Nonnull final URI versionUri) {
        this.fullDomainNameWithSlug = MorePreconditions.checkNotBlank(fullDomainNameWithSlug);
        this.versionUri = MorePreconditions.checkNotBlank(versionUri);
    }

    public FeedbackUrlFactory(@Nonnull final Website fullDomainNameWithSlug) {
        this(fullDomainNameWithSlug, VERSION_1);
    }

    @Nonnull
    @Override
    public URL create() {
        return UrlUtils.getUrl(fullDomainNameWithSlug.toUrl(), versionUri);
    }

    @Nonnull
    @Override
    public URL withRelative(@Nonnull String relativeUri) {
        return UrlUtils.getUrl(create(), relativeUri);
    }

    @Nonnull
    @Override
    public URL withRelative(@Nonnull URI relativeUri) {
        return UrlUtils.getUrl(create(), relativeUri);
    }

    @Nonnull
    public Website getFullDomainNameWithSlug() {
        return fullDomainNameWithSlug;
    }

    @Nonnull
    public URI getVersionUri() {
        return versionUri;
    }
}
