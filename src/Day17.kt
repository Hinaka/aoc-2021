import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        return input.toAllInitialVelocities().maxOf { it.second }.let {
            it * (it + 1) / 2
        }
    }

    fun part2(input: List<String>): Int {
        return input.toAllInitialVelocities().count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    val input = readInput("Day17")

    check(part1(testInput) == 45)
    println(part1(input))

    check(part2(testInput) == 112)
    println(part2(input))
}

private fun List<String>.toAllInitialVelocities(): List<Pair<Int, Int>> {
    val (targetXRange, targetYRange) = getTargetRange()
    val viableXVelocityRange = (0..targetXRange.last)
    val viableYVelocityRange = targetYRange.let {
        val start = it.first.coerceAtMost(0)
        val end = maxOf(abs(it.first), abs(it.last))
        start..end
    }

    val velocities = mutableListOf<Pair<Int, Int>>()
    for (initialX in viableXVelocityRange) {
        for (initialY in viableYVelocityRange) {
            var x = 0
            var y = 0
            var dx = initialX
            var dy = initialY
            while (x <= targetXRange.last && (dy > 0 || y >= targetYRange.first)) {
                if (x in targetXRange && y in targetYRange) {
                    velocities.add(initialX to initialY)
                    break
                }
                if (dx > 0) {
                    x += dx
                    dx--
                }
                y += dy
                dy--
            }
        }
    }

    return velocities
}

private fun List<String>.getTargetRange() = single().removePrefix("target area: ").let {
    val (xr, yr) = it.split(", ")
    val (x1, x2) = xr.removePrefix("x=").split("..").map { x -> x.toInt() }
    val (y1, y2) = yr.removePrefix("y=").split("..").map { y -> y.toInt() }
    (x1..x2) to (y1..y2)
}