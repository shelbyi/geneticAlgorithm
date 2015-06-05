/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.services;

import java.util.List;
import net.hft.algodat.framework.geneticalgorithm.entities.Job;

/**
 *
 * @author Jan
 */
public class JobService {

    public static Job getJob(List<Job> jobs, int number) {
        Job result = null;
        for (Job j : jobs){
            if (j.getNumber() == number){
                result = j;
                break;
            }
        }
        return result;
    }


}
