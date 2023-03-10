/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import java.io.Serializable;

import org.mockito.ArgumentMatcher;

@SuppressWarnings({"unchecked", "serial", "rawtypes"})
public class Not implements ArgumentMatcher<Object>, Serializable {

    private final ArgumentMatcher matcher;

    public Not(ArgumentMatcher<?> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(Object actual) {
        return !matcher.matches(actual);
    }

    @Override
    public Class<?> type() {
        return matcher.type();
    }

    @Override
    public String toString() {
        return "not(" + matcher + ")";
    }
}
