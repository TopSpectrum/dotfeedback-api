import com.google.gson.GsonBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.zipwhip.concurrent.ObservableFuture;
import feedback.web.api.*;
import feedback.web.api.executors.DefaultExecutorFactory;
import feedback.web.api.model.*;
import feedback.web.api.names.Named;
import feedback.web.api.util.IdentityUtils;
import feedback.web.api.util.NameUtils;
import feedback.web.api.util.UrlUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class FeedbackClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackClientTest.class);

    @Test
    public void testMyself() throws Exception {
        final ReviewResponse review = new FeedbackBuilder()
                .withApiKey("074ee555-56d4-4051-a814-707f0736c086")
                .forWebsite("default.feedback")
                .sendFeedback("I really enjoyed the eggs")
                .get(30, TimeUnit.SECONDS);

        LOGGER.debug("Our feedback is available at {}", review.getReviewUrl());
    }

    @Test
    public void setupClient__author() throws Exception {
        final Author author = new Author();

        {
            // examples on setting the identity of the author.
            // identity is optional.
            {
                // helpers
                author.setIdentity(IdentityUtils.email("michael@smyers.net"));
                author.setIdentity(IdentityUtils.username("michaelsmyers"));

                // autodetects
                author.setIdentity(IdentityUtils.toIdentity("michael@smyers.net")); // ( email )
                author.setIdentity(IdentityUtils.toIdentity("michaelsmyers")); // ( username )

                // uri
                author.setIdentity(IdentityUtils.toIdentity("email:/michaelsmyers"));
                author.setIdentity(IdentityUtils.toIdentity("username:/michaelsmyers"));
                author.setIdentity(IdentityUtils.toIdentity("facebook:/michael.smyers"));

                // DO.IT.LIVE.
                author.setIdentity(new Identity("email", "michael@smyers.net"));
            }

            // all fields optional
            author.setImageUrl(UrlUtils.getUrl("...some image..."));
            author.setProfileUrl(UrlUtils.getUrl("...some url..."));

            // displayName, firstName, lastName, location
            {
                // you could do it the boring way...
                {
                    author.setFirstName("Michael");
                    author.setLastName("Smyers");
                    author.setDisplayName("The best guy around");
                }

                // or you could use fancy tools
                // the name parser handles a bunch of cool stuff like 'Michael Smyers Jr. III'
                Named named = NameUtils.parse("Michael Smyers");
                String location = "Seattle";

                {
                    author.setNamed(named);

                    // setNamed(@Nullable named) internally calls:
                    author.setFirstName(named.getFirstName());
                    author.setLastName(named.getFirstName());
                    author.setDisplayName(named.getDisplayName());
                }

                {
                    // even better, use the location
                    author.setNamed(named, location);

                    // setNamed(@Nullable named, @Nullable location) internally calls:
                    author.setNamed(named);
                    author.setLocation(location);
                    author.setDisplayName(NameUtils.getFirstNameLastInitialWithLocation(named, location));
                }
            }
        }

        final Review review = new Review();

        {
            review.setImportedSource(UrlUtils.getUrl("http://www.MyConsumerSite.com/review/33"));

            review.setAuthor(author);

            // default value
            review.setCreatedDate(Instant.now());

            review.setContent("I like pickles.");

            // optional (10 -> 5 stars)
            review.setRating(10);

            // optional IF AND ONLY IF the configuration provides a default website
            review.setFullDomainNameWithSlug("michael.feedback");
        }

//        final ObservableFuture<ReviewResponse> future = client.createReview(review);
//
//        final ReviewResponse response = future.get();
    }

    @Test
    public void nameSetupClient() throws Exception {
        final FeedbackClient client;

        {
            FeedbackClientConfiguration configuration = new FeedbackClientConfiguration();

            // for reviews that do not specify a special website.
            configuration.setDefaultWebsite(Website.parse("michael.feedback"));

            // set the version of the API (defaults to current version: 1)
            configuration.setVersionUri(UrlUtils.getUri("/api/v1")); // default value.
            configuration.setVersionKey("1"); // alias for above.

            // Set your ApiKey here.
            configuration.setAuthorizer(new ApiKeyAuthorizer("074ee555-56d4-4051-a814-707f0736c086"));

            int threadsPerWorker = 10; // default value.
            int threadsPerEvents = 1; // default value.
            configuration.setExecutorFactory(new DefaultExecutorFactory(threadsPerWorker, threadsPerEvents)); //default value.
            configuration.setGson(new GsonBuilder().create()); // default value.

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient(); // default value.

            client = new DefaultFeedbackClient(configuration, asyncHttpClient);
        }

        final Review review = new Review();

        {
            review.setContent("I like pickles.");

            // optional (10 -> 5 stars)
            review.setRating(10);

            // optional IF AND ONLY IF the configuration provides a default website
            review.setFullDomainNameWithSlug("michael.feedback");
        }

        final ObservableFuture<ReviewResponse> future = client.createReview(review);

        final ReviewResponse response = future.get();
    }

    @Test
    public void name() throws Exception {
        FeedbackBuilder feedback = new FeedbackBuilder()
                .withApiKey("074ee555-56d4-4051-a814-707f0736c086")
                .forWebsite("default.feedback");

        ReviewFeedbackBuilder review = feedback.importedFrom(UrlUtils.getUrl("http://www.MyConsumerSite.com/review/322"))
                .rated(10)
                .withContent("I like tomatoes better than pickles.");

        AuthorFeedbackBuilder author = review.writtenBy("Michael Smyers")
                .identifiedBy("michael@smyers.net")
                .havingProfile(UrlUtils.getUrl("http://www.MyConsumerSite.com/user/666"), UrlUtils.getUrl("http://www.MyConsumerSite.com/user/666/picture.png"))
                .locatedIn("Seattle");

        ObservableFuture<ReviewResponse> future = author.send();

        ReviewResponse response = future.get(30, TimeUnit.SECONDS);

        URL postedTo = response.getReviewUrl();
    }

    @Test
    public void testImport() throws Exception {
        Author author = new Author();

        {
            author.setIdentity(IdentityUtils.email("new_user" + (UUID.randomUUID().toString()) + "@gmail.com"));
            author.setDisplayName("displayName");
            author.setFirstName("firstName");
            author.setLastName("lastName");
            author.setImageUrl(UrlUtils.getUrl("http://www.michael.feedback/imageUrl.png"));
            author.setProfileUrl(UrlUtils.getUrl("http://www.michael.feedback/profile"));
            author.setLocation("France");
        }

        final ReviewResponse review = new FeedbackBuilder()
                .withApiKey("074ee555-56d4-4051-a814-707f0736c086")
                .forWebsite("default.feedback")
                .sendFeedback(author, "I really enjoyed the eggs")
                .get(30, TimeUnit.SECONDS);

        ReviewResponse review2 = new FeedbackBuilder()
                .importedFrom(UrlUtils.getUrl("http://www.MyConsumerSite.com/review/322"))
                .rated(10)
                .withContent("I like tomatoes better than pickles.")
                .writtenBy("Michael Smyers")
                .identifiedBy("michael@smyers.net")
                .havingProfile(UrlUtils.getUrl("http://www.MyConsumerSite.com/user/666"), UrlUtils.getUrl("http://www.MyConsumerSite.com/user/666/picture.png"))
                .locatedIn("Seattle")
                .send()
                .get();

        LOGGER.debug("Our feedback is available at {}", review.getReviewUrl());

//        // Everything inline.
//        final ReviewResponse review = new FeedbackBuilder()
//                .withApiKey("074ee555-56d4-4051-a814-707f0736c086")
//                .forWebsite("default.feedback")
//                .sendFeedback("I really enjoyed the eggs")
//                .get(30, TimeUnit.SECONDS);
//
//        final FeedbackBuilder builder = new FeedbackBuilder().withApiKey("074ee555-56d4-4051-a814-707f0736c086");
//
//        final ObservableFuture<ReviewResponse> future = builder.forWebsite("default.feedback")
//                .sendFeedback("I really enjoyed the eggs");
//
////        final ReviewResponse review = future.get(30, TimeUnit.SECONDS);

    }
}
