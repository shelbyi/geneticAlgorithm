/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.crossovermethods;

import java.util.ArrayList;
import java.util.List;
import net.hft.algodat.framework.geneticalgorithm.base.GeneticAlgorithm;
import net.hft.algodat.framework.geneticalgorithm.entities.Individual;
import net.hft.algodat.framework.geneticalgorithm.functions.Crossover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Permutation based encoding using one point crossover 
 *
 * @author Alla
 */
public class CrossoverFunction implements Crossover{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GeneticAlgorithm.class);

    public void executeCrossover(List<Individual> population)
    {
        int probability = 100; // in %
        LOGGER.info("Crossover permutation is started." 
                + " Population size: " + population.size()
        + ". Probability: " + probability + "%");
        
        int indexOfIndividual = 0;
        while(indexOfIndividual < population.size()-1)
        {
            onePointCrossover(population.get(indexOfIndividual), population.get(indexOfIndividual+1));
            
            indexOfIndividual += 2; // make use of probability here
        } 
        LOGGER.info("Crossover permutation is finished");        
    }   
    
    //one point crossover operation
    private void onePointCrossover(Individual mother, Individual father)
    {
        // compute crossover point
        int crossPoint = getRandomIntegerInBetween(1, mother.jobListe.length-1);
      
        // create son and daugther
        Individual daugther = createChildFromParents(mother, father, crossPoint);
        Individual son = createChildFromParents(father, mother, crossPoint);

        // substitute parents by children
        for (int i=0; i < mother.jobListe.length;i++)
        {
            mother.jobListe[i] = daugther.jobListe[i];
            father.jobListe[i] = son.jobListe[i];
        }
    }

    // creates child from parent1 and parent2 individuals
    // crosspoint is passed as parameter
    // child[0 .. crossPoint-1] jobs copied from parent1
    // child[crossPoint .. end] feasible jobs copied from parent2
    // if there are not enough feasible jobs in parent2 then child=parent1
    private Individual createChildFromParents(Individual parent1, Individual parent2, int crossPoint)
    {
        Individual child = new Individual();
        child.reproduce(parent1); // copy parent1 to child

        int indexInParent = 0;
        int indexInChild = crossPoint;

        // fill child after crosspoint from parent2
        while (indexInChild < child.jobListe.length)
        {
            // search for first job index in parent2 which is not yet defined in child
           for(; indexInParent < parent2.jobListe.length; indexInParent++)
           {
                if (!isJobNumberAlreadyExist(child.jobListe, 0, indexInChild-1, parent2.jobListe[indexInParent]))
                {
                    // insert found parent job index into child
                    child.jobListe[indexInChild++] = parent2.jobListe[indexInParent];
                    break;
                }
           }

           if (indexInParent >= parent2.jobListe.length)
           {
               // stop when parent2 job list processed completelly
               break;
           }
        }
        // if whole child's job list is filled return child
        if (indexInChild >= child.jobListe.length)
            return child;

        // not enough feasible jobs in parent2         
        return parent1;
    }

    // Checking if a job number is defined in a specified part of list.Range: [startIndex..endIndex]
    private boolean isJobNumberAlreadyExist(int[] jobListe, int startIndex, int endIndex, int jobNumber)
    {
        boolean flag = false;
        for(int i=startIndex; i<=endIndex; i++)
        {
            if(jobListe[i] == jobNumber)
                return true;  
        }
        return false;
    } 
    
    private static int getRandomIntegerInBetween(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }     
    
    private String jobListToString(Individual individual)
    {
        String outputText = "";
        for (int jobCode : individual.jobListe)
        {
            outputText += " " + jobCode; 
        }
        return outputText;
    }    
}
