package com.scto.codelikebastimove.feature.assetstudio.converter

import org.junit.Test
import kotlin.system.measureTimeMillis

class SvgToAvdConverterBenchmark {
    @Test
    fun benchmarkColorParsing() {
        val converter = SvgToAvdConverter()
        val svgBuilder = StringBuilder()
        svgBuilder.append("<svg viewBox=\"0 0 100 100\">")
        for (i in 0 until 10000) {
            svgBuilder.append("<path d=\"M10 10\" fill=\"rgb(255, 0, 0)\" />")
        }
        svgBuilder.append("</svg>")
        val svg = svgBuilder.toString()

        // Warmup
        for(i in 0..5) {
            converter.convertSvgToAvd(svg)
        }

        // Measure
        val time = measureTimeMillis {
            for(i in 0..10) {
                converter.convertSvgToAvd(svg)
            }
        }
        println("BENCHMARK_RESULT: $time ms")
    }
}
