package simulator.aco;

import simulator.graph.Node;
import simulator.utils.Utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by root on 22/11/17.
 */
public class LocalSearch {

    static boolean dlb_flag = true;

    static Ant executeLocalSearch(Ant ant, Globals globals) {
        Stack<Node> nodesTour = ant.getTour();
        if (nodesTour.size() <= 0) return ant;
        int n = 0;
        Map<Integer, Node> mapIndexes = new HashMap<>();
        for (int i = 0; i < nodesTour.size() - 1; i++) {
            if (!Ant.getFixed(null, null).contains(nodesTour.get(i))) {
                mapIndexes.put(n++, nodesTour.get(i));
            }
        }
        int tour[] = new int[n + 1];
        int distances[][] = new int[n][n];
        for (int i = 0; i < n; i++) {
            tour[i] = i;
            for (int j = 0; j < i; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                } else {
                    int cost1 = globals.routeManager.getRoute(mapIndexes.get(i).getId(), mapIndexes.get(j).getId()).getBestCost().intValue();
                    int cost2 = globals.routeManager.getRoute(mapIndexes.get(j).getId(), mapIndexes.get(i).getId()).getBestCost().intValue();
                    distances[i][j] = (cost1 + cost2) / 2;
                    distances[j][i] = distances[i][j];
                }
            }
        }
        tour[n] = tour[0];
        int nn_ls = Math.min(n - 1, globals.nnListSize);
        int[][] nn_list = Utilities.compute_nn_lists(n, nn_ls, distances);

        try {
            //two_opt_first(tour, distances, n, nn_ls, nn_list);
            three_opt_first(tour, distances, n, nn_ls, nn_list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ant newAnt = ant.clone();
        Stack<Node> nodes = newAnt.getTour();
        nodes.clear();
        for (Node nd : Ant.getFixed(null, null)) {
            nodes.push(nd);
        }
        int startPos = 0;
        int curr = -1;
        for (int i = 0; i < tour.length; i++) {
            if (tour[i] == 0) {
                startPos = i;
                break;
            }
        }
        while (curr != startPos) {
            if (curr == -1) curr = startPos;
            nodes.push(mapIndexes.get(tour[curr]));
            curr++;
            if (curr >= tour.length - 1) {
                curr = 0;
            }
        }
        nodes.push(nodes.get(0));
        newAnt.computeCost();
        if(newAnt.getCost() < ant.getCost()) {
            return newAnt;
        } else {
            return ant;
        }
    }

    static int[] generate_random_permutation(int n) {
        int i, help, node, tot_assigned = 0;
        double rnd;
        int[] r;
        r = new int[n];
        for (i = 0; i < n; i++)
            r[i] = i;
        for (i = 0; i < n; i++) {
            rnd = Math.random();
            node = (int) (rnd * (n - tot_assigned));
            assert (i + node < n);
            help = r[i];
            r[i] = r[i + node];
            r[i + node] = help;
            tot_assigned++;
        }
        return r;
    }

    static void two_opt_first(int[] tour, int[][] distances, int problem_size, int nn_ls, int[][] nn_list) {
        boolean gotoExchange = false;

        int c1, c2; /* cities considered for an exchange */
        int s_c1, s_c2; /* successor cities of c1 and c2 */
        int p_c1, p_c2; /* predecessor cities of c1 and c2 */
        int pos_c1, pos_c2; /* positions of cities c1, c2 */
        int i, j, h, l;
        int help;
        boolean improvement_flag;
        int h1 = 0, h2 = 0, h3 = 0, h4 = 0;
        int radius; /* radius of nn-search */
        int gain = 0;
        int[] random_vector;
        int[] pos; /* positions of cities in tour */
        boolean[] dlb; /* vector containing don't look bits */

        pos = new int[problem_size];
        dlb = new boolean[problem_size];
        for (i = 0; i < problem_size; i++) {
            pos[tour[i]] = i;
            dlb[i] = false;
        }

        improvement_flag = true;
        random_vector = generate_random_permutation(problem_size);

        while (improvement_flag) {

            improvement_flag = false;

            for (l = 0; l < problem_size; l++) {

                c1 = random_vector[l];
                // DEBUG ( assert ( c1 < problem_size && c1 >= 0); )
                if (dlb_flag && dlb[c1])
                    continue;
                pos_c1 = pos[c1];
                s_c1 = tour[pos_c1 + 1];
                radius = distances[c1][s_c1];

		        /* First search for c1's nearest neighbours, use successor of c1 */
                for (h = 0; h < nn_ls; h++) {
                    c2 = nn_list[c1][h]; /* exchange partner, determine its position */
                    if (radius > distances[c1][c2]) {
                        s_c2 = tour[pos[c2] + 1];
                        gain = -radius + distances[c1][c2] + distances[s_c1][s_c2]
                                - distances[c2][s_c2];
                        if (gain < 0) {
                            h1 = c1;
                            h2 = s_c1;
                            h3 = c2;
                            h4 = s_c2;
                            gotoExchange = true;
                            break;
                        }
                    } else
                        break;
                }

                if (gotoExchange) {
                    /* Search one for next c1's h-nearest neighbours, use predecessor c1 */
                    if (pos_c1 > 0)
                        p_c1 = tour[pos_c1 - 1];
                    else
                        p_c1 = tour[problem_size - 1];
                    radius = distances[p_c1][c1];
                    for (h = 0; h < nn_ls; h++) {
                        c2 = nn_list[c1][h]; /* exchange partner, determine its position */
                        if (radius > distances[c1][c2]) {
                            pos_c2 = pos[c2];
                            if (pos_c2 > 0)
                                p_c2 = tour[pos_c2 - 1];
                            else
                                p_c2 = tour[problem_size - 1];
                            if (p_c2 == c1)
                                continue;
                            if (p_c1 == c2)
                                continue;
                            gain = -radius + distances[c1][c2] + distances[p_c1][p_c2]
                                    - distances[p_c2][c2];
                            if (gain < 0) {
                                h1 = p_c1;
                                h2 = c1;
                                h3 = p_c2;
                                h4 = c2;
                                gotoExchange = true;
                                break;
                            }
                        } else
                            break;
                    }
                }

                if (!gotoExchange) {
                    /* No exchange */
                    dlb[c1] = true;
                    continue;
                }

                if (gotoExchange) {
                    gotoExchange = false;
                    improvement_flag = true;
                    dlb[h1] = false;
                    dlb[h2] = false;
                    dlb[h3] = false;
                    dlb[h4] = false;
                    /* Now perform move */
                    if (pos[h3] < pos[h1]) {
                        help = h1;
                        h1 = h3;
                        h3 = help;
                        help = h2;
                        h2 = h4;
                        h4 = help;
                    }
                    if (pos[h3] - pos[h2] < problem_size / 2 + 1) {
                        /* reverse inner part from pos[h2] to pos[h3] */
                        i = pos[h2];
                        j = pos[h3];
                        while (i < j) {
                            c1 = tour[i];
                            c2 = tour[j];
                            tour[i] = c2;
                            tour[j] = c1;
                            pos[c1] = j;
                            pos[c2] = i;
                            i++;
                            j--;
                        }
                    } else {
			            /* reverse outer part from pos[h4] to pos[h1] */
                        i = pos[h1];
                        j = pos[h4];
                        if (j > i)
                            help = problem_size - (j - i) + 1;
                        else
                            help = (i - j) + 1;
                        help = help / 2;
                        for (h = 0; h < help; h++) {
                            c1 = tour[i];
                            c2 = tour[j];
                            tour[i] = c2;
                            tour[j] = c1;
                            pos[c1] = j;
                            pos[c2] = i;
                            i--;
                            j++;
                            if (i < 0)
                                i = problem_size - 1;
                            if (j >= problem_size)
                                j = 0;
                        }
                        tour[problem_size] = tour[0];
                    }
                } else {
                    dlb[c1] = true;
                }

            }
        }
    }

    static void three_opt_first(int[] tour, int[][] distances, int problem_size, int nn_ls, int[][] nn_list) {
	/*
	 * In case a 2-opt move should be performed, we only need to store opt2_move = true,
	 * as h1, .. h4 are used in such a way that they store the indices of the correct move
	 */

        boolean gotoExchange = false;

        int c1, c2, c3; /* cities considered for an exchange */
        int s_c1, s_c2, s_c3; /* successors of these cities */
        int p_c2, p_c3; /* predecessors of these cities */
        int pos_c1, pos_c2, pos_c3; /* positions of cities c1, c2, c3 */
        int i, j, h, g, l;
        boolean improvement_flag;
        int help;
        int h1 = 0, h2 = 0, h3 = 0, h4 = 0, h5 = 0, h6 = 0; /* memorize cities involved in a move */
        int diffs, diffp;
        boolean between = false;
        boolean opt2_flag; /* = true: perform 2-opt move, otherwise none or 3-opt move */
        int move_flag; /*
		        * move_flag = 0 -. no 3-opt move
		        * move_flag = 1 -. between_move (c3 between c1 and c2)
		        * move_flag = 2 -. not_between with successors of c2 and c3
		        * move_flag = 3 -. not_between with predecessors of c2 and c3
		        * move_flag = 4 -. cyclic move
		        */
        int gain, move_value, radius, add1, add2;
        int decrease_breaks; /* Stores decrease by breaking two edges (a,b) (c,d) */
        int[] val = new int[3];
        int n1, n2, n3;
        int[] pos; /* positions of cities in tour */
        boolean[] dlb; /* vector containing don't look bits */
        int[] h_tour; /* help vector for performing exchange move */
        int[] hh_tour; /* help vector for performing exchange move */
        int[] random_vector;

        pos = new int[problem_size];
        dlb = new boolean[problem_size];
        h_tour = new int[problem_size];
        hh_tour = new int[problem_size];

        for (i = 0; i < problem_size; i++) {
            pos[tour[i]] = i;
            dlb[i] = false;
        }
        improvement_flag = true;
        random_vector = generate_random_permutation(problem_size);

        while (improvement_flag) {
            move_value = 0;
            improvement_flag = false;

            for (l = 0; l < problem_size; l++) {

                c1 = random_vector[l];
                if (dlb_flag && dlb[c1])
                    continue;
                opt2_flag = false;

                move_flag = 0;
                pos_c1 = pos[c1];
                s_c1 = tour[pos_c1 + 1];

                h = 0; /* Search for one of the h-nearest neighbours */
                whileLoop: while (h < nn_ls) {

                    c2 = nn_list[c1][h]; /* second city, determine its position */
                    pos_c2 = pos[c2];
                    s_c2 = tour[pos_c2 + 1];
                    if (pos_c2 > 0)
                        p_c2 = tour[pos_c2 - 1];
                    else
                        p_c2 = tour[problem_size - 1];

                    diffs = 0;
                    diffp = 0;

                    radius = distances[c1][s_c1];
                    add1 = distances[c1][c2];

		    /* Here a fixed radius neighbour search is performed */
                    if (radius > add1) {
                        decrease_breaks = -radius - distances[c2][s_c2];
                        diffs = decrease_breaks + add1 + distances[s_c1][s_c2];
                        diffp = -radius - distances[c2][p_c2] + distances[c1][p_c2]
                                + distances[s_c1][c2];
                    } else
                        break;
                    if (p_c2 == c1) /* in case p_c2 == c1 no exchange is possible */
                        diffp = 0;
                    if ((diffs < move_value) || (diffp < move_value)) {
                        improvement_flag = true;
                        if (diffs <= diffp) {
                            h1 = c1;
                            h2 = s_c1;
                            h3 = c2;
                            h4 = s_c2;
                            move_value = diffs;
                            opt2_flag = true;
                            move_flag = 0;
			    /* goto exchange; */
                        } else {
                            h1 = c1;
                            h2 = s_c1;
                            h3 = p_c2;
                            h4 = c2;
                            move_value = diffp;
                            opt2_flag = true;
                            move_flag = 0;
			    /* goto exchange; */
                        }
                    }
		    /* Now perform the innermost search */
                    g = 0;
                    while (g < nn_ls) {

                        c3 = nn_list[s_c1][g];
                        pos_c3 = pos[c3];
                        s_c3 = tour[pos_c3 + 1];
                        if (pos_c3 > 0)
                            p_c3 = tour[pos_c3 - 1];
                        else
                            p_c3 = tour[problem_size - 1];

                        if (c3 == c1) {
                            g++;
                            continue;
                        } else {
                            add2 = distances[s_c1][c3];
			    /* Perform fixed radius neighbour search for innermost search */
                            if (decrease_breaks + add1 < add2) {

                                if (pos_c2 > pos_c1) {
                                    if (pos_c3 <= pos_c2 && pos_c3 > pos_c1)
                                        between = true;
                                    else
                                        between = false;
                                } else if (pos_c2 < pos_c1)
                                    if (pos_c3 > pos_c1 || pos_c3 < pos_c2)
                                        between = true;
                                    else
                                        between = false;
                                else {
                                    System.out.println("Strange !!, pos_1 " + pos_c1 + " == pos_2 " + pos_c2);
                                }

                                if (between) {
				    /*
				     * We have to add edges (c1,c2), (c3,s_c1), (p_c3,s_c2) to get
				     * valid tour; it's the only possibility
				     */

                                    gain = decrease_breaks - distances[c3][p_c3] + add1 + add2
                                            + distances[p_c3][s_c2];

				    /* check for improvement by move */
                                    if (gain < move_value) {
                                        improvement_flag = true; /* g = neigh_ls + 1; */
                                        move_value = gain;
                                        opt2_flag = false;
                                        move_flag = 1;
					/* store nodes involved in move */
                                        h1 = c1;
                                        h2 = s_c1;
                                        h3 = c2;
                                        h4 = s_c2;
                                        h5 = p_c3;
                                        h6 = c3;
                                        gotoExchange = true;
                                        break whileLoop;
                                    }
                                } else { /* not between(pos_c1,pos_c2,pos_c3) */

				    /* We have to add edges (c1,c2), (s_c1,c3), (s_c2,s_c3) */

                                    gain = decrease_breaks - distances[c3][s_c3] + add1 + add2
                                            + distances[s_c2][s_c3];

                                    if (pos_c2 == pos_c3) {
                                        gain = 20000;
                                    }

				    /* check for improvement by move */
                                    if (gain < move_value) {
                                        improvement_flag = true; /* g = neigh_ls + 1; */
                                        move_value = gain;
                                        opt2_flag = false;
                                        move_flag = 2;
					/* store nodes involved in move */
                                        h1 = c1;
                                        h2 = s_c1;
                                        h3 = c2;
                                        h4 = s_c2;
                                        h5 = c3;
                                        h6 = s_c3;
                                        gotoExchange = true;
                                        break whileLoop;
                                    }

				    /* or add edges (c1,c2), (s_c1,c3), (p_c2,p_c3) */
                                    gain = -radius - distances[p_c2][c2] - distances[p_c3][c3]
                                            + add1 + add2 + distances[p_c2][p_c3];

                                    if (c3 == c2 || c2 == c1 || c1 == c3 || p_c2 == c1) {
                                        gain = 2000000;
                                    }

                                    if (gain < move_value) {
                                        improvement_flag = true;
                                        move_value = gain;
                                        opt2_flag = false;
                                        move_flag = 3;
                                        h1 = c1;
                                        h2 = s_c1;
                                        h3 = p_c2;
                                        h4 = c2;
                                        h5 = p_c3;
                                        h6 = c3;
                                        gotoExchange = true;
                                        break whileLoop;
                                    }

				    /*
				     * Or perform the 3-opt move where no subtour inversion is necessary
				     * i.e. delete edges (c1,s_c1), (c2,p_c2), (c3,s_c3) and
				     * add edges (c1,c2), (c3,s_c1), (p_c2,s_c3)
				     */

                                    gain = -radius - distances[p_c2][c2] - distances[c3][s_c3]
                                            + add1 + add2 + distances[p_c2][s_c3];

				    /* check for improvement */
                                    if (gain < move_value) {
                                        improvement_flag = true;
                                        move_value = gain;
                                        opt2_flag = false;
                                        move_flag = 4;
                                        improvement_flag = true;
					/* store nodes involved in move */
                                        h1 = c1;
                                        h2 = s_c1;
                                        h3 = p_c2;
                                        h4 = c2;
                                        h5 = c3;
                                        h6 = s_c3;
                                        gotoExchange = true;
                                        break whileLoop;
                                    }
                                }
                            } else
                                g = nn_ls + 1;
                        }
                        g++;
                    }
                    h++;
                }
                if (move_flag != 0 || opt2_flag || gotoExchange) {
                    // exchange:
                    move_value = 0;

		    /* Now make the exchange */
                    if (move_flag != 0) {
                        dlb[h1] = false;
                        dlb[h2] = false;
                        dlb[h3] = false;
                        dlb[h4] = false;
                        dlb[h5] = false;
                        dlb[h6] = false;
                        pos_c1 = pos[h1];
                        pos_c2 = pos[h3];
                        pos_c3 = pos[h5];

                        if (move_flag == 4) {

                            if (pos_c2 > pos_c1)
                                n1 = pos_c2 - pos_c1;
                            else
                                n1 = problem_size - (pos_c1 - pos_c2);
                            if (pos_c3 > pos_c2)
                                n2 = pos_c3 - pos_c2;
                            else
                                n2 = problem_size - (pos_c2 - pos_c3);
                            if (pos_c1 > pos_c3)
                                n3 = pos_c1 - pos_c3;
                            else
                                n3 = problem_size - (pos_c3 - pos_c1);

			    /* n1: length h2 - h3, n2: length h4 - h5, n3: length h6 - h1 */
                            val[0] = n1;
                            val[1] = n2;
                            val[2] = n3;
			    /* Now order the partial tours */
                            h = 0;
                            help = Integer.MIN_VALUE;
                            for (g = 0; g <= 2; g++) {
                                if (help < val[g]) {
                                    help = val[g];
                                    h = g;
                                }
                            }

			    /* order partial tours according length */
                            if (h == 0) {
				/*
				 * copy part from pos[h4] to pos[h5]
				 * direkt kopiert: Teil von pos[h6] to pos[h1], it
				 * remains the part from pos[h2] to pos[h3]
				 */
                                j = pos[h4];
                                h = pos[h5];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

				/* First copy partial tour 3 in new position */
                                j = pos[h4];
                                i = pos[h6];
                                tour[j] = tour[i];
                                pos[tour[i]] = j;
                                while (i != pos_c1) {
                                    i++;
                                    if (i >= problem_size)
                                        i = 0;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    tour[j] = tour[i];
                                    pos[tour[i]] = j;
                                }

				/* Now copy stored part from h_tour */
                                j++;
                                if (j >= problem_size)
                                    j = 0;
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            } else if (h == 1) {

				/*
				 * copy part from pos[h6] to pos[h1]
				 * direkt kopiert: Teil von pos[h2] to pos[h3], it
				 * remains the part from pos[h4] to pos[h5]
				 */
                                j = pos[h6];
                                h = pos[h1];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

				/* First copy partial tour 3 in new position */
                                j = pos[h6];
                                i = pos[h2];
                                tour[j] = tour[i];
                                pos[tour[i]] = j;
                                while (i != pos_c2) {
                                    i++;
                                    if (i >= problem_size)
                                        i = 0;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    tour[j] = tour[i];
                                    pos[tour[i]] = j;
                                }

				/* Now copy stored part from h_tour */
                                j++;
                                if (j >= problem_size)
                                    j = 0;
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            } else if (h == 2) {
				/*
				 * copy part from pos[h2] to pos[h3]
				 * direkt kopiert: Teil von pos[h4] to pos[h5], it
				 * remains the part from pos[h6] to pos[h1]
				 */
                                j = pos[h2];
                                h = pos[h3];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

				/* First copy partial tour 3 in new position */
                                j = pos[h2];
                                i = pos[h4];
                                tour[j] = tour[i];
                                pos[tour[i]] = j;
                                while (i != pos_c3) {
                                    i++;
                                    if (i >= problem_size)
                                        i = 0;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    tour[j] = tour[i];
                                    pos[tour[i]] = j;
                                }

				/* Now copy stored part from h_tour */
                                j++;
                                if (j >= problem_size)
                                    j = 0;
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            }
                        } else if (move_flag == 1) {

                            if (pos_c3 < pos_c2)
                                n1 = pos_c2 - pos_c3;
                            else
                                n1 = problem_size - (pos_c3 - pos_c2);
                            if (pos_c3 > pos_c1)
                                n2 = pos_c3 - pos_c1 + 1;
                            else
                                n2 = problem_size - (pos_c1 - pos_c3 + 1);
                            if (pos_c2 > pos_c1)
                                n3 = problem_size - (pos_c2 - pos_c1 + 1);
                            else
                                n3 = pos_c1 - pos_c2 + 1;

			    /* n1: length h6 - h3, n2: length h5 - h2, n2: length h1 - h3 */
                            val[0] = n1;
                            val[1] = n2;
                            val[2] = n3;
			    /* Now order the partial tours */
                            h = 0;
                            help = Integer.MIN_VALUE;
                            for (g = 0; g <= 2; g++) {
                                if (help < val[g]) {
                                    help = val[g];
                                    h = g;
                                }
                            }
			    /* order partial tours according length */

                            if (h == 0) {

				/*
				 * copy part from pos[h5] to pos[h2]
				 * (inverted) and from pos[h4] to pos[h1] (inverted)
				 * it remains the part from pos[h6] to pos[h3]
				 */
                                j = pos[h5];
                                h = pos[h2];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

                                j = pos[h1];
                                h = pos[h4];
                                i = 0;
                                hh_tour[i] = tour[j];
                                n2 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    hh_tour[i] = tour[j];
                                    n2++;
                                }

                                j = pos[h4];
                                for (i = 0; i < n2; i++) {
                                    tour[j] = hh_tour[i];
                                    pos[hh_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }

				/* Now copy stored part from h_tour */
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            } else if (h == 1) {

				/* copy part from h3 to h6 (wird inverted) erstellen : */
                                j = pos[h3];
                                h = pos[h6];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

                                j = pos[h6];
                                i = pos[h4];

                                tour[j] = tour[i];
                                pos[tour[i]] = j;
                                while (i != pos_c1) {
                                    i++;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    if (i >= problem_size)
                                        i = 0;
                                    tour[j] = tour[i];
                                    pos[tour[i]] = j;
                                }

				/* Now copy stored part from h_tour */
                                j++;
                                if (j >= problem_size)
                                    j = 0;
                                i = 0;
                                tour[j] = h_tour[i];
                                pos[h_tour[i]] = j;
                                while (j != pos_c1) {
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    i++;
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                }
                                tour[problem_size] = tour[0];
                            }

                            else if (h == 2) {

				/*
				 * copy part from pos[h2] to pos[h5] and
				 * from pos[h3] to pos[h6] (inverted), it
				 * remains the part from pos[h4] to pos[h1]
				 */
                                j = pos[h2];
                                h = pos[h5];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }
                                j = pos_c2;
                                h = pos[h6];
                                i = 0;
                                hh_tour[i] = tour[j];
                                n2 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    hh_tour[i] = tour[j];
                                    n2++;
                                }

                                j = pos[h2];
                                for (i = 0; i < n2; i++) {
                                    tour[j] = hh_tour[i];
                                    pos[hh_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }

				/* Now copy stored part from h_tour */
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            }
                        } else if (move_flag == 2) {

                            if (pos_c3 < pos_c1)
                                n1 = pos_c1 - pos_c3;
                            else
                                n1 = problem_size - (pos_c3 - pos_c1);
                            if (pos_c3 > pos_c2)
                                n2 = pos_c3 - pos_c2;
                            else
                                n2 = problem_size - (pos_c2 - pos_c3);
                            if (pos_c2 > pos_c1)
                                n3 = pos_c2 - pos_c1;
                            else
                                n3 = problem_size - (pos_c1 - pos_c2);

                            val[0] = n1;
                            val[1] = n2;
                            val[2] = n3;
			    /* Determine which is the longest part */
                            h = 0;
                            help = Integer.MIN_VALUE;
                            for (g = 0; g <= 2; g++) {
                                if (help < val[g]) {
                                    help = val[g];
                                    h = g;
                                }
                            }
			    /* order partial tours according length */

                            if (h == 0) {

				/*
				 * copy part from pos[h3] to pos[h2]
				 * (inverted) and from pos[h5] to pos[h4], it
				 * remains the part from pos[h6] to pos[h1]
				 */
                                j = pos[h3];
                                h = pos[h2];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

                                j = pos[h5];
                                h = pos[h4];
                                i = 0;
                                hh_tour[i] = tour[j];
                                n2 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    hh_tour[i] = tour[j];
                                    n2++;
                                }

                                j = pos[h2];
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }

                                for (i = 0; i < n2; i++) {
                                    tour[j] = hh_tour[i];
                                    pos[hh_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
				/* getchar(); */
                            } else if (h == 1) {

				/*
				 * copy part from pos[h2] to pos[h3] and
				 * from pos[h1] to pos[h6] (inverted), it
				 * remains the part from pos[h4] to pos[h5]
				 */
                                j = pos[h2];
                                h = pos[h3];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

                                j = pos[h1];
                                h = pos[h6];
                                i = 0;
                                hh_tour[i] = tour[j];
                                n2 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    hh_tour[i] = tour[j];
                                    n2++;
                                }
                                j = pos[h6];
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                for (i = 0; i < n2; i++) {
                                    tour[j] = hh_tour[i];
                                    pos[hh_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            }

                            else if (h == 2) {

				/*
				 * copy part from pos[h1] to pos[h6]
				 * (inverted) and from pos[h4] to pos[h5],
				 * it remains the part from pos[h2] to
				 * pos[h3]
				 */
                                j = pos[h1];
                                h = pos[h6];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

                                j = pos[h4];
                                h = pos[h5];
                                i = 0;
                                hh_tour[i] = tour[j];
                                n2 = 1;
                                while (j != h) {
                                    i++;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    hh_tour[i] = tour[j];
                                    n2++;
                                }

                                j = pos[h4];
				/* Now copy stored part from h_tour */
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }

				/* Now copy stored part from h_tour */
                                for (i = 0; i < n2; i++) {
                                    tour[j] = hh_tour[i];
                                    pos[hh_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            }
                        } else if (move_flag == 3) {

                            if (pos_c3 < pos_c1)
                                n1 = pos_c1 - pos_c3;
                            else
                                n1 = problem_size - (pos_c3 - pos_c1);
                            if (pos_c3 > pos_c2)
                                n2 = pos_c3 - pos_c2;
                            else
                                n2 = problem_size - (pos_c2 - pos_c3);
                            if (pos_c2 > pos_c1)
                                n3 = pos_c2 - pos_c1;
                            else
                                n3 = problem_size - (pos_c1 - pos_c2);
			    /* n1: length h6 - h1, n2: length h4 - h5, n2: length h2 - h3 */

                            val[0] = n1;
                            val[1] = n2;
                            val[2] = n3;
			    /* Determine which is the longest part */
                            h = 0;
                            help = Integer.MIN_VALUE;
                            for (g = 0; g <= 2; g++) {
                                if (help < val[g]) {
                                    help = val[g];
                                    h = g;
                                }
                            }
			    /* order partial tours according length */

                            if (h == 0) {

				/*
				 * copy part from pos[h2] to pos[h3]
				 * (inverted) and from pos[h4] to pos[h5]
				 * it remains the part from pos[h6] to pos[h1]
				 */
                                j = pos[h3];
                                h = pos[h2];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

                                j = pos[h2];
                                h = pos[h5];
                                i = pos[h4];
                                tour[j] = h4;
                                pos[h4] = j;
                                while (i != h) {
                                    i++;
                                    if (i >= problem_size)
                                        i = 0;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    tour[j] = tour[i];
                                    pos[tour[i]] = j;
                                }
                                j++;
                                if (j >= problem_size)
                                    j = 0;
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            } else if (h == 1) {

				/*
				 * copy part from pos[h3] to pos[h2]
				 * (inverted) and from pos[h6] to pos[h1],
				 * it remains the part from pos[h4] to pos[h5]
				 */
                                j = pos[h3];
                                h = pos[h2];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

                                j = pos[h6];
                                h = pos[h1];
                                i = 0;
                                hh_tour[i] = tour[j];
                                n2 = 1;
                                while (j != h) {
                                    i++;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                    hh_tour[i] = tour[j];
                                    n2++;
                                }

                                j = pos[h6];
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }

                                for (i = 0; i < n2; i++) {
                                    tour[j] = hh_tour[i];
                                    pos[hh_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            }

                            else if (h == 2) {

				/*
				 * copy part from pos[h4] to pos[h5]
				 * (inverted) and from pos[h6] to pos[h1] (inverted)
				 * it remains the part from pos[h2] to pos[h3]
				 */
                                j = pos[h5];
                                h = pos[h4];
                                i = 0;
                                h_tour[i] = tour[j];
                                n1 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    h_tour[i] = tour[j];
                                    n1++;
                                }

                                j = pos[h1];
                                h = pos[h6];
                                i = 0;
                                hh_tour[i] = tour[j];
                                n2 = 1;
                                while (j != h) {
                                    i++;
                                    j--;
                                    if (j < 0)
                                        j = problem_size - 1;
                                    hh_tour[i] = tour[j];
                                    n2++;
                                }

                                j = pos[h4];
				/* Now copy stored part from h_tour */
                                for (i = 0; i < n1; i++) {
                                    tour[j] = h_tour[i];
                                    pos[h_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
				/* Now copy stored part from h_tour */
                                for (i = 0; i < n2; i++) {
                                    tour[j] = hh_tour[i];
                                    pos[hh_tour[i]] = j;
                                    j++;
                                    if (j >= problem_size)
                                        j = 0;
                                }
                                tour[problem_size] = tour[0];
                            }
                        } else {
                            System.err.println(" Some very strange error must have occurred !!!\n\n");
                            System.exit(0);
                        }
                    }
                    if (opt2_flag) {

			/* Now perform move */
                        dlb[h1] = false;
                        dlb[h2] = false;
                        dlb[h3] = false;
                        dlb[h4] = false;
                        if (pos[h3] < pos[h1]) {
                            help = h1;
                            h1 = h3;
                            h3 = help;
                            help = h2;
                            h2 = h4;
                            h4 = help;
                        }
                        if (pos[h3] - pos[h2] < problem_size / 2 + 1) {
			    /* reverse inner part from pos[h2] to pos[h3] */
                            i = pos[h2];
                            j = pos[h3];
                            while (i < j) {
                                c1 = tour[i];
                                c2 = tour[j];
                                tour[i] = c2;
                                tour[j] = c1;
                                pos[c1] = j;
                                pos[c2] = i;
                                i++;
                                j--;
                            }
                        } else {
			    /* reverse outer part from pos[h4] to pos[h1] */
                            i = pos[h1];
                            j = pos[h4];
                            if (j > i)
                                help = problem_size - (j - i) + 1;
                            else
                                help = (i - j) + 1;
                            help = help / 2;
                            for (h = 0; h < help; h++) {
                                c1 = tour[i];
                                c2 = tour[j];
                                tour[i] = c2;
                                tour[j] = c1;
                                pos[c1] = j;
                                pos[c2] = i;
                                i--;
                                j++;
                                if (i < 0)
                                    i = problem_size - 1;
                                if (j >= problem_size)
                                    j = 0;
                            }
                            tour[problem_size] = tour[0];
                        }
                    }
                } else {
                    dlb[c1] = true;
                }
            }
        }

    }
}
