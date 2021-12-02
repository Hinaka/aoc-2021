import java.awt.Point

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.toDirection() }
            .fold(Point(0, 0)) { point, direction ->
                point.apply {
                    when (direction) {
                        is Direction.Forward -> translate(direction.unit, 0)
                        is Direction.Up -> translate(0, -direction.unit)
                        is Direction.Down -> translate(0, direction.unit)
                    }
                }
            }.let { it.x * it.y }
    }

    fun part2(input: List<String>): Int {
        return input
            .map { it.toDirection() }
            .fold(Pair(Point(0, 0), 0)) { (point, aim), direction ->
                val unit = direction.unit
                when (direction) {
                    is Direction.Forward -> point.apply { translate(unit, aim * unit) } to aim
                    is Direction.Up -> point to aim - unit
                    is Direction.Down -> point to aim + unit
                }
            }.let { (point, _) -> point.x * point.y }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

sealed class Direction(val unit: Int) {
    class Forward(unit: Int) : Direction(unit)
    class Up(unit: Int) : Direction(unit)
    class Down(unit: Int) : Direction(unit)
}

fun String.toDirection() = split(" ").let { (direction, value) ->
    val unit = value.toIntOrNull() ?: error("invalid input")
    when (direction) {
        "forward" -> Direction.Forward(unit)
        "up" -> Direction.Up(unit)
        "down" -> Direction.Down(unit)
        else -> error("invalid input")
    }
}
