package io.qtalk.qgamejsinterfacetester.views

import android.animation.Animator
import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.TextViewCompat
import io.qtalk.qgamejsinterfacetester.R


class ExpandableLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val attribArray: TypedArray
    private lateinit var headerContentLayout: LinearLayout
    private lateinit var headerTextView: TextView
    private lateinit var subTitleTextView: TextView
    private lateinit var iconLabelTextView: TextView
    private lateinit var directionCaret: AppCompatImageView
    private var isExpanded = false

    init {
        orientation = VERTICAL
        View.inflate(context, R.layout.expandable_linear_layout, this)
        attribArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ExpandableLinearLayout, 0, 0)
    }

    fun getHeaderLayout() = headerContentLayout

    fun getSubTitleTextView() = subTitleTextView

    fun getIconLabelTextView() = iconLabelTextView

    fun getDirectionIconImageView() = directionCaret

    override fun onFinishInflate() {
        super.onFinishInflate()
        headerContentLayout = findViewById(R.id.contentHeaderLayout)
        directionCaret = findViewById(R.id.directionCaret)

        headerTextView = findViewById(R.id.header)
        subTitleTextView = findViewById(R.id.subtitle)
        iconLabelTextView = findViewById(R.id.iconLabel)

        headerTextView.setVisibilityByText(getTextFromAttribByRes(attribArray, R.styleable.ExpandableLinearLayout_headerText))
        headerTextView.setTextViewStyle(getStyleFromAttribByRes(attribArray, R.styleable.ExpandableLinearLayout_headerTextStyle))

        subTitleTextView.setVisibilityByText(getTextFromAttribByRes(attribArray, R.styleable.ExpandableLinearLayout_subtitleText))
        subTitleTextView.setTextViewStyle(getStyleFromAttribByRes(attribArray, R.styleable.ExpandableLinearLayout_subtitleTextStyle))

        iconLabelTextView.setTextViewStyle(getStyleFromAttribByRes(attribArray, R.styleable.ExpandableLinearLayout_iconLabelTextStyle))
        iconLabelTextView.setVisibilityByText(getTextFromAttribByRes(attribArray, R.styleable.ExpandableLinearLayout_iconLabelText))

        val iconSize: Float = attribArray.getDimension(R.styleable.ExpandableLinearLayout_iconSize, 0f)
        if (iconSize != 0f) {
            directionCaret.layoutParams.height = iconSize.toInt()
            directionCaret.layoutParams.width = iconSize.toInt()
        }
        val backgroundRes: Int = attribArray.getResourceId(R.styleable.ExpandableLinearLayout_headerBackground, 0)
        if (backgroundRes != 0)
            headerContentLayout.setBackgroundResource(backgroundRes)
//        val canAutoGenerateSubText : Boolean = attribArray.getBoolean(R.styleable.ExpandableLinearLayout_autoGenerateSubtitleText, true)
        subTitleTextView.setVisibilityByText(/*if (canAutoGenerateSubText) autoGenerateSubTitleText() else */getTextFromAttribByRes(attribArray, R.styleable.ExpandableLinearLayout_subtitleText))

        headerContentLayout.setOnClickListener { toggleContentLayouts() }

        attribArray.recycle()
    }

    private fun getStyleFromAttribByRes(attribArray: TypedArray, @StyleableRes styleableRes: Int): Int {
        return attribArray.getResourceId(styleableRes, 0)
    }

    private fun getTextFromAttribByRes(attribArray: TypedArray, @StyleableRes styleableRes: Int): String? {
        return attribArray.getText(styleableRes)?.toString()
    }

    private fun toggleContentLayouts(defaultDuration: Int = 200) {
        for (i: Int in 1 until childCount) {
            val currentChild = getChildAt(i)
            // only toggle visibility if the child's tag doesn't mention that needs to be forced invisible
            if (!(currentChild?.layoutParams as LayoutParams).isForcedInvisible) {
                currentChild.visibility = View.GONE - currentChild.visibility
                isExpanded = currentChild.visibility == View.VISIBLE
            }
        }
        animateDirectionIcon(defaultDuration)
    }

    private fun test(view: View?) {
        val s = when (view) {
            is TextView -> view.text
            is ExpandableLinearLayout -> view.headerTextView.text
            else -> "nothing is coming"
        }
        Log.d("ExpandableLinearLayout", "test: $s")
    }

    private fun autoGenerateSubTitleText(childStartIndex: Int = 1): String {
        val s = StringBuilder("")
        fun StringBuilder.appendWithComma(charSequence: CharSequence): StringBuilder {
            return apply {
                if (this.isNotEmpty() || this.isNotBlank()) {
                    append(", ")
                    append(charSequence)
                }
            }
        }
        for (i in childStartIndex until childCount) {
            getChildAt(i)?.let {
                when (it) {
                    is TextView -> s.append(it.text)
                    is ExpandableLinearLayout -> s.append(it.headerTextView.text)
//                    is ViewParent -> s.append(autoGenerateSubTitleText(0))
                    else -> s.append("")
                }
            }
        }
        return s.toString()
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putBoolean("isExpanded", this.isExpanded) // ... save stuff
        return bundle
    }

    public override fun onRestoreInstanceState(savedState: Parcelable?) {
        var state = savedState
        if (state is Bundle)
        // implicit null check
        {
            val bundle = state as Bundle?
            if (bundle != null) {
                this.isExpanded = bundle.getBoolean("isExpanded", false)  // ... load stuff
                state = bundle.getParcelable("superState")
            }
        }
        super.onRestoreInstanceState(state)
        if (isExpanded) {
            toggleContentLayouts(0)
        }
    }

    private fun animateDirectionIcon(defaultDuration: Int = 200) {
        directionCaret.animate().rotation(if (isExpanded) 180f else 0f)
            .setDuration(defaultDuration.toLong()).setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if ((directionCaret.rotation % 180).toInt() == 0) return

                    if (isExpanded) {
                        directionCaret.animate().rotation(180f).duration = 200
                    } else {
                        directionCaret.animate().rotation(0f).duration = 200
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
    }

    private fun TextView.setTextViewStyle(@StyleRes styleRes: Int) {
        if (styleRes != 0) {
            TextViewCompat.setTextAppearance(this, styleRes)
        }
    }

    private fun TextView.setVisibilityByText(text: String? = null) {
        this.visibility = if (text != null) View.VISIBLE else View.GONE
        this.text = text
    }

    // --- code below only added to handle custom attrs for child views.
    override fun generateLayoutParams(attrs: AttributeSet?): LinearLayout.LayoutParams {
        return ExpandableLinearLayout.LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(lp)
    }

    override fun generateDefaultLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is ExpandableLinearLayout.LayoutParams
    }

    class LayoutParams(context: Context?, attrs: AttributeSet?) : LinearLayout.LayoutParams(context, attrs) {
        var isForcedInvisible: Boolean = false

        init {
            val attribArray: TypedArray? = context?.obtainStyledAttributes(attrs, R.styleable.ExpandableLinearLayout_Layout)
            attribArray?.getBoolean(R.styleable.ExpandableLinearLayout_Layout_isForcedInvisible, false)?.let {
                isForcedInvisible = it
            }
            attribArray?.recycle()
        }
    }
}