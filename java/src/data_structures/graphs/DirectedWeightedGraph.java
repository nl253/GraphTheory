package data_structures.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author norbert
 */

@SuppressWarnings({"WeakerAccess", "DesignForExtension", "AssignmentToCollectionOrArrayFieldFromParameter", "unused", "ParameterHidesMemberVariable", "PublicMethodNotExposedInInterface", "InstanceVariableMayNotBeInitialized", "InstanceVariableNamingConvention", "ClassNamingConvention", "PublicConstructor", "ClassWithoutLogger", "FieldNotUsedInToString", "MethodReturnOfConcreteClass", "ImplicitCallToSuper", "NonBooleanMethodNameMayNotStartWithQuestion", "UnusedReturnValue", "CallToSuspiciousStringMethod", "AbstractClassNeverImplemented", "AbstractClassWithoutAbstractMethods", "AlibabaAbstractClassShouldStartWithAbstractNaming"})
public final class DirectedWeightedGraph<E extends Comparable<E>> implements WeightedGraph<E, DirectedWeightedGraph<E>>, AdjacencyList<E, DirectedWeightedGraph<E>> {

    /**
     * a Map that emulates a 2-dimensional matrix for lookup.
     */

    @SuppressWarnings({"FieldMayBeFinal", "CollectionWithoutInitialCapacity"})
    private Map<E, Map<E, Integer>> lookupTable = new HashMap<>();
    private int order;

    /**
     * Allow empty constructor.
     */

    public DirectedWeightedGraph() {}

    /**
     * A version of the Constructor with initial nodes.
     *
     * @param nodes An iterable of nodes
     */

    public DirectedWeightedGraph(final Iterable<? extends E> nodes) {
        this();
        nodes.forEach(this::add);
    }

    /**
     * Compute the number of nodes in the Graph sometimes called order.
     *
     * @return number of nodes in the Graph sometimes called order
     */

    @Override
    public int getOrder() {
        return lookupTable.size();
    }

    @Override
    public void setOrder(final int val) {
        this.order = val;
    }


    /**
     * Lookup the cost of going from nodeA to nodeB. O(1).
     *
     * @param nodeA the first node
     * @param nodeB the second node
     * @return the cost of going from the first to the second node
     */

    @Override
    @SuppressWarnings({"MethodWithMultipleReturnPoints", "SuspiciousIndentAfterControlStatement"})
    public int getWeight(final E nodeA, final E nodeB) {
        if (lookupTable.containsKey(nodeA) && lookupTable.get(nodeA)
                .containsKey(nodeB)) return lookupTable.get(nodeA).get(nodeB);
        return shortestRoute(nodeA, nodeB).map(Collection::size).orElse(-1);
    }

    /**
     * Add a node to this Graph. You need to provide a String identifier.
     *
     * @param item a String identifier
     * @return the Graph itself
     */

    @Override
    @SuppressWarnings("rawtypes")
    public void add(final E item) {
        if (!lookupTable.keySet().contains(item)) {
            lookupTable.put(item, new HashMap<>(lookupTable.size() + 1));
            // the distance from any node to itself is 0
            lookupTable.get(item).put(item, 0);
        }
    }

    // /**
    //  * Removes the node specified by a String identifier from this Graph.
    //  *
    //  * @param node String id that represents a MultiNode
    //  * @return true
    //  */
    //
    // @Override
    // public boolean remove(final Object node) {
    //
    //     boolean found = false;
    //
    //     if (lookupTable.containsKey(node)) {
    //         lookupTable.remove(node);
    //         found = true;
    //     }
    //
    //     // @formatter:off
    //     lookupTable.values()
    //             .stream()
    //             .filter(this::contains)
    //             .forEach(this::remove);
    //
    //     return found;
    //     // @formatter:on
    // }

    @SuppressWarnings({"MagicCharacter", "KeySetIterationMayUseEntrySet", "StandardVariableNames", "MethodWithMultipleLoops"})
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(500);

        for (final E i : lookupTable.keySet()) {
            for (final E j : lookupTable.get(i).keySet()) {
                builder.append('[');
                builder.append(lookupTable.get(i).get(j));
                builder.append(']');
            }
            builder.append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    /**
     * Use the Dijkstra's algorithm to compute the shortest route from a to b.
     *
     * @param start the first node
     * @param dest the second node
     * @return Optionally an {@link Collection} of {@link String}s if such a Route exists, an
     * empty {@link Optional} otherwise
     */

    @Override
    @SuppressWarnings({"DiamondCanBeReplacedWithExplicitTypeArguments", "CollectionWithoutInitialCapacity", "LocalCanBeFinal", "MethodWithMultipleReturnPoints", "LocalVariableOfConcreteClass", "MethodCallInLoopCondition", "ObjectAllocationInLoop", "SuspiciousMethodCalls"})
    public Optional<List<E>> shortestRoute(final E start, final E dest) {

        /**
         * To be used by the HeapBasedPriorityQueue below.
         * Part of Dijkstra's Algorithm.
         */

        @SuppressWarnings({"PackageVisibleField", "LimitedScopeInnerClass", "ClassHasNoToStringMethod", "ComparableImplementedButEqualsNotOverridden", "ReturnOfInnerClass"})
        final class Path implements Comparable<Path> {

            private static final int DEFAULT_PATH_LEN = 20;
            private final List<E> path;
            private int distance;


            public List<E> getPath() {
                return path;
            }

            public int getDistance() {
                return distance;
            }

            public void setDistance(final int distance) {
                this.distance = distance;
            }

            private Path(final Collection<E> nodes, final int distance) {
                this();
                path.addAll(nodes);
                this.distance = distance;
            }

            private Path(final E firstComponent) {
                this();
                path.add(firstComponent);
                distance = getWeight(start, firstComponent);
            }

            private Path() {
                path = new ArrayList<>(DEFAULT_PATH_LEN);
            }

            @SuppressWarnings("CompareToUsesNonFinalVariable")
            @Override
            public final int compareTo(final Path t) {
                return Integer.compare(distance, t.distance);
            }

            @SuppressWarnings("NonFinalFieldReferencedInHashCode")
            @Override
            public final int hashCode() {
                int result = path.hashCode();
                result = (31 * result) + distance;
                return result;
            }

            @SuppressWarnings({"ConditionalExpression", "NonFinalFieldReferenceInEquals", "UnnecessaryUnboxing"})
            @Override
            public final boolean equals(final Object obj) {
                return (obj instanceof Integer) && (((Integer) obj)
                        .intValue() == distance);
            }
        }

        // @formatter:off
        @SuppressWarnings("RedundantTypeArguments")
        final Queue<Path> unvisited = getAdjecentNodes(start)
                .stream()
                .map(Path::new)
                .collect(PriorityQueue<Path>::new, PriorityQueue<Path>::add, PriorityQueue<Path>::addAll);

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        final Map<E,Integer> visited = new HashMap<>(100);

        while (!unvisited.isEmpty()) {

            final Path focus = unvisited.poll();
            final E lastComponent = focus.path.get(focus.path.size() - 1);

            if (lastComponent.equals(dest))
                return Optional.of(focus.path);

            if (!visited.containsKey(focus) || (visited.get(lastComponent) > focus.distance))
                visited.put(lastComponent, focus.distance);

            // @formatter:on
            getAdjecentNodes(lastComponent).forEach((E x) -> {
                final List<E> path = new LinkedList<>(focus.path);
                path.add(x);
                unvisited
                        .add(new Path(path, focus.distance + getWeight(lastComponent, x)));
            });
        }
        // the path could not be found
        return Optional.empty();
    }

    @SuppressWarnings("IfMayBeConditional")
    @Override
    public Set<E> getAdjecentNodes(final E start) {
        // @formatter:off
        if (lookupTable.containsKey(start))

            return lookupTable.get(start)
                    .entrySet()
                    .stream()
                    .filter((Map.Entry<E, Integer> entry) -> entry
                            .getValue() >= 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

        else return new HashSet<>(0);
        // @formatter:on
    }


    @Override
    public Set<Edge<E>> getEdges() {
        // @formatter:off
        return lookupTable
                .keySet()
                .stream()
                .flatMap((E nodeId) -> lookupTable.get(nodeId)
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() >= 1)
                        .map((Map.Entry<E, Integer> entry) -> new Edge<>(
                                nodeId, entry.getValue(), entry.getKey())))
                .collect(Collectors.toSet());
        // @formatter:off
    }

    /**
     * Make a connection between two nodes. If they don't already exist, add
     * them.
     *
     * @param nodeA the first node
     * @param nodeB the second node
     * @param cost the cost of going from the first to the second node
     * @return the graph itself
     */

    @Override
    public void addEdge(final E nodeA, final int cost, final E nodeB) {
        if (!lookupTable.containsKey(nodeA)) add(nodeA);
        if (!lookupTable.containsKey(nodeB)) add(nodeB);
        lookupTable.get(nodeA).put(nodeB, cost);
    }
}

