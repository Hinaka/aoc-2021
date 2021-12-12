fun main() {
    fun part1(input: List<String>): Int {
        return input.toCaveMap()
            .findAllPaths(listOf(listOf(Cave("start"))))
            .count()
    }

    fun part2(input: List<String>): Int {
        return input.toCaveMap()
            .findAllPaths(listOf(listOf(Cave("start"))), true)
            .count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val input = readInput("Day12")

    check(part1(testInput) == 226)
    println(part1(input))

    check(part2(testInput) == 3509)
    println(part2(input))

}

private fun List<String>.toCaveMap() = buildMap<Cave, Set<Cave>> {
    this@toCaveMap.forEach { line ->
        val (name1, name2) = line.split('-')
        val cave1 = Cave(name1)
        val cave2 = Cave(name2)

        merge(cave1, setOf(cave2), Set<Cave>::plus)
        merge(cave2, setOf(cave1), Set<Cave>::plus)
    }
}

private fun Map<Cave, Set<Cave>>.findAllPaths(
    currentPaths: List<List<Cave>>,
    canVisitSingleSmallCaveTwice: Boolean = false
): List<List<Cave>> {
    if (currentPaths.all { path -> path.isEnd }) {
        return currentPaths
    }

    val newPaths = currentPaths.flatMap { path ->
        if (path.isEnd) {
            listOf(path)
        } else {
            val lastCave = path.last()

            val allowRevisit = canVisitSingleSmallCaveTwice && !path.isVisitedSmallCaveTwice
            getValue(lastCave)
                .filterNot { it.isStart }
                .filterNot { it.isSmall && it in path && !allowRevisit}
                .map { cave -> path + cave }
        }
    }

    return findAllPaths(newPaths, canVisitSingleSmallCaveTwice)
}

private val List<Cave>.isEnd get() = last().isEnd

private val List<Cave>.isVisitedSmallCaveTwice
    get() = filter { it.isSmall }.groupBy { it }.any { (_, list) -> list.size >= 2 }

private data class Cave(val name: String) {
    val isLarge = name.all { it.isUpperCase() }
    val isStart = name == "start"
    val isEnd = name == "end"
    val isSmall = !isStart && !isEnd && !isLarge
}
