/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.resolve

class RsMacroResolveTest : RsResolveTestBase() {
    private val `$` = '$'
    fun `test resolve simple matching with multiple pattern definition`() = checkByCode("""
        macro_rules! test {
            ($`$`test:expr) => (
               //X
                $`$`test
                 //^
            )
            ($`$`test:expr) => (
                $`$`test
            )
        }
    """)

    fun `test resolve simple matching with multiple matching`() = checkByCode("""
        macro_rules! test {
            ($`$`test:expr, $`$`ty:ty) => (
               //X
                $`$`test
                 //^
            )
        }
    """)

    fun `test resolve simple matching in complex matching`() = checkByCode("""
        macro_rules! test {
            ($`$`($`$`test:expr),+, $`$`ty:ty) => (
              //X
                $`$`($`$`test),+
                //^
            )
        }
    """)

    fun `test resolve macro same scope`() = checkByCode("""
        macro_rules! foo_bar { () => () }
        //X
        foo_bar!();
        //^
    """)

    fun `test resolve macro in function`() = checkByCode("""
        macro_rules! foo_bar { () => () }
        //X
        fn main() {
            foo_bar!();
            //^
        }
    """)

    fun `test resolve macro mod lower`() = checkByCode("""
        macro_rules! foo_bar { () => () }
        //X
        mod b {
            fn main() {
                foo_bar!();
                //^
            }
        }
    """)

    fun `test resolve macro mod`() = checkByCode("""
        #[macro_use]
        mod a {
            macro_rules! foo_bar { () => () }
            //X
        }
        mod b {
            fn main() {
                foo_bar!();
                //^
            }
        }
    """)

    fun `test resolve macro missing macro_use`() = checkByCode("""
        // Missing #[macro_use] here
        mod a {
            macro_rules! foo_bar { () => () }
        }
        fn main() {
            foo_bar!();
            //^ unresolved
        }
    """)

    fun `test resolve macro missing macro_use mod`() = checkByCode("""
        // Missing #[macro_use] here
        mod a {
            macro_rules! foo_bar { () => () }
        }
        mod b {
            fn main() {
                foo_bar!();
                //^ unresolved
            }
        }
    """)

    // More macro tests in [RsPackageLibraryResolveTest] and [RsStubOnlyResolveTest]
}
