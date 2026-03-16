package com.scto.codelikebastimove.feature.assetstudio.converter

import org.junit.Test
import kotlin.system.measureTimeMillis

class SvgToAvdConverterTest {
    @Test
    fun benchmarkConversion() {
        val converter = SvgToAvdConverter()
        val svg = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" width="100px" height="100px">
            <path d="M10 10 L90 10 L50 90 Z" fill="#ff0000" stroke="black" stroke-width="2" />
            <path d="M20 20 L80 20 L50 80 Z" fill="rgb(0, 255, 0)" />
        </svg>"""

        // Warmup
        for (i in 1..100) {
            converter.convertSvgToAvd(svg)
        }

        val time = measureTimeMillis {
            for (i in 1..10000) {
                converter.convertSvgToAvd(svg)
            }
        }
        println("BM_RESULT=" + time)
    }
}
