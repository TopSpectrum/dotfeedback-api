# dotfeedback-api

A java library to send feedback from yourself, or import from your site. 

---

### The Simple Case

#### Give your own feedback

```java
ReviewResponse review = new FeedbackBuilder()
    .withApiKey("074ee555-56d4-4051-a814-707f0736c086")
    .forWebsite("default.feedback")  
    .sendFeedback("I really enjoyed the eggs") // this returns a future.
    .get(30, TimeUnit.SECONDS);
```

#### Import feedback from your website
    
```java
FeedbackBuilder feedback = new FeedbackBuilder()
        .withApiKey("074ee555-56d4-4051-a814-707f0736c086")
        .forWebsite("default.feedback");

ReviewFeedbackBuilder review = feedback.importedFrom(UrlUtils.getUrl("http://www.MyConsumerSite.com/review/322"))
        .rated(10)
        .withContent("I like tomatoes better than pickles.");

AuthorFeedbackBuilder author = review.writtenBy("Michael Smyers")
        .identifiedBy("michael@smyers.net")
        .havingProfile(
                "http://www.MyConsumerSite.com/user/666", 
                "http://www.MyConsumerSite.com/user/666/picture.png")
        .locatedIn("Seattle");

ObservableFuture<ReviewResponse> future = author.send();

ReviewResponse response = future.get(30, TimeUnit.SECONDS);

URL postedTo = response.getReviewUrl();
```

---

### The Advanced Case

#### Give your own feedback

```java
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

    configuration.setCharset(Charset.forName("UTF-8")); // default value.

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

System.out.println("Feedback posted to " + response.getReviewUrl());
```

#### Import feedback 

```java
final Author author = new Author();

{
    // examples on setting the identity of the author.
    // identity is optional.
    {
        author.setIdentity(IdentityUtil.email("michael@smyers.net"));
        author.setIdentity(IdentityUtil.username("michaelsmyers"));
        // autodetect email
        author.setIdentity(IdentityUtil.toIdentity("michael@smyers.net"));
        // autodetect username
        author.setIdentity(IdentityUtil.toIdentity("michaelsmyers"));
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
        Named named = NameUtil.parse("Michael Smyers");
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
            author.setDisplayName(NameUtil.getFirstNameLastInitialWithLocation(named, location));
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

```
