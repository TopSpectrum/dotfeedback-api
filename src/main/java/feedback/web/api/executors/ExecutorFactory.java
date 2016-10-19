package feedback.web.api.executors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;

/**
 * @author msmyers
 * @since 8/11/16
 */
public interface ExecutorFactory {

    @Nonnull
    ExecutorService create(@Nonnull final ExecutorType type);

    @Nonnull
    ExecutorService create(@Nonnull final ExecutorType type, @Nullable final String prefix);

}
