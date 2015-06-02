/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hft.alogdat.test;

import java.util.ArrayList;
import java.util.List;
import net.hft.alogdat.selectionmethods.TournamentSelection;
import org.junit.Test;
import static org.junit.Assert.*;

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
            int random = TournamentSelection.getRandomInBetween(a);
            if ((random >= 0 || random <= a.size()) && random != a.size()) {
                a.get(random);
            } else {
                isValid = false;
            }
        }
        assertTrue(isValid);

    }

}
