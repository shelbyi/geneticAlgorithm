/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.stream.Stream;
import net.hft.algodat.framework.geneticalgorithm.entities.Individual;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jan
 */
public abstract class Utilities {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Utilities.class);

    @SuppressWarnings("unchecked")
    public static <T> int getArrayListCapacity(ArrayList<T> arrayList) {
        Field field;
        try {
            try {
                field = ArrayList.class.getDeclaredField("elementData");
                field.setAccessible(true);
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
            final T[] elementData = (T[]) field.get(arrayList);
            return elementData.length;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> loadProperties(File file) {
        Map<String, String> propsOfGA = new HashMap<String, String>();
        Properties p = new Properties();

        try {
            p.load(new FileInputStream(file));
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Object key : p.keySet()) {
            propsOfGA.put((String) key, p.getProperty((String) key));
        }
        LOGGER.info("Properties from {} loaded successfully.", file.toString());
        return (HashMap<String, String>) propsOfGA;
    }

    public static Individual getFittestInPopulation(List<Individual> population) {
        Individual best = population.get(0);
        for (Individual i : population) {
            if (i.getFitness() >= best.getFitness()) {
                best = i;
            }
        }
        return best;
    }

}
