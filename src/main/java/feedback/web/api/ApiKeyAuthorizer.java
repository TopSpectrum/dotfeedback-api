package feedback.web.api;

import com.ning.http.client.AsyncHttpClient;

import javax.annotation.Nonnull;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class ApiKeyAuthorizer implements Authorizer {

    @Nonnull
    private final String apiKey;

    public ApiKeyAuthorizer(@Nonnull String apiKey) {
        this.apiKey = MorePreconditions.checkNotBlank(apiKey, "apiKey");
    }

    @Nonnull
    @Override
    public AsyncHttpClient.BoundRequestBuilder attach(@Nonnull AsyncHttpClient.BoundRequestBuilder builder) {
        return builder.addHeader("Authorization", "ApiKey " + apiKey);
    }
}
