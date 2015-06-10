package net.hft.algodat.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.hft.algodat.framework.geneticalgorithm.base.GeneticAlgorithm;
import net.hft.algodat.framework.geneticalgorithm.entities.Individual;
import net.hft.algodat.framework.geneticalgorithm.entities.Job;
import net.hft.algodat.framework.geneticalgorithm.entities.Resource;
import net.hft.algodat.framework.geneticalgorithm.functions.Mutation;
import net.hft.algodat.framework.geneticalgorithm.services.JobParser;
import net.hft.algodat.framework.geneticalgorithm.services.ResourceService;
import net.hft.algodat.framework.geneticalgorithm.utilities.Utilities;
import net.hft.algodat.mutationmethods.MutationSwap;

import org.junit.Test;

public class MutationSwapTest {

	private Map<String, String> properties = new HashMap<>();

    private URL jobFilepath;
    private URL resFilepath;
    
    private List<Individual> population = null;
	
	public void initialization(int populationSize) {
		
		File propertiesFile = null;
        try {
            propertiesFile = new File(getClass().getResource("/config/GA.properties").toURI());
        } catch (URISyntaxException ex) {
        	System.out.println(ex.toString());
        }

        this.properties = Utilities.loadProperties(propertiesFile);

        this.jobFilepath = getClass().getResource(properties.get("jobList"));
        this.resFilepath = getClass().getResource(properties.get("resourceList"));

        System.out.println("Filebased initializsation completed...");
        
     // Load initial data
        File jobFile = null;
        File resFile = null;
        try {
            jobFile = new File(jobFilepath.toURI());
            resFile = new File(resFilepath.toURI());
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Job> jobs = null;
        List<Resource> res = null;

        try {
            jobs = JobParser.toJobs(jobFile);
            res = ResourceService.toResourceList(resFile);
            System.out.println("Jobs and Resources loaded");
        } catch (FileNotFoundException ex) {
        	System.out.println(ex.toString());
        }

        // Calculate Predecessors per job
        System.out.println("Calculating Predecessors. This can take a while...");
        for (Job job : jobs) {
            job.calculatePredecessors(jobs);
        }

     // Create and initialize population
        population = new ArrayList<Individual>();
        for (int g = 0; g < populationSize; g++) {
            Individual i = new Individual();
            i.initializeJobList(jobs);
            i.decodeJobList(jobs, res);
            population.add(i);
        }
        System.out.println("Population created");
	}
	
	@Test
	public void test() {
		
		int populationSize = 1;
		initialization(populationSize);
		
		float probMutation = 1.0f;
		int amountMutations = 2;
		
		for(Individual individual : population) {
			System.out.println(Arrays.toString(individual.getJobListe()));	
		}
		
		Mutation mutationMethod = new MutationSwap();
		mutationMethod.executeMutation(population, probMutation, amountMutations);
        
		System.out.println("Mutated individuals");
		
		for(Individual individual : population) {
			System.out.println(Arrays.toString(individual.getJobListe()));
		}
	}

}
