/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.dfa

import org.intellij.lang.annotations.Language
import org.rust.RsTestBase
import org.rust.lang.core.psi.RsExpr
import org.rust.lang.core.psi.RsFunction
import org.rust.lang.core.psi.ext.ancestorStrict
import org.rust.lang.core.types.liveness
import org.rust.stdext.mapToSet

class RsLivenessTest : RsTestBase() {
    private fun testExpr(@Language("Rust") code: String) {
        InlineFile(code)
        check()
    }

    private fun check() {
        val expectedLastUsages = findElementsWithDataAndOffsetInEditor<RsExpr>().mapToSet { it.first }
        val func = expectedLastUsages.first().ancestorStrict<RsFunction>()!!
        val actualLastUsages = func.liveness!!.lastUsages.values.flatten().toSet()
        check(expectedLastUsages == actualLastUsages) {
            "Last usages mismatch. Expected: ${expectedLastUsages.map { it.text}}, found: ${actualLastUsages.map { it.text }}"
        }
    }

    fun `test one last usage 1`() = testExpr("""
        fn main() {
            let x = 42;
            consume(x);
                  //^ last
        }
    """)

    fun `test one last usage 2`() = testExpr("""
        fn main() {
            let x = 42;
            consume1(x);
            consume2(x);
                   //^ last
        }
    """)

    fun `test two last usages if else 1`() = testExpr("""
        fn main() {
            let x = 42;
            consume1(x);
            if flag {
                consume2(x);
                       //^ last
            }
        }
    """)

    fun `test two last usages if else 2`() = testExpr("""
        fn main() {
            let x = 42;
            if flag {
                consume1(x);
                       //^ last
            } else {
                consume2(x);
                       //^ last
            }
        }
    """)
}
