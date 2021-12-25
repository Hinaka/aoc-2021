fun main() {
    fun part1(input: List<String>): Int {
        var map = input.toSeaCucumberMap()
        val width = input.first().length
        val height = input.size
        var isContinue = true
        var steps = 0

        while (isContinue) {
            steps++
            val (newMap, isMoved) = simulate(map, width, height)
            map = newMap
            isContinue = isMoved
        }

        return steps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    val input = readInput("Day25")

    check(part1(testInput) == 58)
    println(part1(input))
}

private enum class Move {
    EAST, SOUTH
}

private data class SeaCucumber(val move: Move)

private data class Location(val x: Int, val y: Int) {
    fun eastLocation(width: Int): Location {
        return Location(
            x = (x + 1) % width,
            y = y,
        )
    }

    fun southLocation(height: Int): Location {
        return Location(
            x = x,
            y = (y + 1) % height,
        )
    }
}

private fun List<String>.toSeaCucumberMap(): Map<Location, SeaCucumber> {
    val map = mutableMapOf<Location, SeaCucumber>()

    forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            when (c) {
                '>' -> map[Location(x, y)] = SeaCucumber(Move.EAST)
                'v' -> map[Location(x, y)] = SeaCucumber(Move.SOUTH)
            }
        }
    }

    return map
}

private fun Map<Location, SeaCucumber>.print(width: Int, height: Int) {
    for (y in 0 until height) {
        for (x in 0 until width) {
            print(
                when (val sc = this[Location(x, y)]) {
                    is SeaCucumber -> when (sc.move) {
                        Move.EAST -> ">"
                        Move.SOUTH -> "v"
                    }
                    else -> "."
                }
            )
        }
        println()
    }
    println()
}

private fun simulate(
    map: Map<Location, SeaCucumber>,
    width: Int,
    height: Int
): Pair<Map<Location, SeaCucumber>, Boolean> {
    var isSeaCucumberMoving = false
    val newMap = mutableMapOf<Location, SeaCucumber>()

    val eastSeaCucumbers = map.filter { it.value.move == Move.EAST }
    val southSeaCucumbers = map.filter { it.value.move == Move.SOUTH }

    eastSeaCucumbers.forEach { (location, seaCucumber) ->
        val newLocation = location.eastLocation(width)
        when (map[newLocation]) {
            null -> {
                newMap[newLocation] = seaCucumber
                isSeaCucumberMoving = true
            }
            else -> newMap[location] = seaCucumber
        }
    }

    val tmpMap = newMap.toMutableMap()
    tmpMap.putAll(southSeaCucumbers)

    southSeaCucumbers.forEach { (location, seaCucumber) ->
        val newLocation = location.southLocation(height)
        when (tmpMap[newLocation]) {
            null -> {
                newMap[newLocation] = seaCucumber
                isSeaCucumberMoving = true
            }
            else -> newMap[location] = seaCucumber
        }
    }

    return newMap to isSeaCucumberMoving
}