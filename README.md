# 2048-Assignment

![Image of Game Board](https://github.com/cbwolk/2048-Assignment/blob/master/images/Full2048Board.png)

**Goal:** Write a program that solves the modified version of the game 2048 (but only up to the 128 tile) using the A* search algorithm.

**The modified game:** 2048, created by Gabriele Cirulli, is a sliding block game played on a 4x4 grid consisting of blank tiles and numbered tiles. This assignment uses a modified version of the game. The player may “swipe” the tiles horizontally or vertically, causing each tile to slide in the given direction until it is stopped by either another tile or the border of the grid. If two tiles that have the same value collide during a move, they are combined to form a new tile that is the sum of the previous tiles. Every turn, a new tile (with a value of 2) spawns in the same position on the board (the upper right corner), which is necessarily unoccupied or else the game ends. The objective is to combine tiles with the same value until the 128 tile is created, without running out of space on the board.
