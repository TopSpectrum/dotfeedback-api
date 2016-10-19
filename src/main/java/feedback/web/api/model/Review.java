package feedback.web.api.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.net.URL;
import java.time.Instant;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class Review implements Serializable {

    private static final long serialVersionUID = -118921467077671077L;

    @Nullable
    private Author author;

    @Nullable
    private Instant createdDate;

    @Nullable
    private Attachment[] attachments;

    @Nullable
    private String content;

    @Nullable
    private Integer rating;

    @Nullable
    private URL importedSource;

    @Nullable
    private String fullDomainNameWithSlug;

    public Review(Review template) {
        this();

        this.setCreatedDate(template.getCreatedDate());
        this.setRating(template.getRating());
        this.setImportedSource(template.getImportedSource());
        this.setFullDomainNameWithSlug(template.getFullDomainNameWithSlug());
        this.setContent(template.getContent());
        this.setAuthor(template.getAuthor());
        this.setAttachments(template.getAttachments());
    }

    public Review() {

    }

    @Nullable
    public String getFullDomainNameWithSlug() {
        return fullDomainNameWithSlug;
    }

    public void setFullDomainNameWithSlug(@Nullable String fullDomainNameWithSlug) {
        this.fullDomainNameWithSlug = fullDomainNameWithSlug;
    }

    @Nullable
    public Author getAuthor() {
        return author;
    }

    public void setAuthor(@Nullable Author author) {
        this.author = author;
    }

    @Nullable
    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@Nullable Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Nullable
    public Attachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(@Nullable Attachment[] attachments) {
        this.attachments = attachments;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    public void setContent(@Nullable String content) {
        this.content = content;
    }

    @Nullable
    public Integer getRating() {
        return rating;
    }

    public void setRating(@Nullable Integer rating) {
        this.rating = rating;
    }

    @Nullable
    public URL getImportedSource() {
        return importedSource;
    }

    public void setImportedSource(@Nullable URL importedSource) {
        this.importedSource = importedSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return Objects.equal(getAuthor(), review.getAuthor()) &&
                Objects.equal(getCreatedDate(), review.getCreatedDate()) &&
                Objects.equal(getAttachments(), review.getAttachments()) &&
                Objects.equal(getContent(), review.getContent()) &&
                Objects.equal(getRating(), review.getRating()) &&
                Objects.equal(getImportedSource(), review.getImportedSource());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAuthor(), getCreatedDate(), getAttachments(), getContent(), getRating(), getImportedSource());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("author", author)
                .add("createdDate", createdDate)
                .add("attachments", attachments)
                .add("content", content)
                .add("rating", rating)
                .add("importedSource", importedSource)
                .toString();
    }
}
