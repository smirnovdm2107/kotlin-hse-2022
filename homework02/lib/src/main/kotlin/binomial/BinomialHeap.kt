package binomial

import kotlin.math.max

/*
 * BinomialHeap - реализация биномиальной кучи
 *
 * https://en.wikipedia.org/wiki/Binomial_heap
 *
 * Запрещено использовать
 *
 *  - var
 *  - циклы
 *  - стандартные коллекции
 *
 * Детали внутренней реазации должны быть спрятаны
 * Создание - только через single() и plus()
 *
 * Куча совсем без элементов не предусмотрена
 *
 * Операции
 *
 * plus с кучей
 * plus с элементом
 * top - взятие минимального элемента
 * drop - удаление минимального элемента
 */
class BinomialHeap<T : Comparable<T>> private constructor(private val trees: FList<BinomialTree<T>>) :
    SelfMergeable<BinomialHeap<T>> {
    companion object {
        fun <T : Comparable<T>> single(value: T): BinomialHeap<T> = BinomialHeap(flistOf(BinomialTree.single(value)))

    }

    /*
     * слияние куч
     *
     * Требуемая сложность - O(log(n))
     */
    override fun plus(other: BinomialHeap<T>): BinomialHeap<T> =
        BinomialHeap(plus(null, this.trees, other.trees, FList.nil()))

    private tailrec fun plus(
        prev: BinomialTree<T>?,
        left: FList<BinomialTree<T>>,
        right: FList<BinomialTree<T>>,
        list: FList<BinomialTree<T>>
    ): FList<BinomialTree<T>> {
        return when {
            left.isEmpty && right.isEmpty -> (if (prev == null) list else FList.Cons(prev, list)).reverse()
            left.isEmpty && !right.isEmpty -> plus(prev, right, left, list)
            !left.isEmpty && right.isEmpty && prev == null -> plus(
                prev,
                left.tail(),
                right,
                FList.Cons(left.first(), list)
            )

            !left.isEmpty && right.isEmpty && prev != null -> {
                if (prev.order < left.first().order)
                    plus(null, left, right, FList.Cons(prev, list))
                else if (prev.order > left.first().order)
                    plus(prev, left.tail(), right, FList.Cons(left.first(), list))
                else
                    plus(prev + left.first(), left.tail(), right, list)
            }

            !left.isEmpty && !right.isEmpty && left.first().order > right.first().order -> plus(prev, right, left, list)
            !left.isEmpty && !right.isEmpty && prev == null -> {
                if (left.first().order == right.first().order)
                    plus(left.first() + right.first(), left.tail(), right.tail(), list)
                else
                    plus(prev, left.tail(), right, FList.Cons(left.first(), list))
            }

            !left.isEmpty && !right.isEmpty && prev != null -> {
                if (left.first().order == right.first().order && left.first().order == prev.order) {
                    plus(left.first() + prev, left.tail(), right.tail(), FList.Cons(right.first(), list))
                } else if (left.first().order == prev.order) {
                    plus(left.first() + prev, left.tail(), right, list)
                } else if (left.first().order < prev.order) {
                    plus(prev, left.tail(), right, FList.Cons(left.first(), list))
                } else {
                    plus(null, left, right, FList.Cons(prev, list))
                }
            }

            else -> throw IllegalStateException("There is now way you can get here, compiler do not knot that:(")
        }
    }

    /*
     * добавление элемента
     * 
     * Требуемая сложность - O(log(n))
     */
    operator fun plus(elem: T): BinomialHeap<T> {
        return this.run { this + single(elem) }
    }

    /*
     * минимальный элемент
     *
     * Требуемая сложность - O(log(n))
     */
    fun top(): T = trees.minOf { tree -> tree.value }

    /*
     * удаление элемента
     *
     * Требуемая сложность - O(log(n))
     */
    fun drop(): BinomialHeap<T> {
        val minTree = trees.minOfWith({ tree1, tree2 -> tree1.value.compareTo(tree2.value) }) { tree -> tree }
        return BinomialHeap(trees.filter { tree -> tree != minTree })
            .plus(BinomialHeap(minTree.children.reverse()))
    }
}
