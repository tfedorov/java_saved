package com.tfedorov.social.clustering;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.tfedorov.social.clustering.jgrapht.MinimumCutCalculator;
import com.tfedorov.social.clustering.jgrapht.SocialWeightedEdge;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleWeightedGraph;

import au.com.bytecode.opencsv.CSVReader;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.graph.JGraphSimpleLayout;

public class MinimumCutClustererClient {

  @SuppressWarnings("rawtypes")
  private JGraphModelAdapter m_jgAdapter;
  private JFrame frame;

  public void showUI(final SimpleWeightedGraph<String, SocialWeightedEdge> graph) {
    // create a visualization using JGraph, via an adapter
    final JGraph jgraph = createUIGraph(graph);

    frame = new JFrame();
    final JPanel panel = new JPanel(new BorderLayout());
    JPanel controlPanel = new JPanel();
    final JPanel graphPanel = new JPanel();
    graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.X_AXIS));
    JButton button = new JButton("Run");
    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            // graph.removeEdge(e11);
            JGraph converterGraph = createUIGraph(graph);
            graphPanel.add(new JScrollPane(converterGraph));
            graphPanel.updateUI();
            layoutGraph(converterGraph);

          }
        });

        // m_jgAdapter.remove(new Object[]{e11});
      }
    });
    controlPanel.add(button);
    JScrollPane graph1 = new JScrollPane(jgraph);
    graphPanel.add(graph1);
    panel.add(graphPanel, BorderLayout.CENTER);
    panel.add(controlPanel, BorderLayout.SOUTH);
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.pack();
    frame.setSize(1400, 800);
    frame.setVisible(true);

    layoutGraph(jgraph);
  }

  private void layoutGraph(final JGraph jgraph) {
    SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        // JGraphLayout layout = new JGraphFastOrganicLayout();
        // JGraphFacade facade = new JGraphFacade(jgraph);
        // layout.run(facade);

        final JGraphSimpleLayout graphLayout =
            new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE, 200, 200);
        final JGraphFacade graphFacade = new JGraphFacade(jgraph);
        graphLayout.run(graphFacade);
        @SuppressWarnings("rawtypes")
        final Map nestedMap = graphFacade.createNestedMap(true, true);
        jgraph.getGraphLayoutCache().edit(nestedMap);

        frame.getContentPane().invalidate();
      }
    });
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private JGraph createUIGraph(final SimpleWeightedGraph<String, SocialWeightedEdge> graph) {
    m_jgAdapter = new JGraphModelAdapter(graph);
    final JGraph jgraph = new JGraph(m_jgAdapter);
    return jgraph;
  }

  public static void main(String[] args) throws IOException {
    // List<WordsPair> wordsPairs = generateRandomList(100);
    List<CoOccurrenceInfo> wordsPairs =
        loadFromCsvFile("src/test/resources/AXS-3.csv");
    ClustersCalculator minimumCut = new MinimumCutCalculator();
    System.out.println("WordsClusterCalculatorOnMinimumCut.main()"
        + minimumCut.calculate(2, wordsPairs));
  }

  public static List<CoOccurrenceInfo> loadFromCsvFile(String string) throws IOException {
    List<CoOccurrenceInfo> pairs = new ArrayList<CoOccurrenceInfo>();
    CSVReader reader = new CSVReader(new FileReader(string));
    String[] nextLine;
    while ((nextLine = reader.readNext()) != null) {
      // nextLine[] is an array of values from the line
      int countConnections = Integer.parseInt(nextLine[0]);
      String[] words = nextLine[1].split(" ");
      // System.out.println("WordsClusterCalculatorOnMinimumCut.loadFromCsvFile()" + words[0] + " "
      // + words[1]);
      CoOccurrenceInfo wordsPair = new CoOccurrenceInfo(words[0], words[1], countConnections);
      pairs.add(wordsPair);
    }
    reader.close();
    return pairs;
  }

  public static List<CoOccurrenceInfo> generateRandomList(int size) {
    Random random = new Random(3875345);
    List<CoOccurrenceInfo> list = new ArrayList<CoOccurrenceInfo>();

    for (int i = 0; i < size * size - size; i++) {
      int firstWordIndex = random.nextInt(size * 2) + 1;
      int secondWordIndex = random.nextInt(size * 3) + 1;
      int cooocurences = random.nextInt(firstWordIndex + secondWordIndex);
      String firstWord = "word" + firstWordIndex;
      String secondWord = "word" + secondWordIndex;
      list.add(new CoOccurrenceInfo(firstWord, secondWord, cooocurences));
    }
    return list;
  }



}
