package feedback.web.api.model;

import com.google.common.base.Objects;
import feedback.web.api.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URL;

/**
 * This class holds the "parts" of a Server Name.
 * <p>
 * blah.blah.www.michael.feedback/asdfasdf/2323223 ->
 * {
 * fullDomainName: michael.feedback,
 * topLevelDomainName: feedback,
 * customerDomainName: michael
 * }
 *
 * @author msmyers
 * @version 1.0.0
 * @since 2/25/15
 */
public class Website implements Serializable {

    private static final long serialVersionUID = -8270287385013061805L;

    @Nullable
    private final String originalDomainName;

    @Nullable
    private final String customerDomainName;

    @Nullable
    private final String topLevelDomainName;

    @Nullable
    private final String fullDomainName;

    @Nullable
    private final String slug;

    @NotNull
    private final String fullDomainNameWithSlug;

    private final boolean valid;
    private final boolean perfect_valid;

    public Website(@Nullable final String originalDomainName, @Nullable final String customerDomainName, @Nullable final String topLevelDomainName) {
        this(originalDomainName, customerDomainName, topLevelDomainName, null);
    }

    public Website(@Nullable final String originalDomainName, @Nullable final String customerDomainName, @Nullable final String topLevelDomainName, @Nullable final String slug) {
        this.originalDomainName = StringUtils.trimToNull(originalDomainName);
        this.customerDomainName = StringUtils.trimToNull(customerDomainName);
        this.topLevelDomainName = StringUtils.trimToNull(topLevelDomainName);
        this.slug = StringUtils.trimToNull(slug);

        this.fullDomainName = UrlUtils.getFullDomainName(customerDomainName, topLevelDomainName);

        if (StringUtils.isBlank(slug)) {
            this.fullDomainNameWithSlug = fullDomainName;
        } else {
            this.fullDomainNameWithSlug = fullDomainName + "/" + slug;
        }

        if (StringUtils.isNoneBlank(customerDomainName, topLevelDomainName, fullDomainName, fullDomainNameWithSlug)) {
            valid = true;

            perfect_valid = StringUtils.equalsIgnoreCase(fullDomainNameWithSlug, originalDomainName);
        } else {
            valid = false;
            perfect_valid = false;
        }
    }

    /**
     * If the customerDomainName and fullDomainName are populated.
     *
     * @return
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * There were no subdomains that were trimmed out. The fullDomainName matches the originalDomainName.
     *
     * @return
     */
    public boolean isPerfectlyValid() {
        return perfect_valid;
    }

    @Nullable
    public String getOriginalDomainName() {
        return originalDomainName;
    }

    @Nullable
    public String getFullDomainName() {
        return fullDomainName;
    }

    @Nullable
    public String getCustomerDomainName() {
        return customerDomainName;
    }

    @Nullable
    public String getTopLevelDomainName() {
        return topLevelDomainName;
    }

    @Override
    public String toString() {
        return getFullDomainNameWithSlug();
    }

    @Nullable
    public static String optFullDomainName(Website parts) {
        if (null == parts) {
            return null;
        }

        return parts.getFullDomainName();
    }

    @Nullable
    public String getSlug() {
        return slug;
    }

    @Nullable
    public static String optFullDomainNameWithSlug(@Nullable final Website website) {
        if (null == website) {
            return null;
        }

        return website.getFullDomainNameWithSlug();
    }

    @NotNull
    public String getFullDomainNameWithSlug() {
        return fullDomainNameWithSlug;
    }

    public boolean hasSlug() {
        return StringUtils.isNotBlank(slug);
    }

    @NotNull
    public Website toParent() {
        if (!hasParent()) {
            return this;
        }

        return new Website(getFullDomainName(), getCustomerDomainName(), getTopLevelDomainName());
    }

    public boolean hasParent() {
        return hasSlug();
    }

    @NotNull
    public URL toUrl() {
        return UrlUtils.getUrl("http://www." + getFullDomainName());
    }

    @NotNull
    public URL toURL() {
        return toURL("http");
    }

    @NotNull
    public URL toURL(@Nullable String protocol) {
        protocol = org.apache.commons.lang3.StringUtils.defaultIfBlank(protocol, "http");

        return UrlUtils.getUrl(protocol + "://www." + getFullDomainNameWithSlug());
    }

    //region equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Website that = (Website) o;

        return Objects.equal(getFullDomainNameWithSlug(), that.getFullDomainNameWithSlug());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getFullDomainNameWithSlug());
    }

    @Nonnull
    public static Website parse(String fullDomainNameWithSlugOrUrl) {
        return UrlUtils.parseFullDomainNameWithSlug(fullDomainNameWithSlugOrUrl);
    }
    //endregion

}