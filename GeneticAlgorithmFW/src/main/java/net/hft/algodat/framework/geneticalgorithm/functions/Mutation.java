/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.functions;

import java.util.List;
import net.hft.algodat.framework.geneticalgorithm.entities.Individual;

/**
 *
 * @author Jan
 */
public interface Mutation {
    
    public void executeMutation(List<Individual> population);
}
