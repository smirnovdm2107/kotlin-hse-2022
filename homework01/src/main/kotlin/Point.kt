interface Point : DimensionAware

/**
 * Реализация Point по умолчаению
 *
 * Должны работать вызовы DefaultPoint(10), DefaultPoint(12, 3), DefaultPoint(12, 3, 12, 4, 56)
 * с любым количество параметров
 *
 * Сама коллекция параметров недоступна, доступ - через методы интерфейса
 */
class DefaultPoint(private vararg val coordinates: Int) : Point {

    override val ndim: Int = this.coordinates.size
    override fun dim(i: Int): Int = coordinates[i]

    operator fun plus(other: DefaultPoint): DefaultPoint = DefaultPoint(*coordinates.mapIndexed { idx, it ->
        it + other.coordinates[idx]
    }.toIntArray())

}
