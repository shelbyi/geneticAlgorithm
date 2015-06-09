/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.selectionmethods;

import java.util.ArrayList;
import java.util.List;

import net.hft.algodat.framework.geneticalgorithm.entities.Individual;
import net.hft.algodat.framework.geneticalgorithm.functions.Selection;
import net.hft.algodat.utilities.Utilities;

/**
 * Tournament Selection with tournament size 2,chooses best parents by comparing
 * the fitness
 *
 * @author AVATSP
 */
public class TournamentSelection implements Selection {
//Random selection from the population including the min and max Value

    @Override
    public List<Individual> executeSelection(List<Individual> population) {
        List<Individual> selectedList = new ArrayList<>();

        int tournamentSize = 2;
//        Returns half the pupolation size
        for (int j = 0; j < population.size() / 4; j++) {
            for (int i = 0; i < tournamentSize; i++) {
//                Randomly selects index of first parent 
                int selectedIndexForParentOne = Utilities.getRandomInBetween(population);
//                Gets the fitness of the first parent
                int fitnessOfParentOne = population.get(selectedIndexForParentOne).getFitness();
//                Randomly selects the index of second parent
                int selectedIndexForParentTwo = Utilities.getRandomInBetween(population);
//                Checking if both selected parents are the same
                while (true) {
                    if (selectedIndexForParentTwo == selectedIndexForParentOne) {
                        selectedIndexForParentTwo = Utilities.getRandomInBetween(population);
                    } else {
                        break;
                    }
                }
                int fitnessOfParentTwo = population.get(selectedIndexForParentOne).getFitness();
//selecting the best fit parents by comparing the fitness   
                if (fitnessOfParentOne > fitnessOfParentTwo) {
                    System.out.println(fitnessOfParentOne);
                    selectedList.add(population.get(selectedIndexForParentOne));
                } else {
                    selectedList.add(population.get(selectedIndexForParentTwo));
                }

            }

        }
        return selectedList;

    }

}
