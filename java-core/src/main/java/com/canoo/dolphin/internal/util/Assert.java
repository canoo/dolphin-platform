package com.canoo.dolphin.internal.util;


import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.Objects;

/**
 * A collection of utility methods that can assert the state of an instance.
 */
public final class Assert {
    private static final String NOT_NULL_MSG_FORMAT = "Argument '%s' may not be null";
    private static final String NOT_EMPTY_MSG_FORMAT = "Argument '%s' may not be empty";
    private static final String NOT_NULL_ENTRIES_MSG_FORMAT = "Argument '%s' may not contain null values";

    private Assert() {
        // intentionally private and blank
    }

    /**
     * Checks that the specified {@code value} is null and throws {@link java.lang.NullPointerException} with a customized error message if it is.
     *
     * @param value        the value to be checked.
     * @param argumentName the name of the argument to be used in the error message.
     * @return the {@code value}.
     * @throws java.lang.NullPointerException if {@code value} is null.
     */

    public static <T> T requireNonNull(T value, String argumentName) {
        Objects.requireNonNull(argumentName, String.format(NOT_NULL_MSG_FORMAT, "argumentName"));

        return Objects.requireNonNull(value, String.format(NOT_NULL_MSG_FORMAT, argumentName));
    }

    /**
     * Checks that the specified {@code str} {@code blank}, throws {@link IllegalArgumentException} with a customized error message if it is.
     *
     * @param str          the value to be checked.
     * @param argumentName the name of the argument to be used in the error message.
     * @return the {@code str}.
     * @throws java.lang.NullPointerException     if {@code str} is null.
     * @throws java.lang.IllegalArgumentException if {@code str} is blank.
     * @see #requireNonNull(Object, String)
     * @see #isBlank(String)
     */

    public static String requireNonBlank(String str, String argumentName) {
        requireNonNull(str, argumentName);

        if (isBlank(str)) {
            throw new IllegalArgumentException(String.format(NOT_EMPTY_MSG_FORMAT, argumentName));
        }
        return str;
    }

    /**
     * <p>Determines whether a given string is <code>null</code>, empty,
     * or only contains whitespace. If it contains anything other than
     * whitespace then the string is not considered to be blank and the
     * method returns <code>false</code>.</p>
     *
     * @param str The string to test.
     * @return <code>true</code> if the string is <code>null</code>, or
     * blank.
     */
    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks that the specified {@code collection} is empty, throws {@link IllegalStateException} if it is.
     *
     * @param collection the collection to check
     * @return the {@code collection}
     * @throws NullPointerException  if {@code collection} is null
     * @throws IllegalStateException if {@code collection} is empty
     * @see #requireNonEmpty(java.util.Collection, String)
     */

    public static <T> Collection<T> requireNonEmpty(Collection<T> collection) {
        Objects.requireNonNull(collection);

        requireState(!collection.isEmpty());
        return collection;
    }

    /**
     * Checks that the specified {@code collection} is empty, throws {@link IllegalStateException} with a customized error message if it is.
     *
     * @param collection   the collection to be checked.
     * @param argumentName the name of the argument to be used in the error message.
     * @return the {@code collection}.
     * @throws java.lang.NullPointerException  if {@code collection} is null.
     * @throws java.lang.IllegalStateException if {@code collection} is empty.
     * @see #requireNonNull(Object, String)
     * @see #requireNonEmpty(java.util.Collection)
     */

    public static <T> Collection<T> requireNonEmpty(Collection<T> collection, String argumentName) {
        requireNonNull(collection, argumentName);

        requireState(!collection.isEmpty(), String.format(NOT_EMPTY_MSG_FORMAT, argumentName));
        return collection;
    }

    /**
     * Checks whether the specified {@code collection} contains null values, throws {@link IllegalStateException} with a customized error message if it has.
     *
     * @param collection   the collection to be checked.
     * @param argumentName the name of the argument to be used in the error message.
     * @return the {@code collection}.
     * @throws java.lang.NullPointerException  if {@code collection} is null.
     * @throws java.lang.IllegalStateException if {@code collection} contains null values.
     * @see #requireNonEmpty(java.util.Collection, String)
     * @see #requireNonNull(Object, String)
     */

    public static <T> ObservableList<T> requireNonNullEntries(ObservableList<T> collection, String argumentName) {
        requireNonNull(collection, argumentName);

        String msg = String.format(NOT_NULL_ENTRIES_MSG_FORMAT, argumentName);
        for (Object value : collection) {
            requireState(value != null, msg);
        }
        return collection;
    }

    /**
     * Checks that the specified condition is met.
     *
     * @param condition the condition to check
     * @throws IllegalStateException if {@code condition} evaluates to false
     */
    public static void requireState(boolean condition) {
        if (!condition) {
            throw new IllegalStateException();
        }
    }

    /**
     * Checks that the specified condition is met and throws a customized
     * {@link IllegalStateException} if it is.
     *
     * @param condition the condition to check
     * @param message   detail message to be used in the event that a {@code
     *                  IllegalStateException} is thrown
     * @throws IllegalStateException if {@code condition} evaluates to false
     */
    public static void requireState(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(requireNonBlank(message, "message"));
        }
    }

}

