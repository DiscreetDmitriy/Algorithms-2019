package lesson1

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 *
 * //     Трудоёмкость: O(n * log(n))
 * //     Ресурсоёмкость: O(n)
 */
fun sortTimes(inputName: String, outputName: String) {
    val sortedTimes = mutableListOf<Date>()
    val timeFormat = SimpleDateFormat("hh:mm:ss a")

    for (line in File(inputName).readLines()) {
        require("""\d{2}:\d{2}:\d{2} (AM|PM)""".toRegex().matches(line))

        sortedTimes.add(timeFormat.parse(line))
    }

    sortedTimes.sort()

    File(outputName).writeText(sortedTimes
        .joinToString("\n") { date ->
            timeFormat.format(date)
        })
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 *
 * //     Трудоёмкость: O(n)
 * //     Ресурсоёмкость: O(n)
 */
fun sortAddresses(inputName: String, outputName: String) {
    val mapOfAddresses = mutableMapOf<Pair<String, Int>, SortedSet<String>>()

    for (line in File(inputName).readLines()) {
        require(
            """[А-ЯЁA-Z][А-ЯЁA-Zа-яa-zё-]* [А-ЯA-ZЁ][А-ЯЁA-Zа-яa-zё-]* - [А-ЯA-ZЁ][А-ЯЁA-Zа-яa-zё-]* \d+"""
                .toRegex().matches(line)
        )
        val (name, address) = line.split(" - ")
        val splitAddress = address.split(" ")
        val street = splitAddress[0]
        val house = splitAddress[1].toInt()

        mapOfAddresses.getOrPut(street to house) { sortedSetOf(name) }.add(name)
    }

    val sortedAddresses = mapOfAddresses.toSortedMap(compareBy({ it.first }, { it.second }))

    File(outputName).writeText(sortedAddresses.toList()
        .joinToString("\n") { (address, name) ->
            "${address.first} ${address.second} - ${name.joinToString(", ")}"
        })
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 *
 * //     Трудоёмкость: O(n)
 * //     Ресурсоёмкость: O(1)
 */
fun sortTemperatures(inputName: String, outputName: String) {
    val tempRepeats = IntArray(7731) { 0 }
    val min = 273.0 * 10

    val reader = File(inputName).bufferedReader()
    var line = reader.readLine()

    while (line != null) {
        val tempDouble = line.toDouble()

        require(tempDouble in -273.0..500.0)

        val tempInt = (tempDouble * 10 + min).toInt()

        tempRepeats[tempInt]++

        line = reader.readLine()
    }

    File(outputName).bufferedWriter().use {
        for (i in 0..7730)
            if (tempRepeats[i] != 0)
                it.write("${((i - 2730) / 10.0)}\n".repeat(tempRepeats[i]))
    }
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 *
 * //     Трудоёмкость: O(n)
 * //     Ресурсоёмкость: O(n)
 */
fun sortSequence(inputName: String, outputName: String) {
    val numbers = File(inputName).readLines().map { it.toInt() }

    val duplicates = numbers
        .groupingBy { it }
        .eachCount()

    val maxRepeats = duplicates.values.max() ?: 0
    var minRepeatedNumber = Int.MAX_VALUE

    for ((number, repeats) in duplicates)
        if (repeats == maxRepeats && number < minRepeatedNumber)
            minRepeatedNumber = number

    val sequence = mutableListOf<Int>()
    val maxSequence = mutableListOf<Int>()

    for (number in numbers)
        if (number == minRepeatedNumber)
            maxSequence.add(number)
        else
            sequence.add(number)

    sequence.addAll(maxSequence)

    File(outputName).writeText(sequence.joinToString("\n"))
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 *
 * //     Трудоёмкость: O(n)
 * //     Ресурсоёмкость: O(1)
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    var firstIndex = 0
    var secondIndex = first.size

    for (i in second.indices)
        if (secondIndex >= second.size ||
            firstIndex < first.size && first[firstIndex] <= second[secondIndex]!!
        ) {
            second[i] = first[firstIndex]
            firstIndex++
        } else {
            second[i] = second[secondIndex]
            secondIndex++
        }
}

