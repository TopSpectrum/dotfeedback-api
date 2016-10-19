package feedback.web.api;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URL;

/**
 * @author msmyers
 * @since 10/17/16
 */
public interface UrlFactory {

    @Nonnull
    URL create();

    @Nonnull
    URL withRelative(@Nonnull final URI relativeUri);

}
