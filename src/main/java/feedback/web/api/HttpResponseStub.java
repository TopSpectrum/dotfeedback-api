package feedback.web.api;

import com.google.common.base.MoreObjects;
import com.ning.http.client.Response;
import com.zipwhip.util.LocalDirectory;
import com.zipwhip.util.SetDirectory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * {discussion here}
 *
 * @author msmyers
 * @version 1.0.0
 * @since 9/23/15
 */
public class HttpResponseStub {

    private final int statusCode;

    private final LocalDirectory<String, String> headers = new SetDirectory<>();

    @Nullable
    private final InputStream body;

    public HttpResponseStub(@Nonnull final Response response) {
        this.statusCode = response.getStatusCode();

        try {
            this.body = response.getResponseBodyAsStream();
        } catch (IOException e) {
            throw new RuntimeException("Unable to open stream", e);
        }
    }

    public HttpResponseStub(int statusCode, @Nullable final InputStream body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Nonnull
    public LocalDirectory<String, String> getHeaders() {
        return headers;
    }

    @Nullable
    public InputStream getBody() {
        return body;
    }

    public String getBodyAsString() throws IOException {
        if (null == body) {
            return null;
        }

        return ConversionUtils.copyToString(body, Charset.forName("UTF-8"));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("statusCode", statusCode)
                .add("headers", headers)
                .toString();
    }
}
