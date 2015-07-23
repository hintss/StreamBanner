package tk.hintss.streambanner;

import edu.nyu.cs.javagit.api.DotGit;
import edu.nyu.cs.javagit.api.JavaGitException;
import edu.nyu.cs.javagit.api.commands.GitLogResponse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StreamBanner {
    public static void main(String[] args) {
        long lastSecond = 0L;

        while (true) {
            while (lastSecond == System.currentTimeMillis() / 1000) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            lastSecond = System.currentTimeMillis() / 1000;

            StringBuilder sb = new StringBuilder();

            try {
                sb.append("Latest Commit: \"");

                List<GitLogResponse.Commit> commits = DotGit.getInstance("./").getLog();
                GitLogResponse.Commit commit = commits.get(0);
                sb.append(safeSubstring(commit.getSha(), 7));
                sb.append(" - ");
                sb.append(safeSubstring(commit.getMessage().trim(), 80));

                sb.append("\" at ");

                sb.append(commit.getDateString());
            } catch (JavaGitException e) {
                e.printStackTrace();
                sb.append("Error getting commit");
            } catch (IOException e) {
                e.printStackTrace();
                sb.append("Error getting commit");
            }

            /* competition-specific code, set targetTime to the unix time of the competition ending

            sb.append("\r\n");

            long targetTime = 1405209600L;
            long timeUntil = targetTime - System.currentTimeMillis() / 1000;

            if (timeUntil > 36000) {
                sb.append("Time until start: ");
                sb.append(timeToString(timeUntil - 36000));
            } else if (timeUntil < 0) {
                sb.append("Competition over!");
            } else {
                sb.append("Time remaining: ");
                sb.append(timeToString(timeUntil));
            }

            */

            try {
                System.out.println(sb.toString());

                File file = new File("output.txt");

                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(sb.toString());
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String safeSubstring(String in, int cutLength) {
        if (in == null) {
            return null;
        }

        if (in.length() > cutLength) {
            in = in.substring(cutLength);
        }

        return in;
    }

    public static String timeToString(long seconds) {
        StringBuilder sb = new StringBuilder();

        if (seconds > 3600) {
            sb.append(seconds / 3600);
            sb.append(":");
        }

        if (seconds > 60) {
            long minutes = (seconds % 3600) / 60;
            if (minutes < 10) {
                sb.append("0");
            }
            sb.append(minutes);
            sb.append(":");
        }
        
        long second = seconds % 60;
        if (second < 10) {
            sb.append("0");
        }

        sb.append(seconds % 60);

        return sb.toString();
    }
}
