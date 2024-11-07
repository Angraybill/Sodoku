import java.util.ArrayList;

public class Tile {

    private ArrayList<Integer> options;
    private int answer;

    private int boardSize;

    public Tile(int a, int s) {
        options = new ArrayList<Integer>();
        boardSize = s;
        answer = a;
        if (answer != 0) {
            options.add(a);
        } else {
            for (int i = 1; i <= boardSize; i++) {
                options.add(i);
            }
        }
    }

    public boolean solved() {
        return answer != 0;
    }

    public void setOps(int[][] relations) {
        for (int[] check : relations) {
            for (int i = options.size() - 1; i >= 0; i--) {
                if (options.size() == 1) {
                    answer = options.get(0);
                    break;
                }
                for (int j : check) {
                    if (j == options.get(i)) {
                        options.remove(i);
                        break;
                    }
                }
            }
        }
    }

    /** @return false means issues */
    public boolean isGood(int[][] relations) {
        if (answer == 0)
            return true;
        for (int[] set : relations) {
            for (int space : set) {
                if (space == answer) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setAnswer(int a) {
        answer = a;
    }

    public int getAnswer() {
        return answer;
    }

    public ArrayList<Integer> getOps() {
        return options;
    }

    public String toString() {
        return String.valueOf(answer);
    }
}