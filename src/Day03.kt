fun main() {
    fun part1(input: List<String>): Int {
        val bitOccurrenceByIndex = input.first().indices.map { index ->
            input.bitOccurrenceAt(index)
        }

        val mostCommonBits = bitOccurrenceByIndex.joinToString("") { bitOccurrence ->
            bitOccurrence.maxByOrNull { it.value }?.key.toString()
        }

        val leastCommonBits = bitOccurrenceByIndex.joinToString("") { bitOccurrence ->
            bitOccurrence.minByOrNull { it.value }?.key.toString()
        }

        val gamma = mostCommonBits.toInt(2)
        val epsilon = leastCommonBits.toInt(2)

        return gamma * epsilon
    }

    fun part2(input: List<String>): Int {
        val oxygenGeneratorRating = input.findRating { zeroes, ones ->
            if (ones >= zeroes) '1' else '0'
        }.toInt(2)

        val co2ScrubberRating = input.findRating { zeroes, ones ->
            if (ones >= zeroes) '0' else '1'
        }.toInt(2)

        return oxygenGeneratorRating * co2ScrubberRating
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.bitOccurrenceAt(index: Int) = groupingBy { it[index] }.eachCount()

private fun List<String>.findRating(getCriteria: (zeroes: Int, ones: Int) -> Char): String {
    val validInputs = toMutableList()
    for (index in first().indices) {
        val bitOccurrences = validInputs.bitOccurrenceAt(index)
        val zeroes = bitOccurrences['0'] ?: 0
        val ones = bitOccurrences['1'] ?: 0
        val bitCriteria = getCriteria(zeroes, ones)
        validInputs.removeAll { it[index] != bitCriteria }
        if (validInputs.size == 1) break
    }
    return validInputs.single()
}