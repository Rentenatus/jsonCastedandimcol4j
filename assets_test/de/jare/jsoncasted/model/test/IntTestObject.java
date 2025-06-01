/* <copyright> 
 * Copyright (c) 2025, Janusch Rentenatus. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * </copyright>
 */
package de.jare.jsoncasted.model.test;

import de.jare.ndimcol.primint.ArraySeasonHashableInt;

/**
 *
 * @author Janusch Rentenatus
 */
public class IntTestObject {

    ArraySeasonHashableInt intSeasion;

    int[] intArray;

    public ArraySeasonHashableInt getIntSeasion() {
        return intSeasion;
    }

    public void setIntSeasion(ArraySeasonHashableInt intSeasion) {
        this.intSeasion = intSeasion;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

}
