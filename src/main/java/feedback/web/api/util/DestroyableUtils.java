package feedback.web.api.util;

import com.zipwhip.lifecycle.CascadingDestroyable;
import com.zipwhip.lifecycle.Destroyable;
import com.zipwhip.lifecycle.DestroyableBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class DestroyableUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestroyableUtils.class);

    @Nonnull
    public static <T extends CascadingDestroyable> T link(@Nonnull final T parent, @Nullable Closeable closeable) {
        if (null == closeable) {
            return parent;
        }

        return link(parent, new DestroyableBase() {
            @Override
            protected void onDestroy() {
                try {
                    closeable.close();
                } catch (IOException e) {
                    LOGGER.error("(swallowing exception) Failed to close: " + closeable, e);
                }
            }
        });
    }

    @Nonnull
    public static <T extends CascadingDestroyable> T link(@Nonnull final T parent, @Nullable Destroyable destroyable) {
        MorePreconditions.checkNotNull(parent);

        if (null != destroyable) {
            parent.link(destroyable);
        }

        return parent;
    }

}
