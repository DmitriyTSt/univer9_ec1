import java.math.BigInteger

/**
 * Точка эллиптической кривой
 * @param x
 * @param y
 * @param mod - в каком кольце мы действуем
 * @param inf - является ли точка точкой бесконечности
 */
data class Point(
        val x: BigInteger,
        val y: BigInteger,
        val mod: BigInteger,
        val inf: Boolean = false
) {
    override fun toString(): String {
        return if (inf) "P(inf)" else "($x, $y)"
    }

    /**
     * Переопределение оператора сложения (+) для класса Point согласно лекции
     */
    operator fun plus(p: Point): Point {
        if (p.mod != this.mod) throw Exception("Sum point in different field")
        if (this.inf) {
            return p
        }
        if ((this == p && p.y == BigInteger.ZERO) || (this != p && this.x == p.x)) {
            return Point(BigInteger.ZERO, BigInteger.ZERO, mod, true)
        }
        val a = if (this == p) {
            ((3.toBigInteger() * this.x * this.x) * (2.toBigInteger() * this.y).modInverse(mod)).module()
        } else {
            ((p.y - this.y).module() * (p.x - this.x).modInverse(mod)).module()
        }

        val x = (a * a - p.x - this.x).module()
        val y = (a * (this.x - x) - this.y).module()
        return Point(x, y, mod, false)
    }

    /**
     * Умный модуль для отрицательных чисел
     */
    private fun BigInteger.module(): BigInteger {
        return (this % mod + mod) % mod
    }

    /**
     * Бинарный алгоритм умножения точки на число (переопределен оператор *)
     */
    operator fun times(a: BigInteger): Point {
        val binary = a.toString(2).reversed()
        var result = Point(BigInteger.ZERO, BigInteger.ZERO, mod, true)
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