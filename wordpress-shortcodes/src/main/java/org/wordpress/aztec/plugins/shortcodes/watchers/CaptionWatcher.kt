package org.wordpress.aztec.plugins.shortcodes.watchers

import android.text.Spanned
import org.wordpress.aztec.AztecText
import org.wordpress.aztec.Constants
import org.wordpress.aztec.plugins.shortcodes.spans.CaptionShortcodeSpan
import org.wordpress.aztec.util.SpanWrapper
import org.wordpress.aztec.watchers.BlockElementWatcher

class CaptionWatcher(private val aztecText: AztecText) : BlockElementWatcher(aztecText) {

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        super.onTextChanged(s, start, before, count)

        if (start + count < s.length && s[start + count] == Constants.IMG_CHAR) {
            val spans = SpanWrapper.getSpans<CaptionShortcodeSpan>(aztecText.text, start + count, start + count,
                    CaptionShortcodeSpan::class.java)
            spans.forEach {

                // if text is added right before an image, move it out of the caption
                if (it.start < start + count && it.end > start + count) {
                    aztecText.text.setSpan(it.span, start + count, it.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        } else if (count > 0) {
            val spans = SpanWrapper.getSpans<CaptionShortcodeSpan>(aztecText.text, start, start, CaptionShortcodeSpan::class.java)
            spans.forEach {

                // if text is added right after an image, move it below the caption
                if (start > 0 && s[start - 1] == Constants.IMG_CHAR && s[start] != Constants.NEWLINE) {
                    val newText = "" + s.subSequence(start, start + count)
                    aztecText.text.insert(it.end, Constants.NEWLINE_STRING)
                    aztecText.text.delete(start, start + count)
                    aztecText.text.insert(it.end, newText)
                    aztecText.setSelection(it.end + newText.length)
                }
            }
        }
    }
}