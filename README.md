# dotfeedback-api

A java library to send feedback from yourself, or import from your site. 

---

####Here is how to use it.

```java
    final ReviewResponse review = new FeedbackBuilder()
        .withApiKey("074ee555-56d4-4051-a814-707f0736c086")
        .forWebsite("default.feedback")
        .sendFeedback("I really enjoyed the eggs") // this returns a future.
        .get(30, TimeUnit.SECONDS);
```

