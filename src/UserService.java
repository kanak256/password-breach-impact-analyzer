import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class UserService {

    public static class User {
        public String name;
        public String dept;
        public String originalPassword;
        public String salt;
        public String passHash;

        public User(String name, String dept, String originalPassword, String salt, String passHash) {
            this.name = name;
            this.dept = dept;
            this.originalPassword = originalPassword;
            this.salt = salt;
            this.passHash = passHash;
        }
    }

    private List<User> users = new ArrayList<>();
    private SecureRandom sr = new SecureRandom();

    public String check(String p, String user) {
        if (p.length() < 8)
            return "Weak";
        if (p.toLowerCase().contains(user.toLowerCase()))
            return "Weak";

        // Common weak patterns
        String[] bad = { "123", "pass", "admin", "qwerty" };
        for (String b : bad) {
            if (p.toLowerCase().contains(b))
                return "Weak";
        }

        int count = 0;
        if (p.matches(".*[A-Z].*"))
            count++;
        if (p.matches(".*[0-9].*"))
            count++;
        if (p.matches(".*[^a-z0-9A-Z].*"))
            count++;

        if (count >= 3)
            return "Strong";
        if (count >= 2)
            return "Medium";
        return "Weak";
    }

    public void addUser(String name, String dept, String password) throws Exception {
        byte[] saltBytes = new byte[8];
        sr.nextBytes(saltBytes);
        StringBuilder s = new StringBuilder();
        for (byte b : saltBytes) {
            s.append(String.format("%02x", b));
        }
        String salt = s.toString();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update((password + salt).getBytes());
        byte[] out = md.digest();

        StringBuilder finalHash = new StringBuilder();
        for (byte b : out) {
            finalHash.append(String.format("%02x", b));
        }

        users.add(new User(name, dept, password, salt, finalHash.toString()));
    }

    public String getFinalData() {
        if (users.isEmpty())
            return "No users added.\n";
        StringBuilder sb = new StringBuilder();
        sb.append("--- Final User Data ---\n");
        for (User u : users) {
            String shortHash = u.passHash.length() >= 12 ? u.passHash.substring(0, 12) : u.passHash;
            sb.append(u.name).append(" -> ").append(shortHash).append("... [Salt: ").append(u.salt).append("]\n");
        }
        return sb.toString();
    }

    public String getSimilarityGraph() {
        if (users.isEmpty())
            return "";
        int n = users.size();
        ArrayList<ArrayList<Integer>> adj = getRawSimilarityGraph();

        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Similarity Graph (DAA Connections) ---\n");
        for (int i = 0; i < n; i++) {
            sb.append(users.get(i).name).append(" connects to: [");
            List<Integer> connections = adj.get(i);
            for (int k = 0; k < connections.size(); k++) {
                sb.append(users.get(connections.get(k)).name);
                if (k < connections.size() - 1)
                    sb.append(", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    public List<User> getUsers() {
        return users;
    }

    public ArrayList<ArrayList<Integer>> getRawSimilarityGraph() {
        int n = users.size();
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++)
            adj.add(new ArrayList<>());

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isSimilar(users.get(i).originalPassword, users.get(j).originalPassword)) {
                    adj.get(i).add(j);
                    adj.get(j).add(i);
                }
            }
        }
        return adj;
    }

    // Helper method to check if two passwords share a common substring of length >=
    // 4
    // or if one completely contains the other (case-insensitive)
    private boolean isSimilar(String p1, String p2) {
        if (p1 == null || p2 == null)
            return false;
        String s1 = p1.toLowerCase();
        String s2 = p2.toLowerCase();

        // If one fully contains the other, they are similar
        if (s1.contains(s2) || s2.contains(s1))
            return true;

        // Otherwise, check for any shared substring of at least length 4
        int minLen = 4;
        if (s1.length() < minLen || s2.length() < minLen)
            return false;

        for (int i = 0; i <= s1.length() - minLen; i++) {
            String sub = s1.substring(i, i + minLen);
            if (s2.contains(sub)) {
                return true;
            }
        }
        return false;
    }

    public void clearUsers() {
        users.clear();
    }
}