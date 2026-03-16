import kotlin.system.measureTimeMillis

fun extractAttributeUnoptimized(svg: String, attribute: String): String? {
    val regex = Regex("""$attribute\s*=\s*["']([^"']+)["']""")
    return regex.find(svg)?.groupValues?.get(1)
}

val attributeRegexCache = java.util.concurrent.ConcurrentHashMap<String, Regex>()

fun extractAttributeOptimized(svg: String, attribute: String): String? {
    val regex = attributeRegexCache.getOrPut(attribute) {
        Regex("""$attribute\s*=\s*["']([^"']+)["']""")
    }
    return regex.find(svg)?.groupValues?.get(1)
}

fun main() {
    val svg = """<path d="M10 10 L 20 20 Z" fill="black" stroke="none" stroke-width="2.5" fill-opacity="0.8" />"""
    val attributes = listOf("d", "fill", "stroke", "stroke-width", "fill-opacity")

    // Warm-up
    for (i in 0..1000) {
        for (attr in attributes) {
            extractAttributeUnoptimized(svg, attr)
            extractAttributeOptimized(svg, attr)
        }
    }

    val unoptimizedTime = measureTimeMillis {
        for (i in 0..100000) {
            for (attr in attributes) {
                extractAttributeUnoptimized(svg, attr)
            }
        }
    }

    val optimizedTime = measureTimeMillis {
        for (i in 0..100000) {
            for (attr in attributes) {
                extractAttributeOptimized(svg, attr)
            }
        }
    }

    println("Unoptimized time: ${unoptimizedTime}ms")
    println("Optimized time: ${optimizedTime}ms")
}

main()
