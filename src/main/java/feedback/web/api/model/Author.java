package feedback.web.api.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import feedback.web.api.util.ConversionUtils;
import feedback.web.api.util.NameUtil;
import feedback.web.api.names.Named;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.net.URL;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class Author implements Serializable {

    private static final long serialVersionUID = -2724024586643491028L;

    @Nullable
    private Identity identity;

    @Nullable
    private String displayName;

    @Nullable
    private URL profileUrl;

    @Nullable
    private URL imageUrl;

    @Nullable
    private String location;

    @Nullable
    private String lastName;

    @Nullable
    private String firstName;

    public Author() {

    }

    public Author(@Nullable final Author template) {
        if (null == template) {
            return;
        }

        this.setIdentity(template.getIdentity());

        this.setDisplayName(template.getDisplayName());
        this.setLastName(template.getLastName());
        this.setFirstName(template.getFirstName());

        this.setLocation(template.getLocation());

        this.setProfileUrl(template.getProfileUrl());
        this.setImageUrl(template.getImageUrl());
    }

    @Nullable
    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(@Nullable Identity identity) {
        this.identity = identity;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public URL getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(@Nullable URL profileUrl) {
        this.profileUrl = profileUrl;
    }

    @Nullable
    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    public void setLocation(@Nullable String location) {
        this.location = location;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@Nullable String lastName) {
        this.lastName = lastName;
    }

    @Nullable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Nullable String firstName) {
        this.firstName = firstName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author author = (Author) o;
        return Objects.equal(getIdentity(), author.getIdentity()) &&
                Objects.equal(getDisplayName(), author.getDisplayName()) &&
                Objects.equal(getProfileUrl(), author.getProfileUrl()) &&
                Objects.equal(getImageUrl(), author.getImageUrl()) &&
                Objects.equal(getLocation(), author.getLocation()) &&
                Objects.equal(getLastName(), author.getLastName()) &&
                Objects.equal(getFirstName(), author.getFirstName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentity(), getDisplayName(), getProfileUrl(), getImageUrl(), getLocation(), getLastName(), getFirstName());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("identity", identity)
                .add("displayName", displayName)
                .add("profileUrl", profileUrl)
                .add("imageUrl", imageUrl)
                .add("location", location)
                .add("lastName", lastName)
                .add("firstName", firstName)
                .toString();
    }

    public void setNamed(@Nullable final Named named, @Nullable final String location) {
        setNamed(named);
        setLocation(location);
        setDisplayName(NameUtil.getFirstNameLastInitialWithLocation(named, location));
    }
    public void setNamed(@Nullable final Named named) {
        this.setDisplayName(ConversionUtils.optValue(named, Named::getDisplayName));
        this.setFirstName(ConversionUtils.optValue(named, Named::getFirstName));
        this.setLastName(ConversionUtils.optValue(named, Named::getLastName));
    }
}
