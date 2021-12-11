fun main() {
    fun part1(input: List<String>): Int {
        var flashes = 0
        val octopusMap = input.toOctopusMap()

        repeat(100) {
            flashes += octopusMap.step()
        }

        return flashes
    }

    fun part2(input: List<String>): Int {
        val octopusMap = input.toOctopusMap()
        var step = 0

        while (true) {
            step++
            val flashes = octopusMap.step()
            if (flashes == 100) {
                break
            }
        }

        return step
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    val input = readInput("Day11")

    check(part1(testInput) == 1656)
    println(part1(input))

    check(part2(testInput) == 195)
    println(part2(input))
}

private data class Coord(val x: Int, val y: Int) {

    val surrounding
        get() = buildList {
            for (i in -1..1) {
                for (j in -1..1) {
                    if (i == 0 && j == 0) continue
                    add(Coord(x + i, y + j))
                }
            }
        }
}

private data class Octopus(var energy: Int) {

    val isFullEnergy get() = energy >= 10

    val isFlashed get() = energy == 0

    fun charge() {
        energy++
    }

    fun flash() {
        energy = 0
    }
}

private fun List<String>.toOctopusMap() = buildMap {
    this@toOctopusMap.forEachIndexed { x, line ->
        line.forEachIndexed { y, char ->
            put(Coord(x, y), Octopus(char.digitToInt()))
        }
    }
}

private fun Map<Coord, Octopus>.step(): Int {
    var flashes = 0

    values.forEach { it.charge() }
    while (any { it.value.isFullEnergy }) {
        val fullEnergyOctopus = filter { it.value.isFullEnergy }
        fullEnergyOctopus.forEach { (coord, octopus) ->
            octopus.flash()
            flashes++

            val surroundingOctopus = coord.surrounding
                .mapNotNull { get(it) }
                .filter { !it.isFlashed }
            surroundingOctopus.forEach { it.charge() }
        }
    }

    return flashes
}