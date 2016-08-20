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
/* ------------------
 * CSVExporter.java
 * ------------------
 * (C) Copyright 2016, by Dimitrios Michail and Contributors.
 *
 * Original Author:  Dimitrios Michail
 * Contributors: -
 *
 * Changes
 * -------
 * 20-Aug-2016 : Initial Version (DM);
 *
 */
package org.jgrapht.ext;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

/**
 * Exports a graph into a CSV Format or any other Delimiter-separated value
 * format.
 * 
 * <p>
 * The exporter supports three different formats which can be adjusted using the
 * {@link #setFormat(Format) setFormat} method. The supported formats are the
 * same CSV formats used by
 * <a href="https://gephi.org/users/supported-graph-formats/csv-format">Gephi
 * </a>. For some of the formats, the behavior of the exporter can be adjusted
 * using the {@link #setParameter(Parameter, boolean) setParameter} method.
 * </p>
 * 
 * <p>
 * The default output respects
 * <a href="http://www.ietf.org/rfc/rfc4180.txt">rfc4180</a>. The caller can
 * also adjust the separator to something like semicolon or pipe instead of
 * comma. In such a case, all fields are escaped using the new separator. See
 * <a href="https://en.wikipedia.org/wiki/Delimiter-separated_values">Delimiter-
 * separated values</a> for more information.
 * </p>
 *
 * @author Dimitrios Michail
 * @since August 2016
 */
public class CSVExporter<V, E>
{
    private static final char DEFAULT_DELIMITER = ',';

    /**
     * Formats of the exporter.
     */
    public enum Format
    {
        /**
         * If set the exporter outputs the graph as an edge list
         */
        EDGE_LIST,

        /**
         * If set the exporter outputs the graph as an adjacency list
         */
        ADJACENCY_LIST,

        /**
         * If set the exporter outputs the graph as a matrix
         */
        MATRIX,
    }

    /**
     * Parameters that affect the behavior of the exporter.
     */
    public enum Parameter
    {
        /**
         * Whether to export node ids. Only valid for the {@link Format#MATRIX
         * MATRIX} format.
         */
        MATRIX_FORMAT_NODEID,
        /**
         * Whether to export edge weights. Only valid for the
         * {@link Format#MATRIX MATRIX} format.
         */
        MATRIX_FORMAT_EDGE_WEIGHTS,
        /**
         * Whether to export zero as edge weights for missing edges. Only valid
         * for the {@link Format#MATRIX MATRIX} format.
         */
        MATRIX_FORMAT_ZERO_WHEN_NO_EDGE,
    }

    private final VertexNameProvider<V> vertexIDProvider;
    private final Set<Parameter> parameters;
    private Format format;
    private char delimiter;

    /**
     * Creates a new CSVExporter with {@link Format#ADJACENCY_LIST} format and
     * integer name provider for the vertices.
     */
    public CSVExporter()
    {
        this(
            new IntegerNameProvider<>(),
            Format.ADJACENCY_LIST,
            DEFAULT_DELIMITER);
    }

    /**
     * Creates a new CSVExporter with integer name providers for the vertices.
     * 
     * @param format the format to use
     */
    public CSVExporter(Format format)
    {
        this(new IntegerNameProvider<>(), format, DEFAULT_DELIMITER);
    }

    /**
     * Creates a new CSVExporter with integer name providers for the vertices.
     * 
     * @param format the format to use
     * @param delimiter delimiter to use
     */
    public CSVExporter(Format format, char delimiter)
    {
        this(new IntegerNameProvider<>(), format, delimiter);
    }

    /**
     * Constructs a new CSVExporter with the given ID providers and format.
     *
     * @param vertexIDProvider for generating vertex IDs. Must not be null.
     * @param format the format to use
     * @param delimiter delimiter to use
     */
    public CSVExporter(
        VertexNameProvider<V> vertexIDProvider,
        Format format,
        char delimiter)
    {
        if (vertexIDProvider == null) {
            throw new IllegalArgumentException(
                "Vertex id provider cannot be null");
        }
        this.vertexIDProvider = vertexIDProvider;
        this.format = format;
        if (!DSVUtils.isValidDelimiter(delimiter)) {
            throw new IllegalArgumentException(
                "Character cannot be used as a delimiter");
        }
        this.delimiter = delimiter;
        this.parameters = new HashSet<>();
    }

    /**
     * Exports a graph
     *
     * @param g the graph
     * @param writer the writer
     */
    public void exportGraph(Graph<V, E> g, Writer writer)
    {
        PrintWriter out = new PrintWriter(writer);
        switch (format) {
        case EDGE_LIST:
            exportAsEdgeList(g, out);
            break;
        case ADJACENCY_LIST:
            exportAsAdjacencyList(g, out);
            break;
        case MATRIX:
            exportAsMatrix(g, out);
            break;
        }
        out.flush();
    }

    /**
     * Return if a particular parameter of the exporter is enabled
     * 
     * @param p the parameter
     * @return {@code true} if the parameter is set, {@code false} otherwise
     */
    public boolean isParameter(Parameter p)
    {
        return parameters.contains(p);
    }

    /**
     * Set the value of a parameter of the exporter
     * 
     * @param p the parameter
     * @param value the value to set
     */
    public void setParameter(Parameter p, boolean value)
    {
        if (value) {
            parameters.add(p);
        } else {
            parameters.remove(p);
        }
    }

    /**
     * Get the format of the exporter
     * 
     * @return the format of the exporter
     */
    public Format getFormat()
    {
        return format;
    }

    /**
     * Set the format of the exporter
     * 
     * @param format the format to use
     */
    public void setFormat(Format format)
    {
        this.format = format;
    }

    /**
     * Get the delimiter (comma, semicolon, pipe, etc).
     * 
     * @return the delimiter
     */
    public char getDelimiter()
    {
        return delimiter;
    }

    /**
     * Set the delimiter (comma, semicolon, pipe, etc).
     * 
     * @param delimiter the delimiter to use
     */
    public void setDelimiter(char delimiter)
    {
        if (!DSVUtils.isValidDelimiter(delimiter)) {
            throw new IllegalArgumentException(
                "Character cannot be used as a delimiter");
        }
        this.delimiter = delimiter;
    }

    private void exportAsEdgeList(Graph<V, E> g, PrintWriter out)
    {
        for (E e : g.edgeSet()) {
            String s = DSVUtils.escapeDSV(
                vertexIDProvider.getVertexName(g.getEdgeSource(e)),
                delimiter);
            String t = DSVUtils.escapeDSV(
                vertexIDProvider.getVertexName(g.getEdgeTarget(e)),
                delimiter);
            out.println(s + delimiter + t);
        }
    }

    private void exportAsAdjacencyList(Graph<V, E> g, PrintWriter out)
    {
        if (g instanceof DirectedGraph<?, ?>) {
            for (V v : g.vertexSet()) {
                out.print(vertexIDProvider.getVertexName(v));
                for (E e : ((DirectedGraph<V, E>) g).outgoingEdgesOf(v)) {
                    V w = Graphs.getOppositeVertex(g, e, v);
                    out.print(delimiter);
                    out.print(
                        DSVUtils.escapeDSV(
                            vertexIDProvider.getVertexName(w),
                            delimiter));
                }
                out.println();
            }
        } else {
            for (V v : g.vertexSet()) {
                out.print(
                    DSVUtils.escapeDSV(
                        vertexIDProvider.getVertexName(v),
                        delimiter));
                for (E e : g.edgesOf(v)) {
                    V w = Graphs.getOppositeVertex(g, e, v);
                    out.print(delimiter);
                    out.print(
                        DSVUtils.escapeDSV(
                            vertexIDProvider.getVertexName(w),
                            delimiter));
                }
                out.println();
            }
        }
    }

    private void exportAsMatrix(Graph<V, E> g, PrintWriter out)
    {
        boolean exportNodeId = parameters
            .contains(Parameter.MATRIX_FORMAT_NODEID);
        boolean exportEdgeWeights = parameters
            .contains(Parameter.MATRIX_FORMAT_EDGE_WEIGHTS);
        boolean zeroWhenNoEdge = parameters
            .contains(Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE);

        if (exportNodeId) {
            for (V v : g.vertexSet()) {
                out.print(delimiter);
                out.print(
                    DSVUtils.escapeDSV(
                        vertexIDProvider.getVertexName(v),
                        delimiter));
            }
            out.println();
        }
        int n = g.vertexSet().size();
        for (V v : g.vertexSet()) {
            if (exportNodeId) {
                out.print(
                    DSVUtils.escapeDSV(
                        vertexIDProvider.getVertexName(v),
                        delimiter));
                out.print(delimiter);
            }
            int i = 0;
            for (V u : g.vertexSet()) {
                E e = g.getEdge(v, u);
                if (e == null) {
                    if (zeroWhenNoEdge) {
                        out.print("0");
                    }
                } else {
                    if (exportEdgeWeights) {
                        out.print(g.getEdgeWeight(e));
                    } else {
                        out.print("1");
                    }
                }
                if (i++ < n - 1) {
                    out.print(delimiter);
                }
            }
            out.println();
        }
    }

}

// End CSVExporter.java
