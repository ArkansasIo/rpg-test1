package dq1.tools;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to export/import asset metadata to/from CSV under assets/res.
 * Exported CSV columns:
 * relative_path,type,size_bytes,img_width,img_height,duration_seconds
 *
 * Import CSV format (for copying files into assets/res):
 * src_absolute_path,dest_relative_path
 */
public class AssetCsvTool {

    public static void exportAssetsCsv(Path assetsRoot, Path outCsv) throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("relative_path,type,size,img_width,img_height,duration_seconds");
        if (!Files.exists(assetsRoot)) {
            Files.createDirectories(assetsRoot);
        }
        Files.walk(assetsRoot)
                .filter(p -> Files.isRegularFile(p))
                .forEach(p -> {
                    try {
                        String rel = assetsRoot.relativize(p).toString().replace('\\', '/');
                        String type = getTypeByExt(p.getFileName().toString());
                        long size = Files.size(p);
                        String imgW = "";
                        String imgH = "";
                        String dur = "";
                        if (type.equals("image")) {
                            try {
                                BufferedImage img = ImageIO.read(p.toFile());
                                if (img != null) {
                                    imgW = Integer.toString(img.getWidth());
                                    imgH = Integer.toString(img.getHeight());
                                }
                            } catch (Exception ignored) {}
                        }
                        if (type.equals("audio")) {
                            try {
                                AudioFileFormat aff = AudioSystem.getAudioFileFormat(p.toFile());
                                // duration calculation for supported formats
                                if (aff.properties() != null && aff.properties().containsKey("duration")) {
                                    Object d = aff.properties().get("duration");
                                    dur = d.toString();
                                }
                                // fallback: try AudioSystem.getAudioInputStream
                                // but some formats may not be supported
                            } catch (Exception ignored) {}
                        }
                        String line = String.format("%s,%s,%d,%s,%s,%s", escapeCsv(rel), type, size, imgW, imgH, dur);
                        lines.add(line);
                    } catch (Exception ex) {
                        System.err.println("Error inspecting asset: " + p + " -> " + ex.getMessage());
                    }
                });
        Files.createDirectories(outCsv.getParent());
        Files.write(outCsv, lines);
        System.out.println("Exported assets CSV to: " + outCsv.toAbsolutePath());
    }

    public static void importFromCsv(Path csvFile, Path assetsRoot) throws Exception {
        if (!Files.exists(csvFile)) throw new FileNotFoundException(csvFile.toString());
        try (BufferedReader br = Files.newBufferedReader(csvFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // skip header detection
                if (line.startsWith("relative_path") || line.startsWith("src_path")) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 2) {
                    Path src = Path.of(parts[0]);
                    Path destRel = Path.of(parts[1]);
                    Path dest = assetsRoot.resolve(destRel);
                    Files.createDirectories(dest.getParent());
                    Files.copy(src, dest);
                    System.out.println("Copied: " + src + " -> " + dest);
                }
            }
        }
    }

    private static String getTypeByExt(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".gif") || n.endsWith(".bmp")) return "image";
        if (n.endsWith(".wav") || n.endsWith(".aiff") || n.endsWith(".au") || n.endsWith(".mp3") || n.endsWith(".ogg")) return "audio";
        if (n.endsWith(".mid") || n.endsWith(".midi")) return "music";
        if (n.endsWith(".map") || n.endsWith(".json") || n.endsWith(".txt") || n.endsWith(".csv")) return "data";
        return "other";
    }

    private static String escapeCsv(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private static String[] parseCsvLine(String line) {
        // simple CSV parser: handle quoted fields
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"'); i++; // escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString()); cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    // small test runner
    public static void main(String[] args) throws Exception {
        Path root = Path.of("assets/res");
        Path out = Path.of("assets/res/assets_list.csv");
        exportAssetsCsv(root, out);
    }
}
