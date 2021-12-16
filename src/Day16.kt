fun main() {
    fun part1(input: List<String>): Int {
        return Packet.parse(input.toBits()).first.versionSum
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    val input = readInput("Day16")
    check(part1(testInput) == 31)
    println(part1(input))

//    check(part2(testInput) == 5)
//    println(part2(input))
}

private fun List<String>.toBits() = single().map {
    it.digitToInt(16).toString(2).padStart(4, '0')
}.joinToString("")


private sealed interface Packet {
    val version: Int

    val versionSum: Int

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

private class LiteralPacket(override val version: Int, private val value: Long) : Packet {
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

private class OperatorPacket(override val version: Int, private val subPackages: List<Packet>) : Packet {
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
            val subPackages = mutableListOf<Packet>()

            while (str.length - remainStr.length < lengthInBit) {
                val (packet, newRemainStr) = Packet.parse(remainStr)
                subPackages.add(packet)
                remainStr = newRemainStr
            }

            return OperatorPacket(version, subPackages) to remainStr
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

            return OperatorPacket(version, subPackages) to remainStr
        }
    }
}



