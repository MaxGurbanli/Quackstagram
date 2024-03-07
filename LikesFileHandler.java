import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class LikesFileHandler {
    private final String filePath;

    public LikesFileHandler(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, Set<String>> readLikes() throws IOException {
        Map<String, Set<String>> likesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String imageID = parts[0];
                Set<String> users = Arrays.stream(parts[1].split(","))
                                          .collect(Collectors.toSet());
                likesMap.put(imageID, users);
            }
        }
        return likesMap;
    }

    public void saveLikes(Map<String, Set<String>> likesMap) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Map.Entry<String, Set<String>> entry : likesMap.entrySet()) {
                String line = entry.getKey() + ":" + String.join(",", entry.getValue());
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
