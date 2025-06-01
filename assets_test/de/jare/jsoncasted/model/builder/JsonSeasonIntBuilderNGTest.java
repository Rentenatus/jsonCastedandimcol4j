/* <copyright> 
 * Copyright (c) 2025, Janusch Rentenatus. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * </copyright>
 */
package de.jare.jsoncasted.model.builder;

import de.jare.jsoncasted.item.JsonItem;
import de.jare.jsoncasted.model.JsonBuildException;
import de.jare.jsoncasted.model.test.IntTestObject;
import de.jare.jsoncasted.model.test.IntTextObjectDefinition;
import de.jare.jsoncasted.parserwriter.JsonParseException;
import de.jare.jsoncasted.parserwriter.JsonParser;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author jRent
 */
public class JsonSeasonIntBuilderNGTest {

    public JsonSeasonIntBuilderNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("===============================================");
        System.out.println("## Start JsonSeasonIntBuilderNGTest.");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("## End JsonSeasonIntBuilderNGTest.");
        System.out.println("===============================================");
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {

    }

    /**
     * Test of getModel method, of class JsonConfigDefinition.
     */
    @Test
    public void testModel1() {
        System.out.println("getLangDef");

        File configFile = new File("./assets/config/config5.json");
        testModel(configFile, IntTextObjectDefinition.getInstance());
    }

    private void testModel(File configFile, IntTextObjectDefinition definition) {
        System.out.println("=============================================== File");
        System.out.println(configFile.getAbsolutePath());

        JsonItem obj1 = null;
        try {
            obj1 = JsonParser.parse(configFile, definition, definition.getConfigRoot());
        } catch (JsonParseException | IOException | NullPointerException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
            fail(ex.getMessage(), ex);
        }
        assertNotNull(obj1);
        System.out.println("=============================================== Config Class");
        System.out.println(obj1.getClass());

        IntTestObject root = null;
        try {
            final Object buildInstance1 = obj1.buildInstance();
            System.out.println(buildInstance1.getClass().getName());
            assertNotNull(root = (IntTestObject) buildInstance1);

        } catch (JsonBuildException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
            fail(ex.getMessage(), ex);
        }
        System.out.println("===============================================");
    }
}
