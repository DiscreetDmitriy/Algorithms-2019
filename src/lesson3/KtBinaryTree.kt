package lesson3

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max


// Attention: comparable supported but comparator is not
@Suppress("SuspiciousVarProperty")
open class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        get() {
            var res = 0
            for (i in this)
                res++
            return res
        }

    inner class Node<T>(var value: T) {
        var left: Node<T>? = null
        var right: Node<T>? = null
        var parent: Node<T>? = null

        fun minimum(): Node<T>? {
            var current: Node<T>? = this
            while (current!!.left != null) {
                current = current.left
            }
            return current
        }
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        newNode.parent = closest
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    override fun height(): Int = height(root)

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     *
     *  //     Трудоёмкость: O(h)
     *  //     Ресурсоёмкость: O(h)
     */
    override fun remove(element: T): Boolean {
        val closest = find(element)

        return if (closest == null || element.compareTo(closest.value) != 0) false
        else {
            size--
            remove(closest)
        }
    }

    private fun remove(node: Node<T>): Boolean {
        val parent = node.parent

        when {
            node.left == null && node.right == null -> parent.replaceChild(node, null)

            node.left == null -> parent.replaceChild(node, node.right)

            node.right == null -> parent.replaceChild(node, node.left)

            else -> {
                var change = node.right

                while (change!!.left != null)
                    change = change.left

                val replacement = Node(change.value)

                replacement.left = if (replacement.value != node.left?.value) node.left else null
                replacement.right = if (replacement.value != node.right?.value) node.right else node.right?.right

                parent.replaceChild(node, replacement)

                change.parent.replaceChild(change, change.right)
            }
        }

        return true
    }

    private fun Node<T>?.replaceChild(node: Node<T>, newNode: Node<T>?) {
        newNode?.parent = this

        when {
            this == null -> root = newNode

            this.left?.value?.compareTo(node.value) == 0 -> this.left = newNode

            else -> this.right = newNode
        }
    }


    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    open inner class BinaryTreeIterator internal constructor() : MutableIterator<T> {

        private var current: Node<T>? = null
        private var stack: Stack<Node<T>> = Stack()

        init {
            var node = root
            while (node != null) {
                stack.push(node)
                node = node.left
            }
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         *
         *  //     Трудоёмкость: O(1)
         *  //     Ресурсоёмкость: O(1)
         */
        override fun hasNext(): Boolean = stack.isNotEmpty()

        /**
         * Поиск следующего элемента
         * Средняя
         *
         *  //     Трудоёмкость: среднее - O(1), худший случай - O(n)
         *  //     Ресурсоёмкость: O(h)
         */
        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()

            var node = stack.pop()
            val result = node

            if (node.right != null) {
                node = node.right

                while (node != null) {
                    stack.push(node)
                    node = node.left
                }
            }
            current = result
            return current!!.value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         *
         *  //     Трудоёмкость: O(h)
         *  //     Ресурсоёмкость: O(h)
         */
        override fun remove() {
            remove(current ?: return)
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    inner class BinarySubTree<T : Comparable<T>> internal constructor(
        private val tree: KtBinaryTree<T>,
        private val start: T?,
        private val end: T?
    ) : KtBinaryTree<T>() {

        override var size: Int = 0
            get() {
                var res = 0
                for (i in tree)
                    if (i.isValid())
                        res++
                return res
            }

        override fun contains(element: T): Boolean =
            tree.contains(element) && element.isValid()

        override fun add(element: T): Boolean {
            if (element.isValid())
                return tree.add(element)
            else
                throw IllegalArgumentException()
        }

        override fun remove(element: T): Boolean {
            if (element.isValid())
                return tree.remove(element)
            else
                throw IllegalArgumentException()
        }

        override fun first(): T {
            for (i in tree)
                if (i.isValid())
                    return i

            throw NoSuchElementException()
        }

        override fun last(): T {
            for (i in tree.reversed())
                if (i.isValid())
                    return i

            throw NoSuchElementException()
        }

        private fun T.isValid() = (start == null || this >= start) && (end == null || this < end)
    }

    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     *
     *  //     Не уверен насчёт этих сложностей, но думаю тут
     *
     *  //     Трудоёмкость: O(1)
     *  //     Ресурсоёмкость: O(1)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> = BinarySubTree(this, fromElement, toElement)

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     *
     *  //     Трудоёмкость: O(1)
     *  //     Ресурсоёмкость: O(1)
     */
    override fun headSet(toElement: T): SortedSet<T> = BinarySubTree(this, null, toElement)

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     *
     *  //     Трудоёмкость: O(1)
     *  //     Ресурсоёмкость: O(1)
     */
    override fun tailSet(fromElement: T): SortedSet<T> = BinarySubTree(this, fromElement, null)

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    private fun firstNode(): Node<T> {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}

