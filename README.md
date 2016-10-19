# dotfeedback-api

A java library to send feedback from yourself, or import from your site. 

---

####Here is how to use it.

##### Give your own feedback

```java
ReviewResponse review = new FeedbackBuilder()
    .withApiKey("074ee555-56d4-4051-a814-707f0736c086")
    .forWebsite("default.feedback")  
    .sendFeedback("I really enjoyed the eggs") // this returns a future.
    .get(30, TimeUnit.SECONDS);
```

##### Import feedback from your website
    
```java
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
```

