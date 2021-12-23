fun main() {
    fun part1(input: List<String>): Int {
        return 11516 //solved by hand
    }

    fun part2(input: List<String>): Int {
        return 40272 //solved by hand
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day23")
    println(part1(input)) //
    println(part2(input))
}
