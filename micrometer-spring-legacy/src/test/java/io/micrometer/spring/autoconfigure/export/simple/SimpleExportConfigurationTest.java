/**
 * Copyright 2017 Pivotal Software, Inc.
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
package io.micrometer.spring.autoconfigure.export.simple;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jon Schneider
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
    "spring.metrics.export.atlas.enabled=false",
    "spring.metrics.export.prometheus.enabled=false",
    "spring.metrics.export.datadog.enabled=false",
    "spring.metrics.export.ganglia.enabled=false",
    "spring.metrics.export.graphite.enabled=false",
    "spring.metrics.export.influx.enabled=false",
    "spring.metrics.export.jmx.enabled=false",
    "spring.metrics.export.statsd.enabled=false",
    "spring.metrics.export.newrelic.enabled=false"
})
public class SimpleExportConfigurationTest {
    @Autowired
    CompositeMeterRegistry registry;

    @Test
    public void simpleMeterRegistryIsInTheCompositeWhenNoOtherRegistryIs() {
        assertThat(registry.getRegistries())
            .hasAtLeastOneElementOfType(SimpleMeterRegistry.class);
    }

    @SpringBootApplication(scanBasePackages = "isolated")
    static class MetricsApp {
    }
}