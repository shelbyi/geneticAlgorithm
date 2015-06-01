/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import net.hft.algodat.framework.geneticalgorithm.annotations.Convergence;
import net.hft.algodat.framework.geneticalgorithm.annotations.UpperBorder;
import net.hft.algodat.framework.geneticalgorithm.annotations.StopCriteria;
import net.hft.algodat.framework.geneticalgorithm.annotations.TimeBorder;
import net.hft.algodat.framework.geneticalgorithm.entities.Individual;
import net.hft.algodat.framework.geneticalgorithm.entities.Job;
import net.hft.algodat.framework.geneticalgorithm.entities.Resource;
import net.hft.algodat.framework.geneticalgorithm.enums.TypeOfRuntime;
import net.hft.algodat.framework.geneticalgorithm.functions.Crossover;
import net.hft.algodat.framework.geneticalgorithm.functions.Mutation;
import net.hft.algodat.framework.geneticalgorithm.functions.Replacement;
import net.hft.algodat.framework.geneticalgorithm.functions.Selection;
import net.hft.algodat.framework.geneticalgorithm.services.JobParser;
import net.hft.algodat.framework.geneticalgorithm.services.ResourceService;
import net.hft.algodat.framework.geneticalgorithm.services.SCInvestigator;
import net.hft.algodat.framework.geneticalgorithm.utilities.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jan
 */
public final class GeneticAlgorithm implements Algorithm {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeneticAlgorithm.class);

    private Map<String, String> properties = new HashMap<>();

    private int populationSize;
    private URL jobFilepath;
    private URL resFilepath;

    // Parts
    private Selection selectionMethod;
    private Mutation mutationMethod;
    private Crossover crossoverMethod;
    private Replacement replacementMethod;
    private TypeOfRuntime type;

    @StopCriteria
    @UpperBorder(value = 5)
    private int maxFitnessInIteration;

    @StopCriteria
    @UpperBorder(value = 40)
    private int amountOfIterations;

    @StopCriteria
    @Convergence(sizeOfConvergence = 200)
    private List<Individual> amountOfConvergence;

    @StopCriteria
    @TimeBorder(minutes = 0, seconds = 45)
    private Date currentRuntime;

    @Override
    public void run() {
        LOGGER.info("GeneticAlgorithm is started...");
        initGA();
        if (type != null) {
            if (type == TypeOfRuntime.productive) {
                LOGGER.warn("=== Prductive runtime detected! Validation of Data enabled ===");
                validateConfiguration();
            } else if (type == TypeOfRuntime.test) {
                LOGGER.warn("=== Testruntime detected! Validation of Data skipped. \n Please note that the framework is not able to detect errors in modules in this mode ===");
            }
        } else {
            LOGGER.error("=== Runtimetype of the Framework must be set. Program stopped ===");
            System.exit(1);
        }

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
        List<Individual> population = null;

        try {
            jobs = JobParser.toJobs(jobFile);
            res = ResourceService.toResourceList(resFile);
            LOGGER.info("Jobs and Resources loaded");
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.toString());
        }

        // Calculate Predecessors per job
        LOGGER.info("Calculating Predecessors. This can take a while...");
        for (Job job : jobs) {
            job.calculatePredecessors(jobs);
        }

        // Create and initialize population
        population = new ArrayList<Individual>();
        for (int g = 0; g < this.populationSize; g++) {
            Individual i = new Individual();
            i.initializeJobList(jobs);
            i.decodeJobList(jobs, res);
            population.add(i);
        }
        LOGGER.info("Population created");

        // GA- mainpart
        while (true) {
            List<Individual> populationAfterSelection = this.selectionMethod.executeSelection(population);
            this.crossoverMethod.executeCrossover(populationAfterSelection);
            this.mutationMethod.executeMutation(populationAfterSelection);

            this.replacementMethod.executeReplacement(population);

            this.maxFitnessInIteration = Utilities.getFittestInPopulation(population).getFitness();
            this.amountOfConvergence = population;
            this.amountOfIterations++;
        }
    }

    private void initGA() {
        File propertiesFile = null;
        try {
            propertiesFile = new File(getClass().getResource("/config/GA.properties").toURI());
        } catch (URISyntaxException ex) {
            LOGGER.error(ex.toString());
        }

        this.properties = Utilities.loadProperties(propertiesFile);
        this.populationSize = Integer.decode(properties.get("populationSize"));

        try {
            this.amountOfIterations = this.getClass().getDeclaredField("amountOfIterations").getAnnotation(UpperBorder.class).value();
        } catch (NoSuchFieldException | SecurityException ex) {
            LOGGER.error(ex.toString());
        }
        this.jobFilepath = getClass().getResource(properties.get("jobList"));
        this.resFilepath = getClass().getResource(properties.get("resourceList"));
        this.type = TypeOfRuntime.valueOf(properties.get("runtime"));
        this.amountOfConvergence = new ArrayList<>();
        LOGGER.info("Filebased initializsation completed...");
    }

    private void validateConfiguration() {
        if (this.populationSize < 150) {
            throw new IllegalArgumentException("PopulationSize must be higher than 150!");
        }
        if (this.amountOfIterations <= 39) {
            throw new IllegalArgumentException("Iterations must be higher than 40!");
        }
        if (selectionMethod == null) {
            throw new IllegalArgumentException("SelectionMethod may not be null");
        }
        if (crossoverMethod == null) {
            throw new IllegalArgumentException("CrossoverMethod may not be null");
        }
        if (mutationMethod == null) {
            throw new IllegalArgumentException("MutationMethod may not be null");
        }
        if (replacementMethod == null) {
            throw new IllegalArgumentException("ReplacementMethod may not be null");
        }
        LOGGER.info("Validation completed...");
    }

    public void setSelectionMethod(Selection selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    public void setMutationMethod(Mutation mutationMethod) {
        this.mutationMethod = mutationMethod;
    }

    public void setCrossoverMethod(Crossover crossoverMethod) {
        this.crossoverMethod = crossoverMethod;
    }

    public void setReplacementMethod(Replacement replacementMethod) {
        this.replacementMethod = replacementMethod;
    }

}
