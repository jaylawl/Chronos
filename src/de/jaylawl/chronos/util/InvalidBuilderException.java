package de.jaylawl.chronos.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

// Copied from CommonLjb 03/03/2022

public class InvalidBuilderException extends Exception {

    private final String[] problems;

    public InvalidBuilderException(@NotNull String message, @NotNull Collection<String> problems) {
        this(message, problems.toArray(new String[0]));
    }

    public InvalidBuilderException(@NotNull String message, @NotNull String problem) {
        this(message, new String[]{problem});
    }

    public InvalidBuilderException(@NotNull String message) {
        this(message, new String[]{});
    }

    public InvalidBuilderException(@NotNull String message, @NotNull String[] problems) {
        super(message);
        this.problems = problems;
    }

    //

    public @NotNull String[] getProblems() {
        return this.problems;
    }

    //

    public static void validate(boolean expression, @NotNull String message, @NotNull Collection<String> problemCollection) {
        if (!expression) {
            problemCollection.add(message);
        }
    }

}