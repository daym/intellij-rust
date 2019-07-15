/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.runconfig

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner
import org.rust.cargo.runconfig.command.CargoCommandConfiguration

open class CargoCommandRunner : DefaultProgramRunner() {
    override fun getRunnerId(): String = RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile): Boolean =
        executorId == DefaultRunExecutor.EXECUTOR_ID &&
            profile is CargoCommandConfiguration &&
            profile.clean() is CargoCommandConfiguration.CleanConfiguration.Ok

    companion object {
        const val RUNNER_ID: String = "CargoCommandRunner"
    }
}