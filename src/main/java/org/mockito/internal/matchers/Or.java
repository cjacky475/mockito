/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import java.io.Serializable;

import org.mockito.ArgumentMatcher;

@SuppressWarnings({"unchecked", "serial", "rawtypes"})
public class Or implements ArgumentMatcher<Object>, Serializable {
    private final ArgumentMatcher m1;
    private final ArgumentMatcher m2;

    public Or(ArgumentMatcher<?> m1, ArgumentMatcher<?> m2) {
        this.m1 = m1;
        this.m2 = m2;
    }

    @Override
    public boolean matches(Object actual) {
        return m1.matches(actual) || m2.matches(actual);
    }

    @Override
    public Class<?> type() {
        return m1.type().isAssignableFrom(m2.type())
                ? m1.type()
                : m2.type().isAssignableFrom(m1.type()) ? m2.type() : ArgumentMatcher.super.type();
    }

    @Override
    public String toString() {
        return "or(" + m1 + ", " + m2 + ")";
    }
}
