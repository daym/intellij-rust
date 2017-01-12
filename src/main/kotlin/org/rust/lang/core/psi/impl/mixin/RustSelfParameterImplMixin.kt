package org.rust.lang.core.psi.impl.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.stubs.IStubElementType
import org.rust.ide.icons.RustIcons
import org.rust.lang.core.psi.RsSelfParameter
import org.rust.lang.core.psi.impl.RustStubbedElementImpl
import org.rust.lang.core.stubs.RsSelfParameterStub

abstract class RustSelfParameterImplMixin : RustStubbedElementImpl<RsSelfParameterStub>,
                                            PsiNameIdentifierOwner,
                                            RsSelfParameter {
    constructor(node: ASTNode) : super(node)
    constructor(stub: RsSelfParameterStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getNameIdentifier(): PsiElement = self
    override fun getName(): String = "self"

    override fun setName(name: String): PsiElement? {
        // can't rename self
        throw UnsupportedOperationException()
    }

    override fun getIcon(flags: Int) = RustIcons.ARGUMENT
}


val RsSelfParameter.isMut: Boolean get() = stub?.isMut ?: (mut != null)
val RsSelfParameter.isRef: Boolean get() = stub?.isRef ?: (and != null)
