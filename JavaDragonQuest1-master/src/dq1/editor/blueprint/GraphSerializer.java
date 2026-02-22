package dq1.editor.blueprint;

import java.io.*;

public class GraphSerializer {
    public static void save(BlueprintGraph graph, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(graph);
        }
    }

    public static BlueprintGraph load(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (BlueprintGraph) ois.readObject();
        }
    }
}
