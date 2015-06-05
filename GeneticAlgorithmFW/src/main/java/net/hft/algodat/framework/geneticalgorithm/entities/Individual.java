/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.entities;

import java.util.ArrayList;
import java.util.List;
import net.hft.algodat.framework.geneticalgorithm.services.JobService;

/**
 *
 * @author Jan
 */
public class Individual {

    public int[] jobListe;//genotype
    int[] schedule;//phänotype

    //example of the data structure:
    //jobListe: 1 3 6 2 4 5 7  ; that means, jobs should schedule by the order 1,3,...,7; the Jobs 1 and 7 are the dummy jobs
    //schedule: 0 0 0 1 2 4 6  ; that means, that job with number 6 starts at time 0; that job with number 4 starts at time 2; that dummy job with number 7 starts at time 6 which also means, that the project is finished 
    public void reproduce(Individual elter) {
        //generate a clone of elter, that means the jobListe of elter is copied
        jobListe = new int[elter.jobListe.length];
        for (int i = 0; i < jobListe.length; i++) {
            jobListe[i] = elter.jobListe[i];
        }
    }

    public void mutate(List<Job> jobs) {
        //make one feasible swap move on the jobListe

        boolean swapFeasible = false;
        int pos1 = 1;
        int pos2 = 2;
        int nr1;
        int nr2;

        while (!swapFeasible) {
            pos1 = 1 + (int) ((jobListe.length - 2) * Math.random());
            pos2 = pos1 + 1;
            nr1 = jobListe[pos1];
            nr2 = jobListe[pos2];

            Job j = JobService.getJob(jobs, nr1);
            ArrayList<Integer> nachfolger = j.getPredecessors();

            if (!nachfolger.contains(nr2)) {
                swapFeasible = true;
            }
        }

        int tmp = jobListe[pos1];
        jobListe[pos1] = jobListe[pos2];
        jobListe[pos2] = tmp;
    }

    public void initializeJobList(List<Job> jobs) {

        ArrayList<Job> eligibleJobs = new ArrayList<>();
        jobListe = new int[jobs.size()];

        // 1. Job to jobListe
        int count = 0;
        jobListe[count] = jobs.get(0).getNumber();
        count++;
        ArrayList<Integer> nachfolgerAkt = jobs.get(0).getSuccessors();
        for (Integer nachfolgerAkt1 : nachfolgerAkt) {
            eligibleJobs.add(JobService.getJob(jobs, nachfolgerAkt1));
        }

        while (count != jobs.size()) {
                Job min = eligibleJobs.get(0);
                int minDauer = eligibleJobs.get(0).getDuration();
                for (Job eligibleJob : eligibleJobs) {
                    if (eligibleJob.getDuration() < minDauer) {
                        minDauer = eligibleJob.getDuration();
                        min = eligibleJob;
                    }
                }

                jobListe[count] = min.getNumber();
                count++;
                eligibleJobs.remove(min);

                nachfolgerAkt = min.getSuccessors();
                for (int i = 0; i < nachfolgerAkt.size(); i++) {
                    Job aktuellerNachfolgerJob = JobService.getJob(jobs, nachfolgerAkt.get(i));
                    ArrayList<Integer> vorgaengerAkt = aktuellerNachfolgerJob.getPredecessors();
                    boolean alleVorgaenger = true;
                    for (int j = 0; j < vorgaengerAkt.size(); j++) {
                        boolean found = false;
                        for (int k = 0; k < jobListe.length; k++) {
                            if (jobListe[k] == vorgaengerAkt.get(j)) {
                                found = true;
                            }
                        }
                        if (!found) {
                            alleVorgaenger = false;
                            break;
                        }

                    }
                    if (alleVorgaenger) {
                        eligibleJobs.add(JobService.getJob(jobs, nachfolgerAkt.get(i)));
                    }
                }
            }

    }

    public void decodeJobList(List<Job> jobs, List<Resource> res) {
        //calculate the starting times of the jobs in the order of jobListe

        schedule = new int[jobListe.length];

        //calculate the maximum possible makespan "maxDauer" of the project
        int maxDuration = 0;
        for (Job job : jobs) {
            maxDuration += job.getDuration();
        }

        //initialize a resource table, which is used to check resource restrictions
        //each row is a resource
        //each column is a period
        //so the following resource table contains for each resource and each period the amount of available capacities
        int[][] resourcenTableau = new int[res.size()][maxDuration];

        for (int i = 0; i < resourcenTableau.length; i++) {
            for (int j = 0; j < resourcenTableau[i].length; j++) {
                resourcenTableau[i][j] = res.get(i).maxVerfuegbarkeit();
            }
        }

        for (int i = 0; i < jobListe.length; i++) {
            int nr = jobListe[i];

            Job j = JobService.getJob(jobs, nr);
            int p1 = earliestPossibleStarttime(j, jobs);
            int p2 = starttime(j, p1, resourcenTableau);
            actualizeResources(j, resourcenTableau, p2);

            schedule[i] = p2;
        }

    }

    public int getFitness() {
        //the start time of last job (= dummy job) is the makespan of the project and thus the fitness of the individual
        return schedule[schedule.length - 1];
    }

    public int earliestPossibleStarttime(Job j, List<Job> jobs) {
        // calculate the time, when all successors of j have been finished

        ArrayList<Integer> vorgaenger = j.getPredecessors();

        int earliestStart = 0;

        for (int i = 0; i < vorgaenger.size(); i++) {
            Job vorg = JobService.getJob(jobs, vorgaenger.get(i));

            for (int k = 0; k < jobListe.length; k++) {
                if (jobListe[k] == vorg.getNumber()) {
                    if ((schedule[k] + vorg.getDuration()) > earliestStart) {
                        earliestStart = schedule[k] + vorg.getDuration();
                    }
                }
            }
        }
        return earliestStart;
    }

    public int starttime(Job j, int p1, int[][] resTab) {
        // Prüfen, ob ab diesem Zeitpunkt genügend resourcen für die Dauer des Jobs vorhanden sind
        int[] verwendeteResourcen = j.verwendeteResourcen;
        boolean genug = true;
        int count = 0;
        do {
            genug = true;
            if (count != 0) {
                p1++;
            }
            for (int k = 0; k < resTab.length; k++) {
                for (int i = p1; i < (p1 + j.getDuration()); i++) {
                    if (resTab[k][i] < verwendeteResourcen[k]) {
                        genug = false;
                    }
                }
            }
            count++;
        } while (!genug);
        return p1;
    }

    public void actualizeResources(Job j, int[][] resTab, int start) {

        int[] verwendeteResourcen = j.verwendeteResourcen;
        for (int k = 0; k < resTab.length; k++) {
            for (int i = start; i < (start + j.getDuration()); i++) {
                resTab[k][i] -= verwendeteResourcen[k];
            }
        }
    }
}
