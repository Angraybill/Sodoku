import java.io.File;
import java.util.*;
import java.util.Scanner;

public class ReadFile {

    public static String[] getInput() throws Exception {
        ReadFile r = new ReadFile();
        String[] lines = r.getArrayFromFile("RawPuzzles.txt");
        return lines;
    }

    public String[] getArrayFromFile(String filename) throws Exception {
        File file = new File(filename);
        List<String> lines = new LinkedList<String>();
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine())
            lines.add(sc.nextLine());
        sc.close();
        return lines.toArray(new String[lines.size()]);
    }
}

