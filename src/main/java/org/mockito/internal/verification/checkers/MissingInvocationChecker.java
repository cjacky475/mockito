/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification.checkers;

import static org.mockito.internal.exceptions.Reporter.argumentsAreDifferent;
import static org.mockito.internal.exceptions.Reporter.wantedButNotInvoked;
import static org.mockito.internal.exceptions.Reporter.wantedButNotInvokedInOrder;
import static org.mockito.internal.invocation.InvocationsFinder.findAllMatchingUnverifiedChunks;
import static org.mockito.internal.invocation.InvocationsFinder.findInvocations;
import static org.mockito.internal.invocation.InvocationsFinder.findPreviousVerifiedInOrder;
import static org.mockito.internal.invocation.InvocationsFinder.findSimilarInvocation;
import static org.mockito.internal.verification.argumentmatching.ArgumentMatchingTool.getNotMatchingArgsWithSameName;
import static org.mockito.internal.verification.argumentmatching.ArgumentMatchingTool.getSuspiciouslyNotMatchingArgsIndexes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mockito.internal.reporting.SmartPrinter;
import org.mockito.internal.verification.api.InOrderContext;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.Location;
import org.mockito.invocation.MatchableInvocation;

public class MissingInvocationChecker {

    private MissingInvocationChecker() {}

    public static void checkMissingInvocation(
            List<Invocation> invocations, MatchableInvocation wanted) {
        List<Invocation> actualInvocations = findInvocations(invocations, wanted);

        if (!actualInvocations.isEmpty()) {
            return;
        }

        Invocation similar = findSimilarInvocation(invocations, wanted);
        if (similar == null) {
            throw wantedButNotInvoked(wanted, invocations);
        }

        Integer[] indexesOfSuspiciousArgs =
                getSuspiciouslyNotMatchingArgsIndexes(wanted.getMatchers(), similar.getArguments());
        Set<String> classesWithSameSimpleName =
                getNotMatchingArgsWithSameName(wanted.getMatchers(), similar.getArguments());
        SmartPrinter smartPrinter =
                new SmartPrinter(
                        wanted, invocations, indexesOfSuspiciousArgs, classesWithSameSimpleName);
        List<Location> actualLocations =
                invocations.stream().map(Invocation::getLocation).collect(Collectors.toList());

        throw argumentsAreDifferent(
                similar,
                wanted,
                smartPrinter.getWanted(),
                smartPrinter.getActuals(),
                actualLocations);
    }

    public static void checkMissingInvocation(
            List<Invocation> invocations, MatchableInvocation wanted, InOrderContext context) {
        List<Invocation> chunk = findAllMatchingUnverifiedChunks(invocations, wanted, context);

        if (!chunk.isEmpty()) {
            return;
        }

        Invocation previousInOrder = findPreviousVerifiedInOrder(invocations, context);
        if (previousInOrder != null) {
            throw wantedButNotInvokedInOrder(wanted, previousInOrder);
        }

        checkMissingInvocation(invocations, wanted);
    }
}
