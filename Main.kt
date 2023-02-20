package connectfour

import kotlin.math.max
import kotlin.math.min

var rows = 6
var columns = 7
val inputRegex = Regex("\\d+\\s*[Xx]\\s*\\d+")
val splitRegex = Regex("\\s*[Xx]\\s*")
var isFirstPlayerTurn = true
var movesLeft = columns * rows
var score1 = 0
var score2 = 0
var totalGames = 1

fun main() {
    println("Connect Four")
    println("First player's name:")
    val player1 = readln()
    println("Second player's name:")
    val player2 = readln()

    while (true) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val input = readln().trim()
        if (input.isEmpty()) {
            break
        } else if (!inputRegex.matches(input)) {
            println("Invalid input")
        } else {
            rows = input.split(splitRegex)[0].toInt()
            columns = input.split(splitRegex)[1].toInt()
            if (rows !in 5..9) {
                println("Board rows should be from 5 to 9")
            } else if (columns !in 5..9) {
                println("Board columns should be from 5 to 9")
            } else {
                break
            }
        }
    }
    while (true) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        val input = readln()
        if (input.isNotEmpty() && (!"\\d+".toRegex().matches(input) || input.toInt() < 1)) {
            println("Invalid input")
        } else {
            totalGames = if (input.isEmpty()) {
                1
            } else {
                input.toInt()
            }
            break
        }
    }

    var board = Array(columns) { CharArray(rows) { ' ' } }
    var freeSpace = IntArray(columns)
    movesLeft = columns * rows

    println("$player1 VS $player2")
    println("$rows X $columns board")
    if (totalGames > 1) println("Total $totalGames games")
    for (game in 1..totalGames) {
        if (totalGames > 1) {
            println("Game #$game")
        } else {
            println("Single game")
        }
        turns@ while (true) {
            printBoard(rows, columns, board)
            val playerTurn = if (isFirstPlayerTurn) player1 else player2
            val disc = if (isFirstPlayerTurn) 'o' else '*'
            while (true) {
                println("$playerTurn's turn:")
                val input = readln()
                if (input == "end") {
                    println("Game over!")
                    return
                }
                if (!"\\d+".toRegex().matches(input)) {
                    println("Incorrect column number")
                    continue
                }
                val move = input.toInt()
                if (move !in 1..columns) {
                    println("The column number is out of range (1 - $columns)")
                } else if (freeSpace[move - 1] == rows) {
                    println("Column $move is full")
                } else {
                    board[move - 1][freeSpace[move - 1]] = disc
                    movesLeft--
                    if (gameOver(move - 1, freeSpace[move - 1], board, disc, playerTurn)) {
                        if (totalGames > 1) {
                            println("Score")
                            println("$player1: $score1 $player2: $score2")
                            isFirstPlayerTurn = game % 2 == 0
                            board = Array(columns) { CharArray(rows) { ' ' } }
                            movesLeft = columns * rows
                            freeSpace = IntArray(columns)
                        }
                        if (game == totalGames) {
                            println("Game over!")
                            return
                        }
                        break@turns
                    }
                    isFirstPlayerTurn = !isFirstPlayerTurn
                    freeSpace[move - 1]++
                    break
                }
            }
        }
    }
}

fun printBoard(rows: Int, columns: Int, board: Array<CharArray>) {
    for (i in 1..columns) {
        print(" $i")
    }
    for (i in rows - 1 downTo 0) {
        print("\n║")
        for (j in 0 until columns) {
            print("${board[j][i]}║")
        }
    }
    print("\n╚═")
    repeat(columns - 1) {
        print("╩═")
    }
    println("╝")
}

fun gameOver(x: Int, y: Int, b: Array<CharArray>, disc: Char, player: String): Boolean {
    var vertical = false
    var horizontal = false
    var diagonal1 = false
    var diagonal2 = false

    if (y > 2) vertical = b[x][y - 1] == disc && b[x][y - 2] == disc && b[x][y - 3] == disc

    val left = max(x - 3, 0)
    val right = min(x + 3, columns - 1)
    val top = min(y + 3, rows - 1)
    for (i in left..right - 3) {
        horizontal = b[i][y] == disc &&b[i + 1][y] == disc && b[i + 2][y] == disc && b[i + 3][y] == disc
        if (horizontal) {
            break
        }
    }

    var dif1 = min(x, y)
    var dif2 = min(top - y, right - x)
    if (dif1 + dif2 > 2) {
        var connect = 0
        for (i in -dif1..dif2) {
            if (b[x + i][y + i] == disc) {
                connect++
                if (connect == 4) {
                    diagonal1 = true
                    break
                }
            } else {
                connect = 0
            }
        }
    }

    dif1 = min(x, top - y)
    dif2 = min(y, right - x)
    if (dif1 + dif2 > 2) {
        var connect = 0
        for (i in -dif1..dif2) {
            if (b[x + i][y - i] == disc) {
                connect++
                if (connect == 4) {
                    diagonal2 = true
                    break
                }
            } else {
                connect = 0
            }
        }
    }

    val won = vertical || horizontal || diagonal1 || diagonal2
    val draw = movesLeft == 0
    if (won || draw) printBoard(rows, columns, b)
    if (won) {
        if (isFirstPlayerTurn) {
            score1 += 2
        } else {
            score2 += 2
        }
        println("Player $player won")
    } else if (draw) {
        score1++
        score2++
        println("It is a draw")
    }

    return won || draw
}