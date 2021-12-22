import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Long {
        val initialRange = -50L..50L
        return input.toSteps().filter { step ->
            step.cuboid.isInRange(initialRange)
        }.reboot()
    }

    fun part2(input: List<String>): Long {
        return input.toSteps().reboot()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    val input = readInput("Day22")

    check(part1(testInput) == 474140L)
    println(part1(input))

    check(part2(testInput) == 2758514936282235L)
    println(part2(input))
}

private fun List<String>.toSteps() = map { line ->
    val (type, ranges) = line.split(" ")
    val instruction = if (type == "on") Instruction.ON else Instruction.OFF
    val (xRange, yRange, zRange) = ranges
        .split(",")
        .map { it.split("=").last() }
        .map {
            val (i, j) = it.split("..").map { it.toLong() }.sorted()
            i..j
        }
    Step(instruction, Cuboid(xRange, yRange, zRange))
}

private fun List<Step>.reboot(): Long {
    val cubesStates = mutableListOf<CubesState>()

    forEach { step ->
        val diffCubesStates = mutableListOf<CubesState>()
        cubesStates.forEach { cubesState ->
            // fastest way to intersect between cuboid and cubesState state
            val x1 = max(step.cuboid.xRange.first, cubesState.xRange.first)
            val x2 = min(step.cuboid.xRange.last, cubesState.xRange.last)
            val y1 = max(step.cuboid.yRange.first, cubesState.yRange.first)
            val y2 = min(step.cuboid.yRange.last, cubesState.yRange.last)
            val z1 = max(step.cuboid.zRange.first, cubesState.zRange.first)
            val z2 = min(step.cuboid.zRange.last, cubesState.zRange.last)

            if (x1 <= x2 && y1 <= y2 && z1 <= z2) {
                diffCubesStates.add(CubesState(
                    state = -cubesState.state,
                    xRange = x1..x2,
                    yRange = y1..y2,
                    zRange = z1..z2,
                ))
            }
        }
        cubesStates.addAll(diffCubesStates)
        if (step.instruction == Instruction.ON) cubesStates.add(CubesState(
            state = 1,
            xRange = step.cuboid.xRange,
            yRange = step.cuboid.yRange,
            zRange = step.cuboid.zRange,
        ))
    }

    return cubesStates.sumOf { it.value }
}

private data class Step(val instruction: Instruction, val cuboid: Cuboid)

private enum class Instruction {
    ON, OFF
}

private data class Cuboid(
    val xRange: LongRange,
    val yRange: LongRange,
    val zRange: LongRange,
)

private fun Cuboid.isInRange(range: LongRange) = (xRange - range).isEmpty() && (yRange - range).isEmpty() && (zRange - range).isEmpty()

private data class CubesState(
    val state: Int, // on 1, off -1
    val xRange: LongRange,
    val yRange: LongRange,
    val zRange: LongRange,
) {
    val value: Long get() = state * (xRange.last - xRange.first + 1) * (yRange.last - yRange.first + 1) * (zRange.last - zRange.first + 1)
}
