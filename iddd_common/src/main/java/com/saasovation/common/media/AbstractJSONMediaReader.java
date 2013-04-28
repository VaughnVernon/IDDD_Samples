//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.saasovation.common.media;

import java.math.BigDecimal;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saasovation.common.serializer.AbstractSerializer;

public abstract class AbstractJSONMediaReader {

    private JsonObject representation;
    private JSONReader reader;

    public AbstractJSONMediaReader(String aJSONRepresentation) {
        super();

        this.initialize(aJSONRepresentation);
    }

    public AbstractJSONMediaReader(JsonObject aRepresentationObject) {
        super();

        this.setRepresentation(aRepresentationObject);
    }

    public JsonArray array(String... aKeys) {
        JsonArray array = null;

        JsonElement element = this.navigateTo(this.representation(), aKeys);

        if (element != null) {
            array = element.getAsJsonArray();
        }

        return array;
    }

    public BigDecimal bigDecimalValue(String... aKeys) {
        String stringValue = this.stringValue(aKeys);

        return stringValue == null ? null : new BigDecimal(stringValue);
    }

    public Boolean booleanValue(String... aKeys) {
        String stringValue = this.stringValue(aKeys);

        return stringValue == null ? null : Boolean.parseBoolean(stringValue);
    }

    public Date dateValue(String... aKeys) {
        String stringValue = this.stringValue(aKeys);

        return stringValue == null ? null : new Date(Long.parseLong(stringValue));
    }

    public Double doubleValue(String... aKeys) {
        String stringValue = this.stringValue(aKeys);

        return stringValue == null ? null : Double.parseDouble(stringValue);
    }

    public Float floatValue(String... aKeys) {
        String stringValue = this.stringValue(aKeys);

        return stringValue == null ? null : Float.parseFloat(stringValue);
    }

    public Integer integerValue(String... aKeys) {
        String stringValue = this.stringValue(aKeys);

        return stringValue == null ? null : Integer.parseInt(stringValue);
    }

    public Long longValue(String... aKeys) {
        String stringValue = this.stringValue(aKeys);

        return stringValue == null ? null : Long.parseLong(stringValue);
    }

    public String stringValue(String... aKeys) {
        return this.stringValue(this.representation(), aKeys);
    }

    protected JsonElement elementFrom(JsonObject aJsonObject, String aKey) {
        JsonElement element = aJsonObject.get(aKey);

        if (element == null) {
            element = aJsonObject.get("@" + aKey);
        }

        return element;
    }

    protected JsonElement navigateTo(JsonObject aStartingJsonObject, String... aKeys) {
        if (aKeys.length == 0) {
            throw new IllegalArgumentException("Must specify one or more keys.");
        } else if (aKeys.length == 1 && (aKeys[0].startsWith("/") || aKeys[0].contains("."))) {
            aKeys = this.parsePath(aKeys[0]);
        }

        int keyIndex = 1;

        JsonElement element = this.elementFrom(aStartingJsonObject, aKeys[0]);

        if (!element.isJsonNull() && !element.isJsonPrimitive() && !element.isJsonArray()) {
            JsonObject object = element.getAsJsonObject();

            for ( ; element != null &&
                    !element.isJsonPrimitive() &&
                    keyIndex < aKeys.length;
                 ++keyIndex) {

                element = this.elementFrom(object, aKeys[keyIndex]);

                if (!element.isJsonPrimitive()) {

                    element = this.elementFrom(object, aKeys[keyIndex]);

                    if (element.isJsonNull()) {
                        element = null;
                    } else {
                        object = element.getAsJsonObject();
                    }
                }
            }
        }

        if (element != null) {
            if (!element.isJsonNull()) {
                if (keyIndex != aKeys.length) {
                    throw new IllegalArgumentException("Last name must reference a simple value.");
                }
            } else {
                element = null;
            }
        }

        return element;
    }

    protected JsonObject representation() {
        return representation;
    }

    protected String stringValue(JsonObject aStartingJsonObject, String... aKeys) {
        String value = null;

        JsonElement element = this.navigateTo(aStartingJsonObject, aKeys);

        if (element != null) {
            value = element.getAsString();
        }

        return value;
    }

    private void initialize(String aJSONRepresentation) {
        this.setReader(new JSONReader(false, false));

        this.setRepresentation(this.reader().deserialize(aJSONRepresentation));
    }

    private void setRepresentation(JsonObject aRepresentation) {
        this.representation = aRepresentation;
    }

    private String[] parsePath(String aPropertiesPath) {
        boolean startsWithSlash = aPropertiesPath.startsWith("/");

        String[] propertyNames = null;

        if (startsWithSlash) {
            propertyNames = aPropertiesPath.substring(1).split("/");
        } else {
            propertyNames = aPropertiesPath.split("\\.");
        }

        return propertyNames;
    }

    private JSONReader reader() {
        return this.reader;
    }

    private void setReader(JSONReader aReader) {
        this.reader = aReader;
    }

    private static class JSONReader extends AbstractSerializer {
        protected JSONReader(boolean isCompact) {
            super(isCompact);
        }

        protected JSONReader(boolean isPretty, boolean isCompact) {
            super(isPretty, isCompact);
        }

        protected JsonObject deserialize(String aSerialization) {
            JsonParser parser = new JsonParser();

            try {
                JsonObject object = parser.parse(aSerialization).getAsJsonObject();

                return object;

            } catch (Exception e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            }
        }

        @Override
        protected Gson gson() {
            return super.gson();
        }
    }
}
