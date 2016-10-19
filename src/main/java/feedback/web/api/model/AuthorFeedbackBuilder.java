package feedback.web.api.model;

import com.zipwhip.concurrent.ObservableFuture;
import feedback.web.api.util.MorePreconditions;
import feedback.web.api.names.Name;
import feedback.web.api.util.NameUtil;
import feedback.web.api.names.Named;
import feedback.web.api.util.ConversionUtils;
import feedback.web.api.util.IdentityUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class AuthorFeedbackBuilder {

    @NotNull
    private ReviewFeedbackBuilder parent;

    private Author template = new Author();

    //region constructors()
    protected AuthorFeedbackBuilder(@Nullable final ReviewFeedbackBuilder parent) {
        this.parent = parent;
    }
    //endregion

    //region finishers
    @Nonnull
    public Author toAuthor() {
        return new Author(template);
    }

    @Nonnull
    public Review toReview() {
        @Nonnull
        Review review = parent.toReview();

        review.setAuthor(toAuthor());

        return review;
    }

    @Nonnull
    public ObservableFuture<ReviewResponse> send() {
        return parent.writtenBy(toAuthor()).send();
    }
    //endregion

    @Nonnull
    public AuthorFeedbackBuilder named(@Nullable final Named named) {
        return named(
                ConversionUtils.optValue(named, Named::getDisplayName),
                ConversionUtils.optValue(named, Named::getFirstName),
                ConversionUtils.optValue(named, Named::getLastName));
    }

    @Nonnull
    public AuthorFeedbackBuilder named(@Nullable final Name named) {
        return named(NameUtil.optNamed(named));
    }

    @Nonnull
    public AuthorFeedbackBuilder named(@Nullable final String displayName) {
        template.setDisplayName(displayName);

        return this;
    }

    @Nonnull
    public AuthorFeedbackBuilder named(@Nullable final String firstName, @Nullable final String lastName) {
        template.setFirstName(firstName);
        template.setLastName(lastName);

        return this;
    }

    @Nonnull
    public AuthorFeedbackBuilder named(@Nullable final String displayName, @Nullable final String firstName, @Nullable final String lastName) {
        template.setDisplayName(displayName);
        template.setDisplayName(firstName);
        template.setDisplayName(lastName);

        return this;
    }

    @Nonnull
    public AuthorFeedbackBuilder locatedIn(String location) {
        template.setLocation(location);

        return this;
    }

    @Nonnull
    public <T> AuthorFeedbackBuilder withAddition(@NotNull final BiConsumer<Author, T> addition, @Nullable final T value) {
        addition.accept(template, value);

        return this;
    }

    @Nonnull
    public AuthorFeedbackBuilder withAddition(@Nonnull Consumer<Author> consumer) {
        MorePreconditions.checkNotNull(consumer, "consumer");

        consumer.accept(template);

        return this;
    }

    @Nonnull
    public AuthorFeedbackBuilder havingProfileImage(URL imageUrl) {
        template.setImageUrl(imageUrl);

        return this;
    }

    @Nonnull
    public AuthorFeedbackBuilder havingProfile(URL profileUrl) {
        template.setProfileUrl(profileUrl);

        return this;
    }

    @Nonnull
    public AuthorFeedbackBuilder havingProfile(URL profileUrl, URL imageUrl) {
        return havingProfile(profileUrl)
                .havingProfileImage(imageUrl);
    }

    public AuthorFeedbackBuilder identifiedBy(Identity identity) {
        template.setIdentity(identity);

        return this;
    }

    public AuthorFeedbackBuilder identifiedBy(String identity) {
        return identifiedBy(IdentityUtil.toIdentity(identity));
    }
}
