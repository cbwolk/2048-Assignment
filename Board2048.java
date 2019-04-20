
/*****************************************************************************
 ***************************Developed by Colton Wolk**************************
 *****************************************************************************/

/*****************************************************************************
 *  Name:    Colton Wolk
 *  NetID:   cwolk
 *  Precept: P05
 * 
 *  Date: May 2018
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 * 
 *  Description: Board is a data type that models a 4x4 board of tiles (with 
 *               value of either zero or a power of 2) used in the game 2048.
 * 
 *  Citation:    Based off of Board.java and Solver.java written for Princeton 
 *               University's 8-Puzzle assignment. Internet resources were used
 *               for research--see the accompanying word document for sources.
 * 
 *               The original 2048 game was created by Gabriele Cirulli.
 * 
 *  Note:        Since the direction of the swipe determines if the rows or
 *               the cols move, it was easier to make a "constant" and 
 *               "changing" variable within the for loops to make it clear 
 *               which one was changing (in certain methods).
 * 
 *****************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdRandom;

public class Board2048 {
    
    // the 4x4 board
    private int[][] board;
    // number of filled tiles on board
    // note that n is NOT the length; it is the number of non-zero elements
    private int n;
    // length of the board
    private final int L;
    // if you reach the 2048 tile you win the game
    private boolean won;
    // if you reach the 2048 tile you win the game
    private boolean gameFinished;
    // weights of the board; higher weights are bottom left
    private final int[][] WEIGHTS;
    // location/weights priority function
    private final int weighted;
    // 2048 is the goal
    private final int GOAL_SCORE = 256;
    // 2 tiles to start
    // private final int START_TILES = 2;
    // largest tile in the board
    private int largest;
        
    // create a board from a 4-by-4 array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board2048(int[][] tiles) {
        
        L = tiles.length;
        
        if (L != tiles[0].length)
            throw new IllegalArgumentException("length = " + tiles.length + 
                                               ", width = " + tiles[0].length);
            
        board = new int[L][L];
        gameFinished = false;
        won = false;
        
        // copy tiles into the board
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                if (tiles[i][j] != 0) {
                    board[i][j] = tiles[i][j];
                    // must've had GOAL_SCORE at some point if > GOAL_SCORE
                    if (board[i][j] >= GOAL_SCORE) {
                        won = true;
                        gameFinished = true;
                    }
                    n++;
                }
            }
        }
        
        WEIGHTS = new int[L][L];
        computeWeights();
        // for testing
        // WEIGHTS = new int[][] {{3, 2, 1, 0}, {4, 3, 2, 1}, 
        //                        {5, 4, 3, 2}, {6, 5, 4, 3}};
        weighted = computeWeightedScore();
        largest = computeLargest();
    }
    
    // compute largest
    private int computeLargest() {
        int lgst = 0;
        
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                if (board[i][j] > lgst)
                    lgst = board[i][j];
            }
        }
        return lgst;
    }
    
    // return the largest tile in the board (cached)
    public int largestTile() {
        return largest;
    }

/*****************************************************************************
 *                             Priority Functions
 *****************************************************************************/
    
    // compute the values in the array of weights
    private void computeWeights() {
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                WEIGHTS[i][j] = (L + i - j - 1);
            }
        }
    }
    
    // give most weight to bottom left corner, less weight to top right
    // uses strategy of keeping largest tile in bottom left corner
    private int computeWeightedScore() {
        int score = 0;
        
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                score += WEIGHTS[i][j] * board[i][j];
            }
        }
        return score;
    }
    
    // return the weighted score
    public int weightedScore() {
        return weighted;
    }
    
    // get snake pattern
    public int snakeScore() {
        int penalty = 0;
        int score1 = 0;
        int score2 = 0;
        int score3 = 0;
        int score4 = 0;
        int currentTile;
        
        // start at biggest tile
        int lgst = -1;
        int lgstRow = -1;
        int lgstCol = -1;
        
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                if (board[i][j] > lgst) {
                    lgst = board[i][j];
                    lgstRow = i;
                    lgstCol = j;
                }
            }
        }
        
        currentTile = lgst;
        for (int i = lgstRow; i >= 0; i--) {
            score1 += computeSnakeScore(currentTile, i, lgstCol);
            //currentTile = board[i][lgstCol];
        }
        for (int j = lgstCol; j >= 0; j--) {
            score2 += computeSnakeScore(currentTile, lgstRow, j);
            //currentTile = board[lgstRow][j];
        }
        for (int i = lgstRow; i < L; i++) {
            score3 += computeSnakeScore(currentTile, i, lgstCol);
            //currentTile = board[i][lgstCol];
        }
        for (int j = lgstCol; j < L; j++) {
            score4 += computeSnakeScore(currentTile, lgstRow, j);
            //currentTile = board[lgstRow][j];
        }
        
        int max1 = Math.max(score1, score2);
        int max2 = Math.max(score3, score4);
        //return max1 + max2;
        //return Math.max(max1, max2);
        return score1 + score2 + score3 + score4;
    }
    
    // compute snake
    private int computeSnakeScore(int currentTile, int row, int col) {
        int newScore = 0;
        
        if (board[row][col] == 0)
            return 0;
        
        // check how many factors of 2 the tiles are from each other
        for (int i = 2; i <= 2048; i *= 2) {
            if (board[row][col] * i == currentTile) {
                // the closer the tiles are, the higher the score
                newScore += currentTile / i;
                break;
            }
        }
        
        return newScore;
    }
    
    public int penalty() {
        int penalty = 0;
        int currentTile;
        
        //penalty -= largest
        
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                
                currentTile = board[i][j];
                
                //if (currentTile == 0)
                //    continue;
                
                if (i - 1 >= 0) {
                    // check left
                    penalty += Math.abs(currentTile - board[i - 1][j]);
                }
                if (i + 1 < L) {
                    // check right
                    penalty += Math.abs(currentTile - board[i + 1][j]);
                }
                if (j - 1 >= 0) {
                    // check above
                    penalty += Math.abs(currentTile - board[i][j - 1]);
                }
                if (j + 1 < L) {
                    // check below
                    penalty += Math.abs(currentTile - board[i][j + 1]);
                }
            }
        }
        return penalty;
    }

/*****************************************************************************
 *                  Key Functions to Perform 2048 Operations
 *****************************************************************************/
    
    // add a tile always in upper right position
    private void addTile() {
        int row = 0;
        int col = L - 1;
        
        if (board[row][col] != 0) {
            gameFinished = true;
            return;
        } 
        
        board[row][col] = 2;
        n++;
    }
    
    // string representation of this board
    public String toString() {
        
        StringBuilder s = new StringBuilder();
        s.append("\n" /* + "gameFinished(): " + gameFinished() + "\n" + 
         "hasWon(): " + hasWon() + "\n" */);
        // add each element of the array to the string
        for (int row = 0; row < L; row++) {
            for (int col = 0; col < L; col++) {
                s.append(String.format("%4d ", tileAt(row, col)));
            }
            s.append("\n");
        }
        return s.toString();
        
    }
    
    // tile at (row, col) or 0 if not occupied
    public int tileAt(int row, int col) {
        if (row > L - 1 || row < 0 || col > L - 1 || col < 0)
            throw new java.lang.IllegalArgumentException("row = " + row + 
                                                         ", col = " + col);
        
        // blank tiles have the value 0, so this works for all positions
        return board[row][col];
    }
    
    // number of elements (occupied positions) in board n
    public int size() {
        return n;
    }
    
    // does this baord have a 2048 tile?
    public boolean hasWon() {
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                if (board[i][j] >= GOAL_SCORE)
                    return true;
            }
        }
        return false;
    }
    
    // are there no more possible swipes?
    public boolean gameFinished() {
        return (!(canSwipe(1) || canSwipe(2) || canSwipe(3) || canSwipe(4)) 
                || gameFinished);
    }
    
    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) 
            return true;
        if (y == null) 
            return false;
        if (y.getClass() != this.getClass()) 
            return false;
        Board2048 that = (Board2048) y;
        if (this.n != that.n)
            return false;
        if (this.gameFinished != that.gameFinished)
            return false;
        if (this.weighted != that.weighted)
            return false;
        if (this.largest != that.largest)
            return false;
        
        // can't use equals() for 2D arrays
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (that.board[i][j] != this.board[i][j])
                    return false;
            }
        }
        return (this.won == that.won);
    }
    
    // all neighboring boards
    public Iterable<Board2048> neighbors() {
        Queue<Board2048> neighbors = new Queue<>();
        
        // 1 is up, 2 is right, 3 is down, 4 is left

        // swipe up
        if (canSwipe(1)) {
            Board2048 b = new Board2048(copyAndSwipe(board, 1));
            b.addTile();
            neighbors.enqueue(b); 
        }
        // swipe right
        if (canSwipe(2)) {
            Board2048 b = new Board2048(copyAndSwipe(board, 2));
            b.addTile();
            neighbors.enqueue(b); 
        }
        // swipe down
        if (canSwipe(3)) {
            Board2048 b = new Board2048(copyAndSwipe(board, 3));
            b.addTile();
            neighbors.enqueue(b); 
        }
        // swipe left
        if (canSwipe(4)) {
            Board2048 b = new Board2048(copyAndSwipe(board, 4));
            b.addTile();
            neighbors.enqueue(b); 
        }
        
        
        return neighbors;
    }
    
    // can you swipe in a given direction?
    private boolean canSwipe(int direction) {
        if (direction < 1 || direction > 4)
            throw new IllegalArgumentException("direction must be 1-4");
        
        int temp1;
        int temp2;
        
        // 1 is up, 2 is right, 3 is down, 4 is left
        
        // For each row/col parallel to the swipe, determine if an unoccupied 
        // tile exists after an occupied tile (in the direction of the swipe)
        // OR if a possible merge exists
        for (int i = 0; i < L; i++) {
            // reset temps to values that don't match
            temp1 = -1;
            temp2 = -2;
            for (int j = 0; j < L; j++) {
                if (direction == 1 && board[i][j] != 0) {
                    // temp1 is set to the first occupied tile seen
                    temp1 = board[i][j];
                    if (i > 0) {
                        temp2 = board[i - 1][j];
                        // temp1 == temp2 signifies a merge
                        if (board[i - 1][j] == 0 || temp1 == temp2) {
                            // return true since there is an occupied tile
                            // that can be shifted
                            return true;
                        }
                    }
                }
                if (direction == 2 && board[i][j] != 0) {
                    temp1 = board[i][j];
                    if (j != L - 1) {
                        temp2 = board[i][j + 1];
                        if (board[i][j + 1] == 0 || temp1 == temp2) {
                            return true;
                        }
                    }
                }
                if (direction == 3 && board[i][j] != 0) {
                    temp1 = board[i][j];
                    if (i != L - 1) {
                        temp2 = board[i + 1][j];
                        if (board[i + 1][j] == 0 || temp1 == temp2) {
                            return true;
                        }
                    }
                }
                if (direction == 4 && board[i][j] != 0) {
                    temp1 = board[i][j];
                    if (j > 0) {
                        temp2 = board[i][j - 1];
                        if (board[i][j - 1] == 0 || temp1 == temp2) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    // swap rowA, colA with rowB, colB
    private void swapTiles(int[][] b, int rowA, int colA, int rowB, int colB) {
        
        // perform the swap
        int temp = b[rowA][colA];
        b[rowA][colA] = b[rowB][colB];
        b[rowB][colB] = temp;
    }
    
    private int[][] copyArray(int[][] b) {
        if (b == null)
            throw new IllegalArgumentException("array to copy is null");
        
        int[][] copy = new int[L][L];
        
        // copy over each value
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                copy[i][j] = b[i][j];
            }
        }
        
        return copy;
    }
    
    // make a copy of the int[][] and swap rowA, colA with rowB, colB
    private int[][] copyAndSwipe(int[][] b, int direction) {
        
        int[][] copy = copyArray(b);
        
        // swipe the tiles depending on direction
        
        if (direction == 1)
            swipeTiles(copy, 1);
        else if (direction == 2)
            swipeTiles(copy, 2);
        else if (direction == 3)
            swipeTiles(copy, 3);
        else if (direction == 4)
            swipeTiles(copy, 4);
        
        
        return copy;
    }
    
    // perform the actual merge operation
    private int[] merge(int b[][], int first, int second, int temp1, int temp2, 
                        int position, int direction) {
        
        // array to send updated integer values back
        int[] update;
        
        if (b[first][second] != 0 && temp1 == 0) {
            temp1 = b[first][second];
            if (direction == 1 || direction == 3)
                position = first;
            else if (direction == 2 || direction == 4)
                position = second;
        }
        
        // check if any occupied spaces exist between two of the 
        // same numbers. if none, combine (add) the numbers
        else if (b[first][second] != 0) {
            if (temp1 == b[first][second]) {
                b[first][second] *= 2;
                if (direction == 1 || direction == 3)
                    b[position][second] = 0;
                else if (direction == 2 || direction == 4)
                    b[first][position] = 0;
                temp1 = 0;
                temp2 = 0;
                // decrement number of occupied positions
                n--;
            }
            else {
                // there is no match so set temp1 to next position
                temp1 = b[first][second];
                if (direction == 1 || direction == 3)
                    position = first;
                else if (direction == 2 || direction == 4)
                    position = second;
            }
        }
        
        update = new int[] {temp1, temp2, position};
        
        return update;
    }
    
    // merge tiles that have the same value together
    private void mergeTiles(int[][] b, int direction) {
        
        int temp1;
        int temp2;
        int position;
        int[] arr = new int[3];
        
        // for each row/col (depending on direction), perform applicable merges
        for (int constant = 0; constant < L; constant++) {
            temp1 = 0;
            temp2 = 0;
            position = 0;
            for (int changing = 0; changing < L; changing++) {
                
                if (direction == 1) {
                    // merge method returns an array of 3 integers
                    // why? ints are primitives; passed by value, not reference
                    arr = merge(b, changing, constant, 
                                temp1, temp2, position, 1);
                }
                else if (direction == 2) {
                    arr = merge(b, constant, L - changing - 1, 
                                temp1, temp2, position, 2);
                }
                else if (direction == 3) {
                    arr = merge(b, L - changing - 1, constant, 
                                temp1, temp2, position, 3);
                }
                else if (direction == 4) {
                    arr = merge(b, constant, changing, 
                                temp1, temp2, position, 4);
                }
                // update temp1, temp2, and position
                temp1 = arr[0];
                temp2 = arr[1];
                position = arr[2];
            }
        }
    }
    
    // keeps track of a pointer and swaps tiles
    private int swipe(int b[][], int first, int second, int pointer, int dir) {
        
        // first determine new pointer based on direction if necessary
        // depends on if the row or the column is the one changing
        if (b[first][second] == 0 && pointer == -1) {
            if (dir == 1 || dir == 3)
                pointer = first;
            else if (dir == 2 || dir == 4)
                pointer = second;
        }
        
        // swap the occupied tile with unoccupied one and update pointer
        if (pointer != -1 && b[first][second] != 0) {
            if (dir == 1 || dir == 3)
                swapTiles(b, first, second, pointer, second);
            else if (dir == 2 || dir == 4)
                swapTiles(b, first, second, first, pointer);
            if (dir == 2 || dir == 3)
                pointer--;
            else if (dir == 1 || dir == 4)
                pointer++;
        }
        
        
        return pointer;
    }
    
    // swipe the board by swapping tiles until zeros are pushed opposite swipe
    private void swipeTiles(int[][] b, int direction) {
        
        // first merge tiles, if applicable
        mergeTiles(b, direction);
        int pointer = -1;
        
        // 1 is up, 2 is right, 3 is down, 4 is left
        
        // for each row/col, move 0's away from swipe direction
        // e.g., if swipe is up, then move the zeros to the bottom
        // INVARIANT: the order of the non-zero tiles does not change
        for (int constant = 0; constant < L; constant++) {
            // reset pointer for each rol/col
            pointer = -1;
            for (int changing = 0; changing < L; changing++) {
                // pointer starts at the end of the board the swipe is going to
                // e.g., if swipe up then pointer begins at row 0
                if (direction == 1) {
                    pointer = swipe(b, changing, constant, pointer, 1);
                }
                else if (direction == 2) {
                    pointer = swipe(b, constant, 
                                    L - changing - 1, pointer, 2);
                }
                else if (direction == 3) {
                    pointer = swipe(b, L - changing - 1, 
                                    constant, pointer, 3);
                }
                else if (direction == 4) {
                    pointer = swipe(b, constant, changing, pointer, 4);
                }
            }
        }
    }

/*****************************************************************************
 *                         Possibly Useful Functions
 *****************************************************************************/

    // constructor no arguments (puts two weighted random values on board)
    public Board2048() {
        // unoccupied positions have value of 0
        board = new int[L][L];
        
        // put two tiles in random positions to start
        for (int i = 0; i < START_TILES; i++)
            addTile();
        
        WEIGHTS = new int[L][L];
        computeWeights();
        weighted = computeWeightedScore();
        largest = computeLargest();
        
        won = false;
        gameFinished = false;
    }
    
    
    // sum; add up tiles
    public int sum() {
        int sum = 0;
        
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
                sum += board[i][j];
            }
        }
        return sum;
    }
    
    
    // count number of empty spots
    public int emptySpots() {
        int count = 0;
        
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
                if (board[i][j] == 0)
                    count++;
            }
        }
        return count;
    }
    
    
    // add a tile in a random open slot in board
    private void addRandomTile() {
        if (gameFinished())
            throw new IllegalStateException("no open slots");
            
        int row;
        int col;
        
        // if unlucky worst case is very bad
        do {
            row = StdRandom.uniform(4);
            col = StdRandom.uniform(4);
        } while (board[row][col] != 0); 
        
        board[row][col] = chooseValue();
        n++;
    }
    
    // get a weighted value (either 2 or 4)
    private int chooseValue() {
        
        // chooses a weighted random number (either 2 or 4)
        // 2 has a probability of 0.9 as per the official 2048 game
        if (StdRandom.uniform() < 0.9)
            return 2;
        else
            return 4;
    }
    
    // Is the board full? are there no more open slots?
    // WARNING: possible merges may still exist
    // Must check gameFinished() to determine if there are no more swipes
    // This method may not be particularly useful
    public boolean isFull() {
        if (n == L * L)
            return true;
        else
            return false;
    }

/*****************************************************************************
 *                              Unit Testing
 *****************************************************************************/
    
    public static void main(String[] args) {
                
        int[][] tiles;
        
        // ALL BOARDS ARE IN TEXT FILES
        // the following code is for testing purposes
        // uncomment certain lines for testing different parts
        
        /*
        test with starting board
        tiles = new int[][] {{0, 0, 0, 0}, {0, 0, 0, 0}, 
                                {0, 0, 0, 0}, {0, 0, 0, 2}};
        test with no merges
        tiles = new int[][] {{2, 0, 0, 0}, {0, 4, 0, 0}, 
                                {0, 0, 8, 0}, {0, 0, 0, 16}};
        test with left and right merges
        tiles = new int[][] {{2, 0, 0, 2}, {0, 4, 4, 0}, 
                                {8, 0, 8, 0}, {0, 16, 0, 16}};
        test with up and down merges
        tiles = new int[][] {{2, 0, 0, 16}, {2, 4, 0, 0}, 
                                {0, 4, 8, 0}, {0, 0, 8, 16}};
        test with merges in all directions
        tiles = new int[][] {{2, 2, 0, 0}, {0, 2, 2, 0}, 
                                {0, 0, 2, 2}, {2, 0, 0, 2}};
        important: test merge with 3 of the same tile in a row
        tiles = new int[][] {{2, 2, 2, 0}, {0, 4, 4, 4}, 
                                {8, 8, 8, 0}, {0, 16, 16, 16}};
        important: test merge with 3 of the same tile in a col
        tiles = new int[][] {{2, 0, 8, 0}, {2, 4, 8, 16}, 
                                {2, 4, 8, 16}, {0, 4, 0, 16}};
        important: test gameFinished()
        tiles = new int[][] {{2, 4, 8, 16}, {16, 8, 4, 2}, 
                                {2, 4, 8, 16}, {16, 8, 4, 2}};
        */
        important: test hasWon()
        tiles = new int[][] {{2, 2, 4, 0}, {0, 0, 4, 0}, 
                                {0, 0, 0, 0}, {2048, 0, 0, 0}};
        
        Board2048 testBoard = new Board2048(tiles);
        Board2048 otherBoard = new Board2048(tiles);
        
        StdOut.println("THE BOARD:");
        StdOut.println();
        StdOut.println(testBoard.toString());
        StdOut.println();
        StdOut.println("tile at (2,1): " + testBoard.tileAt(2, 2));
        StdOut.println("tile at (3,3): " + testBoard.tileAt(3, 3));
        StdOut.println("size(): " + testBoard.size());
        StdOut.println("equals(otherBoard): " + testBoard.equals(otherBoard));
        StdOut.println("equals(itself): " + testBoard.equals(testBoard));
        StdOut.println("hasWon(): " + testBoard.hasWon());
        StdOut.println("gameFinished(): " + testBoard.gameFinished());
        StdOut.println("neighbors(): " + "\n" + testBoard.neighbors());
        
    }
    
}