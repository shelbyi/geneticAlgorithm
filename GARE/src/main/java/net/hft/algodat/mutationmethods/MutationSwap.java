package net.hft.algodat.mutationmethods;

import java.util.ArrayList;
import java.util.List;

import net.hft.algodat.framework.geneticalgorithm.entities.Individual;
import net.hft.algodat.framework.geneticalgorithm.entities.Job;
import net.hft.algodat.framework.geneticalgorithm.functions.Mutation;
import net.hft.algodat.framework.geneticalgorithm.services.JobService;

public class MutationSwap implements Mutation {

	@Override
	public void executeMutation(List<Individual> population, float prob, int amountOfChanges) {
		
		// iterate over whole population and may mutate a individual
		for(int i = 0; i < population.size(); i++) {
			
			int[] jobListe = population.get(i).getJobListe();
			List<Job> jobs = population.get(i).getJobs();
			
			double random = Math.random();
			if (random < prob) {
				
				for(int j = 0; j < amountOfChanges; j++) {
					
					boolean swapFeasible = false;
					int pos1 = 1;
					int pos2 = 2;
					int nr1;
					int nr2;
					
					while(!swapFeasible){
						
						pos1 = 1+(int)((jobListe.length-2)*Math.random());
						pos2 = pos1+1;
						nr1  = jobListe[pos1];
						nr2  = jobListe[pos2];
						
						Job job = JobService.getJob(jobs, nr1);
						ArrayList<Integer> nachfolger = job.getPredecessors();
						
						if(!nachfolger.contains(nr2))
							swapFeasible = true;
					}
					
					int tmp = jobListe[pos1];
					jobListe[pos1] = jobListe[pos2];
					jobListe[pos2] = tmp;
				}		
	        }
		}
	}
}
