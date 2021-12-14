fun main() {
    fun part1(input: List<String>): Long {
        return solve(input, 10)
    }

    fun part2(input: List<String>): Long {
        return solve(input, 40)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    val input = readInput("Day14")
    check(part1(testInput) == 1588L)
    println(part1(input))

    check(part2(testInput) == 2188189693529)
    println(part2(input))
}

private fun solve(input: List<String>, times: Int): Long {
    val (template, rulesMap) = input.toTemplateAndRulesMap()

    val polymerMap = template.windowed(2)
        .groupingBy { it }
        .eachCount()
        .mapValues { (_, v) -> v.toLong() }
        .toMutableMap()

    repeat(times) {
        polymerMap.step(rulesMap)
    }

    val commonAppearanceMap = mutableMapOf<Char, Long>()
    polymerMap.forEach { (pair, count) ->
        val secondLetter = pair[1]
        commonAppearanceMap.merge(secondLetter, count, Long::plus)
    }
    val firstLetter = template.first()
    commonAppearanceMap.merge(firstLetter, 1L, Long::plus)

    return commonAppearanceMap.maxOf { it.value } - commonAppearanceMap.minOf { it.value }
}

private fun List<String>.toTemplateAndRulesMap(): Pair<String, Map<String, String>> {
    val template = take(1).single()

    val rulesMap = drop(2).associate { line ->
        val (from, to) = line.split(" -> ")
        from to to
    }

    return template to rulesMap
}

private fun MutableMap<String, Long>.step(rulesMap: Map<String, String>) {
    val newMap = mutableMapOf<String, Long>()
    forEach { (pair, count) ->
        rulesMap[pair]?.also {
            val first = pair[0] + it
            val second = it + pair[1]
            newMap.merge(first, count, Long::plus)
            newMap.merge(second, count, Long::plus)
        }
    }

    clear()
    putAll(newMap)
}