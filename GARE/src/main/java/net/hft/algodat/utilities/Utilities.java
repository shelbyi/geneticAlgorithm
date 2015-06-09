package net.hft.algodat.utilities;

import java.util.List;

import net.hft.algodat.framework.geneticalgorithm.entities.Individual;

public abstract class Utilities {
	

    public static int getRandomInBetween(List<Individual> population) {
        int min = 0;
        return getRandomIntegerInBetween(min, population.size() - 1);
    }

    private static int getRandomIntegerInBetween(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }


}
