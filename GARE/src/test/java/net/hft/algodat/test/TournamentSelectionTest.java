/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.test;
<<<<<<< HEAD:GARE/src/test/java/net/hft/algodat/test/TournamentSelectionTest.java
=======

import static org.junit.Assert.assertTrue;
>>>>>>> MCHOKOEVA::GARE/src/test/java/net/hft/algodat/test/TournamentSelectionTest.java

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD:GARE/src/test/java/net/hft/algodat/test/TournamentSelectionTest.java
import net.hft.algodat.selectionmethods.TournamentSelection;

import org.junit.Test;

import static org.junit.Assert.*;
=======
import net.hft.algodat.framework.geneticalgorithm.utilities.Utilities;

import org.junit.Test;
>>>>>>> MCHOKOEVA::GARE/src/test/java/net/hft/algodat/test/TournamentSelectionTest.java

/**
 *
 * @author AVATSP
 */
public class TournamentSelectionTest {

    @Test
    public void testRandomSelection() {
        boolean isValid = true;
        List a = new ArrayList();
        a.add("");
        a.add("");
        a.add("");
        a.add("");
        for (int i = 0; i < 100000; i++) {
            // Important: we have to ask for size()-1 if we are requesting the random indices
            int random = Utilities.getRandomInBetween(a);
            if ((random >= 0 || random <= a.size()) && random != a.size()) {
                a.get(random);
            } else {
                isValid = false;
            }
        }
        assertTrue(isValid);

    }

}
