package feedback.web.api;

import com.google.common.net.InternetDomainName;
import com.zipwhip.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class UrlUtils {

    @Nonnull
    public static String getFullDomainName(@NotNull final String customerDomainName, @NotNull final String topLevelDomainName) {
        String c = MorePreconditions.checkNotBlank(StringUtils.trimToNull(customerDomainName), "customerDomainName");
        String t = MorePreconditions.checkNotBlank(StringUtils.trimToNull(topLevelDomainName), "topLevelDomainName");
        String fullDomainName = c + "." + t;

        if (!InternetDomainName.isValid(fullDomainName)) {
            throw new IllegalStateException("Not valid: " + fullDomainName);
        }

        return fullDomainName;
    }

    @Nonnull
    public static String getFullDomainNameWithSlug(@NotNull final String fullDomainName, @Nullable final String slug) {
        MorePreconditions.checkNotBlank(fullDomainName, "fullDomainName");

        if (StringUtils.isBlank(slug)) {
            return fullDomainName;
        } else {
            return fullDomainName + "/" + slug;
        }
    }

    @Nonnull
    public static String getFullDomainNameWithSlug(@NotNull final String customerDomainName, @NotNull final String topLevelDomainName, @Nullable final String slug) {
        return getFullDomainNameWithSlug(getFullDomainName(customerDomainName, topLevelDomainName), slug);
    }

    @NotNull
    public static Website parseFullDomainNameWithSlug(@NotNull final String url) {
        MorePreconditions.checkNotBlank(url, "url");

        if (StringUtils.contains(url, ":/")) {
            return parseFullDomainNameWithSlug(getUrl(url));
        } else {
            return parseFullDomainNameWithSlug(getUrl("http://" + url));
        }
    }

    @NotNull
    public static Website parseFullDomainNameWithSlug(@NotNull final URL url) {
        MorePreconditions.checkNotBlank(url);

        String hostName = url.getHost();
        String slug = parseFirstSlug(url);

        String topLevelDomainName;
        String customerDomainName;
        String fullDomainName;
        String fullDomainNameWithSlug;

        if (!StringUtils.contains(hostName, ".")) {
            throw new IllegalStateException("Not a domain name: " + hostName);
        }

        try {
            fullDomainName = InternetDomainName.from(hostName)
                    .topPrivateDomain()
                    .toString();
        } catch (IllegalArgumentException | IllegalStateException e) {
            // It might not be a TLD. But we still need to parse it.
            String domainNameOrSubdomains = StringUtils.substringBeforeLast(hostName, ".");

            topLevelDomainName = StringUtils.substringAfterLast(hostName, ".");

            if (domainNameOrSubdomains.contains(".")) {
                customerDomainName = StringUtils.substringAfterLast(domainNameOrSubdomains, ".");
            } else {
                customerDomainName = domainNameOrSubdomains;
            }

            fullDomainName = getFullDomainName(customerDomainName, topLevelDomainName);
        }

        customerDomainName = StringUtils.substringBefore(fullDomainName, ".");
        topLevelDomainName = StringUtils.substringAfter(fullDomainName, ".");
        fullDomainNameWithSlug = getFullDomainNameWithSlug(customerDomainName, topLevelDomainName, slug);

        return new Website(fullDomainNameWithSlug, customerDomainName, topLevelDomainName, slug);
    }

    @Nullable
    public static URL optUrl(@Nullable final URI uri) {
        if (null == uri) {
            return null;
        }

        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to convert: " + uri.toString(), e);
        }
    }

    @Nonnull
    public static URL getUrl(@Nonnull final String url) {
        MorePreconditions.checkNotBlank(url);

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Bad url: " + url, e);
        }
    }

    @Nonnull
    public static URL getUrl(@Nullable final Website website) {
        MorePreconditions.checkNotBlank(website, "parsedDomainParts");

        assert website != null;
        return website.toUrl();
    }

    @Nonnull
    public static URL getUrl(@Nonnull Website website, @Nullable final String relativeUri) {
        return getUrl(getUrl(website), relativeUri);
    }

    @Nonnull
    public static URL getUrl(@Nonnull final URL url, @Nullable final URI relativeUri1, @Nullable final URI relativeUri2) {
        return getUrl(getUrl(url, relativeUri1), relativeUri2);
    }

    @Nonnull
    public static URL getUrl(@Nonnull final URL url, @Nullable final URI relativeUri) {
        if (null == relativeUri || MorePreconditions.isGenericallyBlank(relativeUri)) {
            return url;
        }

        return getUrl(url, relativeUri.toString());
    }


    @Nonnull
    public static URL getUrl(@Nonnull final URL url, String relativeUri) {
        if (StringUtils.isBlank(relativeUri)) {
            return url;
        }

        try {
            if (StringUtils.isBlank(url.getFile())) {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), relativeUri, null);
            } else {
                if (StringUtils.endsWith(url.getFile(), "/")) {
                    if (StringUtil.startsWith(relativeUri, "/")) {
                        relativeUri = relativeUri.substring(1);
                    }
                } else if (!StringUtils.startsWith(relativeUri, "/")) {
                    relativeUri = "/" + relativeUri;
                }

                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + relativeUri, null);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Unable to concat %s and %s", url, relativeUri), e);
        }
    }

    @Nullable
    public static String parseFirstSlug(@Nullable final URL url) {
        if (null == url) {
            return null;
        }

        String path = url.getPath();

        if (StringUtils.isBlank(path)) {
            return null;
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        String attempt1 = StringUtils.substringBefore(path, "/");
        String attempt2 = StringUtils.substringBefore(path, "?");

        // pick the shorter one.
        if (attempt1.length() > attempt2.length()) {
            return nukeDots(attempt2);
        } else {
            return nukeDots(attempt1);
        }
    }

    @NotNull
    private static String nukeDots(@Nullable final String string) {
        if (org.apache.commons.lang3.StringUtils.contains(string, ".")) {
            return "";
        } else {
            return org.apache.commons.lang3.StringUtils.defaultString(string);
        }
    }

    @Nonnull
    public static String ensureSuffix(@Nullable final String string, @Nonnull final String suffix) {
        if (StringUtils.startsWith(string, suffix) && null != string) {
            return string;
        }

        return StringUtils.defaultString(string) + suffix;
    }

    @Nonnull
    public static URI getUri(@Nonnull final String string) {
        try {
            return new URI(string);
        } catch (URISyntaxException e) {
            throw new RuntimeException("not valid: " + string, e);
        }
    }

    public static boolean isNotBlank(@Nullable final URI uri) {
        return !isBlank(uri);
    }

    public static boolean isNotBlank(@Nullable final URL url) {
        return !isBlank(url);
    }

    public static boolean isBlank(@Nullable final URI uri) {
        if (null == uri) {
            return true;
        }

        return StringUtils.isBlank(uri.toString());
    }

    public static boolean isBlank(@Nullable final URL url) {
        if (null == url) {
            return true;
        }

        return StringUtils.isBlank(url.toString());
    }
}
