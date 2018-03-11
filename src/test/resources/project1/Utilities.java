package simulator.utils;

import java.util.Random;

public class Utilities {


    static void swap2(int v[], int v2[], int i, int j) {
        int tmp;

        tmp = v[i];
        v[i] = v[j];
        v[j] = tmp;
        tmp = v2[i];
        v2[i] = v2[j];
        v2[j] = tmp;
    }

    public static void sort2(int v[], int v2[], int left, int right) {
        int k, last;

        if (left >= right)
            return;
        swap2(v, v2, left, (left + right) / 2);
        last = left;
        for (k = left + 1; k <= right; k++)
            if (v[k] < v[left])
                swap2(v, v2, ++last, k);
        swap2(v, v2, left, last);
        sort2(v, v2, left, last);
        sort2(v, v2, last + 1, right);
    }

    public static int[][] compute_nn_lists(int n, int nn, int[][] distance) {
        int i, node;
        int[] distance_vector = new int[n];
        int[] help_vector = new int[n];

        int[][] m_nnear = new int[n][nn];

        for (node = 0; node < n; node++) { /* compute cnd-sets for all node */
            for (i = 0; i < n; i++) { /* Copy distances from nodes to the others */
                distance_vector[i] = distance[node][i];
                help_vector[i] = i;
            }
            distance_vector[node] = Integer.MAX_VALUE; /* city is not nearest neighbour */
            Utilities.sort2(distance_vector, help_vector, 0, n - 1);
            for (i = 0; i < nn; i++) {
                m_nnear[node][i] = help_vector[i];
            }
        }
        return m_nnear;
    }

}
