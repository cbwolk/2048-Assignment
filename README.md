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

*Merges.* You should implement a private method mergeTiles() to handle all merges. For up and down swipes, this method should iterate through every column and find adjacent tiles that have the same value. For left and right swipes, this method should iterate through every row and find adjacent tiles that have the same value. If there are three adjacent tiles with the same value, you must combine the two tiles closest to the border in the direction of motion. An example is shown below. 

0   0   0   0                                  0   0   0   0
0   2   2   2          swipe left         4   2   0   0
0   0   0   0       —————>     0   0   0   0
4   4   4   0                                  8   4   0   0

0   0   0   0                                  0   0   0   0
0   2   2   2         swipe right       0   0   2   4
0   0   0   0       —————>     0   0   0   0
4   4   4   0                                  0   0   4   8

*Swipes.* You should also implement a private swipeTiles() method which, after performing any merges, shifts the numbered tiles in the direction of the swipe. You may also think about it as shifting the blank tiles (zero tiles) in the opposite direction of the swipe. However, if there are no blank tiles in the given row or column, do not modify it. One idea is to maintain a pointer that moves in the opposite direction of the swipe. In this method, you may save the position of the first zero the pointer comes across and swap that zero with the first numbered tile seen by the pointer. Then, reset the pointer to the position after the swapped numbered tile. Importantly, you must not change the order of the non-zero tiles. Therefore, simply sorting the tiles will not work.

2   0   4   0                                  0   0   2   4
4   2   0   0         swipe right       0   0   4   2
0   0   0   0       —————>     0   0   0   0
2   8   4   2                                  2   8   4   2

*Adding tiles.* Lastly, you should implement a private `addTile()` method which adds a 2 in the upper right corner. This method should be called once after each swipe is completed. If there is no room for the tile to be added—if the position in the upper right corner is occupied—update gameFinished to true. 

*Constructor.*  You may assume that the constructor receives a L-by-L array containing numbered tiles of powers of 2, and at least one blank tile represented by 0. Though 2048 is played on a board where L = 4, you should not hardwire this value into the program. You may assume 2 ≤ L ≤ 32,768.

*String representation.*  The `toString()` method returns a string composed of L + 1 lines. The first line contains the number of elements n; the remaining L lines contains the L-by-L grid of tiles in row-major order, using 0 to designate blank tiles.

*Tile extraction.*  Throw a java.lang.IllegalArgumentException in `tileAt()` unless both row and col are between 0 and L − 1. 

*Comparing two boards for equality.*  Two boards are equal if they are have the same size and number elements and their corresponding tiles are in the same positions. The `equals()` method is inherited from java.lang.Object, so it must obey all of Java’s requirements.

*Neighboring boards.*  The `neighbors()` method returns an iterable containing the neighbors of the board. A board can have 1, 2, 3, or 4 neighbors, depending on the number of valid moves a player has. A move is considered valid if it changes the state of the board, as in it changes the location of one or more tiles or a merge is performed. You may find it helpful to add a private method to determine if swiping in a given direction is a valid move. For example, in the board shown below, swiping up, left, or right does not change the state. The only valid move is to swipe down, so only one neighbor exists.



** This README is under construction. See the PDF document for more information.**

*Written by Colton Wolk, adapted from 8-Puzzle. Special thanks to Maia Ginsburg.*

![Image of Game Board](https://github.com/cbwolk/2048-Assignment/blob/master/images/Full2048Board.png)
