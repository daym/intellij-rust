/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.project.configurable

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.layout.panel
import org.rust.RsBundle
import org.rust.cargo.project.model.cargoProjects
import org.rust.cargo.project.settings.RustProjectSettingsService
import org.rust.cargo.project.settings.RustProjectSettingsService.MacroExpansionEngine
import org.rust.cargo.project.settings.rustSettings
import org.rust.cargo.project.settings.ui.RustProjectSettingsPanel
import org.rust.cargo.toolchain.RsToolchainBase
import org.rust.openapiext.pathAsPath
import java.nio.file.Paths
import javax.swing.ListCellRenderer

class RsProjectConfigurable(
    project: Project
) : RsConfigurableBase(project, RsBundle.message("settings.rust.toolchain.name")), Configurable.NoScroll {
    private val settings: RustProjectSettingsService = project.rustSettings
    private val projectDir = project.cargoProjects.allProjects.firstOrNull()?.rootDir?.pathAsPath ?: Paths.get(".")
    private val rustProjectSettings = RustProjectSettingsPanel(projectDir)

    override fun createPanel(): DialogPanel = panel {
        rustProjectSettings.attachTo(this)
        row(RsBundle.message("settings.rust.toolchain.expand.macros.label")) {
            comboBox(
                EnumComboBoxModel(MacroExpansionEngine::class.java),
                settings.state::macroExpansionEngine,
                createExpansionEngineListRenderer()
            ).comment(RsBundle.message("settings.rust.toolchain.expand.macros.comment"))
        }
        row {
            checkBox(
                RsBundle.message("settings.rust.toolchain.inject.rust.in.doc.comments.checkbox"),
                settings.state::doctestInjectionEnabled
            )
        }
    }

    private fun createExpansionEngineListRenderer(): ListCellRenderer<MacroExpansionEngine?> {
        return SimpleListCellRenderer.create("") {
            when (it) {
                MacroExpansionEngine.DISABLED -> RsBundle.message("settings.rust.toolchain.expand.macros.disable.label")
                MacroExpansionEngine.OLD -> RsBundle.message("settings.rust.toolchain.expand.macros.old.engine.label")
                MacroExpansionEngine.NEW -> RsBundle.message("settings.rust.toolchain.expand.macros.new.engine.label")
                null -> error("Unreachable")
            }
        }
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
        Disposer.dispose(rustProjectSettings)
    }

    override fun reset() {
        super.reset()
        val toolchain = settings.toolchain ?: RsToolchainBase.suggest(projectDir)

        rustProjectSettings.data = RustProjectSettingsPanel.Data(
            toolchain = toolchain,
            explicitPathToStdlib = settings.explicitPathToStdlib
        )
    }

    override fun isModified(): Boolean {
        if (super.isModified()) return true
        val data = rustProjectSettings.data
        return data.toolchain?.location != settings.toolchain?.location
            || data.explicitPathToStdlib != settings.explicitPathToStdlib
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        rustProjectSettings.validateSettings()
        settings.modify {
            it.toolchain = rustProjectSettings.data.toolchain
            it.explicitPathToStdlib = rustProjectSettings.data.explicitPathToStdlib
        }
        super.apply()
    }
}
