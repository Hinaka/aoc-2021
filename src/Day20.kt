fun main() {
    fun part1(input: List<String>): Int {
        return enhanceImage(input, 2).sumOf { it.sum() }
    }

    fun part2(input: List<String>): Int {
        return enhanceImage(input, 50).sumOf { it.sum() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    val input = readInput("Day20")
    check(part1(testInput) == 35)
    println(part1(input))

    check(part2(testInput) == 3351)
    println(part2(input))
}

private fun enhanceImage(input: List<String>, times: Int): Array<IntArray> {
    val algorithm = input.toAlgorithm()
    var image = input.toInputImage()
    val isBgInvertible = algorithm[0] == 1
    repeat(times) { time ->
        val height = image.size
        val width = image.first().size
        val newHeight = height + 2
        val newWidth = width + 2
        val newImage = Array(newHeight) { IntArray(newWidth) }
        val bgPixel = if (isBgInvertible) time % 2 else 0

        for (i in 0 until newHeight) {
            for (j in 0 until newWidth) {
                var index = 0
                for (di in -1..1) {
                    for (dj in -1..1) {
                        val pixelValue = image.getOrNull(i + di - 1)?.getOrNull(j + dj - 1) ?: bgPixel
                        index = (index shl 1) + pixelValue
                    }
                }
                newImage[i][j] = algorithm[index]
            }
        }
        image = newImage
    }
    return image
}

private fun List<String>.toAlgorithm() = first().map { if (it == '#') 1 else 0 }
private fun List<String>.toInputImage() = drop(2).map { it.map { if (it == '#') 1 else 0 }.toIntArray() }.toTypedArray()
