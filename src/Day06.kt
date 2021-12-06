fun main() {
    fun part1(input: List<String>): Long {
        return simulate(input.getInitialPool(), 80)
    }

    fun part2(input: List<String>): Long {
        return simulate(input.getInitialPool(), 256)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.getInitialPool() = LongArray(9).apply {
    this@getInitialPool.single().split(",").forEach {
        this[it.toInt()]++
    }
}

private fun simulate(initialPool: LongArray, days: Int): Long {
    val currentPool = initialPool.copyOf()
    repeat(days) {
        val fishesAtZero = currentPool[0]
        currentPool.copyInto(currentPool, 0, 1)
        currentPool[8] = fishesAtZero
        currentPool[6] += fishesAtZero
    }
    return currentPool.sum()
}