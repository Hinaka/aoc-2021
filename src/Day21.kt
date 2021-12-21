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

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    val input = readInput("Day21")

    check(part1(testInput) == 739785L)
    println(part1(input))

    check(part2(testInput) == 5)
    println(part2(input))
}
