package ru.kabylin.andrey.vocabulator.containers

class Either<out L, out R>(val left: L?, val right: R?) {
    init {
        assert((left == null && right != null) || (right == null && left != null))
    }

    companion object {
        fun <L, R> left(left: L): Either<L, R> {
            return Either<L, R>(left, null)
        }

        fun <L, R> right(right: R): Either<L, R> {
            return Either<L, R>(null, right)
        }
    }

    fun apply(left: (value: L) -> Unit, right: (value: R) -> Unit) {
        when {
            this.left != null -> left(this.left)
            this.right != null -> right(this.right)
        }
    }
}
