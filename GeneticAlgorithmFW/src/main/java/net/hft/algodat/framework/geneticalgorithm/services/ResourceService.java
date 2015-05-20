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
import net.hft.algodat.framework.geneticalgorithm.entities.Resource;

/**
 *
 * @author Jan
 */
public class ResourceService {

    public static List<Resource> toResourceList(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        List<Resource> resources = new ArrayList<>(4);
        boolean found = false;
        while (scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            if (nextLine.equals("")) {
                continue;
            }
            Scanner next = new Scanner(nextLine);
            String nextString = next.next();

            if (!found && nextString.equals("R")) {
                found = true;
                continue;
            }
            if (found) {
                resources.add(0, new Resource(Integer.parseInt(nextString), 1));
                if (next.hasNext()) {
                    resources.add(1, new Resource(Integer.parseInt(nextString), 2));
                    if (next.hasNext()) {
                        resources.add(2, new Resource(Integer.parseInt(nextString), 3));
                        if (next.hasNext()) {
                            resources.add(3, new Resource(Integer.parseInt(nextString), 4));
                        }
                    }
                }
                break;
            }
        }
        return resources;
    }

}
