package net.hft.algodat.framework.geneticalgorithm.entities;

import java.util.ArrayList;
import java.util.List;

public class Job {

    // Number of a job
    private int number;

    // successors; each element contains the job-number (int)
    private ArrayList<Integer> successors;

    // predecessors; each element contains the job-number (int)
    private ArrayList<Integer> predecessors;

    // duration of a job
    private int duration;

    // needed resource capacities  
    // verwendeteResourcen[0] --> capacities of resource R1
    // verwendeteResourcen[1] --> capacities of resource R2
    // verwendeteResourcen[2] --> capacities of resource R3
    // verwendeteResourcen[3] --> capacities of resource R4
    int[] verwendeteResourcen;

    public Job(int nummer, ArrayList<Integer> nachfolger, int duration, int[] verwendeteResourcen) {
        this.number = nummer;
        this.successors = nachfolger;
        this.duration = duration;
        this.verwendeteResourcen = verwendeteResourcen;
        this.predecessors = new ArrayList<Integer>();
    }

    public int getNumber() {
        return number;
    }

    public ArrayList<Integer> getSuccessors() {
        return successors;
    }

    public ArrayList<Integer> getPredecessors() {
        return predecessors;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmountOfSuccessors() {
        return successors.size();
    }

    public void calculatePredecessors(List<Job> jobs) {
        for (int i = 0; i < jobs.size(); i++) {
            for (int j = 0; j < jobs.get(i).getSuccessors().size(); j++) {
                if (this.number == jobs.get(i).getSuccessors().get(j)) {
                    this.predecessors.add(jobs.get(i).getNumber());
                }
            }
        }
    }

    public int verwendeteResource(int i) {
        if (i >= 0 && i <= 3) {
            return verwendeteResourcen[i];
        } else {
            throw new IllegalArgumentException("Parameter muss zwischen 0 und 3 sein!");
        }
    }

}
