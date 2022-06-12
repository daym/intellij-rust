/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.openapiext

import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil
import java.io.IOException

class StdOutputCollectingProcessListener(private val myOutput: Appendable) : ProcessAdapter() {
    private var myStoredLength = 0

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        val text = synchronized(this) {
            if (myStoredLength > 16384) {
                return
            }
            val text = event.text
            if (StringUtil.isEmptyOrSpaces(text)) {
                return
            }
            myStoredLength += text.length
            text
        }

        try {
            myOutput.append(text)
        } catch (ignored: IOException) {
        }
    }
}
