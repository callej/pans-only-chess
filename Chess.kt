package chess

data class Player(val c: String, val name: String) {
    fun color() = if (c == "W") "white" else "black"
}

class Chess(val players: List<Player>) {
    private var turn = 0
    private var last = ""
    private var two = false
    private var gameOver = false
    private var board = List(8) { MutableList(8) { " " } }

    init {
        for (lane in 'a'..'h') {
            board[1][lane - 'a'] = "W"
            board[6][lane - 'a'] = "B"
        }
    }

    private fun playersTurn() = players[turn]

    private fun nextPlayer(): Player {
        turn = (turn + 1) % players.size
        return players[turn]
    }

    private fun valid(move: String, player: Player): Boolean {
        return when {
            player.c == "W" && !(move[0] == move[2] && (move[1] + 1 == move[3]) || move[1] == '2' && move[3] == '4' && board[move[1] - '1' + 1][move[0] - 'a'] == " ") -> false
            player.c == "B" && !(move[0] == move[2] && (move[1] - 1 == move[3]) || move[1] == '7' && move[3] == '5' && board[move[1] - '1' - 1][move[0] - 'a'] == " ") -> false
            board[move[3] - '1'][move[2] - 'a'] != " " -> false
            else -> true
        }
    }

    private fun canMove(rank: Int, lane: Char, player: Player): Boolean {
        val move1 = if (player.c == "W") listOf(lane, rank.toString(), lane, (rank + 1).toString()).joinToString("") else listOf(lane, rank.toString(), lane, (rank - 1).toString()).joinToString("")
        val move2 = if (player.c == "W") listOf(lane, rank.toString(), lane, (rank + 2).toString()).joinToString("") else listOf(lane, rank.toString(), lane, (rank - 2).toString()).joinToString("")
        val move3 = if (player.c == "W") listOf(lane, rank.toString(), lane + 1, (rank + 1).toString()).joinToString("") else listOf(lane, rank.toString(), lane + 1, (rank - 1).toString()).joinToString("")
        val move4 = if (player.c == "W") listOf(lane, rank.toString(), lane - 1, (rank + 1).toString()).joinToString("") else listOf(lane, rank.toString(), lane - 1, (rank - 1).toString()).joinToString("")
        val moves = listOf(move1, move2, move3, move4)
        for (move in moves) {
            if (!Regex("([a-h][1-8]){2}").matches(move)) continue
            if (diagonal(move, player) && board[move[3] - '1'][move[2] - 'a'] !in listOf(" ", player.c)) return true
            if (diagonal(move, player) && two && (player.c == "W" && lastMove(1) == move.takeLast(2) || player.c == "B" && lastMove(-1) == move.takeLast(2))) return true
            if (valid(move, player)) return true
        }
        return false
    }

    private fun stalemate(player: Player): Boolean {
        for (rank in 1..8) {
            for (lane in 'a'..'h') {
                if (board[rank - 1][lane - 'a'] == players[1 - turn].c && canMove(rank, lane, players[1 - turn])) return false
            }
        }
        return true
    }

    private fun isGameOver(move: String, player: Player): Boolean {
        if (player.c == "W" && move[3] == '8' || player.c == "B" && move[3] == '1' ||
            player.c == "W" && !board.flatten().contains("B") || player.c =="B" && !board.flatten().contains("W")) {
            println("${player.color().replaceFirstChar { it.uppercase() }} Wins!")
            return true
        }
        if (stalemate(player)) {
            println("Stalemate!")
            return true
        }
        return false
    }

    private fun doTheMove(move: String, player: Player): Player {
        board[move[1] - '1'][move[0] - 'a'] = " "
        board[move[3] - '1'][move[2] - 'a'] = player.c
        last = move.takeLast(2)
        two = player.c == "W" && move[1] + 2 == move[3] || player.c == "B" && move[1] - 2 == move[3]
        println(this)
        gameOver = isGameOver(move, player)
        return nextPlayer()
    }

    private fun diagonal(move: String, player: Player): Boolean {
        if (player.c == "W" && move[1] + 1 == move[3] && (move[0] + 1 == move[2] || move[0] - 1 == move[2])) return true
        if (player.c == "B" && move[1] - 1 == move[3] && (move[0] + 1 == move[2] || move[0] - 1 == move[2])) return true
        return false
    }

    private fun diagonalCapture(move: String, player: Player): Boolean {
        return diagonal(move, player) && board[move[3] - '1'][move[2] - 'a'] !in listOf(" ", player.c)
    }

    private fun lastMove(step: Int) = listOf(last[0], last[1] + step).joinToString("")

    private fun passant(move: String, player: Player): Boolean {
        if (diagonal(move, player) && two && (player.c == "W" && lastMove(1) == move.takeLast(2) || player.c == "B" && lastMove(-1) == move.takeLast(2))) {
            board[last[1] - '1'][last[0] - 'a'] = " "
            return true
        }
        return false
    }

    private fun capture(move: String, player: Player): Boolean {
        return (diagonalCapture(move, player)) || (passant(move, player))
    }

    fun isOn() {
        println(this)
        var player = playersTurn()
        println("${player.name}'s turn:")
        var move = readln()
        while (move != "exit") {
            when {
                !Regex("([a-h][1-8]){2}").matches(move) -> println("Invalid Input")
                player.c != board[move[1] - '1'][move[0] - 'a'] -> println("No ${player.color()} pawn at ${move.take(2)}")
                capture(move, player) -> player = doTheMove(move, player)
                valid(move, player) -> player = doTheMove(move, player)
                else -> println("Invalid Input")
            }
            if (gameOver) return
            println("${player.name}'s turn:")
            move = readln()
        }
    }

    override fun toString(): String {
        var chessboard = ""
        for (rank in 8 downTo 1) {
            chessboard += "  +---+---+---+---+---+---+---+---+\n"
            chessboard += "$rank | ${board[rank - 1].joinToString(" | ")} |\n"
        }
        chessboard += "  +---+---+---+---+---+---+---+---+\n"
        chessboard += "    a   b   c   d   e   f   g   h\n"
        return chessboard
    }
}