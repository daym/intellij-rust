package org.rust.lang.refactoring

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.rust.lang.core.psi.RsPatBinding

class RustRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
        return element is RsPatBinding
    }

    override fun getIntroduceVariableHandler() = RustLocalVariableHandler()

    // needed this one too to get it to show up in the dialog.
    override fun getIntroduceVariableHandler(element: PsiElement?) = RustLocalVariableHandler()

}

