package feedback.web.api;

import com.google.common.base.Converter;
import com.google.common.base.Function;

import javax.annotation.Nonnull;


/**
 * @author msmyers
 * @since 10/17/16
 */
public class SimpleConverter<Input, Output> extends Converter<Input, Output> implements Function<Input, Output>, java.util.function.Function<Input, Output> {

    @Override
    protected Output doForward(@Nonnull Input input) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    protected Input doBackward(@Nonnull Output output) {
        throw new IllegalArgumentException("Not implemented");
    }
}
