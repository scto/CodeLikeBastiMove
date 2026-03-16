package com.scto.codelikebastimove.feature.assetstudio.converter

import org.junit.Test
import kotlin.system.measureTimeMillis

class SvgToAvdConverterPerformanceTest {

    @Test
    fun benchmarkExtractPaths() {
        val converter = SvgToAvdConverter()

        // Generate a large SVG with many path elements to simulate heavy load
        val svgBuilder = StringBuilder()
        svgBuilder.append("""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">""")
        for (i in 0 until 10000) {
            svgBuilder.append("""<path d="M1 1h2v2h-2z" fill="#000000" />""")
        }
        svgBuilder.append("</svg>")
        val svgContent = svgBuilder.toString()

        // Warmup
        for (i in 0 until 5) {
            converter.convertSvgToAvd(svgContent)
        }

        // Measure
        var totalTime = 0L
        val iterations = 50
        for (i in 0 until iterations) {
            totalTime += measureTimeMillis {
                converter.convertSvgToAvd(svgContent)
            }
        }

        println("Average time per conversion (10,000 paths): ${totalTime / iterations} ms")
    }
}
