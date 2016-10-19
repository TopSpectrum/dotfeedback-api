package feedback.web.api.util;

import feedback.web.api.names.HumanNameParser;
import feedback.web.api.names.InlineNamed;
import feedback.web.api.names.Name;
import feedback.web.api.names.Named;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {discussion here}
 *
 * @author msmyers
 * @version 1.0.0
 * @since 5/20/15
 */
public class NameUtils {

    @Nullable
    public static String parseFirstName(@Nullable final String displayName) {
        final String normalizedName = normalizeName(displayName);

        if (StringUtils.isBlank(normalizedName)) {
            return null;
        }

        final int index = normalizedName.indexOf(" ");

        if (-1 == index) {
            return normalizedName;
        } else if (0 == index) {
            return null;
        }

        return normalizedName.substring(0, index - 1);
    }

    @Nullable
    public static String getFirstName(@Nullable final Named named) {
        if (null == named) {
            return null;
        }

        return normalizeName(named.getFirstName());
    }

    @Nonnull
    public static String getFirstName(@Nullable final String displayName) {
        return StringUtils.defaultString(parse(displayName).getFirstName(), "Guest");
    }

    @Nonnull
    public static String getLastName(@Nullable final String displayName) {
        return StringUtils.defaultString(parse(displayName).getLastName());
    }

    @Nullable
    public static String getLastName(@Nullable final Named named) {
        if (null == named) {
            return null;
        }

        return normalizeName(named.getLastName());
    }

    @Nullable
    public static String optDisplayName(@Nullable final String firstName, @Nullable final String lastName) {
        return optDisplayName(null, firstName, lastName);
    }

    @Nullable
    public static String optDisplayName(@Nullable final String displayName, @Nullable final String firstName, @Nullable final String lastName) {
        final String f = normalizeName(firstName);
        final String l = normalizeName(lastName);

        if (isNamed(displayName)) {
            return displayName;
        } else if (isUnnamed(l)) {
            return f;
        } else if (isUnnamed(f)) {
            return l;
        }

        return String.join(" ", f, l);
    }

    public static boolean isUnnamed(@Nullable final String name) {
        return StringUtils.isBlank(name) || StringUtils.equalsIgnoreCase(name, "Guest") || StringUtils.startsWithIgnoreCase(name, "Guest ");
    }

    public static boolean isUnnamed(@Nullable final Named named) {
        if (null == named) {
            return true;
        }

        return isUnnamed(optDisplayName(named));
    }

    @Nullable
    public static String optDisplayName(@Nullable final Named named) {
        if (null == named) {
            return null;
        }

        return optDisplayName(named.getDisplayName(), named.getFirstName(), named.getLastName());
    }

    @Nullable
    public static String normalizeName(@Nullable final String name) {
        return StringUtils.trimToNull(name);
    }

    public static boolean isNamed(@Nullable final Named named) {
        return !isUnnamed(named);
    }

    public static boolean isNamed(@Nullable final String name) {
        return !isUnnamed(name);
    }

    @Nullable
    public static String nullify(@Nullable final String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }

        return string;
    }

    @Nullable
    public static String optFirstName(@Nullable final Named user) {
        return ConversionUtils.optValue(user, Named::getFirstName);
    }

    @Nullable
    public static String optLastName(@Nullable final Named user) {
        return ConversionUtils.optValue(user, Named::getLastName);
    }

    @Nullable
    public static String getInitial(@Nullable String name) {
        return StringUtils.substring(StringUtils.trimToNull(name), 0, 1);
    }

    public static boolean hasBothNames(@Nullable final Named named) {
        return ConversionUtils.isNoneNull(
                StringUtils.trimToNull(optFirstName(named)),
                StringUtils.trimToNull(optLastName(named)));
    }

    @Nullable
    public static String getFullName(@Nullable final String firstName, @Nullable final String lastName) {
        if (StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName)) {
            return null;
        }

        if (StringUtils.isBlank(firstName)) {
            return StringUtils.trimToNull(lastName);
        } else if (StringUtils.isBlank(lastName)) {
            return StringUtils.trimToNull(firstName);
        }

        return firstName + " " + lastName;
    }

    @Nullable
    public static String getFullName(@Nullable final Named named) {
        String firstName = optFirstName(named);
        String lastName = optLastName(named);

        return getFullName(firstName, lastName);
    }

    public static boolean hasFirstName(@Nullable final Named named) {
        return StringUtils.isNotBlank(optFirstName(named));
    }

    public static boolean hasLastName(@Nullable final Named named) {
        return StringUtils.isNotBlank(optFirstName(named));
    }

    @Nullable
    public static String getFirstNameLastInitialWithLocation(@Nullable final Named named, @Nullable final String location) {
        String firstNameLastInitial = getFirstNameLastInitial(named);

        if (StringUtils.isBlank(firstNameLastInitial)) {
            return null;
        }

        final String locationString = StringUtils.trimToNull(location);

        if (StringUtils.isBlank(locationString)) {
            return firstNameLastInitial;
        }

        return firstNameLastInitial + " of " + locationString;
    }

    @Nullable
    public static String getFirstNameLastInitial(@Nullable final Named named) {
        if (isUnnamed(named)) {
            // isUnnamed(null) == true
            return null;
        }

        @Nullable
        final String firstName = getFirstNameOrDisplayName(named);

        if (StringUtils.isBlank(firstName)) {
            return null;
        }

        @Nullable
        String lastInitial = getInitial(named.getLastName());

        if (StringUtils.isBlank(lastInitial)) {
            return StringUtils.capitalize(firstName);
        } else {
            return StringUtils.capitalize(firstName) + " " + StringUtils.capitalize(lastInitial) + ".";
        }
    }

    @Nullable
    public static String getFirstNameOrDisplayName(@Nullable final Named named) {
        if (isUnnamed(named)) {
            return null;
        }

        if (StringUtils.isNotBlank(named.getFirstName())) {
            return normalizeName(named.getFirstName());

        } else if (StringUtils.isNotBlank(named.getDisplayName())) {
            return normalizeName(named.getDisplayName());
        }

        return null;
    }

    @Nonnull
    public static String getFirstName(@Nullable final Named user, @Nonnull final String defaultFirstName) {
        final String firstName = optFirstName(user);

        return StringUtils.defaultIfBlank(firstName, defaultFirstName);
    }

    @Nullable
    public static String optFullName(@Nullable final Named author) {
        if (null == author) {
            return null;
        }

        String firstName = getFirstName(author);
        String lastName = getLastName(author);

        if (StringUtils.isBlank(lastName)) {
            return firstName;
        }

        return firstName + " " + lastName;
    }

    @Nullable
    public static Named optNamed(@Nullable Name name) {
        if (null== name) {
            return null;
        }

        return toNamed(name);
    }

    @Nonnull
    public static Named toNamed(@Nonnull Name name) {
        String firstName = StringUtils.trimToNull(name.getFirstName());
        String lastName = StringUtils.trimToNull(name.getLastName());
        String fullName = getFullName(firstName, lastName);
        String displayName = null;

        return new InlineNamed(fullName, firstName, lastName);
    }

    public static boolean couldBeNamed(@Nullable final Named named) {
        if (null == named) {
            return false;
        }

        return isNamed(named.getDisplayName()) || isNamed(named.getFirstName()) || isNamed(named.getLastName());
    }

    @Nonnull
    public static Named parse(@Nullable final String authorName) {
        if (StringUtils.isBlank(authorName)) {
            return new InlineNamed(null, null, null);
        }

        HumanNameParser parser = new HumanNameParser();

        Name name = parser.parse(authorName);

//        String firstName = com.topspectrum.util.StringUtils.parse(Pattern.compile("([^\\s]+)\\s+([^\\s.]+)\\.*"), authorName, 1);
//        String lastName = com.topspectrum.util.StringUtils.parse(Pattern.compile("([^\\s]+)\\s+([^\\s.]+)\\.*"), authorName, 2);

        return toNamed(name);
    }

    public static boolean equals(@Nullable final Named name1, @Nullable final Named name2) {
        if (ConversionUtils.bothNull(name1, name2)) {
            return true;
        } else if (ConversionUtils.onlyOneNull(name1, name2)) {
            return false;
        }

        return StringUtils.equals(optDisplayName(name1), optDisplayName(name2));
    }
}
