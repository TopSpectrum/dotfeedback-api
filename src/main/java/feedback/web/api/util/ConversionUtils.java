package feedback.web.api.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.zipwhip.concurrent.ObservableFuture;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author msmyers
 * @since 10/17/16
 */
public class ConversionUtils {

    public static final int BUFFER_SIZE = 4096;

    public static boolean bothNull(@Nullable final Object object1, @Nullable final Object object2) {
        return null == object1 && null == object2;
    }

    public static boolean onlyOneNull(@Nullable final Object object1, @Nullable final Object object2) {
        return !bothNull(object1, object2) && (null == object1 || null == object2);
    }

    public static boolean isNoneNull(@Nullable final Object... objects) {
        if (null == objects) {
            return false;
        }

        for (Object object : objects) {
            if (null == object) {
                return false;
            }
        }

        return true;
    }

    public static boolean isAllNull(@Nullable final Object... objects) {
        if (null == objects) {
            return true;
        }

        for (Object object : objects) {
            if (null != object) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    public static String toString(@Nullable Object object) {
        if (null == object) {
            return null;
        }

        return String.valueOf(object);
    }

    @Nonnull
    public static <T> Stream<T> toStream(@Nullable final Iterable<T> iterable) {
        if (null == iterable) {
            return Stream.empty();
        }

        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <K, V> Stream<Map.Entry<K, V>> toStream(@Nullable final Map<K, V> map) {
        if (null == map) {
            return toStream();
        }

        return map.entrySet().stream();
    }

    @Nonnull
    public static <T> Stream<T> toStream() {
        return Stream.empty();
    }

    /**
     * Copy the contents of the given InputStream into a String.
     * Leaves the stream open when done.
     *
     * @param in the InputStream to copy from
     * @return the String that has been copied to
     * @throws IOException in case of I/O errors
     */
    public static String copyToString(@Nullable InputStream in, @Nullable final Charset charset) throws IOException {
        Preconditions.checkNotNull(in, "No InputStream specified");

        final StringBuilder out = new StringBuilder();
        final InputStreamReader reader = new InputStreamReader(in, ConversionUtils.defaultObject(charset, Charset.defaultCharset()));
        final char[] buffer = new char[BUFFER_SIZE];

        int bytesRead;

        while ((bytesRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, bytesRead);
        }

        return out.toString();
    }

    @Nonnull
    public static <T> T defaultObject(@Nullable final T first, @Nullable final T second) {
        if (null == first) {
            return Preconditions.checkNotNull(second, "second");
        } else {
            return first;
        }
    }

    public static List<ObservableFuture> immutableList(List<ObservableFuture> collection) {
        return null;
    }

    @Nonnull
    public static <T> List<T> immutableList(@Nullable final Collection<T> values) {
        if (CollectionUtils.isEmpty(values)) {
            return immutableList();
        }

        ImmutableList.Builder<T> builder = ImmutableList.builder();

        ConversionUtils.toStream(values)
                .filter(MorePredicates::isNotNull)
                .forEach(builder::add);

        return builder.build();
    }

    @Nonnull
    public static <T> Stream<T> toStream(@Nullable final T[] array) {
        if (null == array) {
            return Stream.empty();
        }

        return Arrays.stream(array);
    }

    //    public static <T> Stream<T> toStream(@Nullable final ImmutableList<T> list) {
//
//    }
//
    @Nonnull
    public static <T> Stream<T> toStream(@Nullable final Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return Stream.empty();
        }

        return collection.stream();
    }


    @Nonnull
    public static <T> List<T> immutableList() {
        ImmutableList.Builder<T> builder = ImmutableList.builder();

        return builder.build();
    }

    @Nonnull
    public static Charset defaultCharset(@Nullable final Charset charset) {
        if (null == charset) {
            return Charset.defaultCharset();
        }

        return charset;
    }

    @Nullable
    public static <T> T defaultObjectOrNull(@Nullable final T first, @Nullable final T second) {
        if (null == first) {
            return second;
        } else {
            return first;
        }
    }

    @Nullable
    public static <T, R> R optValue(@Nullable final T object, @NotNull final Function<T, R> reaper) {
        if (null == object) {
            return null;
        }

        return reaper.apply(object);
    }

}
