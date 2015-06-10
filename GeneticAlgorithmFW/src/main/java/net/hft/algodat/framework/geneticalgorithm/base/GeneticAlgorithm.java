/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import net.hft.algodat.framework.geneticalgorithm.annotations.Convergence;
import net.hft.algodat.framework.geneticalgorithm.annotations.PartOfAlgorithm;
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
    
    float probStartUp;
    float probGA;
    
    int amountMutationStartUp;
    int amountMutationsGA;
    
    
    // Parts
    @PartOfAlgorithm
    private Selection selectionMethod;
    @PartOfAlgorithm
    private Mutation mutationMethod;
    @PartOfAlgorithm
    private Crossover crossoverMethod;
    @PartOfAlgorithm
    private Replacement replacementMethod;

    private TypeOfRuntime type;

    @StopCriteria
    @UpperBorder(value = 150)
    private int maxFitnessInIteration;

    @StopCriteria
    @UpperBorder(value = 50)
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
            throw new RuntimeException();
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
        boolean startUp = true;
        do {
            if (startUp) {
                this.mutationMethod.executeMutation(population, probStartUp, amountMutationStartUp);
                startUp = false;
            }
            List<Individual> populationAfterSelection = this.selectionMethod.executeSelection(population);

            this.crossoverMethod.executeCrossover(populationAfterSelection);
            this.mutationMethod.executeMutation(populationAfterSelection, probGA, amountMutationsGA);
            this.replacementMethod.executeReplacement(population);
/*
            if (this.crossoverMethod != null)
                this.crossoverMethod.executeCrossover(populationAfterSelection);
            if (this.mutationMethod != null)
                this.mutationMethod.executeMutation(populationAfterSelection);
            if (this.replacementMethod != null)
                this.replacementMethod.executeReplacement(population);
*/
            this.maxFitnessInIteration = Utilities.getFittestInPopulation(population).getFitness();
            this.amountOfConvergence = population;
            this.amountOfIterations++;
        } while (!SCInvestigator.isStopCriteraReached(this));
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
        this.amountMutationsGA = Integer.valueOf(properties.get("amountMutationGA"));
        this.amountMutationStartUp = Integer.valueOf(properties.get("amountMutationStartup"));
        this.probStartUp = Float.valueOf(properties.get("prob_startup"));
        this.probGA = Float.valueOf(properties.get("prob_GA"));

        LOGGER.info("Filebased initializsation completed...");
    }

    private void validateConfiguration() {
        SCInvestigator.validateConfiguration(this);
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
