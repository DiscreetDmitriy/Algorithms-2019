package lesson2

import java.io.File
import kotlin.math.sqrt


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

    var maxDiff = Int.MIN_VALUE
    var minIndex = 0
    var buyAndSellMoments = 1 to 2

    for (i in 1 until stocks.size) {
        if (maxDiff < stocks[i] - stocks[minIndex]) {
            maxDiff = stocks[i] - stocks[minIndex]
            buyAndSellMoments = minIndex + 1 to i + 1
        }
        if (stocks[minIndex] > stocks[i]) minIndex = i
    }
    return buyAndSellMoments
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
    require(menNumber > 0 && choiceInterval > 0 && menNumber >= choiceInterval)

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

    for (i in first.indices) {
        for (j in second.indices)
            if (first[i] == second[j]) {
                current[j] =
                    (if (j == 0) 0 else previous[j - 1]) + 1

                if (current[j] > maxLength) {
                    maxLength = current[j]
                    bestEnd = j + 1
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
 *
 * //     Трудоёмкость: O(n * log(log(n)))
 * //     Ресурсоёмкость: O(n)
 */
fun calcPrimesNumber(limit: Int): Int {
    if (limit < 2) return 0

    val values = BooleanArray((limit / 2)) { true }

    for (i in 3..sqrt(limit.toDouble()).toInt() step 2)
        if (values[i / 2])
            for (j in i * i..limit step i)
                if (j % 2 == 1) values[j / 2] = false

    return values.filter { it }.count()
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
 *
 * //     Трудоёмкость: O(length * width * words.size * word.length)
 * //     Ресурсоёмкость: O(length * width)
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    require(inputName.isNotEmpty() && words.isNotEmpty())

    val matrix =
        File(inputName).readLines()
            .map { line ->
                line.split(" ")
                    .map { char -> char[0] }
            }

    val foundWords = mutableSetOf<String>()

    for (y in matrix.indices)
        for (x in matrix[y].indices)
            for (word in words)
                if (matrix[y][x] == word[0]
                    && matrix.containsWord(
                        partOfWord = word.substring(1),
                        coordinates = x to y
                    )
                ) foundWords.add(word)

    return foundWords
}

private fun List<List<Char>>.containsWord(
    partOfWord: String, coordinates: Pair<Int, Int>
): Boolean {
    if (partOfWord.isEmpty()) return true

    val directions = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)

    for ((dx, dy) in directions) {
        val x = coordinates.first + dx
        val y = coordinates.second + dy

        if (x >= 0 && x < this[coordinates.second].size &&
            y >= 0 && y < this.size && this[y][x] == partOfWord.first()
            && this.containsWord(
                partOfWord = partOfWord.substring(1),
                coordinates = x to y
            )
        ) return true
    }

    return false
}

