import kotlin.math.abs

fun main() {
    fun part1(beacons: Set<Point>): Int {
        return beacons.count()
    }

    fun part2(scannerPoints: Set<Point>): Int {
        return scannerPoints.map { first ->
            scannerPoints.map { second ->
                first.distance(second)
            }
        }.flatten().maxOf { it }
    }

    fun solve(input: List<String>): Pair<Int, Int> {
        var originalScanners = input.toScanners()
        // All points absolute position will be expressed relative to first scanner
        // Meaning, first scanner will be point (0,0,0), and other point will be mapped
        // to its orientation and facing direction.
        val beacons = mutableSetOf<Point>()
        beacons.addAll(originalScanners.first())
        val scannerPoints = mutableSetOf<Point>()

        originalScanners = originalScanners.drop(1)
        while (originalScanners.isNotEmpty()) {
            originalScanners = originalScanners.mapNotNull {
                val (scannerPoint, scannerBeacons) = beacons.toList().overlap(it) ?: return@mapNotNull it
                beacons.addAll(scannerBeacons)
                scannerPoints.add(scannerPoint)
                null
            }
        }

        return part1(beacons) to part2(scannerPoints)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    val (testPart1, testPart2) = solve(testInput)
    check(testPart1 == 79)
    check(testPart2 == 3621)

    val input = readInput("Day19")
    val (part1, part2) = solve(input)
    println(part1)
    println(part2)
}

private data class Point(val x: Int, val y: Int, val z: Int)

private fun Point.distance(point: Point) = abs(x - point.x) + abs(y - point.y) + abs(z - point.z)

private typealias Scanner = List<Point>

private fun Scanner.toAllOrientations() = listOf(
    // (x,y,z)
    map { Point(it.x, it.y, it.z) },
    map { Point(it.x, it.y, -it.z) },
    map { Point(it.x, -it.y, it.z) },
    map { Point(it.x, -it.y, -it.z) },
    map { Point(-it.x, it.y, it.z) },
    map { Point(-it.x, it.y, -it.z) },
    map { Point(-it.x, -it.y, it.z) },
    map { Point(-it.x, -it.y, -it.z) },
    // (x, z, y)
    map { Point(it.x, it.z, it.y) },
    map { Point(it.x, -it.z, it.y) },
    map { Point(it.x, it.z, -it.y) },
    map { Point(it.x, -it.z, -it.y) },
    map { Point(-it.x, it.z, it.y) },
    map { Point(-it.x, -it.z, it.y) },
    map { Point(-it.x, it.z, -it.y) },
    map { Point(-it.x, -it.z, -it.y) },
    // (y,z,x)
    map { Point(it.y, it.z, it.x) },
    map { Point(it.y, -it.z, it.x) },
    map { Point(-it.y, it.z, it.x) },
    map { Point(-it.y, -it.z, it.x) },
    map { Point(it.y, it.z, -it.x) },
    map { Point(it.y, -it.z, -it.x) },
    map { Point(-it.y, it.z, -it.x) },
    map { Point(-it.y, -it.z, -it.x) },
    // (y,x,z)
    map { Point(it.y, it.x, it.z) },
    map { Point(it.y, it.x, -it.z) },
    map { Point(-it.y, it.x, it.z) },
    map { Point(-it.y, it.x, -it.z) },
    map { Point(it.y, -it.x, it.z) },
    map { Point(it.y, -it.x, -it.z) },
    map { Point(-it.y, -it.x, it.z) },
    map { Point(-it.y, -it.x, -it.z) },
    // (z,x,y)
    map { Point(it.z, it.x, it.y) },
    map { Point(-it.z, it.x, it.y) },
    map { Point(it.z, it.x, -it.y) },
    map { Point(-it.z, it.x, -it.y) },
    map { Point(it.z, -it.x, it.y) },
    map { Point(-it.z, -it.x, it.y) },
    map { Point(it.z, -it.x, -it.y) },
    map { Point(-it.z, -it.x, -it.y) },
    // (z, y, x)
    map { Point(it.z, it.y, it.x) },
    map { Point(-it.z, it.y, it.x) },
    map { Point(it.z, -it.y, it.x) },
    map { Point(-it.z, -it.y, it.x) },
    map { Point(it.z, it.y, -it.x) },
    map { Point(-it.z, it.y, -it.x) },
    map { Point(it.z, -it.y, -it.x) },
    map { Point(-it.z, -it.y, -it.x) },
)

private fun List<Point>.translate(point: Point) = map {
    Point(
        it.x - point.x,
        it.y - point.y,
        it.z - point.z,
    )
}

private fun List<Point>.overlap(scanner: Scanner): Pair<Point, List<Point>>? {
    scanner.toAllOrientations().forEach { orientedScanner ->
        forEach { pointPivot ->
            val translatedPoints = translate(pointPivot)
            orientedScanner.forEach { scannerPivot ->
                val translatedScannerPoints = orientedScanner.translate(scannerPivot)

                val intersections = translatedScannerPoints.intersect(translatedPoints)
                if (intersections.size >= 12) {
                    // we can conclude that 2 pivot is the same point
                    val scannerPoint = Point(
                        x = scannerPivot.x - pointPivot.x,
                        y = scannerPivot.y - pointPivot.y,
                        z = scannerPivot.z - pointPivot.z,
                    )

                    val beacons = orientedScanner.map {
                        Point(
                            x = it.x - scannerPivot.x + pointPivot.x,
                            y = it.y - scannerPivot.y + pointPivot.y,
                            z = it.z - scannerPivot.z + pointPivot.z,
                        )
                    }

                    return scannerPoint to beacons
                }
            }
        }
    }

    return null
}

private fun List<String>.toScanners(): List<Scanner> {
    val scanners = mutableListOf<Scanner>()
    val points = mutableListOf<Point>()

    for (line in this) {
        if (line.contains(',')) {
            val (x, y, z) = line.split(',').map { it.toInt() }
            points.add(Point(x, y, z))
        } else if (points.isNotEmpty()) {
            scanners.add(points.toList())
            points.clear()
        }
    }
    scanners.add(points.toList()) // last scanner

    return scanners
}