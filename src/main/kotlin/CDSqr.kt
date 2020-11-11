import java.math.BigInteger

object CDSqr {
	private const val THRESHOLD = 15

	fun get(p: BigInteger, d: BigInteger): Pair<BigInteger, BigInteger>? {
		if (p.bitLength() <= THRESHOLD) {
			return getCDSimple(p)
		}

		val sl = Legendre.get(-d, p)
		if (sl == -1) {
			return null
		}

		// Вычисляем U = sqr(d) mod p, в нашем случае d = -3
		var i = 0
		val uuu = getU(-d, p) ?: return null
		val u = mutableListOf(uuu)
		val m = mutableListOf(p)
		do {
			val ui = u.last()
			val mi = m.last()
			val mi1 = (ui * ui + d) / mi
			val ui1 = if (ui % mi1 < (mi1 - ui) % mi1) {
				ui % mi1
			} else {
				(mi1 - ui) % mi1
			}
			u.add(ui1)
			m.add(mi1)
			if (mi1 != BigInteger.ONE) {
				i++
			}
		} while (mi1 != BigInteger.ONE)

		val a = MutableList(i + 1) { BigInteger.ZERO }
		a[a.lastIndex] = u[i]
		val b = MutableList(i + 1) { BigInteger.ZERO }
		b[b.lastIndex] = BigInteger.ONE
		if (i == 0) {
			return a.last() to b.last()
		}

		while (i > 0) {
			// выбираем так, чтобы деление было целочисленное из ap1 и ap2
			val ap1 = u[i - 1] * a[i] + d * b[i]
			val ap2 = -u[i - 1] * a[i] + d * b[i]
			val div = a[i] * a[i] + d * b[i] * b[i]
			a[i - 1] = if (ap1 % div == BigInteger.ZERO) {
				ap1 / div
			} else {
				ap2 / div
			}
			// выбираем так, чтобы деление было целочисленное из bp1 и bp2
			val bp1 = -a[i] + u[i - 1] * b[i]
			val bp2 = -a[i] - u[i - 1] * b[i]
			b[i - 1] = if (bp1 % div == BigInteger.ZERO) {
				bp1 / div
			} else {
				bp2 / div
			}
			i--
		}
		return a.first().abs() to b.first().abs()
	}

	fun getU(d: BigInteger, p: BigInteger): BigInteger? {
		if (p % 4.toBigInteger() == 3.toBigInteger()) {
			return getU34(d, p)
		}
		if (p % 8.toBigInteger() == 5.toBigInteger()) {
			getU58(d, p)
		}
		return null
	}

	private fun getU34(a: BigInteger, q: BigInteger): BigInteger? {
		val x = a.modPow((q + BigInteger.ONE) / 4.toBigInteger(), q)
		return if ((x * x) % q == (a % q + q) % q) {
			x
		} else {
			null
		}
	}

	private fun getU58(a: BigInteger, q: BigInteger): BigInteger? {
		val b = a.modPow((q + 3.toBigInteger()) / 8.toBigInteger(), q)
		val c = a.modPow((q - BigInteger.ONE) / 4.toBigInteger(), q)
		return when {
			c.abs() != BigInteger.ONE -> null
			c == BigInteger.ONE -> b
			else -> {
				val i = 2.toBigInteger().modPow((q - BigInteger.ONE) / 4.toBigInteger(), q)
				(b * i) % q
			}
		}
	}

	private fun getCDSimple(p: BigInteger): Pair<BigInteger, BigInteger>? {
		var c = BigInteger.ONE
		while (c < p) {
			var d = BigInteger.ONE
			while (d < p) {
				if (c * c + 3.toBigInteger() * d * d == p) {
					return c to d
				}
				d++
			}
			c++
		}
		return null
	}
}