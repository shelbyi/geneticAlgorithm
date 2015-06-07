/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.replacementmethods;

import java.util.ArrayList;
import java.util.List;

import net.hft.algodat.framework.geneticalgorithm.entities.Individual;
import net.hft.algodat.framework.geneticalgorithm.functions.Replacement;
import net.hft.algodat.framework.geneticalgorithm.utilities.Utilities;
import net.hft.algodat.selectionmethods.TournamentSelection;


/**
 * Replacement using elitist recombination strategy
 *
 * @author MCHOKOEVA
 */
public class ReplacementStrategy implements Replacement {

    @Override
    public void executeReplacement(List<Individual> population) {
    	/*
    	 * 1.randomly select 2 individuals of the population
    	 */
    	int selectedIndexForParentOne = Utilities.getRandomInBetween(population);	
        int selectedIndexForParentTwo = Utilities.getRandomInBetween(population);
        
        //make sure that parents are different 
        while (selectedIndexForParentTwo == selectedIndexForParentOne) {
                selectedIndexForParentTwo = Utilities.getRandomInBetween(population);
        }
        
        //put parents in a list
        List<Individual> parentsList = new ArrayList<Individual>();
        parentsList.add(population.get(selectedIndexForParentOne));
        parentsList.add(population.get(selectedIndexForParentTwo));
        
        int fitnessOfParentOne = population.get(selectedIndexForParentOne).getFitness();
        int fitnessOfParentTwo = population.get(selectedIndexForParentOne).getFitness();
        
        /*
         * 2. Create children using crossover and mutation
         */
        //TODO: uncomment this lines, once the Crossover is implemented
//        CrossoverFunction crossOverFkt = new CrossoverFunction();
//        crossOverFkt.executeCrossover(parentsList);
        
        /*
         * 3. Calculate fitness of offspring
         */
        int fitnessChild1 = parentsList.get(0).getFitness();
        int fitnessChild2 = parentsList.get(1).getFitness();
        
        /*
         * 4. aply replacement strategy; here: elitist recombination (take the 2 best indivuduals)
         */
        boolean isParentOneFitter = false;
        if(fitnessOfParentOne < fitnessOfParentTwo){
        	isParentOneFitter = true;
        }
        
        boolean isChildOneFitter = false;
        if(fitnessChild1 < fitnessChild2){
        	isChildOneFitter = true;
        }
        
        if(isParentOneFitter && isChildOneFitter){
        	if(fitnessChild1 < fitnessOfParentOne){
        		population.set(selectedIndexForParentOne, parentsList.get(0));
        	}
    		if(fitnessChild2 < fitnessOfParentTwo){
    			population.set(selectedIndexForParentTwo, parentsList.get(1));
    		}
        }else if(!isParentOneFitter && isChildOneFitter){
        	if(fitnessChild1 < fitnessOfParentTwo){
        		population.set(selectedIndexForParentTwo, parentsList.get(0));
        	}
        	if(fitnessChild2 < fitnessOfParentOne){
        		population.set(selectedIndexForParentOne, parentsList.get(1));
        	}
        }else if(isParentOneFitter && !isChildOneFitter){
        	if(fitnessChild2 < fitnessOfParentOne){
        		population.set(selectedIndexForParentOne, parentsList.get(1));
        	}
        	if(fitnessChild1 < fitnessOfParentTwo){
        		population.set(selectedIndexForParentTwo, parentsList.get(0));
        	}
        }else{
        	if(fitnessChild2 < fitnessOfParentTwo){
        		population.set(selectedIndexForParentTwo, parentsList.get(1));
        	}
        	if(fitnessChild1 < fitnessOfParentOne){
        		population.set(selectedIndexForParentOne, parentsList.get(0));
        	}
        }
        
    }

}

