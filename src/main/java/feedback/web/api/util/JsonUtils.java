package feedback.web.api.util;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class JsonUtils {

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> asMap(Gson gson, String json) {
        return fromJson(gson, json, Map.class);
    }

    public static <T> T fromJson(@Nonnull final Gson gson, @Nullable final String string, @Nonnull final Class<T> clazz) {
        Preconditions.checkNotNull(gson, "gson");
        Preconditions.checkNotNull(clazz, "clazz");

        if (StringUtils.isBlank(string)) {
            return null;
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Creating {} from '{}'", clazz, string);
        }

        return gson.fromJson(string, clazz);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    @Nonnull
    public static String toJson(@Nonnull final Gson gson, @Nullable final Object payload) {
        Preconditions.checkNotNull(gson, "gson");

        if (null == payload) {
            return "";
        }

        return gson.toJson(payload);
    }
}
