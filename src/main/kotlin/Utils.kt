import java.math.BigInteger
import kotlin.random.Random

object Utils {
    /** вывод дополнительной информации */
    const val DEBUG = false
    /** MOD из условия 1 варианта для генерации кривой с j = 0 */
    private val MOD = 6.toBigInteger()

    private val random = java.util.Random()

    /**
     * Генерация простого p, удовлетворяющего условия задания (Ростовцев, 15.3.1, п 1)
     */
    fun generatePrimeP(bits: Int, lastBad: BigInteger?): BigInteger? {
        var start = 2.toBigInteger()
        start = start.pow((bits - 1))
        val end = start * 2.toBigInteger()
        while (start % MOD != BigInteger.ONE) {
            start++
        }
        while (!isPrime(start) || start <= lastBad ?: BigInteger.ZERO) {
            start += MOD
        }
        if (start >= end) {
            return null
        } else {
            return start
        }
    }

    /**
     * Вероятностная проверка на простоту
     */
    private fun isPrime(p: BigInteger): Boolean {
        return p.isProbablePrime(128)
    }

    /**
     * Нахождение чисел N r (Ростовцев, 15.3.1, п 3)
     */
    fun getNR(p: BigInteger, c: BigInteger, d: BigInteger): Pair<BigInteger, BigInteger>? {
        // возможные T
        val tArr = listOf(
                c + 3.toBigInteger() * d,
                c - 3.toBigInteger() * d,
                2.toBigInteger() * c
        ).flatMap { listOf(it, -it) }
        logd("T = $tArr")
        // на что будем делить
        val div = listOf(1, 2, 3, 6).map { it.toBigInteger() }
        val nr = tArr.flatMap { t ->
            val n = p + BigInteger.ONE + t
            // просеиваем их, чтобы хотя бы для одного выполнялось равенство
            div.filter { n % it == BigInteger.ZERO && isPrime(n / it) }.map { n to (n / it) }
        }
        // или возвращаем числа, или null, если не смогли найти нужное
        return nr.firstOrNull()
    }

    /**
     * Генерация роизвольной точки, координаты которой != 0
     */
    fun generatePoint(p: BigInteger): Point {
        var x0 = Random.nextBigInteger(p)
        while (x0 == BigInteger.ZERO) {
            x0 = Random.nextBigInteger(p)
        }
        var y0 = Random.nextBigInteger(p)
        while (y0 == BigInteger.ZERO) {
            y0 = Random.nextBigInteger(p)
        }
        return Point(x0, y0, p)
    }

    /**
     * Проверка на квадратичный вычет/невычет
     */
    fun isSqrResidue(b: BigInteger, p: BigInteger): Boolean {
        val pow = b.modPow((p - 1.toBigInteger()) / 2.toBigInteger(), p)
        return pow == 1.toBigInteger()
    }

    /**
     * Проверка на кубический вычет/невычет
     */
    fun isCubeResidue(b: BigInteger, p: BigInteger): Boolean? {
        if ((p - 1.toBigInteger()) % 3.toBigInteger() == BigInteger.ZERO) {
            return b.modPow((p - 1.toBigInteger()) / 3.toBigInteger(), p) % p == 1.toBigInteger()
        } else if ((p - 1.toBigInteger()) % 3.toBigInteger() == 2.toBigInteger()) {
            return true
        } else {
            return null
        }
    }

    private fun Random.Default.nextBigInteger(p: BigInteger): BigInteger {
        return BigInteger(p.bitLength(), random) % p
    }
}