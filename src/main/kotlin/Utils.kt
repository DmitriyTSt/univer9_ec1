import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

object Utils {
    const val DEBUG = false
    const val MOD = 6L

    fun generatePrimeP(bits: Int, lastBad: Long?): Long? {
        var start = 2.0.pow((bits - 1).toDouble()).toLong()
        val end = start * 2
        while (start % MOD != 1L) {
            start++
        }
        while (!isPrime(start) || start <= lastBad ?: 0) {
            start += MOD
        }
        if (start >= end) {
            return null
        } else {
            return start
        }
    }

    private fun isPrime(p: Long): Boolean {
        for (i in 2..sqrt(p.toDouble()).toInt()) {
            if (p % i == 0L) {
                return false
            }
        }
        return true
    }

    fun getCD(p: Long): Pair<Long, Long>? {
        val sqrtP = sqrt(p.toDouble()).toLong()
        var c = 1L
        while (c < sqrtP) {
            var d = 1L
            while (d < sqrtP) {
                if (c * c + 3 * d * d == p) {
                    return c to d
                }
                d++
            }
            c++
        }
        return null
    }

    fun getNR(p: Long, c: Long, d: Long): Pair<Long, Long>? {
        val tArr = listOf(c + 3 * d, c - 3 * d, 2 * c).flatMap { listOf(it, -it) }
        logd("T = $tArr")
        val div = listOf(1, 2, 3, 6)
        val nr = tArr.flatMap { t ->
            val n = p + 1 + t
            div.filter { n % it == 0L && isPrime(n / it) }.map { n to (n / it) }
        }
        return nr.firstOrNull()
    }

    fun generatePoint(p: Long): Point {
        var x0 = Random.nextLong(p)
        while (x0 == 0L) {
            x0 = Random.nextLong(p)
        }
        var y0 = Random.nextLong(p)
        while (y0 == 0L) {
            y0 = Random.nextLong(p)
        }
        return Point(x0, y0, p)
    }

    /**
     * Вычисление символа Лежандра
     */
    private fun L(a: Long, p: Long): Int {
        return Legendre.get(a, p)
    }

//    fun isSqrResidue(b: Long, p: Long): Boolean? {
//        return when (L(b, p)) {
//            1 -> true
//            -1 -> false
//            else -> null
//        }
//    }

    fun isSqrResidue(b: Long, p: Long): Boolean {
        val pow = b.pow((p - 1) / 2, p)
        return pow == 1L || pow == p - 1
    }

    fun isCubeResidue(b: Long, p: Long): Boolean? {
        if ((p - 1) % 3 == 0L) {
            return b.pow((p-1) / 3, p) % p == 1L
        } else if ((p - 1) % 3 == 2L) {
            return true
        } else {
            return null
        }
    }
}