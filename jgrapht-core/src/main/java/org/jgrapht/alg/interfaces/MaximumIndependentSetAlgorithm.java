/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* -----------------
 * MaximumIndependentSetAlgorithm.java
 * -----------------
 * (C) Copyright 2016, by Nils Olberg and Contributors.
 *
 * Original Author: Nils Olberg
 * Contributor(s): Joris Kinable
 *
 */

package org.jgrapht.alg.interfaces;

import org.jgrapht.UndirectedGraph;

import java.util.Set;

/**
 * Computes a maximum independent set in an undirected graph. An independent set in a graph is a set of vertices V such that for each pair
 * u, v in V there is an edge between u and v. A maximum independent set is an independent set having the highest possible number of vertices 
 * for a given graph. An independent set of maximum weight is an independent set where the sum of weights assigned to the individual vertices 
 * in the independent set has been maximized. The maximum independent set problem is a special case of the maximum weighted independent set problem 
 * where all vertices have equal weight.
 */

public interface MaximumIndependentSetAlgorithm<V,E> {

    /**
     * Computes a maximum independent set; all vertices are considered to have equal weight.
     * @return a maximum independent set
     */
	IndependentSet<V> getIndependentSet(UndirectedGraph<V,E> graph);

    interface IndependentSet<V>{

        /**
         * Returns the weight of the independent set. When solving the maximum weighted independent set problem, the weight
         * returned is the sum of the weights of the vertices in the independent set. When solving the unweighted variant, the
         * cardinality of the independent set is returned instead.
         * @return weight of the independent set
         */
        double getWeight();

        /**
         * Set of vertices constituting the independent set
         * @return vertices in the independent set
         */
        Set<V> getVertices();
    }


    class IndependentSetImpl<V> implements IndependentSet<V>{
        protected Set<V> set;
        protected double weight;

        public IndependentSetImpl(){}

        public IndependentSetImpl(Set<V> set, double weight){
            this.set = set;
            this.weight = weight;
        }

        @Override
        public double getWeight(){
            return weight;
        }

        @Override
        public Set<V> getVertices(){
            return set;
        }

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder("IndependentSet(");
            builder.append(this.getWeight());
            builder.append("): ");
            builder.append(this.getVertices().toString());
            return builder.toString();
        }
    }
}
