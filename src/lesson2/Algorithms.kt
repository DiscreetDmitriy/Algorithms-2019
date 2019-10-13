@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 *
 * //     Трудоёмкость: O(n)
 * //     Ресурсоёмкость: O(n)
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    val stocks = File(inputName).readLines().map {
        require(it.matches(Regex("""^\d+$""")))
        it.toInt()
    }

    val arrayOfMin = IntArray(stocks.size) { Int.MAX_VALUE }
    val arrayOfMinDays = IntArray(stocks.size) { 0 }

    arrayOfMin[0] = stocks.first()
    // minListDay[0] = 0

    for (i in 1 until stocks.size)
        if (stocks[i] < arrayOfMin[i - 1]) {
            arrayOfMin[i] = stocks[i]
            arrayOfMinDays[i] = i
        } else {
            arrayOfMin[i] = arrayOfMin[i - 1]
            arrayOfMinDays[i] = arrayOfMinDays[i - 1]
        }

    val arrayOfMax = IntArray(stocks.size) { Int.MIN_VALUE }
    val arrayOfMaxDays = IntArray(stocks.size) { 0 }

    arrayOfMax[stocks.lastIndex] = stocks.last()
    arrayOfMaxDays[stocks.lastIndex] = stocks.lastIndex

    for (i in stocks.size - 2 downTo 0)
        if (stocks[i] > arrayOfMax[i + 1]) {
            arrayOfMax[i] = stocks[i]
            arrayOfMaxDays[i] = i
        } else {
            arrayOfMax[i] = arrayOfMax[i + 1]
            arrayOfMaxDays[i] = arrayOfMaxDays[i + 1]
        }

    var result = 1 to 2
    var diff = 0

    for (i in stocks.indices)
        if (arrayOfMax[i] - arrayOfMin[i] > diff) {
            diff = arrayOfMax[i] - arrayOfMin[i]
            result = arrayOfMinDays[i] + 1 to arrayOfMaxDays[i] + 1
        }

    return result
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 *
 * //     Трудоёмкость: O(n)
 * //     Ресурсоёмкость: O(1)
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    var result = 1

    for (i in 2..menNumber)
        result = (choiceInterval - 1 + result) % i + 1

    return result
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 *
 * //     Трудоёмкость: O(first.length * second.length)
 * //     Ресурсоёмкость: O(min(first.length, second.length))
 */
fun longestCommonSubstring(first: String, second: String): String {
    require(first.isNotEmpty() && second.isNotEmpty())

    if (first.length < second.length)
        return longestCommonSubstring(second, first)

    var previous = IntArray(second.length) { 0 }
    var current = IntArray(second.length) { 0 }

    var maxLength = 0
    var bestEnd = 0

    for (i in 1..first.length) {
        for (j in 1..second.length)
            if (first[i - 1] == second[j - 1]) {
                current[j] = previous[j - 1] + 1

                if (current[j] > maxLength) {
                    maxLength = current[j]
                    bestEnd = i
                }
            } else
                current[j] = 0

        val swap = previous
        previous = current
        current = swap
    }

    return second.substring(bestEnd - maxLength, bestEnd)
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
fun calcPrimesNumber(limit: Int): Int {
    TODO()
}

/**
 * Балда
 * Сложная
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}