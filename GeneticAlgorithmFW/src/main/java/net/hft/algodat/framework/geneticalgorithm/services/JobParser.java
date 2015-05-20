/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import net.hft.algodat.framework.geneticalgorithm.entities.Job;
import net.hft.algodat.framework.geneticalgorithm.utilities.Utilities;

/**
 *
 * @author Jan
 */
public class JobParser {

    public static List<Job> toJobs(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);
        List<Job> jobs = new ArrayList<Job>();
        int index = 0;

        ArrayList<ArrayList<Integer>> successors = new ArrayList<ArrayList<Integer>>();
        boolean startJob = false;

        while (scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            if (nextLine.equals("")) {
                continue;
            }
            Scanner lineScanner = new Scanner(nextLine);
            String nextString = lineScanner.next();

            if (nextString.equals("jobs")) {
                boolean found = false;
                while (!found) {
                    if (lineScanner.next().equals("):")) {
                        int length = lineScanner.nextInt();
                        jobs = new ArrayList<>(length);
                        for (int i = 0; i <= length-1; i++) {
                            successors.add(new ArrayList<Integer>());
                        }
                        found = true;
                    }
                }
                continue;
            }
            if (nextString.equals("jobnr.")) {
                startJob = true;
            }
            if (startJob) {
                try {
                    if (lineScanner.hasNext()) {
                        lineScanner.next();
                        // second val is needed for correct shifting ;)
                        lineScanner.next();
                        while (lineScanner.hasNext()) {
                            int suc = Integer.valueOf(lineScanner.next());
                            successors.get(index).add(suc);
                        }
                        index++;

                        if (index == Utilities.getArrayListCapacity((ArrayList<Job>) jobs)) {
                            break;
                        } else {

                        }

                    }
                } catch (NumberFormatException e) {
                }
            }

        }
        index = 0;
        boolean startRequests = false;
        scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String next = scanner.nextLine();

            if (next.equals("")) {
                continue;
            }
            Scanner lineScanner = new Scanner(next);
            String nextString = lineScanner.next();
            if (!startRequests && lineScanner.hasNext()) {
                if (lineScanner.next().equals("mode")) {
                    startRequests = true;
                }
            }
            if (startRequests) {
                try {
                    int nummer = Integer.valueOf(nextString);
                    nextString = lineScanner.next();
                    int[] res = new int[4];
                    if (lineScanner.hasNext()) {
                        int duration = Integer.valueOf(lineScanner.next());
                        if (lineScanner.hasNext()) {
                            nextString = lineScanner.next();
                            res[0] = Integer.valueOf(nextString);
                            if (lineScanner.hasNext()) {
                                nextString = lineScanner.next();
                                res[1] = Integer.valueOf(nextString);
                                if (lineScanner.hasNext()) {
                                    nextString = lineScanner.next();
                                    res[2] = Integer.valueOf(nextString);
                                    if (lineScanner.hasNext()) {
                                        nextString = lineScanner.next();
                                        res[3] = Integer.valueOf(nextString);
                                    }
                                }
                            }

                        }
                        jobs.add(new Job(nummer, successors.get(index), duration, res));
                        index++;
                        if (index == Utilities.getArrayListCapacity((ArrayList<Job>) jobs)) {
                            break;
                        }
                    }

                } catch (NumberFormatException e) {
                }
            }

        }
        return jobs;
    }

}
