import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    private static long[][] dpCost; // dp[v][0] = cost if not tested, dp[v][1] = cost if tested
    private static int[] globalCost;
    private static int[][] treeNb;
    private static boolean[] testedResult;

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        treeNb = nb;
        globalCost = cost;
        dpCost = new long[n][2];
        dfsCost(0, -1);
        testedResult = new boolean[n];
        boolean rootTested = dpCost[0][1] <= dpCost[0][0];
        rebuildTests(0, -1, rootTested);
        return testedResult;
    }
    private static void dfsCost(int u, int p) {
        long costTested = globalCost[u];
        long costNotTested = 0;
        for (int v : treeNb[u]) {
            if (v == p) continue;
            dfsCost(v, u);
            costTested += Math.min(dpCost[v][0], dpCost[v][1]);
            costNotTested += dpCost[v][1];
        }
        dpCost[u][1] = costTested;
        dpCost[u][0] = costNotTested;
    }
    private static void rebuildTests(int u, int p, boolean isTested) {
        testedResult[u] = isTested;
        for (int v : treeNb[u]) {
            if (v == p) continue;
            if (!isTested) {
                rebuildTests(v, u, true);
            } else {
                boolean vTested = dpCost[v][1] <= dpCost[v][0];
                rebuildTests(v, u, vTested);
            }
        }
    }

    private static int maxChainLen;

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        maxChainLen = 0;
        dfsChain(0, -1, nb);
        return maxChainLen;
    }
    private static int dfsChain(int u, int p, int[][] nb) {
        int max1 = -1;
        int max2 = -1;
        for (int v : nb[u]) {
            if (v == p) continue;
            int depthV = dfsChain(v, u, nb);
            int branchLen = depthV + 1;
            if (branchLen > max1) {
                max2 = max1;
                max1 = branchLen;
            } else if (branchLen > max2) {
                max2 = branchLen;
            }
        }
        int currentPath = 0;
        if (max1 != -1 && max2 != -1) {
            currentPath = max1 + max2;
        } else if (max1 != -1) {
            currentPath = max1;
        }
        if (currentPath > maxChainLen) {
            maxChainLen = currentPath;
        }
        return (max1 != -1) ? max1 : 0;
    }
}