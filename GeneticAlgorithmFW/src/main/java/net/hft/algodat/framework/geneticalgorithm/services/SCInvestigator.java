/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.framework.geneticalgorithm.services;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import net.hft.algodat.framework.geneticalgorithm.annotations.Convergence;
import net.hft.algodat.framework.geneticalgorithm.annotations.PartOfAlgorithm;
import net.hft.algodat.framework.geneticalgorithm.annotations.UpperBorder;
import net.hft.algodat.framework.geneticalgorithm.annotations.StopCriteria;
import net.hft.algodat.framework.geneticalgorithm.annotations.TimeBorder;
import net.hft.algodat.framework.geneticalgorithm.base.GeneticAlgorithm;
import net.hft.algodat.framework.geneticalgorithm.entities.Individual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jan
 */
public abstract class SCInvestigator {

    private final static Logger LOGGER = LoggerFactory.getLogger(SCInvestigator.class);

    public static boolean isStopCriteraReached(GeneticAlgorithm instance) {

        boolean isReached = false;

        int maxValueBorder = 0;
        int maxValueAtRuntime = 0;
        List<Individual> convList = new ArrayList<>();

        for (Field field : GeneticAlgorithm.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(StopCriteria.class)) {
                if (field.isAnnotationPresent(UpperBorder.class)) {
                    try {
                        maxValueBorder = field.getAnnotation(UpperBorder.class).value();
                        maxValueAtRuntime = (Integer) field.get(instance);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        LOGGER.error(ex.toString());
                    }
                    if (maxValueAtRuntime >= maxValueBorder) {
                        if (maxValueAtRuntime == 0) {
                            LOGGER.warn("Fittness is after the iteration still 0. Please make sure that the fittnessfunction is correkt for the GA");
                        }
                        LOGGER.warn("Upper border is reached: {} for Criteria: {}. Bordervalue is {}", maxValueAtRuntime, field.getName(), maxValueBorder);
                        isReached = true;
                        break;
                    }
                }

                if (field.isAnnotationPresent(Convergence.class)) {
                    maxValueBorder = field.getAnnotation(Convergence.class).sizeOfConvergence();
                    try {
                        convList = (List<Individual>) field.get(instance);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        LOGGER.error(ex.toString());
                    }

                    List<Individual> smallList = convList.subList(convList.size() - maxValueBorder, convList.size());
                    // Calculate avg
                    int avg = 0;
                    for (Individual i : smallList) {
                        avg += i.getFitness();
                    }
                    avg = avg / smallList.size();

                    // check convergence
                    int convCounter = 0;

                    for (Individual i : smallList) {
                        if (i.getFitness() <= avg - 1 || i.getFitness() >= avg + 1) {
                            convCounter++;
                        }
                    }

                    if (convCounter >= smallList.size() * 0.75) {
                        LOGGER.warn("The last {} values seem to be convergent!", maxValueBorder);
                        isReached = true;
                        break;
                    }
                }

                if (field.isAnnotationPresent(TimeBorder.class)) {
                    // Getting values
                    int min = field.getAnnotation(TimeBorder.class).minutes();
                    int sec = field.getAnnotation(TimeBorder.class).seconds();
                    validateTimeValues(min, sec);

                    // Load local variable
                    Date dateField = null;
                    try {
                        dateField = (Date) field.get(instance);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        LOGGER.error(ex.toString());
                    }

                    if (dateField == null) {
                        dateField = new Date();
                    } else {
                        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
                        Date d = null;
                        try {
                            d = df.parse(dateField.toString());
                        } catch (ParseException ex) {
                            LOGGER.error(ex.toString());
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(d);
                        cal.add(Calendar.MINUTE, min);
                        cal.add(Calendar.SECOND, sec);

                        if (dateField.after(cal.getTime())) {
                            LOGGER.warn("Time- border is reached");
                            isReached = true;
                            break;
                        }

                    }

                }
            }

        }

        return isReached;
    }

    public static void validateConfiguration(GeneticAlgorithm instance) {
        for (Field field : GeneticAlgorithm.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object obj = null;
            if (field.isAnnotationPresent(PartOfAlgorithm.class)) {
                if (isFieldNull(field, instance)) {
                    throw new IllegalArgumentException("Field is null " + field.getName());
                }
            }
            if (field.isAnnotationPresent(UpperBorder.class)) {
                if (isFieldNull(field, instance)) {
                    throw new IllegalArgumentException("Field is null " + field.getName());
                }
                if (field.getAnnotation(UpperBorder.class).value() <= 0) {
                    throw new IllegalArgumentException("Value must be higher than 0");
                }
            }
            if (field.isAnnotationPresent(Convergence.class)) {
                if (isFieldNull(field, instance)) {
                    throw new IllegalArgumentException("Field is null " + field.getName());
                }
                if (field.getAnnotation(UpperBorder.class).value() <= 0) {
                    throw new IllegalArgumentException("Value must be higher than 0");
                }
            }
            if (field.isAnnotationPresent(TimeBorder.class)) {
                if (isFieldNull(field, instance)) {
                    throw new IllegalArgumentException("Field is null " + field.getName());
                }
                if ((field.getAnnotation(TimeBorder.class).minutes() <= 0
                        && field.getAnnotation(TimeBorder.class).minutes() >= 60)
                        || (field.getAnnotation(TimeBorder.class).seconds() <= 0
                        && field.getAnnotation(TimeBorder.class).seconds() >= 60)) {
                    throw new IllegalArgumentException("Values for Time are invalid");
                }
            }

        }
    }

    private static boolean isFieldNull(Field f, GeneticAlgorithm instance) {
        Object obj = null;
        boolean isNull = false;
        try {
            obj = f.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SCInvestigator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (obj == null) {
            isNull = true;
            LOGGER.error("Field may not be null: {}", f.getName());
        }
        return isNull;
    }

    private static void validateTimeValues(int min, int sec) {
        if (min < 0 || sec < 0) {
            throw new IllegalArgumentException("Values for Minute or Second are invalid");
        }
    }

}
