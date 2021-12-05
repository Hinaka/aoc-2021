fun main() {
    fun part1(input: List<String>): Int {
        val lines = input.toLines().filter {
            it.start.x == it.end.x || it.start.y == it.end.y
        }
        return lines.countIntersections()
    }

    fun part2(input: List<String>): Int {
        val lines = input.toLines()
        return lines.countIntersections()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.toLines() = map {
    val (start, end) = it.split(" -> ")
    val (x1, y1) = start.split(",")
    val (x2, y2) = end.split(",")

    Line(
        start = Coordinate(x1.toInt(), y1.toInt()),
        end = Coordinate(x2.toInt(), y2.toInt())
    )
}

private data class Coordinate(val x: Int, val y: Int)

private data class Line(val start: Coordinate, val end: Coordinate) {
    val isVertical = start.x == end.x
}

private fun List<Line>.countIntersections(): Int {
    val diagram = mutableMapOf<Coordinate, Int>()

    forEach { line ->
        line.toCoordinates().forEach { coordinate ->
            diagram.merge(coordinate, 1, Int::plus)
        }
    }
    return diagram.filter { it.value >= 2 }.count()
}

private fun Line.toCoordinates(): List<Coordinate> = with(standardized()) {
    if (isVertical) {
        return (start.y..end.y).map { Coordinate(start.x, it) }
    }

    val distance = end.x - start.x
    val delta = when {
        start.y < end.y -> 1
        start.y > end.y -> -1
        else -> 0 // horizontal
    }

    return (0..distance).map { d ->
        Coordinate(start.x + d, start.y + d * delta)
    }
}

private fun Line.standardized() = when {
    start.x < end.x -> this // L -> R
    start.x == end.x && start.y < end.y -> this // T -> B
    else -> Line(end, start)
}