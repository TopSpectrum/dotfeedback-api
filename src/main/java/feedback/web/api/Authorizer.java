package feedback.web.api;

import com.ning.http.client.AsyncHttpClient;

import javax.annotation.Nonnull;

/**
 * @author msmyers
 * @since 10/17/16
 */
public interface Authorizer {

    @Nonnull
    AsyncHttpClient.BoundRequestBuilder attach(@Nonnull final AsyncHttpClient.BoundRequestBuilder builder);

}
