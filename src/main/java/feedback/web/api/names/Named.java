package feedback.web.api.names;

import javax.annotation.Nullable;

/**
 * {discussion here}
 *
 * @author msmyers
 * @version 1.0.0
 * @since 5/20/15
 */
public interface Named {

    @Nullable
    String getFirstName();

    @Nullable
    String getLastName();

    @Nullable
    String getDisplayName();

}
