package feedback.web.api.util;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.ning.http.client.*;
import com.zipwhip.concurrent.DefaultObservableFuture;
import com.zipwhip.concurrent.FakeFailingObservableFuture;
import com.zipwhip.concurrent.MutableObservableFuture;
import com.zipwhip.concurrent.ObservableFuture;
import feedback.web.api.exceptions.HttpRequestException;
import feedback.web.api.model.HttpStatus;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.function.Function;

/**
 * @author msmyers
 * @since 5/17/16
 */
public class AsyncHttpClientUtils {

    @Nonnull
    public static ObservableFuture<String> optString(@Nonnull final AsyncHttpClient client, @Nullable final URI url) {
        if (null == url) {
            return failWithNull();
        }

        try {
            return optString(client, url.toURL());
        } catch (MalformedURLException e) {
            return fail(e);
        }
    }

    @Nonnull
    public static <T> ObservableFuture<T> opt(@Nonnull final AsyncHttpClient client, @Nullable final URI uri, @Nonnull final Function<Response, T> adapter) {
        return opt(client, UrlUtils.optUrl(uri), adapter);
    }

    @Nonnull
    public static <T> ObservableFuture<T> opt(@Nonnull final AsyncHttpClient client, @Nullable final URL uri, @Nonnull final Function<Response, T> adapter) {
        if (null == uri) {
            return failWithNull();
        }

        return adapt(execute(client, ConversionUtils.toString(uri)), adapter);
    }

    @SuppressWarnings({"null", "ConstantConditions"})
    public static <T> ObservableFuture<T> optFromJson(@Nonnull final Gson gson, @Nonnull final AsyncHttpClient client, @Nullable final URL url, @Nonnull final Class<T> clazz) {
        return opt(client, url, (response -> JsonUtils.fromJson(gson, AsyncHttpClientUtils.toString(response), clazz)));
    }

    @Nonnull
    public static ObservableFuture<String> optString(@Nonnull final AsyncHttpClient client, @Nullable final URL url) {
        if (null == url) {
            return failWithNull();
        }

        return optString(client, url.toString());
    }

    @Nonnull
    public static ObservableFuture<String> optString(@Nonnull final AsyncHttpClient client, @Nullable final String url) {
        return adapt(execute(client, url), AsyncHttpClientUtils::toString);
    }

    @Nonnull
    protected static <TSource, TDestination> ObservableFuture<TDestination> adapt(ObservableFuture<TSource> future, Function<TSource, TDestination> adapter) {
        DefaultObservableFuture<TDestination> result = new DefaultObservableFuture<>(future);

        future.addObserver((o, future1) -> {
            try {
                result.setSuccess(adapter.apply(future1.getResult()));
            } catch (Throwable e) {
                result.setFailure(e);
            }
        });

        return result;
    }

    @Nullable
    public static String optString(@Nullable final Response response) {
        if (null == response) {
            return null;
        }

        if (response.hasResponseBody()) {
            try {
                return response.getResponseBody();
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Nullable
    public static String toString(@Nullable final Response response) {
        if (null == response) {
            return null;
        }

        try {
            if (response.hasResponseBody()) {
                return response.getResponseBody();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static Map<String, Object> toJson(@Nonnull final Gson gson, @Nullable final Response response) {
        return JsonUtils.asMap(gson, toString(response));
    }

    @Nonnull
    public static <T> Function<Response, T> toJsonConverter(@Nonnull final Gson gson, @Nonnull final Class<T> clazz) {
        Preconditions.checkNotNull(gson, "gson");
        Preconditions.checkNotNull(clazz, "clazz");

        return ((response) -> {
            HttpStatus status = HttpStatus.valueOf(response.getStatusCode());

            if (status.is2xxSuccessful()) {
                return JsonUtils.fromJson(gson, AsyncHttpClientUtils.toString(response), clazz);
            } else {
                throw new HttpRequestException("Request failed: " + AsyncHttpClientUtils.optString(response), status);
            }
        });
    }

    @Nonnull
    public static <T> ObservableFuture<T> execute(@Nonnull final AsyncHttpClient.BoundRequestBuilder builder, @Nonnull final Function<Response, T> converter) {
        return adapt(execute(builder), converter);
    }

    @Nonnull
    public static ObservableFuture<Response> execute(@Nonnull final AsyncHttpClient.BoundRequestBuilder builder) {
        MutableObservableFuture<Response> future = future();

        builder.execute(new CompletionHandler(future));

        return future;
    }

    @Nonnull
    public static ObservableFuture<Response> execute(@Nonnull final AsyncHttpClient client, @Nullable final String url) {
        if (StringUtils.isBlank(url)) {
            return failWithNull();
        }

        final MutableObservableFuture<Response> future = new DefaultObservableFuture<>(url);

        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);

        builder.execute(new AsyncCompletionHandler() {
            @Override
            public String onCompleted(Response response) throws Exception {
                future.setSuccess(response);

                return null;
            }

            @Override
            public void onThrowable(Throwable t) {
                future.setFailure(t);
            }
        });

        return future;
    }

    protected static <T> MutableObservableFuture<T> failWithNull() {
        return new FakeFailingObservableFuture<>(AsyncHttpClientUtils.class, new IllegalStateException("null"));
    }

    protected static <T> MutableObservableFuture<T> fail(Throwable t) {
        return new FakeFailingObservableFuture<>(AsyncHttpClientUtils.class, t);
    }

    protected static <T> MutableObservableFuture<T> future() {
        return new DefaultObservableFuture<>(AsyncHttpClientUtils.class);
    }

    public static void addQueryParam(@Nonnull final AsyncHttpClient.BoundRequestBuilder builder, @Nullable final Map<String, Object> request) {
        if (MapUtils.isEmpty(request)) {
            return;
        }

        ConversionUtils.toStream(request)
                .forEach(entry -> {
                    builder.addQueryParam(entry.getKey(), StringUtils.defaultString(ConversionUtils.toString(entry.getValue())));
                });
    }

    public static void setPostBody(@Nonnull final Gson gson, @Nonnull final AsyncHttpClient.BoundRequestBuilder builder, @Nullable final Object object) {
        final String json = JsonUtils.toJson(gson, object);

        builder.setBody(json);
        builder.addHeader("Content-Type", "application/json");
    }

    @Nonnull
    public static AsyncHttpClient newInstance(@Nullable final AsyncHttpClientConfig config) {
        return newInstance(config, null);
    }

    @Nonnull
    public static AsyncHttpClient newInstance(@Nullable final AsyncHttpClientConfig config, @Nullable final AsyncHttpProvider provider) {
        if (null == config && null == provider) {
            return new AsyncHttpClient();
        } else if (null == config) {
            return new AsyncHttpClient(provider);
        } else if (null == provider) {
            return new AsyncHttpClient(config);
        } else {
            return new AsyncHttpClient(provider, config);
        }
    }

    public static class SimpleCompletionHandler<T> extends AsyncCompletionHandlerBase {

        @Nonnull
        private final MutableObservableFuture<T> future;

        @Nonnull
        private final Function<Response, T> converter;

        public SimpleCompletionHandler(@Nonnull final MutableObservableFuture<T> future, @Nonnull final Function<Response, T> converter) {
            this.future = Preconditions.checkNotNull(future, "future");
            this.converter = converter;
        }

        @Override
        public Response onCompleted(Response response) throws Exception {
            try {
                future.setSuccess(converter.apply(response));
            } catch (Exception e) {
                future.setFailure(e);
            }

            return response;
        }

        @Override
        public void onThrowable(Throwable t) {
            future.setFailure(t);
        }

    }

    public static class CompletionHandler extends AsyncCompletionHandlerBase {

        @Nonnull
        private final MutableObservableFuture<Response> future;

        public CompletionHandler(@Nonnull final MutableObservableFuture<Response> future) {
            this.future = Preconditions.checkNotNull(future, "future");
        }

        @Override
        public Response onCompleted(Response response) throws Exception {
            try {
                future.setSuccess(response);
            } catch (Exception e) {
                future.setFailure(e);
            }

            return response;
        }

        @Override
        public void onThrowable(Throwable t) {
            future.setFailure(t);
        }
    }
}