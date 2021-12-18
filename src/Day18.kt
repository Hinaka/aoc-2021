import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    fun part1(input: List<String>): Int {
        return input.toSnailsNumbers().reduce { acc, node -> acc + node }.magnitude
    }

    fun part2(input: List<String>): Int {
        val sums = mutableSetOf<Int>()
        val snailNumbers = input.toSnailsNumbers()

        snailNumbers.forEachIndexed { i, first ->
            snailNumbers.forEachIndexed { j, second ->
                if (i != j) {
                    sums += (first + second).magnitude
                }
            }
        }

        return sums.maxOrNull()!!
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    val input = readInput("Day18")

    check(part1(testInput) == 4140)
    println(part1(input))

    check(part2(testInput) == 3993)
    println(part2(input))
}

private const val OPEN_ELEMENT = '['
private const val CLOSE_ELEMENT = ']'

private fun List<String>.toSnailsNumbers() = map { line ->
    val stack = ArrayDeque<Node>()
    line.forEach { char ->
        when (char) {
            OPEN_ELEMENT -> {
                val parenNode = stack.lastOrNull()
                val node = Node(parenNode)
                parenNode?.also {
                    if (it.left == null) {
                        it.left = node
                    } else {
                        it.right = node
                    }
                }
                stack.add(node)
            }
            CLOSE_ELEMENT -> {
                // Only need to keep the first node, since it contains reference to all its children
                if (stack.size > 1) {
                    stack.removeLast()
                }
            }
            else -> {
                // avoid `,` character
                if (char.isDigit()) {
                    val node = stack.last()
                    val childNode = Node.ofValue(node, char.digitToInt())
                    if (node.left == null) {
                        node.left = childNode
                    } else {
                        node.right = childNode
                    }
                }
            }
        }
    }


    stack.removeLast()
}

private class Node(private var parent: Node?) {
    private var value = 0
    var left: Node? = null
    var right: Node? = null

    override fun toString(): String {
        return if (isPair()) "[$left,$right]" else "$value"
    }

    private fun isPair(): Boolean {
        return left != null && right != null
    }

    operator fun plus(other: Node): Node {
        return ofPair(this.clone(), other.clone()).reduce()
    }

    private fun clone(): Node {
        val newNode = Node(parent)

        val leftNode = left!!
        newNode.left = if (leftNode.isPair()) {
            leftNode.clone().apply { parent = newNode }
        } else ofValue(
            newNode,
            leftNode.value
        )

        val rightNode = right!!
        newNode.right = if (rightNode.isPair()) {
            rightNode.clone().apply { parent = newNode }
        } else ofValue(
            newNode,
            rightNode.value
        )

        return newNode
    }

    private fun reduce(): Node {
        do {
            val explodableNode = findExplodableNode()
            val splittableNode = findSplittableNode()
            if (explodableNode != null) {
                explodableNode.explode()
            } else splittableNode?.split()
        } while (explodableNode != null || splittableNode != null)

        return this
    }

    private fun findExplodableNode(): Node? {
        val stack = ArrayDeque<Node>()
        stack.add(this)
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            if (node.isPair()) {
                stack.add(node.right!!)
                stack.add(node.left!!)
            } else if (node.parent!!.depth >= 4) {
                return node.parent
            }
        }
        return null
    }

    private val depth: Int
        get() {
            var depth = 0
            var node = parent
            while (node != null) {
                depth++
                node = node.parent
            }

            return depth
        }

    private fun findSplittableNode(): Node? {
        val stack = ArrayDeque<Node>()
        stack.add(this)
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            if (node.isPair()) {
                stack.add(node.right!!)
                stack.add(node.left!!)
            } else if (node.value >= 10) {
                return node
            }
        }
        return null
    }

    private fun explode() {
        // Only pair node can explore
        if (!isPair()) return

        val left = findFirstRegularToTheLeft()
        val right = findFirstRegularToTheRight()

        if (left != null) {
            left.value += this.left!!.value
        }
        if (right != null) {
            right.value += this.right!!.value
        }

        this.value = 0
        this.left = null
        this.right = null
    }

    private fun findFirstRegularToTheLeft(): Node? {
        var node: Node? = null
        val visited = mutableSetOf<Node>()
        visited.add(this)
        var next = parent
        while (next != null) {
            if (next.left !in visited) {
                node = next.left
                break
            } else {
                visited.add(next)
                next = next.parent
            }
        }

        while (node != null && node.isPair()) {
            node = node.right
        }

        return node
    }

    private fun findFirstRegularToTheRight(): Node? {
        var node: Node? = null
        val visited = mutableSetOf<Node>()
        visited.add(this)
        var next = parent
        while (next != null) {
            if (next.right !in visited) {
                node = next.right
                break
            } else {
                visited.add(next)
                next = next.parent
            }
        }

        while (node != null && node.isPair()) {
            node = node.left
        }

        return node
    }

    private fun split() {
        // only regular number node can split
        if (isPair()) return
        val splitValue = value / 2.0
        val leftValue = floor(splitValue).toInt()
        val rightValue = ceil(splitValue).toInt()

        this.value = 0
        this.left = ofValue(this, leftValue)
        this.right = ofValue(this, rightValue)
    }

    val magnitude: Int
        get() {
            val leftNode = left!!
            val rightNode = right!!

            val leftMagnitude = if (leftNode.isPair()) leftNode.magnitude else leftNode.value
            val rightMagnitude = if (rightNode.isPair()) rightNode.magnitude else rightNode.value

            return (leftMagnitude * 3) + (rightMagnitude * 2)
        }

    companion object {
        fun ofValue(parent: Node, value: Int) = Node(parent).apply {
            this.value = value
        }

        fun ofPair(left: Node, right: Node) = Node(null).apply {
            this.left = left
            this.right = right

            left.parent = this
            right.parent = this
        }
    }
}
