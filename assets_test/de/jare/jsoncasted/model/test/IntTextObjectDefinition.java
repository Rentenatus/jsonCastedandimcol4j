/* <copyright>
 * Copyright (C) 2022 Janusch Rentenatus & Thomas Weber 
 * Copyright (c) 2025, Janusch Rentenatus. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * </copyright>
 */
package de.jare.jsoncasted.model.test;

import static de.jare.jsoncasted.model.JsonCollectionType.ARRAY;
import static de.jare.jsoncasted.model.JsonCollectionType.LIST;
import de.jare.jsoncasted.model.JsonModel;
import de.jare.jsoncasted.model.builder.JsonSeasonIntBuilder;
import de.jare.jsoncasted.model.item.JsonClass;
import de.jare.jsoncasted.parserwriter.JsonCastingLevel;
import de.jare.jsoncasted.parserwriter.JsonItemDefinition;

/**
 *
 * @author Janusch Rentenatus
 */
public class IntTextObjectDefinition implements JsonItemDefinition {

    public static final IntTextObjectDefinition INSTANCE = new IntTextObjectDefinition();

    public static IntTextObjectDefinition getInstance() {
        return INSTANCE;
    }

    private final JsonModel model;
    private final JsonClass configRoot;

    public IntTextObjectDefinition() {
        model = new JsonModel("Test");
        model.addBasicModel();

        JsonClass seasonInt = model.newJsonClass(int.class, new JsonSeasonIntBuilder());

        configRoot = model.newJsonReflect(IntTestObject.class);

        configRoot.addField("intArray", seasonInt, ARRAY);
        configRoot.addField("intSeasion", seasonInt, LIST);

    }

    @Override
    public JsonModel getModel() {
        return model;
    }

    public JsonClass getConfigRoot() {
        return configRoot;
    }

    @Override
    public JsonCastingLevel getCastingLevel() {
        return JsonCastingLevel.NEVER;
    }
}
