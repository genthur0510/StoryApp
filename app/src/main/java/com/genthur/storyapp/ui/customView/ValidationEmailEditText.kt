package com.genthur.storyapp.ui.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.genthur.storyapp.R

class ValidationEmailEditText: AppCompatEditText, TextWatcher {

    private var isError = false

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    init {
        addTextChangedListener(this)
    }

    override fun onTextChanged(
        s: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        if (!s.isNullOrEmpty()) {
            isError = !android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()
            error = if (isError) {
                context.getString(R.string.email_format_not_valid)
            } else {
                null
            }
        } else {
            error = null
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {}
}