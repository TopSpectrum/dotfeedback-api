package feedback.web.api.model;

import com.google.common.base.Objects;
import feedback.web.api.util.MorePreconditions;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author msmyers
 * @since 10/17/16
 */

public class Identity implements Serializable, Comparable<Identity> {

    private static final long serialVersionUID = -5541748699026086952L;

    @Nonnull
    private final String providerId;

    @Nonnull
    private final String providerUserId;

    public Identity(String providerId, String providerUserId) {
        this.providerId = MorePreconditions.checkNotBlank(providerId);
        this.providerUserId = MorePreconditions.checkNotBlank(providerUserId);
    }

    @Nonnull
    public String getProviderId() {
        return this.providerId;
    }

    @Nonnull
    public String getProviderUserId() {
        return this.providerUserId;
    }

    @Nonnull
    public String toString() {
        return this.getProviderId() + ":/" + this.getProviderUserId();
    }

    public int compareTo(@Nonnull final Identity o) {
        return String.valueOf(this).compareTo(String.valueOf(o));
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            Identity identity = (Identity)o;
            return Objects.equal(this.providerId, identity.providerId) && Objects.equal(this.providerUserId, identity.providerUserId);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.providerId, this.providerUserId);
    }
}