package org.rust.lang.core.psi.impl.mixin

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import org.rust.ide.icons.RustIcons
import org.rust.lang.core.psi.*
import org.rust.lang.core.psi.impl.RustPsiImplUtil
import org.rust.lang.core.psi.impl.RustStubbedNamedElementImpl
import org.rust.lang.core.stubs.RsConstantStub

abstract class RustConstantImplMixin : RustStubbedNamedElementImpl<RsConstantStub>, RsConstant {
    constructor(node: ASTNode) : super(node)

    constructor(stub: RsConstantStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun getIcon(flags: Int) = iconWithVisibility(flags, when (kind) {
        RustConstantKind.CONST -> RustIcons.CONSTANT
        RustConstantKind.MUT_STATIC -> RustIcons.MUT_STATIC
        RustConstantKind.STATIC -> RustIcons.STATIC
    })

    override val isPublic: Boolean get() = RustPsiImplUtil.isPublic(this, stub)
}

enum class RustConstantKind {
    STATIC,
    MUT_STATIC,
    CONST
}

val RsConstant.kind: RustConstantKind get() = when {
    mut != null -> RustConstantKind.MUT_STATIC
    const != null -> RustConstantKind.CONST
    else -> RustConstantKind.STATIC
}

enum class RustConstantRole {
    FREE,
    TRAIT_CONSTANT,
    IMPL_CONSTANT,
    FOREIGN
}

val RsConstant.role: RustConstantRole get() {
    return when (parent) {
        is RustItemsOwner -> RustConstantRole.FREE
        is RsTraitItem -> RustConstantRole.TRAIT_CONSTANT
        is RsImplItem -> RustConstantRole.IMPL_CONSTANT
        is RsForeignModItem -> RustConstantRole.FOREIGN
        else -> error("Unexpected constant parent: $parent")
    }
}

val RsConstant.default: PsiElement?
    get() = node.findChildByType(RustTokenElementTypes.DEFAULT)?.psi
