package com.scto.codelikebastimove.feature.submodulemaker

import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

class BuildVariantsViewModelBenchmarkTest {

    @Test
    fun benchmarkExtractMethods() {
        val viewModel = BuildVariantsViewModel()
        val dummyAndroidBlock = """
            android {
                buildTypes {
                    release {
                        minifyEnabled false
                    }
                    debug {
                        minifyEnabled false
                    }
                    benchmark {
                        minifyEnabled true
                    }
                    staging {
                        minifyEnabled true
                    }
                    create("qa") {
                        minifyEnabled true
                    }
                    getByName("beta") {
                        minifyEnabled true
                    }
                    named("alpha") {
                        minifyEnabled true
                    }
                    canary {
                        minifyEnabled true
                    }
                }
                productFlavors {
                    create("free") {
                        dimension "tier"
                    }
                    getByName("paid") {
                        dimension "tier"
                    }
                    demo {
                        dimension "tier"
                    }
                    full {
                        dimension "tier"
                    }
                    dev {
                        dimension "env"
                    }
                    prod {
                        dimension "env"
                    }
                }
            }
        """.trimIndent()

        // Use reflection to access private methods for benchmarking
        val extractBuildTypesMethod = BuildVariantsViewModel::class.java.getDeclaredMethod("extractBuildTypesFromAndroidBlock", String::class.java)
        extractBuildTypesMethod.isAccessible = true

        val extractProductFlavorsMethod = BuildVariantsViewModel::class.java.getDeclaredMethod("extractProductFlavorsFromAndroidBlock", String::class.java)
        extractProductFlavorsMethod.isAccessible = true

        // Warmup
        for (i in 0 until 100) {
            extractBuildTypesMethod.invoke(viewModel, dummyAndroidBlock)
            extractProductFlavorsMethod.invoke(viewModel, dummyAndroidBlock)
        }

        val iterations = 10000

        val timeBuildTypes = measureTimeMillis {
            for (i in 0 until iterations) {
                extractBuildTypesMethod.invoke(viewModel, dummyAndroidBlock)
            }
        }

        val timeFlavors = measureTimeMillis {
            for (i in 0 until iterations) {
                extractProductFlavorsMethod.invoke(viewModel, dummyAndroidBlock)
            }
        }

        println("BENCHMARK_RESULT_BUILD_TYPES: ${timeBuildTypes}ms for $iterations iterations")
        println("BENCHMARK_RESULT_FLAVORS: ${timeFlavors}ms for $iterations iterations")
        println("BENCHMARK_TOTAL: ${timeBuildTypes + timeFlavors}ms")

        // Just a dummy assert to make it a test
        assertTrue(true)
    }
}
