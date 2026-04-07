import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class QuizSystem {

    private List<String> q;
    private List<String> a;
    private int s;

    public QuizSystem() {
        q = new ArrayList<>();
        a = new ArrayList<>();
        s = 0;
        lq();
        la();
    }

    private void lq() {
        try (BufferedReader r = new BufferedReader(new FileReader("Questions.txt"))) {
            String l;
            StringBuilder t = new StringBuilder();
            while ((l = r.readLine()) != null) {
                if (l.trim().isEmpty()) {
                    String qt = t.toString().trim();
                    if (!qt.isEmpty()) {
                        q.add(qt);
                        t = new StringBuilder();
                    } else {
                        t = new StringBuilder();
                    }
                } else {
                    t.append(l).append("\n");
                }
            }
            String qt = t.toString().trim();
            if (!qt.isEmpty()) q.add(qt);
        } catch (IOException e) {
            System.out.println("Questions file missing!");
            System.exit(0);
        }
    }

    private void la() {
        try (BufferedReader r = new BufferedReader(new FileReader("Answers.txt"))) {
            String l;
            while ((l = r.readLine()) != null) {
                if (!l.trim().isEmpty()) {
                    a.add(l.trim().toUpperCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Answers file missing");
            System.exit(0);
        }
    }

    public void go() {
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < q.size(); i++) {
            System.out.println("Q" + (i + 1) + ":\n" + q.get(i));
            String ansU = get(sc, 10);
            if (i < a.size() && ansU.equals(a.get(i))) s++;
            System.out.println();
        }
        show();
        save(sc);
        sc.close();
    }

    private String get(Scanner sc, int t) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<String> f = ex.submit(() -> {
            System.out.print("Answer (A/B/C/D): ");
            return sc.nextLine().trim().toUpperCase();
        });
        String u = "";
        try {
            u = f.get(t, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("\nTime's up");
            f.cancel(true);
        } catch (Exception e) {
            System.out.println("Input error");
        }
        ex.shutdownNow();
        return u;
    }

    private void show() {
        System.out.println("Quiz Finished");
        System.out.println("Score: " + s + "/" + q.size());
        double p = (s * 100.0) / q.size();
        System.out.println("Percent: " + String.format("%.2f", p) + "%");
    }

    private void save(Scanner sc) {
        System.out.print("Your name: ");
        String n = sc.nextLine().trim();
        try (BufferedWriter w = new BufferedWriter(new FileWriter("Result.txt", true))) {
            w.write(n + " scored " + s + "/" + q.size() + "\n");
            System.out.println("Saved to Result.txt");
        } catch (IOException e) {
            System.out.println("Cannot save result");
        }
    }

    public static void main(String[] args) {
        QuizSystem x = new QuizSystem();
        x.go();
    }
}
