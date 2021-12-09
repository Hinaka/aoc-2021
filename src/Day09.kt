fun main() {
    fun part1(input: List<String>): Int {
        val points = input.toPoints()
        val n = points.size
        val m = points.first().size

        var lowPointsSum = 0
        for (i in 0 until n) {
            for (j in 0 until m) {
                val surroundingPoints = listOfNotNull(
                    points[i].getOrNull(j - 1),
                    points[i].getOrNull(j + 1),
                    points.getOrNull(i - 1)?.get(j),
                    points.getOrNull(i + 1)?.get(j),
                )

                if (points[i][j] < surroundingPoints.minOrNull()!!) lowPointsSum += 1 + points[i][j]
            }
        }

        return lowPointsSum
    }

    fun part2(input: List<String>): Int {
        val points = input.toPoints()
        val n = points.size
        val m = points.first().size
        val heightRange = (0 until n)
        val widthRange = (0 until m)

        val visitedPoints = Array(n) { BooleanArray(m) }

        fun basinSizeAt(i: Int, j: Int): Int {
            val outOfRange = i !in heightRange || j !in widthRange
            if (outOfRange || visitedPoints[i][j] || points[i][j] == 9) return 0
            visitedPoints[i][j] = true
            return 1 + basinSizeAt(i + 1, j) + basinSizeAt(i - 1, j) + basinSizeAt(i, j - 1) + basinSizeAt(i, j + 1)
        }

        val basinSizes = mutableListOf<Int>()

        for (i in heightRange) {
            for (j in widthRange) {
                if (!visitedPoints[i][j] && points[i][j] != 9) {
                    basinSizes.add(basinSizeAt(i, j))
                }
            }
        }

        return basinSizes.sortDescending().let { basinSizes[0] * basinSizes[1] * basinSizes[2] }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.toPoints() = map { it.map { it.digitToInt() } }