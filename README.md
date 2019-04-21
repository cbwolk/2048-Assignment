# 2048-Assignment

**Goal:** Write a program that solves the modified version of the game 2048 (but only up to the 128 tile) using the A* search algorithm.

**The modified game:** 2048, created by Gabriele Cirulli, is a sliding block game played on a 4x4 grid consisting of blank tiles and numbered tiles. This assignment uses a modified version of the game. The player may “swipe” the tiles horizontally or vertically, causing each tile to slide in the given direction until it is stopped by either another tile or the border of the grid. If two tiles that have the same value collide during a move, they are combined to form a new tile that is the sum of the previous tiles. Every turn, a new tile (with a value of 2) spawns in the same position on the board (the upper right corner), which is necessarily unoccupied or else the game ends. The objective is to combine tiles with the same value until the 128 tile is created, without running out of space on the board.

**Board data type.** To begin, create a data type that models a L-by-L board with sliding tiles. Implement an immutable data type Board with the following API:

```java
public class Board {
    // create a board from a L x L array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles)
    // string representation of this board                            
    public String toString()
    // tile at (row, col) or 0 if blank           
    public int tileAt(int row, int col)
    // number of non-zero tiles on the board 
    public int size()
    // return the largest tile in the board
    public int largestTile()
    // the total weighted value of the board
    public int weightedScore()
    // snake value of the board
    public int snakeScore()
    // penalty for having similar values far away
    public int penalty()
    // does this board have the GOAL_TILE?
    public boolean hasWon()
    // are there no more possible swipes?
    public boolean gameFinished() 
    // does this board equal y?
    public boolean equals(Object y)
    // all neighboring boards
    public Iterable<Board> neighbors()
    // unit testing (required)
    public static void main(String[] args)
}

```


**See PDF document for full assignment.**

*Written by Colton Wolk, adapted from 8-Puzzle. Special thanks to Maia Ginsburg.*

![Image of Game Board](https://github.com/cbwolk/2048-Assignment/blob/master/images/Full2048Board.png)
