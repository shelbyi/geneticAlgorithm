/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.runtime.main;

import net.hft.algodat.framework.geneticalgorithm.base.GeneticAlgorithm;
import net.hft.alogdat.selectionmethods.TournamentSelection;

/**
 *
 * @author Jan
 */
public class Main {
    
    public static void main(String[] args) {
        GeneticAlgorithm geneticAlgo = new GeneticAlgorithm();
        
        geneticAlgo.setSelectionMethod(new TournamentSelection());
        geneticAlgo.setCrossoverMethod(null);
        geneticAlgo.setMutationMethod(null);
        geneticAlgo.setReplacementMethod(null);
        
        geneticAlgo.run();
    }
    
}
