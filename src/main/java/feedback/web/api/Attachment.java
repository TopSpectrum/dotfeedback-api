package feedback.web.api;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.net.URL;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class Attachment implements Serializable {

    private static final long serialVersionUID = -5366081360066898588L;

    @Nonnull
    private URL url;

    @Nullable
    private String mediaType;

    @Nullable
    private String id;

    @Nonnull
    public URL getUrl() {
        return url;
    }

    public void setUrl(@Nonnull URL url) {
        this.url = url;
    }

    @Nullable
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(@Nullable String mediaType) {
        this.mediaType = mediaType;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attachment)) return false;
        Attachment that = (Attachment) o;
        return Objects.equal(getUrl(), that.getUrl()) &&
                Objects.equal(getMediaType(), that.getMediaType()) &&
                Objects.equal(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUrl(), getMediaType(), getId());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", url)
                .add("mediaType", mediaType)
                .add("id", id)
                .toString();
    }
}
