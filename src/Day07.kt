import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val initialPositions = input.toInitialPositions()
        return calculateOptimizedCost(initialPositions) { position, targetPosition ->
            abs(position - targetPosition)
        }
    }

    fun part2(input: List<String>): Int {
        val initialPositions = input.toInitialPositions()
        return calculateOptimizedCost(initialPositions) { position, targetPosition ->
            val steps = abs(position - targetPosition)
            steps * (steps + 1) / 2
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

private fun calculateOptimizedCost(
    initialPositions: List<Int>,
    costOf: (position: Int, targetPosition: Int) -> Int
): Int {
    return initialPositions.minMaxRange().minOf { targetPosition ->
        initialPositions.sumOf { position -> costOf(position, targetPosition) }
    }
}

private fun List<String>.toInitialPositions() = single().split(",").map { it.toInt() }

private fun List<Int>.minMaxRange(): IntRange {
    val min = minOrNull()!!
    val max = maxOrNull()!!
    return (min..max)
}