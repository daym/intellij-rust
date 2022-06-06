/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.project.configurable

import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.Label
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.toBinding
import org.rust.RsBundle
import org.rust.cargo.project.model.cargoProjects
import org.rust.cargo.project.settings.ExternalLinterProjectSettingsService
import org.rust.cargo.project.settings.externalLinter
import org.rust.cargo.project.settings.rustSettings
import org.rust.cargo.toolchain.ExternalLinter
import org.rust.cargo.toolchain.RustChannel
import org.rust.cargo.util.CargoCommandCompletionProvider
import org.rust.cargo.util.RsCommandLineEditor
import javax.swing.JLabel

class RsExternalLinterConfigurable(project: Project) : RsConfigurableBase(project, RsBundle.message("settings.rust.external.linters.name")) {
    private val settings: ExternalLinterProjectSettingsService = project.rustSettings.externalLinter

    private val additionalArguments: RsCommandLineEditor =
        RsCommandLineEditor(project, CargoCommandCompletionProvider(project.cargoProjects, "check ") { null })

    private val channelLabel: JLabel = Label(RsBundle.message("settings.rust.external.linters.channel.label"))
    private val channel: ComboBox<RustChannel> = ComboBox<RustChannel>().apply {
        RustChannel.values()
            .sortedBy { it.index }
            .forEach { addItem(it) }
    }

    private val environmentVariables: EnvironmentVariablesComponent = EnvironmentVariablesComponent()

    override fun createPanel(): DialogPanel = panel {
        row(RsBundle.message("settings.rust.external.linters.tool.label")) {
            comboBox(
                EnumComboBoxModel(ExternalLinter::class.java),
                settings.state::tool,
            ).comment(RsBundle.message("settings.rust.external.linters.tool.comment"))
        }

        blockRow {
            row(RsBundle.message("settings.rust.external.linters.additional.arguments.label")) {
                additionalArguments(pushX, growX)
                    .comment(RsBundle.message("settings.rust.external.linters.additional.arguments.comment"))
                    .withBinding(
                        componentGet = { it.text },
                        componentSet = { component, value -> component.text = value },
                        modelBinding = settings.state::additionalArguments.toBinding()
                    )

                channelLabel.labelFor = channel
                channelLabel()
                channel().withBinding(
                    componentGet = { it.item },
                    componentSet = { component, value -> component.item = value },
                    modelBinding = settings.state::channel.toBinding()
                )
            }

            row(environmentVariables.label) {
                environmentVariables(growX)
                    .withBinding(
                        componentGet = { it.envs },
                        componentSet = { component, value -> component.envs = value },
                        modelBinding = settings.state::envs.toBinding()
                    )
            }
        }

        row {
            checkBox(
                RsBundle.message("settings.rust.external.linters.on.the.fly.label"),
                settings.state::runOnTheFly,
                comment = RsBundle.message("settings.rust.external.linters.on.the.fly.comment")
            )
        }
    }
}
