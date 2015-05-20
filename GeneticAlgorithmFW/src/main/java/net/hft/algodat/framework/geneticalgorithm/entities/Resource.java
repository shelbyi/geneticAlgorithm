/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Jan
 */
public class Resource {

    // Number of a resource
    private int nummer;

    // Maximum availability 
    private int maxVerfuegbarkeit;

    public Resource(int verfuegbarkeit, int nummer) {
        this.maxVerfuegbarkeit = verfuegbarkeit;
        this.nummer = nummer;
    }

    public int maxVerfuegbarkeit() {
        return maxVerfuegbarkeit;
    }

    public int nummer() {
        return nummer;
    }

    

}
