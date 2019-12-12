@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson5

import lesson5.Graph.Edge
import lesson5.Graph.Vertex
import lesson5.impl.GraphBuilder
import java.util.*

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 *
 *  //     Трудоёмкость: O(V + E)
 *  //     Ресурсоёмкость: O(V + E)
 */
fun Graph.findEulerLoop(): List<Edge> {
    if (this.vertices.isEmpty()
        || this.edges.isEmpty()
        || !this.hasEulerLoop()
    ) return listOf()

    val edges = this.edges
    val vertexStack = Stack<Vertex>()
    val eulerLoopEdges = mutableListOf<Edge>()

    vertexStack.push(this.vertices.first())

    while (vertexStack.isNotEmpty()) {
        val currVertex = vertexStack.peek()

        for (vertex in this.vertices) {
            val edge = this.getConnection(currVertex, vertex) ?: continue

            if (edges.contains(edge)) {
                vertexStack.push(vertex)
                edges.remove(edge)
                break
            }
        }

        if (currVertex == vertexStack.peek()) {
            vertexStack.pop()

            if (vertexStack.isNotEmpty())
                eulerLoopEdges.add(this.getConnection(currVertex, vertexStack.peek())!!)
        }
    }

    return eulerLoopEdges
}

private fun Graph.hasEulerLoop() = this.vertices.none { this.getNeighbors(it).size % 2 != 0 }

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 *
 *  //     Трудоёмкость: O(V + E)
 *  //     Ресурсоёмкость: O(V + E)
 */
fun Graph.minimumSpanningTree(): Graph {
    val res = GraphBuilder()

    if (this.vertices.isEmpty()
        || this.edges.isEmpty()
    ) return res.build()

    val info = this.shortestPath(this.vertices.first())

    for (vertex in this.vertices)
        res.addVertex(vertex.name)

    for ((vertex, vertexInfo) in info)
        if (vertexInfo.prev != null)
            res.addConnection(vertexInfo.prev, vertex, vertexInfo.distance)

    return res.build()
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 *
 *  //     Трудоёмкость: O(V + E)
 *  //     Ресурсоёмкость: O(V^2)
 */
fun Graph.largestIndependentVertexSet(): Set<Vertex> {
    if (this.vertices.isEmpty()
        || this.edges.isEmpty()
    ) return emptySet()

    val bridges = this.findBridges()

    require(bridges.isNotEmpty())

    val independentSets = mutableMapOf<Vertex, Set<Vertex>>()
    val independentVertices = mutableSetOf<Vertex>()
    val unconnectedVerticesSet = this.unconnectedVerticesSet()

    for (vertex in unconnectedVerticesSet)
        independentVertices.addAll(
            this.independentVerticesSet(
                independentSets = independentSets,
                parent = null,
                vertex = vertex
            )
        )

    return independentVertices
}

private fun Graph.unconnectedVerticesSet(): Set<Vertex> {
    val unconnectedVertices = mutableSetOf<Vertex>()

    this.dfs(this.vertices.first())

    unconnectedVertices.add(this.vertices.first())

    for (vertex in this.vertices)
        if (!vertex.isVisited) {
            unconnectedVertices.add(vertex)
            this.dfs(vertex)
        }

    return unconnectedVertices
}

private fun Graph.dfs(vertex: Vertex) {
    vertex.isVisited = true

    for (neighbour in this.getNeighbors(vertex))
        if (!neighbour.isVisited)
            this.dfs(neighbour)
}

private fun Graph.independentVerticesSet(
    independentSets: MutableMap<Vertex, Set<Vertex>>,
    parent: Vertex?,
    vertex: Vertex
): Set<Vertex> = independentSets.getOrPut(vertex) {
    val children = this.independentChildrenList(independentSets, parent, vertex)

    val grandChildren =
        this.getNeighbors(vertex)
            .filterNot { it == parent }
            .flatMap { this.independentChildrenList(independentSets, vertex, it) }
            .plus(vertex)

    if (grandChildren.size < children.size - 1)
        children.toSet()
    else
        grandChildren.toSet()
}

private fun Graph.independentChildrenList(
    independentSets: MutableMap<Vertex, Set<Vertex>>,
    parent: Vertex?,
    vertex: Vertex
): List<Vertex> = this.getNeighbors(vertex)
    .filterNot { it == parent }
    .flatMap { this.independentVerticesSet(independentSets, vertex, it) }


/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 *
 *  //     Трудоёмкость: O(V!)
 *  //     Ресурсоёмкость: O(V!)
 */
fun Graph.longestSimplePath(): Path {
    if (this.vertices.isEmpty()
        || this.edges.isEmpty()
    ) return Path()

    var longestPath = Path(this.vertices.first())
    val queue = PriorityQueue<Path>()

    this.vertices.mapTo(queue) { Path(it) }

    while (queue.isNotEmpty()) {
        val current = queue.poll()

        if (current.length > longestPath.length)
            longestPath = current

        val last = current.vertices.last()
        val neighbors = this.getNeighbors(last)

        for (it in neighbors)
            if (it !in current)
                queue.add(Path(current, this, it))
    }

    return longestPath
}