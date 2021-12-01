fun main() {
    fun part1(input: List<String>): Int {
        return input
            .mapNotNull { it.toIntOrNull() }
            .zipWithNext()
            .count { (prev, next) -> next > prev }
    }

    fun part2(input: List<String>): Int {
        return input
            .mapNotNull { it.toIntOrNull() }
            .windowed(3) {it.sum()}
            .zipWithNext()
            .count { (prev, next) -> next > prev }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
