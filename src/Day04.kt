fun main() {
    fun part1(input: List<String>): Int {
        val draws = input.getDraws()
        val boards = input.getBoards().map { Board.from(it) }

        for (currentDraw in draws) {
            for (board in boards) {
                board.mark(currentDraw)
                if (board.isBingo()) {
                    val unmarkedSum = board.unmarkedFields().sumOf { it.value }
                    return unmarkedSum * currentDraw
                }
            }
        }

        return 0
    }

    fun part2(input: List<String>): Int {
        val draws = input.getDraws()
        val boards = input.getBoards().map { Board.from(it) }.toMutableList()

        for (currentDraw in draws) {
            val iterator = boards.iterator()
            while (iterator.hasNext()) {
                val board = iterator.next()
                board.mark(currentDraw)
                if (board.isBingo()) {
                    if (boards.size == 1) {
                        val unmarkedSum = board.unmarkedFields().sumOf { it.value }
                        return unmarkedSum * currentDraw
                    } else {
                        iterator.remove()
                    }
                }
            }
        }

        return 0
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.getDraws() = first().split(",").map { it.toInt() }

private fun List<String>.getBoards() = drop(1)
    .chunked(6)
    .map { lines ->
        lines.filter { line -> line.isNotBlank() }
    }.map { board ->
        board.map { line ->
            line.split(" ")
                .filter { it.isNotBlank() }
                .map { it.trim().toInt() }
        }
    }

data class Field(val value: Int, val isMarked: Boolean = false)

data class Board(val fields: List<MutableList<Field>>) {

    private val indices = fields.indices

    fun mark(value: Int) {
        for (row in fields) {
            row.replaceAll { field ->
                if (field.value == value) field.copy(isMarked = true) else field
            }
        }
    }

    fun isBingo() = isRowBingo() || isColumnBingo()

    private fun isRowBingo() = fields.any { row -> row.all { it.isMarked } }

    private fun isColumnBingo(): Boolean {
        for (col in indices) {
            var bingo = true
            for (row in indices) {
                if (!fields[row][col].isMarked) {
                    bingo = false
                    break
                }
            }
            if (bingo) return true
        }
        return false
    }

    fun unmarkedFields() = fields.flatten().filter { !it.isMarked }

    companion object {
        fun from(input: List<List<Int>>) = Board(
            input.map { line -> line.map { Field(it) }.toMutableList() }
        )
    }
}