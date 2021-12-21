fun main() {
    fun part1(input: List<String>): Long {
        val positions = input.map { it.last().digitToInt().toLong() }.toMutableList()
        val scores = mutableListOf(0L, 0L)

        var player = 0

        var rollTimes = 0L
        var rollValue = 0L
        fun roll(): Long {
            rollTimes++
            rollValue += 1 % 101
            return rollValue
        }

        while (scores.maxOf { it } < 1000) {
            val rollSum = (1..3).sumOf { roll() }
            positions[player] = (positions[player] + rollSum - 1) % 10 + 1
            scores[player] += positions[player]
            player = (player + 1) % 2
        }

        return scores.minOf { it } * rollTimes
    }

    fun part2(input: List<String>): Long {
        val positions = input.map { it.last().digitToInt() }
        return Score(positions[0], positions[1], 0, 0).let { (first, second) ->
            maxOf(first, second)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    val input = readInput("Day21")

    check(part1(testInput) == 739785L)
    println(part1(input))

    check(part2(testInput) == 444356092776315L)
    println(part2(input))
}

private const val TOTAL_STATE = 44100 // 10 distinct position, 0 - 21 possible point, and 2 player = (10 * 21)^2 states

private object Score {
    private val dice = sequence {
        for (i in 1..3) for (j in 1..3) for (k in 1..3) yield(i + j + k)
    }.groupingBy { it }.eachCount()

    private val x = LongArray(TOTAL_STATE)
    private val y = LongArray(TOTAL_STATE)

    operator fun invoke(p1: Int, p2: Int, s1: Int, s2: Int): Pair<Long, Long> {
        // p1 has 10 possible values
        // [p1, p2] has 10 * 10 = 100 possible values
        // [pi, p2, s1] has 10 * 10 * 21 = 2100 possible values
        // p1, p2 start from 1 to 10 so the zero index is for case of [1,1,0,0] = 11
        val i = p1 + 10 * p2 + 100 * s1 + 2100 * s2 - 11
        if (x[i] == 0L && y[i] == 0L) {
            var x1 = 0L
            var y1 = 0L
            for ((d, n) in dice) {
                val play = (p1 + d - 1) % 10 + 1
                if (s1 + play < 21) {
                    val (x2, y2) = this(p2, play, s2, s1 + play)
                    x1 += n * y2
                    y1 += n * x2
                } else {
                    x1 += n
                }
            }
            x[i] = x1
            y[i] = y1
        }
        return x[i] to y[i]
    }
}
