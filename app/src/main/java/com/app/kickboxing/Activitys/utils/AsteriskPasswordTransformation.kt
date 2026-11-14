package com.app.kickboxing.Activitys.utils

import android.text.method.PasswordTransformationMethod
import android.view.View

class AsteriskPasswordTransformation : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        return if (source == null) "" else PasswordCharSequence(source)
    }

    private class PasswordCharSequence(private val source: CharSequence) : CharSequence {
        override val length: Int
            get() = source.length

        override fun get(index: Int): Char {
            if (index < 0 || index >= length) throw IndexOutOfBoundsException()
            return '*'
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            if (startIndex < 0 || endIndex > length || startIndex > endIndex) {
                throw IndexOutOfBoundsException()
            }
            return "*".repeat(endIndex - startIndex)
        }

        override fun toString(): String {
            return "*".repeat(length)
        }
    }
}

