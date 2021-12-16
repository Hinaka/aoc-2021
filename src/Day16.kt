fun main() {
    fun part1(input: List<String>): Int {
        return Packet.parse(input.toBits()).first.versionSum
    }

    fun part2(input: List<String>): Long {
        val (packet, _) = Packet.parse(input.toBits())
        return packet.value
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")
    println(part1(input))

    check(part2(testInput) == 1L)
    println(part2(input))
}

private fun List<String>.toBits() = single().map {
    it.digitToInt(16).toString(2).padStart(4, '0')
}.joinToString("")


private sealed interface Packet {
    val version: Int

    val versionSum: Int

    val value: Long

    companion object {
        fun parse(str: String): Pair<Packet, String> {
            val (version, typeId, remainStr) = parseHeader(str)
            return when (typeId) {
                4 -> LiteralPacket.parse(version, remainStr)
                else -> OperatorPacket.parse(version, typeId, remainStr)
            }
        }

        private fun parseHeader(str: String): Triple<Int, Int, String> {
            val version = str.take(3).toInt(2)
            val typeId = str.drop(3).take(3).toInt(2)
            val remainStr = str.drop(6)
            return Triple(version, typeId, remainStr)
        }
    }
}

private class LiteralPacket(override val version: Int, override val value: Long) : Packet {
    override val versionSum: Int
        get() = version

    companion object {
        fun parse(version: Int, str: String): Pair<LiteralPacket, String> {
            val (value, remainStr) = parseLiteralValue(str)
            return LiteralPacket(version, value) to remainStr
        }

        private fun parseLiteralValue(str: String, acc: String = ""): Pair<Long, String> =
            if (str.first() == '1') {
                parseLiteralValue(str.drop(5), acc + str.drop(1).take(4))
            } else {
                val value = (acc + str.drop(1).take(4)).toLong(2)
                value to str.drop(5)
            }

    }
}

private sealed class OperatorPacket(override val version: Int, protected val subPackages: List<Packet>) : Packet {
    override val versionSum: Int
        get() = version + subPackages.sumOf { it.versionSum }

    companion object {
        fun parse(version: Int, typeId: Int, str: String): Pair<OperatorPacket, String> {
            val lengthType = str.take(1).toInt(2)

            return if (lengthType == 0) {
                parseMode0(version, typeId, str.drop(1))
            } else {
                parseMode1(version, typeId, str.drop(1))
            }
        }

        private fun parseMode0(version: Int, typeId: Int, str: String): Pair<OperatorPacket, String> {
            val lengthInBit = str.take(15).toInt(2)
            var remainStr = str.drop(15)
            val lengthBeforeParse = remainStr.length
            val subPackages = mutableListOf<Packet>()

            while (lengthBeforeParse - remainStr.length < lengthInBit) {
                val (packet, newRemainStr) = Packet.parse(remainStr)
                subPackages.add(packet)
                remainStr = newRemainStr
            }

            return getOperatorPacket(version, typeId, subPackages, remainStr)
        }

        private fun parseMode1(version: Int, typeId: Int, str: String): Pair<OperatorPacket, String> {
            val numOfSubPackage = str.take(11).toInt(2)
            var remainStr = str.drop(11)
            val subPackages = mutableListOf<Packet>()

            repeat(numOfSubPackage) {
                val (packet, newRemainStr) = Packet.parse(remainStr)
                subPackages.add(packet)
                remainStr = newRemainStr
            }

            return getOperatorPacket(version, typeId, subPackages, remainStr)
        }

        private fun getOperatorPacket(
            version: Int,
            typeId: Int,
            subPackages: List<Packet>,
            remainStr: String
        ): Pair<OperatorPacket, String> {
            val operatorPacket = when (typeId) {
                0 -> SumOperatorPacket(version, subPackages)
                1 -> ProductOperatorPacket(version, subPackages)
                2 -> MinimumOperatorPacket(version, subPackages)
                3 -> MaximumOperatorPacket(version, subPackages)
                5 -> GreaterThanOperatorPacket(version, subPackages)
                6 -> LessThanOperatorPacket(version, subPackages)
                7 -> EqualToOperatorPacket(version, subPackages)
                else -> error(typeId)
            }

            return operatorPacket to remainStr
        }
    }
}


private class SumOperatorPacket(version: Int, subPackages: List<Packet>) : OperatorPacket(version, subPackages) {
    override val value: Long
        get() = subPackages.sumOf { it.value }
}

private class ProductOperatorPacket(version: Int, subPackages: List<Packet>) : OperatorPacket(version, subPackages) {
    override val value: Long
        get() = subPackages.fold(1L) { acc, packet ->
            acc * packet.value
        }
}

private class MinimumOperatorPacket(version: Int, subPackages: List<Packet>) : OperatorPacket(version, subPackages) {
    override val value: Long
        get() = subPackages.minOf { it.value }
}

private class MaximumOperatorPacket(version: Int, subPackages: List<Packet>) : OperatorPacket(version, subPackages) {
    override val value: Long
        get() = subPackages.maxOf { it.value }
}

private class GreaterThanOperatorPacket(
    version: Int, subPackages: List<Packet>
) : OperatorPacket(version, subPackages) {
    override val value: Long
        get() {
            val (first, second) = subPackages
            return if (first.value > second.value) 1 else 0
        }
}

private class LessThanOperatorPacket(version: Int, subPackages: List<Packet>) : OperatorPacket(version, subPackages) {
    override val value: Long
        get() {
            val (first, second) = subPackages
            return if (first.value < second.value) 1 else 0
        }
}

private class EqualToOperatorPacket(version: Int, subPackages: List<Packet>) : OperatorPacket(version, subPackages) {
    override val value: Long
        get() {
            val (first, second) = subPackages
            return if (first.value == second.value) 1 else 0
        }
}