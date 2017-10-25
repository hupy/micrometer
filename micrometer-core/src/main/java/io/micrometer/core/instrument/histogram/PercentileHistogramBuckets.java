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
package io.micrometer.core.instrument.histogram;

import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Generator for a set of histogram buckets intended for use by a monitoring system that supports aggregable
 * percentile approximations such as Prometheus' {@code histogram_quantiles} or Atlas' {@code :percentiles}.
 */
public class PercentileHistogramBuckets {
    // Number of positions of base-2 digits to shift when iterating over the long space.
    private static final int DIGITS = 2;

    // Bucket values to use, see static block for initialization.
    private static final NavigableSet<Long> PERCENTILE_BUCKETS;

    // The set of buckets is generated by using powers of 4 and incrementing by one-third of the
    // previous power of 4 in between as long as the value is less than the next power of 4 minus
    // the delta.
    //
    // <pre>
    // Base: 1, 2, 3
    //
    // 4 (4^1), delta = 1
    //     5, 6, 7, ..., 14,
    //
    // 16 (4^2), delta = 5
    //    21, 26, 31, ..., 56,
    //
    // 64 (4^3), delta = 21
    // ...
    // </pre>
    static {
        PERCENTILE_BUCKETS = new TreeSet<>();
        PERCENTILE_BUCKETS.add(1L);
        PERCENTILE_BUCKETS.add(2L);
        PERCENTILE_BUCKETS.add(3L);

        int exp = DIGITS;
        while (exp < 64) {
            long current = 1L << exp;
            long delta = current / 3;
            long next = (current << DIGITS) - delta;

            while (current < next) {
                PERCENTILE_BUCKETS.add(current);
                current += delta;
            }
            exp += DIGITS;
        }
        PERCENTILE_BUCKETS.add(Long.MAX_VALUE);
    }

    /**
     * Pick values from a static set of percentile buckets that yields a decent error bound on most real world
     * timers and distribution summaries because monitoring systems like Prometheus require us to report the
     * same buckets at every interval, regardless of where actual samples have been observed.
     */
    public static NavigableSet<Long> buckets(StatsConfig statsConfig) {
        return PERCENTILE_BUCKETS.subSet(statsConfig.getMinimumExpectedValue(), true,
            statsConfig.getMaximumExpectedValue(), true);
    }
}
