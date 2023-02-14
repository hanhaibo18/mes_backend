/**
 * Copyright © 2017 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.richfit.mes.sync.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ashvayka on 16.01.17.
 */
@Slf4j
public class ConfigurationTools {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T readConfiguration(JsonNode configurationNode, Class<T> clazz) throws IOException {
        try {
            return mapper.treeToValue(configurationNode, clazz);
        } catch (IOException e) {
            log.error("Failed to load {} configuration from {}", clazz, configurationNode);
            throw e;
        }
    }

    public static <T> T readFileConfiguration(String configurationFile, Class<T> clazz) throws IOException {
        try {
            return mapper.readValue(getFileAsStream(configurationFile), clazz);
        } catch (IOException e) {
            log.error("Failed to load {} configuration from {}", clazz, configurationFile);
            throw e;
        }
    }
    
    private static InputStream getResourceAsStream(String fileContent) {
        byte[] decoded = Base64.decodeBase64(fileContent);
        return new ByteArrayInputStream(decoded);
    }

    private static InputStream getFileAsStream(String configurationFile) {
        return ConfigurationTools.class.getClassLoader().getResourceAsStream(configurationFile);
    }
}
