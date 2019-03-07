 /******************************************************************************
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
 *  Description: Solver implements the A* search to solve the 2048 game.
 * 
 *****************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

public class Solver2048 {
    
    // initial search node
    private final SearchNode solution;
        
    
    // create a comparable search node class so MinPQ can be used
    private static class SearchNode implements Comparable<SearchNode> {
        
        // the board
        private final Board2048 board;
        // number of moves to reach the board
        private final int moves;
        // reference to the previous node
        private final SearchNode previous;
        
        // assign instance variables
        public SearchNode(Board2048 b, int m, SearchNode pre) {
            board = b;
            previous = pre;
            moves = m;
        }
        
        // compare priorities
        public int compareTo(SearchNode node) {
            int boardPriority = 0 - board.weightedScore() /*- board.penalty()*/ + moves;
            //int boardLargest = board.largestTile();
            int nodePriority =  0 - node.board.weightedScore()  /*- board.penalty()*/ + node.moves;
            //int nodeLargest = node.board.largestTile();
            
            if (boardPriority > nodePriority)
                return 1;
            else if (nodePriority == boardPriority)
                return 0;
            else
               return -1;
        }
        
    }
    
    // find a solution to the initial board (using the A* algorithm)
    public Solver2048(Board2048 initial) {
        
        if (initial == null)
            throw new java.lang.IllegalArgumentException();
        //if (!initial.isSolvable())
        //    throw new java.lang.IllegalArgumentException("Unsolvable puzzle");
       
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        // insert the initial search node
        pq.insert(new SearchNode(initial, 0, null));
        // the node with minimum priority
        SearchNode min = pq.delMin();
        
        
        int count = 0;
        while (!min.board.hasWon() && !min.board.gameFinished()) {
            // delete search node with minimum priority from priority queue
            if (count != 0)
                min = pq.delMin();
            // insert all neighboring search nodes
            for (Board2048 neighbor: min.board.neighbors()) {
                if ((min.previous == null || !min.previous.board.equals(neighbor))) {
                    pq.insert(new SearchNode(neighbor, 
                                             min.moves + 1, min));
                //StdOut.println(neighbor);
                }
            }
            count++;
        }
        //StdOut.println("Count: " + count); // for testing
        solution = min;
    }
    
    // min number of moves to solve initial board
    public int moves() {
        return solution.moves;
    }
    
    // sequence of boards in a shortest solution
    public Iterable<Board2048> solution() {
        Stack<Board2048> st = new Stack<>();
        SearchNode node = solution;
        
        // add each board to the stack
        while (node.previous != null) {
            st.push(node.board);
            node = node.previous;
        }
        st.push(node.board);
        return st;
    }
    
    // test client (see below)
    public static void main(String[] args) {
        
        String filename = args[0];
        In in = new In(filename);
        int n = in.readInt();
        //StdOut.println("n = " + n);
        
        int[][] tiles = new int[n][n];
        
        // read values
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        
        Board2048 b = new Board2048(tiles);
        Solver2048 sol = new Solver2048(b);
        
        // the number of moves, but its not necessarily the minimum
        StdOut.println("Number of moves = " + sol.moves());
        for (Board2048 step: sol.solution()) {
            StdOut.println(step);
        }
        
    }
    
}