fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.split(" | ").last() }
            .flatMap { it.split(" ") }
            .count { it.length in listOf(2, 3, 4, 7) }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { calculateOutput(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

private fun calculateOutput(input: String): Int {
    val (lhs, rhs) = input.split(" | ")

    val signalPatterns = lhs.split(" ").map { it.toSet() }.groupBy { it.size }
    val one = signalPatterns[2]!!.single()
    val four = signalPatterns[4]!!.single()
    val seven = signalPatterns[3]!!.single()
    val eight = signalPatterns[7]!!.single()
    val (threes, twoFive) = signalPatterns[5]!!.partition { it.intersect(one).size == 2 }
    val three = threes.single()
    val (twos, fives) = twoFive.partition { (it - four).size == 3 }
    val two = twos.single()
    val five = fives.single()
    val (sixes, zeroNine) = signalPatterns[6]!!.partition { it.intersect(one).size == 1 }
    val six = sixes.single()
    val (zeroes, nines) = zeroNine.partition { (it - three).size == 2 }
    val zero = zeroes.single()
    val nine = nines.single()

    val digitBySegments = listOf(zero, one, two, three, four, five, six, seven, eight, nine)
    return rhs.split(" ").fold(0) { output, s ->
        10 * output + digitBySegments.indexOf(s.toSet())
    }
}