import org.knowm.xchart.*
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import java.io.File
import java.math.BigInteger
import kotlin.system.exitProcess

/**
 * Генерация эллиптической кривой с j = 0
 */
fun main() {
    println("Введите l >= 3 (для существования p)")
    var l = readInt()
    while (l < 3) {
        println("Введите l >= 3 (для существования p)")
        l = readInt()
    }
    println("Введите m")
    val m = readInt()
    val timeStart = System.currentTimeMillis()
    // находим первое возможное p
    var p = Utils.generatePrimeP(l, null)
    // пытаемся решить с ним
    var result = solve(p, m)
    // если решений нет, то перегенерируем p и решаем до тех пор, пока не найдем, или не переберем все p заданной длины
    while (result == null) {
        p = Utils.generatePrimeP(l, p)
        result = solve(p, m)
    }
    val timeAll = System.currentTimeMillis() - timeStart
    println("Time spent: %d.%03d s".format(timeAll / 1000, timeAll % 1000))
    println("Result:\n$result")
    showChart(result)
}

/**
 * Умное чтение числа из консоли
 */
private fun readInt(): Int {
    var int = readLine()?.toIntOrNull()
    while (int == null) {
        println("Введите число")
        int = readLine()?.toIntOrNull()
    }
    return int
}

/**
 * Эллиптическая кривая в поле p
 * @param p
 * @param q - образующая порядка r
 * @param r
 */
class Result(
        val p: BigInteger,
        val b: BigInteger,
        val q: Point,
        val r: BigInteger
) {
    override fun toString(): String {
        return "p = $p\nb = $b\nq = $q\nr = $r"
    }
}

/**
 * Построение кривой, если хотя бы один пункт не соответствует проверкам, возвращаем null и идем за новым p
 */
fun solve(p: BigInteger?, m: Int): Result? {
    // если возможное p вылетело за границу размера, то решений нет
    if (p == null) {
        println("Для заданного l решение не найдено")
        exitProcess(0)
    }
    logd("p = $p")
    // вычисление разложения c^2 + d^2 = p в sqrt(-d) (Ростовцев, 15.3.1, п 2)
    val cd = CDSqr.get(p, 3.toBigInteger()) ?: return null
    val c = cd.first
    val d = cd.second
    logd("$p = ($c)^2 + 3*($d)^2,    c = $c, d = $d")
    // нахождение чисел N r (Ростовцев, 15.3.1, п 3)
    val nr = Utils.getNR(p, c, d) ?: return null
    val n = nr.first
    val r = nr.second
    logd("N = $n, r = $r")
    // проверки на отношение p и r, при неудаче - перегенерируем (Ростовцев, 15.3.1, п 4)
    if (p == r) return null
    var pp = p
    repeat(m) {
        if (pp % r == BigInteger.ONE) {
            return null
        }
        pp *= p
    }
    logd("p != r, p^i != 1 (mod r)")
    var result: Result?  = null
    // нашли нужные n r, генерируем точку и пытаемся проверить созданную точку b из произвольной точки
    // (Ростовцев, 15.3.1, п 5)
    while (result == null) {
        val point = Utils.generatePoint(p)
        val x0 = point.x
        val y0 = point.y
        val b = ((y0 * y0 % n - (x0 * x0 * x0) % n) + n) % n
        logd("($x0, $y0) b = $b")
        val checkB = when (n / r) {
            // проверка b - квадратичный и кубический НЕвычет для n == r
            1.toBigInteger() -> !Utils.isSqrResidue(b, n) && Utils.isCubeResidue(b, r) == false
            // проверка b - квадратичный и кубический вычет для n == 6r
            6.toBigInteger() -> Utils.isSqrResidue(b, n) && Utils.isCubeResidue(b, r) == true
            // проверка b - квадратичный НЕвычет и кубический вычет для n == 2r
            2.toBigInteger() -> !Utils.isSqrResidue(b, n) && Utils.isCubeResidue(b, r) == true
            // проверка b - квадратичный вычет и кубический НЕвычет для n == 3r
            3.toBigInteger() -> Utils.isSqrResidue(b, r) &&
                    Utils.isSqrResidue(b, 3.toBigInteger()) && Utils.isCubeResidue(b, r) == false
            else -> false
        }
        // если прошли проверку B, то проходим проверку на произведение точки на число, иначе result останется null
        // и будет сгенерирована новая произвольная точка
        if (checkB) {
            // проверка N * point = P(inf) (знаменатель углового коэф в формуле сложения обращается в 0)
            // (Ростовцев, 15.3.1, п 6)
            val newP = point * n
            logd("n * point = $newP")
            if (newP.coord == null) {
                // вычисляем Q (Ростовцев, 15.3.1, п 7)
                val q = point * (n / r)
                // нашли удовлетворяющий нас результат
                result = Result(p, b, q, r)
            }
        }
    }

    return result
}

/**
 * Построение найденной эллиптической кривой и вывод точек в файл (только для чисел меньше 20 бит)
 */
fun showChart(result: Result) {
    if (result.p.bitLength() < 20) {
        var start = result.q
        val xData = mutableListOf<Long>()
        val yData = mutableListOf<Long>()
        val points = mutableListOf<String>()

        do {
            if (start.coord != null) {
                xData.add(start.x.toLong())
                yData.add(start.y.toLong())
            }
            points.add(start.toString())
            start += result.q
        } while (start != result.q)

        val pointsFile = File("points.txt")
        pointsFile.writeText(points.joinToString("\n"))

        val chart: XYChart = XYChartBuilder()
                .width(1300)
                .height(900)
                .title("Elliptic")
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .build()
        chart.styler.apply {
            defaultSeriesRenderStyle = XYSeriesRenderStyle.Scatter
            isChartTitleVisible = false
            isLegendVisible = false
            markerSize = 10
        }
        chart.addSeries("Elliptic 1", xData, yData)
        SwingWrapper(chart).displayChart()
        println("Точки выведены в файл ${pointsFile.absolutePath}")
    } else {
        println("p = ${result.p}\nСлишком большое, чтобы нарисовать график, график рисуется для l < 20")
    }
}