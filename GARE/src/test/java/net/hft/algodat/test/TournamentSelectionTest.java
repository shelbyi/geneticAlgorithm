/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.algodat.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.hft.algodat.utilities.Utilities;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Test;

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
