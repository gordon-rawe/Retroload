package rawe.gordon.com.retrodownload.download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by gordon on 10/8/16.
 */
public class PersistUtil {
    public static void saveTextFile(String srcString, String dest) {
        File file = new File(dest);
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(srcString);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null && bufferedWriter != null) {
                    fileWriter.close();
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String readFileAsText(String src) {
        File srcFile = new File(src);
        if (srcFile.exists() && srcFile.isFile()) {
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;
            StringBuilder retBuilder = new StringBuilder();
            try {
                fileReader = new FileReader(srcFile);
                bufferedReader = new BufferedReader(fileReader);
                String line;
                retBuilder.append(line = bufferedReader.readLine());
                while (line != null) {
                    line = bufferedReader.readLine();
                    if (line != null) retBuilder.append(line);
                }
                return retBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileReader != null && bufferedReader != null) {
                    try {
                        fileReader.close();
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }
}
