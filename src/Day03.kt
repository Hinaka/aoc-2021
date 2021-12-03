fun main() {
    fun part1(input: List<String>): Int {
        val inputTable = input.map { line -> line.map { it.digitToInt() } }
        val bitsSumIndexed = inputTable.bitsSumIndexed()

        val gamma = bitsSumIndexed
            .map { bit -> if (bit > 0) 1 else 0 }
            .joinToString("")
            .toInt(2)
        val epsilon = bitsSumIndexed
            .map { bit -> if (bit > 0) 0 else 1 }
            .joinToString("")
            .toInt(2)

        return gamma * epsilon
    }

    fun part2(input: List<String>): Int {
        val inputTable = input.map { line -> line.map { it.digitToInt() } }
        val oxygenGeneratorRating = inputTable.getRating { bitsSum ->
            if (bitsSum >= 0) 1 else 0
        }
        val co2ScrubberRating = inputTable.getRating { bitsSum ->
            if (bitsSum >= 0) 0 else 1
        }

        return oxygenGeneratorRating * co2ScrubberRating
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

/**
 * Increase by 1 if bit is 1, and decrease by 1 if bit is 0. Hence, a positive value indicates more 1 bits
 * at that index, 0 means equals, and a negative value indicates more 0 bits.
 */
private fun List<List<Int>>.bitsSumIndexed() = first().indices
    .map { index ->
        calculateBitsSumAt(index)
    }

private fun List<List<Int>>.calculateBitsSumAt(index: Int) =
    fold(0) { occurrences, bits ->
        if (bits[index] == 1) {
            occurrences + 1
        } else {
            occurrences - 1
        }
    }

private fun List<List<Int>>.getRating(index: Int = 0, createBitCriteria: (bitsSum: Int) -> Int): Int {
    val bitsSum = calculateBitsSumAt(index)
    val bitCriteria = createBitCriteria(bitsSum)
    val validInputs = filter { input -> input[index] == bitCriteria }
    return if (validInputs.size == 1) {
        validInputs.first().joinToString("").toInt(2)
    } else {
        validInputs.getRating(index +1, createBitCriteria)
    }
}