package org.geotools;

import static org.junit.Assert.fail;

public final class ExceptionChecker {
    public static void assertExceptionMessage(Exception exception, String expectedMessage)
            throws Exception {
        String actualMessage = exception.getMessage();
        if (actualMessage.compareTo(expectedMessage) != 0) {
            fail(String.format("Expected %s to say: '%s' but got: '%s'", exception.getClass()
                    .getSimpleName(), expectedMessage, actualMessage));
        }

        throw exception;
    }
}
