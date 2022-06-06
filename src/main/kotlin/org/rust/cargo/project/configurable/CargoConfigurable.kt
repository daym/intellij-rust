/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.project.configurable

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.panel
import org.rust.RsBundle
import org.rust.cargo.project.settings.RustProjectSettingsService
import org.rust.cargo.project.settings.rustSettings

class CargoConfigurable(project: Project) : RsConfigurableBase(project, RsBundle.message("settings.rust.cargo.name")) {
    private val settings: RustProjectSettingsService = project.rustSettings

    override fun createPanel(): DialogPanel = panel {
        row {
            checkBox(
                RsBundle.message("settings.rust.cargo.show.first.error.label"),
                settings.state::autoShowErrorsInEditor
            )
        }
        row {
            checkBox(
                RsBundle.message("settings.rust.cargo.auto.update.project.label"),
                settings.state::autoUpdateEnabled
            )
        }
        row {
            checkBox(
                RsBundle.message("settings.rust.cargo.compile.all.targets.label"),
                settings.state::compileAllTargets,
                comment = RsBundle.message("settings.rust.cargo.compile.all.targets.comment")
            )
        }
        row {
            checkBox(
                RsBundle.message("settings.rust.cargo.offline.mode.label"),
                settings.state::useOffline,
                comment = RsBundle.message("settings.rust.cargo.offline.mode.comment")
            )
        }
    }
}
