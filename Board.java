import java.util.Scanner;

public class Board {

    private Tile[][] grid;
    private Tile[][] squares;

    private static final int size = 9;
    private static final int sizeRoot = (int) Math.sqrt(size);

    private int steps;

   // private VisualGrid vg;

    public Board(int[] input) {
        grid = new Tile[size][size];
        squares = new Tile[size][size];
        steps = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                grid[x][y] = new Tile(input[x * size + y], size);
                squares[(x / sizeRoot) * sizeRoot + y / sizeRoot][(x % sizeRoot) * sizeRoot
                        + y % sizeRoot] = grid[x][y];
            }
        }
    }

    public String toString() {
        String ret = "";
        boolean solved = true;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (y % sizeRoot == 0)
                    ret += "|";
                ret += (grid[x][y].getAnswer() == 0 ? " "
                        : "\u001b[" + (39 + grid[x][y].getAnswer()) + "m" + grid[x][y].getAnswer() + "\u001b[0m") + "|";
                solved = solved && grid[x][y].solved();
            }
            ret += "\n";
        }
        if (solved)
            ret += "Done!";
        return ret;
    }

    public String opsLenToString() {
        String ret = "";
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (y % sizeRoot == 0)
                    ret += "|";
                ret += grid[x][y].getOps().size() + "|";
            }
            ret += "\n";
        }

        return ret;
    }

    public boolean isDone() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (!grid[x][y].solved())
                    return false;
            }
        }
        return true;
    }

    private int[][] getRelations(int x, int y) {
        int save = grid[x][y].getAnswer();
        grid[x][y].setAnswer(0);
        int[] row = new int[size];
        int[] col = new int[size];
        int[] sq = new int[size];
        for (int i = 0; i < size; i++) {
            row[i] = grid[x][i].getAnswer();
            col[i] = grid[i][y].getAnswer();
            sq[i] = squares[(x / sizeRoot) * sizeRoot + y / sizeRoot][i].getAnswer();
        }
        grid[x][y].setAnswer(save);
        return new int[][] { row, col, sq };
    }

    public void setOptions() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (!grid[x][y].solved()) {
                    grid[x][y].setOps(getRelations(x, y));
                    //vg.updateAlt(convert());
                }
            }
        }
    }

    /**
     * @return false if the board has a problem, true if the board does not have a
     *         problem
     */
    private boolean noIssues() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (!grid[x][y].isGood((getRelations(x, y))))
                    return false;
            }
        }
        return true;
    }

    private int[] progress(int[] coords) {
        int x = coords[0];
        int y = coords[1];
        x++;
        if (x == size) {
            x = 0;
            y++;
        }
        return new int[] { x, y };
    }

    public boolean randomCheck(int[] coords) {
        int x = coords[1];
        int y = coords[0];
        if (grid[x][y].getOps().size() == 1) {
            int[] progressed = progress(new int[] { x, y });
            if (progressed[1] == size)
                return true;
            return randomCheck(progressed);

        } else {
            int index = 0;
            grid[x][y].setAnswer(grid[x][y].getOps().get(index));
            boolean nextOk = true;
            while (index < grid[x][y].getOps().size()) {
                steps++;
                grid[x][y].setAnswer(grid[x][y].getOps().get(index));

                if (noIssues()) {
                    int[] progressed = progress(new int[] { x, y });
                    if (progressed[1] == size)
                        return true;
                    nextOk = randomCheck(progressed);
                    if (nextOk)
                        break;
                }
                if (!noIssues() || !nextOk) {
                    index++;
                    if (index >= grid[x][y].getOps().size()) {
                        index = 0;
                        grid[x][y].setAnswer(0);
                        return false;
                    }
                }
            }

        }
        return grid[x][y].isGood(getRelations(x, y));
    }


    public int[] convert() {
        int[] ret = new int[size * size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                ret[x * size + y] = grid[x][y].getAnswer();
            }
        }
        return ret;
    }

    public int getAt(int x, int y) {
        return grid[x][y].getAnswer();
    }

    public Tile[][] getBoard() {
        return grid;
    }

    public boolean equals(Object o) {
        Board p = (Board) o;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (p.getAt(x, y) != this.getAt(x, y))
                    return false;
            }
        }
        return true;
    }

    public int getSteps() {
        return steps;
    }

    public static void main(String[] args) throws Exception {
        // https://www.nytimes.com/puzzles/sudoku/hard
        String[] allBoards = ReadFile.getInput();

        // Change this to change the board
        int gameIndex = 0;
        String thisGame = allBoards[gameIndex];
        int[] grid = new int[81];
        for (int i = 0; i < 81; i++) {
            grid[i] = Integer.parseInt(thisGame.substring(i, i + 1));
        }

        long startTime = System.currentTimeMillis();
        Board b = new Board(grid);
        //b.makeGraphic(grid);
        
        System.out.println("Original\n" + b);

        Board save = new Board(b.convert());
        b.setOptions();
        while (!save.equals(b)) {
            b.setOptions();
            save = new Board(b.convert());
        }
        b.setOptions();
        System.out.println("Initial Runthrough\n" + b);
        //int p = 0;
        while (!b.isDone()) {
            b.randomCheck(new int[] { 0, 0 });
            b.setOptions();
        }

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

        System.out.println(b);
        System.out.println(b.getSteps());
        System.out.println(b.noIssues());
        // System.out.println(b.getAt(1, 0));
    }
}
