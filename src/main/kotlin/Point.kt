data class Point(
        val x: Long,
        val y: Long,
        val mod: Long,
        val inf: Boolean = false
) {
    override fun toString(): String {
        return if (inf) "P(inf)" else "($x, $y)"
    }

    operator fun plus(p: Point): Point {
        if (p.mod != this.mod) throw Exception("Sum point in different field")
        if (this.inf) {
            return p
        }
        if ((this == p && p.y == 0L) || (this != p && this.x == p.x)) {
            return Point(0, 0, mod, true)
        }
        val a = if (this == p) {
            ((3 * this.x * this.x) * extendedGcd(2 * this.y, mod).second.module()).module()
        } else {
            ((p.y - this.y).module() * extendedGcd((p.x - this.x).module(), mod).second.module()).module()
        }

        val x = (a * a - p.x - this.x).module()
        val y = (a * (this.x - x) - this.y).module()
        return Point(x, y, mod, false)
    }

    private fun extendedGcd(_a: Long, _b: Long): Triple<Long, Long, Long> {
        var x0 = 1L
        var y0 = 0L
        var x1 = 0L
        var y1 = 1L
        var r0 = _a
        var r1 = _b
        do {
            val q1 = r0 / r1
            val r2 = r0 % r1
            if (r2 != 0L) {
                val x2 = x0 - q1 * x1
                val y2 = y0 - q1 * y1
                // продолжаем
                x0 = x1
                y0 = y1
                r0 = r1
                x1 = x2
                y1 = y2
                r1 = r2
            }
        } while (r2 != 0L)
        return Triple(r1, x1, y1)
    }

    private fun Long.module(): Long {
        return (this % mod + mod) % mod
    }

    operator fun times(a: Long): Point {
        val binary = a.toString(2).reversed()
        var result = Point(0, 0, mod, true)
        var addEnd = this
        binary.forEach {
            if (it == '1') {
                result += addEnd
            }
            addEnd += addEnd
        }
        return result
    }
}