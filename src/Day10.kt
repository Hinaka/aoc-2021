fun main() {
    fun part1(input: List<String>): Int {
        val pointMap = mapOf(
            ')' to 3,
            ']' to 57,
            '}' to 1197,
            '>' to 25137,
        )

        return input.toLines()
            .filterIsInstance<SyntaxErrorLine>()
            .fold(0) { acc, syntaxErrorLine ->
                acc + pointMap.getValue(syntaxErrorLine.errorChar)
            }
    }

    fun part2(input: List<String>): Long {
        val pointMap = mapOf(
            ')' to 1,
            ']' to 2,
            '}' to 3,
            '>' to 4,
        )

        return input.toLines()
            .filterIsInstance<IncompleteLine>()
            .map { incompleteLine ->
                incompleteLine.missingChars.fold(0L) { acc, char ->
                    acc * 5 + pointMap.getValue(char)
                }
            }.sorted()
            .let {
                it[it.size / 2]
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

private sealed interface NavigationLine

private data class SyntaxErrorLine(val errorChar: Char) : NavigationLine
private data class IncompleteLine(val missingChars: List<Char>) : NavigationLine

private fun List<String>.toLines() = map { line ->
    parseLine(line)
}

private fun parseLine(line: String): NavigationLine {
    val deque = ArrayDeque<Char>()
    for (char in line) {
        when (char) {
            in syntaxMap.keys -> deque.addFirst(syntaxMap.getValue(char))
            else -> {
                val expectedChar = deque.removeFirstOrNull()
                if (expectedChar != char) {
                    return SyntaxErrorLine(char)
                }
            }
        }
    }

    return IncompleteLine(deque)
}

private val syntaxMap = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)