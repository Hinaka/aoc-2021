import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        return getLowestTotalRisk(input.toCaveGrid(1))
    }

    fun part2(input: List<String>): Int {
        return getLowestTotalRisk(input.toCaveGrid(5))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    val input = readInput("Day15")

    check(part1(testInput) == 40)
    println(part1(input))

    check(part2(testInput) == 315)
    println(part2(input))
}

private data class Position(val x: Int, val y: Int, val riskValue: Int)

private fun List<String>.toCaveGrid(scale: Int): List<List<Int>> {
    val n = size
    return (0 until n * scale).map { x ->
        (0 until n * scale).map { y ->
            val inputValue = this[x % n][y % n].digitToInt()
            val scaledValue = inputValue + (x / n + y / n)
            if (scaledValue < 10) scaledValue else scaledValue - 9
        }
    }
}

private fun getLowestTotalRisk(caveGrid: List<List<Int>>): Int {
    val indices = caveGrid.indices

    val queue = PriorityQueue(compareBy(Position::riskValue))

    val riskTable = Array(caveGrid.size) { IntArray(caveGrid.size) { Int.MAX_VALUE } }
    riskTable[0][0] = 0

    fun updatePath(x: Int, y: Int, riskValue: Int) {
        if (x !in indices || y !in indices) return
        val newRiskValue = riskValue + caveGrid[x][y]
        if (newRiskValue < riskTable[x][y]) {
            riskTable[x][y] = newRiskValue
            queue += Position(x, y, newRiskValue)
        }
    }

    queue.add(Position(0, 0, 0))
    while (queue.isNotEmpty()) {
        val (posX, posY, riskValue) = queue.poll()
        surroundingPositionsOf(posX, posY).forEach { (x, y) ->
            updatePath(x, y, riskValue)
        }
    }

    return riskTable.last().last()
}

private fun surroundingPositionsOf(x: Int, y: Int) = buildList {
    for (i in -1..1) {
        if (i == 0) continue
        add((x + i) to y)
        add(x to (y + i))
    }
}
