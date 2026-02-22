package dq1.editor.panels;

import java.awt.Point;
import java.io.*;
import java.util.*;

public class GraphModel {
    public static class Node {
        public final String id;
        public String title;
        public int x, y;
        public Node(String id, String title, int x, int y) { this.id = id; this.title = title; this.x = x; this.y = y; }
    }

    public static class Link {
        public final String from, to;
        public Link(String f, String t) { from = f; to = t; }
    }

    public final List<Node> nodes = new ArrayList<>();
    public final List<Link> links = new ArrayList<>();
    private int idCounter = 1;

    public Node addNode(String title, int x, int y) {
        String id = "n" + (idCounter++);
        Node n = new Node(id, title, x, y);
        nodes.add(n);
        return n;
    }

    public void addLink(String fromId, String toId) {
        // avoid duplicate links
        for (Link l : links) if (l.from.equals(fromId) && l.to.equals(toId)) return;
        links.add(new Link(fromId, toId));
    }

    public Node getNode(String id) {
        for (Node n : nodes) if (n.id.equals(id)) return n; return null;
    }

    public Node findNodeAt(Point p) {
        return findNodeAt(p.x, p.y);
    }

    public Node findNodeAt(int x, int y) {
        for (Node n : nodes) {
            if (x >= n.x && x <= n.x + 120 && y >= n.y && y <= n.y + 30) return n;
        }
        return null;
    }

    public void clear() { nodes.clear(); links.clear(); idCounter = 1; }

    // Node/link helpers
    public boolean removeNode(String id) {
        Node target = getNode(id);
        if (target == null) return false;
        // remove links referencing this node
        links.removeIf(l -> l.from.equals(id) || l.to.equals(id));
        nodes.remove(target);
        return true;
    }

    public boolean removeLink(String fromId, String toId) {
        Iterator<Link> it = links.iterator();
        while (it.hasNext()) {
            Link l = it.next();
            if (l.from.equals(fromId) && l.to.equals(toId)) { it.remove(); return true; }
        }
        return false;
    }

    public boolean renameNode(String id, String newTitle) {
        Node n = getNode(id);
        if (n == null) return false;
        n.title = newTitle;
        return true;
    }

    // Simple legacy serializer: each line a token
    // N id title x y
    // L fromId toId
    @Deprecated
    public void saveToFile(File f) throws IOException {
        // prefer JSON format
        saveToJsonFile(f);
    }

    @Deprecated
    public void loadFromFile(File f) throws IOException {
        // detect JSON (start with '{') else legacy format
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.mark(2048);
            int c;
            // skip whitespace
            do { c = br.read(); } while (c != -1 && Character.isWhitespace(c));
            if (c == '{') {
                br.reset();
                loadFromJsonFile(f);
                return;
            }
        }
        // fallback to legacy parser
        loadFromLegacyFile(f);
    }

    private void loadFromLegacyFile(File f) throws IOException {
        clear();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; Map<String,Node> tmp = new HashMap<>();
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                char t = line.charAt(0);
                String[] parts = line.substring(2).split(" ", 4);
                if (t == 'N') {
                    String id = parts[0];
                    String title = unescape(parts[1]);
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);
                    Node n = new Node(id, title, x, y);
                    nodes.add(n); tmp.put(id, n);
                } else if (t == 'L') {
                    String[] p = line.substring(2).split(" ");
                    if (p.length >= 2) links.add(new Link(p[0], p[1]));
                }
            }
            // update idCounter
            int maxId = 0;
            for (String id : tmp.keySet()) {
                if (id.startsWith("n")) {
                    try { maxId = Math.max(maxId, Integer.parseInt(id.substring(1))); } catch (Exception ex) {}
                }
            }
            idCounter = maxId + 1;
        }
    }

    // JSON serializer (simple, no external libs)
    public void saveToJsonFile(File f) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write("{"); bw.newLine();
            // nodes
            bw.write("  \"nodes\": ["); bw.newLine();
            for (int i = 0; i < nodes.size(); i++) {
                Node n = nodes.get(i);
                bw.write(String.format("    {\"id\":\"%s\", \"title\":\"%s\", \"x\":%d, \"y\":%d}",
                        jsonEscape(n.id), jsonEscape(n.title), n.x, n.y));
                if (i < nodes.size() - 1) bw.write(",");
                bw.newLine();
            }
            bw.write("  ],"); bw.newLine();
            // links
            bw.write("  \"links\": ["); bw.newLine();
            for (int i = 0; i < links.size(); i++) {
                Link l = links.get(i);
                bw.write(String.format("    {\"from\":\"%s\", \"to\":\"%s\"}", jsonEscape(l.from), jsonEscape(l.to)));
                if (i < links.size() - 1) bw.write(",");
                bw.newLine();
            }
            bw.write("  ]"); bw.newLine();
            bw.write("}"); bw.newLine();
        }
    }

    public void loadFromJsonFile(File f) throws IOException {
        clear();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
        }
        String json = sb.toString();
        // Very small, tolerant parser for our simple structure
        Map<String, Map<String, String>> nodeMap = new LinkedHashMap<>();
        List<String[]> linkList = new ArrayList<>();

        int idx = 0; int len = json.length();
        while (idx < len) {
            idx = json.indexOf('{', idx);
            if (idx < 0) break;
            int end = json.indexOf('}', idx);
            if (end < 0) break;
            String obj = json.substring(idx + 1, end).trim();
            if (obj.contains("\"id\"")) {
                // parse fields naively
                String id = extractJsonString(obj, "id");
                String title = extractJsonString(obj, "title");
                String xs = extractJsonNumber(obj, "x");
                String ys = extractJsonNumber(obj, "y");
                if (id != null) {
                    int x = xs == null ? 0 : Integer.parseInt(xs);
                    int y = ys == null ? 0 : Integer.parseInt(ys);
                    nodeMap.put(id, Map.of("title", title == null ? "" : title, "x", String.valueOf(x), "y", String.valueOf(y)));
                }
            } else if (obj.contains("\"from\"")) {
                String a = extractJsonString(obj, "from");
                String b = extractJsonString(obj, "to");
                if (a != null && b != null) linkList.add(new String[]{a,b});
            }
            idx = end + 1;
        }
        // reconstruct nodes preserving order
        for (Map.Entry<String, Map<String,String>> e : nodeMap.entrySet()) {
            String id = e.getKey(); Map<String,String> m = e.getValue();
            String title = m.getOrDefault("title", "");
            int x = Integer.parseInt(m.getOrDefault("x","0"));
            int y = Integer.parseInt(m.getOrDefault("y","0"));
            nodes.add(new Node(id, title, x, y));
        }
        for (String[] p : linkList) links.add(new Link(p[0], p[1]));
        // update idCounter
        int maxId = 0;
        for (Node n : nodes) {
            String id = n.id;
            if (id.startsWith("n")) {
                try { maxId = Math.max(maxId, Integer.parseInt(id.substring(1))); } catch (Exception ex) {}
            }
        }
        idCounter = maxId + 1;
    }

    private static String extractJsonString(String obj, String key) {
        String needle = "\"" + key + "\"";
        int i = obj.indexOf(needle);
        if (i < 0) return null;
        int colon = obj.indexOf(':', i);
        if (colon < 0) return null;
        int q1 = obj.indexOf('"', colon);
        if (q1 < 0) return null;
        int q2 = obj.indexOf('"', q1 + 1);
        if (q2 < 0) return null;
        String raw = obj.substring(q1 + 1, q2);
        return jsonUnescape(raw);
    }

    private static String extractJsonNumber(String obj, String key) {
        String needle = "\"" + key + "\"";
        int i = obj.indexOf(needle);
        if (i < 0) return null;
        int colon = obj.indexOf(':', i);
        if (colon < 0) return null;
        int j = colon + 1;
        while (j < obj.length() && Character.isWhitespace(obj.charAt(j))) j++;
        int end = j;
        while (end < obj.length() && (Character.isDigit(obj.charAt(end)) || obj.charAt(end) == '-')) end++;
        return obj.substring(j, end).trim();
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int)c));
                    } else sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String jsonUnescape(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(i + 1);
                switch (n) {
                    case '\\': sb.append('\\'); i++; break;
                    case '"': sb.append('"'); i++; break;
                    case 'n': sb.append('\n'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    case 'u':
                        if (i + 5 < s.length()) {
                            String hex = s.substring(i + 2, i + 6);
                            try { int code = Integer.parseInt(hex, 16); sb.append((char) code); i += 5; }
                            catch (Exception ex) { sb.append('u'); i++; }
                        } else { sb.append('u'); i++; }
                        break;
                    default:
                        sb.append(n); i++; break;
                }
            } else sb.append(c);
        }
        return sb.toString();
    }

    private static String escape(String s) { return s.replace(" ", "_S_"); }
    private static String unescape(String s) { return s.replace("_S_", " "); }

    public List<String> simpleRunOrder() {
        // simple topological-ish traversal: start from nodes with no incoming
        Map<String, Integer> indeg = new HashMap<>();
        Map<String, List<String>> out = new HashMap<>();
        for (Node n : nodes) { indeg.put(n.id, 0); out.put(n.id, new ArrayList<>()); }
        for (Link l : links) {
            // skip links where from node isn't present
            if (!out.containsKey(l.from)) continue;
            out.get(l.from).add(l.to);
            indeg.put(l.to, indeg.getOrDefault(l.to, 0) + 1);
        }
        Queue<String> q = new LinkedList<>();
        for (Map.Entry<String,Integer> e : indeg.entrySet()) if (e.getValue() == 0) q.add(e.getKey());
        List<String> order = new ArrayList<>();
        while (!q.isEmpty()) {
            String id = q.poll();
            Node n = getNode(id);
            order.add(n == null ? id : n.title + "(" + id + ")");
            for (String to : out.getOrDefault(id, Collections.emptyList())) {
                indeg.put(to, indeg.get(to) - 1);
                if (indeg.get(to) == 0) q.add(to);
            }
        }
        // append remaining (cycles)
        for (Node n : nodes) if (!order.contains(n.title + "(" + n.id + ")")) order.add(n.title + "(" + n.id + ")");
        return order;
    }
}