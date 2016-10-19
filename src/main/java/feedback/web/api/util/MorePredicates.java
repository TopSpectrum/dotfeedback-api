package feedback.web.api.util;

import javax.annotation.Nullable;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class MorePredicates {

    public static <T> boolean isNotNull(@Nullable final T object) {
        return object != null;
    }

    public static <T> boolean isNull(@Nullable final T object) {
        return object != null;
    }
}
