package com.example.myapplication

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory

/**
 * Ensure that we can get the secrets from the environment variables and local properties files
 *
 * Requires the following plugins:
 * - for env: https://plugins.gradle.org/plugin/co.uzzu.dotenv.gradle
 * - for local.properties: https://github.com/google/secrets-gradle-plugin
 */
class SecretsTest {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Test fun envSecretTest() {
        val envSecret = BuildConfig.ENV_SECRET
        logger.info { "envSecret: $envSecret" }
        assertTrue(envSecret.isNotEmpty(), "Expecting a value in the ENV_SECRET. You're probably missing a .env file")
        assertEquals("test", envSecret, "Expecting the ENV secret to be 'test', set this in your .env file or GH actions secret")
    }

    @Test fun localPropertyTest() {
        val propertySecret = BuildConfig.PROPERTY_SECRET
        logger.info { "propertySecret: $propertySecret" }
        assertTrue(propertySecret.isNotEmpty(), "Expecting a value in the PROPERTY_SECRET. You're probably missing a definition in the local.properties file")
        assertEquals("property_test", propertySecret, "Expecting the PROPERTY_SECRET secret to be 'property_test', set this in your local_properties file or GH actions secret")
    }
}
