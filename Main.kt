package chess

fun initGame(): Chess {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayer = readln()
    println("Second Player's name:")
    val secondPlayer = readln()
    return Chess(listOf(Player("W", firstPlayer), Player("B", secondPlayer)))
}

fun main() {
    val game = initGame()
    game.isOn()
    println("Bye!")
}