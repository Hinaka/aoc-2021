fun main() {
    fun part1(input: List<String>): Int {
        val (dots, folds) = input.toDotsAndFolds()
        return dots.mapTo(mutableSetOf(), folds.first()::invoke).count()
    }

    fun part2(input: List<String>): String {
        val (dots, folds) = input.toDotsAndFolds()
        return folds.fold(dots) { currentDots, fold ->
            currentDots.mapTo(mutableSetOf(), fold::invoke)
        }.visualize()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    val input = readInput("Day13")

    check(part1(testInput) == 17)
    println(part1(input))

    println(part2(input))
}

private data class Dot(val x: Int, val y: Int)

private sealed interface Fold {

    operator fun invoke(dot: Dot): Dot

    class X(private val x: Int) : Fold {

        override fun invoke(dot: Dot): Dot =
            if (dot.x < x) dot else dot.copy(
                x = 2 * x - dot.x
            )
    }

    class Y(private val y: Int) : Fold {

        override fun invoke(dot: Dot): Dot =
            if (dot.y < y) dot else dot.copy(
                y = 2 * y - dot.y
            )
    }
}

private fun List<String>.toDotsAndFolds(): Pair<Set<Dot>, List<Fold>> {
    val index = indexOf("")

    val dots = take(index).map {
        val (x, y) = it.split(",")
        Dot(x.toInt(), y.toInt())
    }.toSet()

    val folds = drop(index + 1).map {
        val (direction, value) = it.removePrefix("fold along ").split('=')
        when (direction) {
            "x" -> Fold.X(value.toInt())
            "y" -> Fold.Y(value.toInt())
            else -> error(direction)
        }
    }

    return dots to folds
}

private fun Set<Dot>.visualize(): String {
    val maxX = maxOf { it.x }
    val maxY = maxOf { it.y }

    return buildString {
        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                if (Dot(x, y) in this@visualize) {
                    append('#')
                } else {
                    append(' ')
                }
            }
            appendLine()
        }
    }
}