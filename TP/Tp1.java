
/**
 * Referensi ide: Immanuel (2006463162)
 */

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

import static java.lang.Math.min;
import static java.lang.Math.max;

public class Tp1 {
    private static InputReader in;
    private static PrintWriter out;

    // Untuk menyimpan seluruh agent dengan key: Nama dalam String, value: Objek
    // tersebut.
    private static Map<String, Agent> agentsData = new HashMap<String, Agent>();

    // Untuk mengurutkan ranking dari agent secara keseluruhan.
    private static PriorityQueue<Agent> pqData = new PriorityQueue<Agent>();

    // Container dari banyak bakso dan siomay.
    private static Map<Integer, ArrayList<Integer>> bsContainer = new HashMap<Integer, ArrayList<Integer>>();

    // Untuk menyimpan ke dalam kompeHashMap.
    private static Stack<Agent> kompeStack = new Stack<Agent>();

    // Untuk meniympan agen siapa yang ditunjuk terbanyak oleh siesta.
    private static Map<Integer, String> kompeHashMap = new HashMap<Integer, String>();

    // Untuk mencetak evaluasi agar terurut.
    private static Queue<String> qEval = new LinkedList<String>();

    // Untuk menyimpan agent yang merupakan kang bakso.
    private static Queue<String> qBakso = new LinkedList<String>();

    // Untuk menyimpan agent yang merupakan kang siomay.
    private static Queue<String> qSiomay = new LinkedList<String>();

    // Menyimpan urutan-urutan agent yang akan di-deploy (bakso = 0, siomay = 1)
    private static int[] arrDeploy = new int[agentsData.size()];

    // Untuk menyimpan status apakah sudah ditunjuk atau belum.
    /**
     * 1001 = Banyak murid maksimal, 501 = Banyak grup maksimal, 2 = Untuk bakso dan
     * siomay
     */
    private static boolean[][][] boolChecker = new boolean[1001][501][2];

    // Untuk menyimpan perhitungan deploy (dynamic programming)
    private static long[][][] dp = new long[1001][501][2];

    // The max ammount of appointments from siesta
    private static int maxValue;

    // Used for keeping track of the best rank (the lower the better)
    private static int minR;

    // Used for keeping track of the worst rank (the higher the worse)
    private static int maxR;

    // Used to count how many ways to deploy.
    private static long deployWays;

    /**
     * Method untuk mencari berapa banyak kang bakso dan kang siomay untuk n-rank
     * pertama.
     * 
     * @param numOfToppest integer yang merupakan n-rank pertama.
     * @return integer banyak kang bakso dan kang siomay dalam 1 line.
     */
    public static String panutan(int numOfToppest) {
        ArrayList<Integer> baksoAndSiomay = bsContainer.get(numOfToppest);

        // Indeks 0 adalah bakso, indeks 1 adalah siomay.
        return baksoAndSiomay.get(0) + " " + baksoAndSiomay.get(1);
    }

    public static String kompetitif() {
        return kompeHashMap.get(maxValue) + " " + maxValue;
    }

    public static void evaluasi() {
        // Utilize PrintWriter
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Mencetak isi qEval.
        if (qEval.isEmpty()) {
            out.println("TIDAK ADA");
        } else {
            while (!qEval.isEmpty()) {
                out.print(qEval.poll() + " ");
            }
            out.println("");
        }
    }

    public static void duo() {
        // Utilize PrintWriter
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        if (qBakso.size() < qSiomay.size()) {
            while (!qBakso.isEmpty()) {
                out.print(qBakso.poll() + " " + qSiomay.poll());
                out.println("");
            }

            out.print("TIDAK DAPAT: ");

            while (!qSiomay.isEmpty()) {
                out.print(qSiomay.poll() + " ");
            }

            out.println();
        } else if (qSiomay.size() < qBakso.size()) {
            while (!qSiomay.isEmpty()) {
                out.print(qBakso.poll() + " " + qSiomay.poll());
                out.println("");
            }

            out.print("TIDAK DAPAT: ");

            while (!qBakso.isEmpty()) {
                out.print(qBakso.poll() + " ");
            }

            out.println();
        } else {
            while (!qSiomay.isEmpty()) {
                out.print(qBakso.poll() + " " + qSiomay.poll());
                out.println("");
            }
        }
    }

    public static void deployHelper(int numOfGroups) {
        if (numOfGroups * 2 > arrDeploy.length) {
            System.out.println(0);
        } else {
            boolChecker = new boolean[1001][501][2];
            deployWays = deploy(1, numOfGroups, arrDeploy[0]);
            System.out.println((long) deployWays);
        }
    }

    public static long deploy(int index, int numOfGroups, int target) {

        // Check the saved value.
        if (boolChecker[index][numOfGroups][target]) {
            return dp[index][numOfGroups][target];
        }

        // Base cases
        if (index > arrDeploy.length - 1) { // If index out of bound (ex: [111]1 -> index +
            // 2 error)
            return 0;
        }

        if (numOfGroups == 0 && index < arrDeploy.length - 1) { // Jika grup yang terbentuk lebih dari yang
                                                                // diinginkan.
            return 0;
        }

        if (numOfGroups == 1 && index == arrDeploy.length - 1) {
            if (target == arrDeploy[index]) { // Memenuhi syarat
                return 1;
            } else {
                return 0;
            }
        }

        long temp = 0;
        // Recursive cases
        if (target == arrDeploy[index] && index < arrDeploy.length - 1) {
            // Memperlebar grup dan menghentikan grup saat ini (buat baru lagi)
            temp = (deploy(index + 1, numOfGroups, target) + deploy(index + 2, numOfGroups - 1, arrDeploy[index + 1]))
                    % (1000000007L);
        } else if (target != arrDeploy[index] && index < arrDeploy.length - 1) {
            // Recursive case: Memperlebar grup (memindahkan pointer)
            temp = deploy(index + 1, numOfGroups, target) % (1000000007L);
        }

        // Dynamic programming part
        boolChecker[index][numOfGroups][target] = true;
        dp[index][numOfGroups][target] = temp;

        return temp % 1000000007L;
    }

    /**
     * Method to appoint agent, either to place it on rank 1 or the bottom rank
     * depending on the eventCode.
     * 
     * @param agentCode Which agent that will be ascended or descended
     * @param eventCode Which action that will be taken (0 = ascend, 1 = descend)
     */
    public static void appoint(String agentCode, int eventCode) {
        Agent chosenAgent = agentsData.get(agentCode);
        if (eventCode == 0) {
            chosenAgent.increaseAscend();
            chosenAgent.setCurrentRank(minR);
            minR--;
        } else {
            chosenAgent.increaseDescend();
            chosenAgent.setCurrentRank(maxR);
            maxR++;
        }
    }

    /**
     * Menyiapkan segala kebutuhan yang diperlukan untuk memenuhi evaluasi kompe.
     */
    public static void kompePrep() {
        // Mencari nilai penunjukkan siesta yang terbanyak.
        while (!kompeStack.isEmpty()) {
            int currentValue = kompeStack.peek().countAscDes();
            if (Math.max(maxValue, currentValue) == currentValue) {
                maxValue = currentValue;
            }
            // Menyimpan ke dalam HashMap agar dapat dipanggil di Kompe.
            kompeHashMap.put(currentValue, kompeStack.pop().getCode());
        }
    }

    public static void printArrayEtc(int day, int days) {
        // Utilize PrintWriter
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        agentsData.forEach((key, value) -> {
            // Sorting the agents rnk
            pqData.add(value);
        });

        // Counter untuk mengisi HashMap bsContainer
        int firstNRank = 1;
        int rankCounter = 1;
        int bakso = 0;
        int siomay = 0;

        // Untuk deploy
        int indexDeploy = 0;
        arrDeploy = new int[agentsData.size()];

        while (!pqData.isEmpty()) {
            // Mengisi stack sesuai dengan urutan rank agar jika ada yang duplikat maka
            // otomatis mengambil yang rank tertinggi.
            kompeStack.push(pqData.peek());

            // Normalisasi tanpa mengecek rank sebelumnya.
            pqData.peek().setCurrentRank(rankCounter++);

            // Normalisasi rank dari masing-masing agent.
            pqData.peek().setLastRankAndStatus();

            if (day == days - 1) {
                // Mengisi HashMap jika sudah hari terakhir (Untuk panutan)
                if (pqData.peek().getSpecialization() == 'B') {
                    bakso++;
                } else {
                    siomay++;
                }
                // Mengisi container bakso dan siomay
                bsContainer.put(firstNRank++, new ArrayList<Integer>(Arrays.asList(bakso, siomay)));

                // Menambahkan siapa saja yang tidak pernah naik rank.
                if (pqData.peek().getIsNeverIncrease()) {
                    qEval.add(pqData.peek().getCode());
                }

                // Untuk memisahkan siomay dan bakso ke container-nya masing-masing.
                if (pqData.peek().getSpecialization() == 'B') {
                    qBakso.add(pqData.peek().getCode());
                } else {
                    qSiomay.add(pqData.peek().getCode());
                }

                // Mengisi array yang akan digunakan untuk deploy (0 untuk bakso, 1 untuk
                // siomay)
                if (pqData.peek().getSpecialization() == 'B') {
                    arrDeploy[indexDeploy++] = 0;
                } else {
                    arrDeploy[indexDeploy++] = 1;
                }
            }

            // Normalisasi batas bawah dan batas atas
            minR = 0;
            maxR = agentsData.size() + 1;

            // Print urutan rank.
            out.print(pqData.poll().getCode() + " ");

        }

        out.println("");
    }

    public static void main(String args[]) throws IOException {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int batch;
        int agents;
        int days;
        int events;

        batch = in.nextInt();

        for (int tmp = 0; tmp < batch; tmp++) {
            maxValue = 0;
            qBakso.clear();
            qSiomay.clear();

            // Make new HashMap instead of clear cause clear has O(N) complexity.
            agentsData = new HashMap<String, Agent>();
            kompeHashMap = new HashMap<Integer, String>();

            // Prompt for agents data
            agents = in.nextInt();

            // Untuk penempatan ranking pada hashmap.
            minR = 0;
            maxR = agents + 1;

            for (int agent = 0; agent < agents; agent++) {
                String agentCode = in.next();
                char agentSpecialization = in.next().charAt(0);

                Agent initiatedAgent = new Agent(agentCode, agentSpecialization);

                // Set rank of the newly added agent
                initiatedAgent.setCurrentRank(agent + 1);
                initiatedAgent.setLastRankAndStatus();

                // Save agent object
                agentsData.put(initiatedAgent.getCode(), initiatedAgent);
            }

            // Prompt for ammount of days
            days = in.nextInt();
            for (int day = 0; day < days; day++) {
                // Prompt for ammount of siesta appointing someone events
                events = in.nextInt();
                for (int event = 0; event < events; event++) {
                    String agentCode = in.next();
                    int eventCode = in.nextInt();

                    // Siesta points an agent (it can be ascending, or descending)
                    appoint(agentCode, eventCode);
                }

                // Melakukan berbagai perhitungan.
                printArrayEtc(day, days);
                kompePrep();

                // Agar dapat tercetak dengan tepat
                out.flush();
            }
            // Prompt for last evaluation
            String evalCommand = in.next();

            if (evalCommand.equals("PANUTAN") || evalCommand.equals("DEPLOY")) {
                int num = in.nextInt();
                if (evalCommand.equals("PANUTAN")) {
                    out.println(panutan(num));
                } else {
                    deployHelper(num);
                }
            } else if (evalCommand.equals("KOMPETITIF")) {
                out.println(kompetitif());
            } else if (evalCommand.equals("EVALUASI")) {
                evaluasi();
            } else if (evalCommand.equals("DUO")) {
                duo();
            }
            out.flush();

            // Me-reset queue.
            qEval.clear();
        }
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

    }
}

class Agent implements Comparable<Agent> {
    private char specialization;
    private String code;
    private int ascend;
    private int descend;
    private int currentRank;
    private int lastRank;
    private boolean isNeverIncrease = true;

    public Agent(String code, char specialization) {
        this.code = code;
        this.specialization = specialization;
        this.ascend = 0;
        this.descend = 0;
        this.lastRank = 0;
    }

    public String getCode() {
        return this.code;
    }

    public char getSpecialization() {
        return this.specialization;
    }

    public int getAscend() {
        return this.ascend;
    }

    public int getDescend() {
        return this.descend;
    }

    public int getCurrentRank() {
        return this.currentRank;
    }

    public boolean getIsNeverIncrease() {
        return this.isNeverIncrease;
    }

    public void setCurrentRank(int currentRank) {
        this.currentRank = currentRank;
    }

    public void setLastRankAndStatus() {
        if (this.lastRank > this.currentRank) {
            this.changeStatus();
        }
        this.lastRank = this.currentRank;
    }

    public void increaseAscend() {
        this.ascend++;
    }

    public void increaseDescend() {
        this.descend++;
    }

    /**
     * Menghitung dan me-return banyak rank 1 dan turun rank terakhir suatu agen.
     * 
     * @return integer banyak rank 1 dan turun rank terakhir suatu agen.
     */
    public int countAscDes() {
        return this.descend + this.ascend;
    }

    /**
     * Mengubah status dari isNeverIncrease menjadi false jika suatu agen pernah
     * naik rank.
     */
    public void changeStatus() {
        this.isNeverIncrease = false;
    }

    /**
     * Untuk sort saat berada di dalam priority queue.
     * 
     * @param anotherAgent Agent yang akan dibandingkan saat melakukan sorting.
     * @return Positif, nol, atau negatif.
     */
    @Override
    public int compareTo(Agent anotherAgent) {
        return this.getCurrentRank() - anotherAgent.getCurrentRank();
    }
}