interface NDArray : SizeAware, DimensionAware {
    /*
     * Получаем значение по индексу point
     *
     * Если размерность point не равна размерности NDArray
     * бросаем IllegalPointDimensionException
     *
     * Если позиция по любой из размерностей некорректна с точки зрения
     * размерности NDArray, бросаем IllegalPointCoordinateException
     */
    fun at(point: Point): Int

    /*
     * Устанавливаем значение по индексу point
     *
     * Если размерность point не равна размерности NDArray
     * бросаем IllegalPointDimensionException
     *
     * Если позиция по любой из размерностей некорректна с точки зрения
     * размерности NDArray, бросаем IllegalPointCoordinateException
     */
    fun set(point: Point, value: Int)

    /*
     * Копируем текущий NDArray
     *
     */
    fun copy(): NDArray

    /*
     * Создаем view для текущего NDArray
     *
     * Ожидается, что будет создан новая реализация интерфейса.
     * Но она не должна быть видна в коде, использующем эту библиотеку как внешний артефакт
     *
     * Должна быть возможность делать view над view.
     *
     * In-place-изменения над view любого порядка видна в оригнале и во всех view
     *
     * Проблемы thread-safety игнорируем
     */
    fun view(): NDArray

    /*
     * In-place сложение
     *
     * Размерность other либо идентична текущей, либо на 1 меньше
     * Если она на 1 меньше, то по всем позициям, кроме "лишней", она должна совпадать
     *
     * Если размерности совпадают, то делаем поэлементное сложение
     *
     * Если размерность other на 1 меньше, то для каждой позиции последней размерности мы
     * делаем поэлементное сложение
     *
     * Например, если размерность this - (10, 3), а размерность other - (10), то мы для три раза прибавим
     * other к каждому срезу последней размерности
     *
     * Аналогично, если размерность this - (10, 3, 5), а размерность other - (10, 5), то мы для пять раз прибавим
     * other к каждому срезу последней размерности
     */
    fun add(other: NDArray)

    /*
     * Умножение матриц. Immutable-операция. Возвращаем NDArray
     *
     * Требования к размерности - как для умножения матриц.
     *
     * this - обязательно двумерна
     *
     * other - может быть двумерной, с подходящей размерностью, равной 1 или просто вектором
     *
     * Возвращаем новую матрицу (NDArray размерности 2)
     *
     */
    fun dot(other: NDArray): NDArray
}

/*
 * Базовая реализация NDArray
 *
 * Конструкторы должны быть недоступны клиенту
 *
 * Инициализация - через factory-методы ones(shape: Shape), zeros(shape: Shape) и метод copy
 */
class DefaultNDArray private constructor(
    private val shape: Shape,
    private val defaultValue: Int = 0,
    private val points: IntArray = IntArray(shape.size) { defaultValue }
) : NDArray {


    companion object {
        fun ones(shape: Shape): DefaultNDArray {
            return DefaultNDArray(shape, 1)
        }

        fun zeros(shape: Shape): DefaultNDArray {
            return DefaultNDArray(shape, 0)
        }
    }

    override val ndim: Int = shape.ndim
    override fun dim(i: Int): Int = shape.dim(i)

    override val size: Int = shape.size

    override fun at(point: Point): Int {
        return points[findIdx(point)]
    }

    private fun findIdx(point: Point): Int {
        val pointDim = point.ndim
        val shapeDim = shape.ndim
        if (ndim != pointDim) {
            throw NDArrayException.IllegalPointDimensionException(pointDim, shapeDim, "wrong point dimension")
        }
        for (dim in 0 until shapeDim) {
            val shapeDimSize = shape.dim(dim)
            val pointValue = point.dim(dim)
            if (0 > pointValue || shapeDimSize <= pointValue) {
                throw NDArrayException.IllegalPointCoordinateException(
                    pointValue, shapeDimSize, "wrong point coordinate"
                )
            }
        }
        var idx = 0
        var block = size
        for (tdim in 0 until ndim) {
            val tsize = shape.dim(tdim)
            val coordinate = point.dim(tdim)
            block /= tsize
            idx += coordinate * block
        }
        return idx
    }

    override fun set(point: Point, value: Int) {
        points[findIdx(point)] = value
    }


    override fun copy(): NDArray = DefaultNDArray(this.shape, this.defaultValue, this.points.copyOf())

    override fun view(): NDArray = DefaultNDArrayView(this)

    class DefaultNDArrayView(array: NDArray) : NDArray by array

    override fun add(other: NDArray) {
        when (this.ndim) {
            other.ndim -> addEquals(other)
            other.ndim + 1 -> addNotEquals(other)
            else -> throw NDArrayException.IllegalDimensionException(
                "you can add array only with same dimension or one less"
            )
        }
    }

    private fun getPointByIndex(index: Int): Point {
        val coords = IntArray(this.ndim)
        var tindex = index
        var rest = size
        var current = 0
        while (rest > 0 && current < this.ndim) {
            val dim = this.dim(current)
            coords[current] = tindex / (rest / dim)
            rest /= dim
            tindex %= rest
            current++
        }
        return DefaultPoint(*coords)
    }

    private fun addEquals(other: NDArray) {
        for (dim in 0 until this.ndim) {
            if (this.dim(dim) != other.dim(dim)) {
                throw NDArrayException.IllegalDimensionException(
                    "you can add array with same size on each coordinate if they have same dimension"
                )
            }
        }
        addOther(other)
    }

    private fun addNotEquals(other: NDArray) {
        var brokenIdx = -1
        var diff = 0
        for (idx in 0 until this.ndim - 1) {
            if (this.dim(idx) != other.dim(idx + diff)) {
                if (brokenIdx != -1) {
                    throw NDArrayException.IllegalDimensionException(
                        "only one index must be different if dimension is one less"
                    )
                }
                brokenIdx = idx
                diff = -1
            }
        }
        addOther(other)
    }

    private fun getOtherPoint(other: NDArray, point: Point): Point {
        if (other.ndim == this.ndim)
            return point
        val array = IntArray(point.ndim - 1)
        for (i in 0 until other.ndim) {
            array[i] = point.dim(i)
        }
        return DefaultPoint(*array)
    }

    private fun addOther(other: NDArray) {
        for (index in 0 until this.points.size) {
            val point = getPointByIndex(index)
            this.set(
                point,
                this.at(point) + other.at(getOtherPoint(other, point))
            )
        }
    }

    override fun dot(other: NDArray): NDArray {
        if (this.ndim != 2 || other.ndim > 2)
            throw NDArrayException.IllegalDimensionException("you can't .dot only matrix with such dimensions")
        return if (other.ndim == 2)
            dotMatrix(other)
        else
            dotVector(other)
    }

    private fun dotVector(other: NDArray): NDArray {
        if (this.dim(1) != other.dim(0))
            throw NDArrayException.IllegalDimensionException("uncompetitive matrices")
        val resultArray = zeros(DefaultShape(this.dim(0)))
        for (dim in 0 until this.dim(0)) {
            var sum = 0
            for (idx in 0 until this.dim(1)) {
                sum += this.at(DefaultPoint(dim, idx)) * other.at(DefaultPoint(idx))
            }
            resultArray.set(DefaultPoint(dim), sum)
        }
        return resultArray
    }

    private fun dotMatrix(other: NDArray): NDArray {
        if (other.ndim != 2)
            throw NDArrayException.IllegalDimensionException("matrix must be rectangle")
        if (this.dim(1) != other.dim(0))
            throw NDArrayException.IllegalDimensionException("uncompetitive matrices")
        val resultArray = zeros(DefaultShape(this.dim(0), other.dim(1)))
        for (leftDim in 0 until this.dim(0)) {
            for (rightDim in 0 until other.dim(1)) {
                var sum = 0
                for (idx in 0 until this.dim(1)) {
                    sum += this.at(DefaultPoint(leftDim, idx)) * other.at(DefaultPoint(idx, rightDim))
                }
                resultArray.set(DefaultPoint(leftDim, rightDim), sum)
            }
        }
        return resultArray;
    }
}

sealed class NDArrayException : Exception() {
    class IllegalPointDimensionException(
        val pointDimension: Int,
        val arrayDimension: Int,
        override val message: String
    ) : NDArrayException() {
    }

    class IllegalPointCoordinateException(
        val pointCoordinate: Int,
        val arrayDimensionSize: Int,
        override val message: String
    ) : NDArrayException() {
    }

    class IllegalDimensionException(override val message: String) : NDArrayException() {
    }
}
