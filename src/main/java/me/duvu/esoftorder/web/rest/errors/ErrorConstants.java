package me.duvu.esoftorder.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {
    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final URI DEFAULT_TYPE = URI.create("");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create("");

    private ErrorConstants() {}
}
