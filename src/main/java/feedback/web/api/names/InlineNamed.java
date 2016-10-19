package feedback.web.api.names;

import javax.annotation.Nullable;

/**
 * {discussion here}
 *
 * @author msmyers
 * @version 1.0.0
 * @since 2/5/16
 */
public class InlineNamed implements Named {

    @Nullable
    private final String displayName;

    @Nullable
    private final String firstName;

    @Nullable
    private final String lastName;

    public InlineNamed(@Nullable final String displayName, @Nullable final String firstName, @Nullable final String lastName) {
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public InlineNamed(@Nullable final String displayName) {
        this.displayName = displayName;

        this.firstName = displayName;
        this.lastName = null;
    }

    @Nullable
    @Override
    public String getFirstName() {
        return firstName;
    }

    @Nullable
    @Override
    public String getLastName() {
        return lastName;
    }

    @Nullable
    @Override
    public String getDisplayName() {
        return displayName;
    }
}
