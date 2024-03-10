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
                System.out.println(parts.length);
                if (parts.length == 1) {
                    likesMap.put(imageID, new HashSet<>());
                    continue;
                }
                Set<String> users = Arrays.stream(parts[1].split(","))
                        .collect(Collectors.toSet());
                likesMap.put(imageID, users);
            }
        }
        return likesMap;
    }
}
