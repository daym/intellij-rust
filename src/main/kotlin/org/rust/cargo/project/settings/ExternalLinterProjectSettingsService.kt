/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.project.settings

import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import org.rust.cargo.project.settings.ExternalLinterProjectSettingsService.State
import org.rust.cargo.toolchain.ExternalLinter
import org.rust.cargo.toolchain.RustChannel

val RustProjectSettingsService.externalLinter: ExternalLinterProjectSettingsService
    get() = project.getService(ExternalLinterProjectSettingsService::class.java)
        ?: error("Failed to get ${serviceName}Service for $this")

private const val serviceName: String = "ExternalLinterProjectSettings"

@com.intellij.openapi.components.State(name = serviceName, storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class ExternalLinterProjectSettingsService(project: Project) : RsProjectSettingsServiceBase<State>(project, State()) {
    val tool: ExternalLinter get() = state.tool
    val additionalArguments: String get() = state.additionalArguments
    val channel: RustChannel get() = state.channel
    val envs: Map<String, String> get() = state.envs
    val runOnTheFly: Boolean get() = state.runOnTheFly

    override fun noStateLoaded() {
        val rustSettings = project.rustSettings
        modify {
            it.tool = rustSettings.state.externalLinter
            rustSettings.state.externalLinter = ExternalLinter.DEFAULT
            it.additionalArguments = rustSettings.state.externalLinterArguments
            rustSettings.state.externalLinterArguments = ""
            it.runOnTheFly = rustSettings.state.runExternalLinterOnTheFly
            rustSettings.state.runExternalLinterOnTheFly = false
        }
    }

    class State : StateBase<State>() {
        @AffectsHighlighting
        var tool by enum(ExternalLinter.DEFAULT)
        @AffectsHighlighting
        var additionalArguments by property("") { it.isEmpty() }
        @AffectsHighlighting
        var channel by enum(RustChannel.DEFAULT)
        @AffectsHighlighting
        var envs by map<String, String>()
        @AffectsHighlighting
        var runOnTheFly by property(false)

        override fun copy(): State {
            val state = State()
            state.copyFrom(this)
            return state
        }
    }

    override fun createSettingsChangedEvent(oldEvent: State, newEvent: State): SettingsChangedEvent {
        return SettingsChangedEvent(oldEvent, newEvent)
    }

    class SettingsChangedEvent(oldState: State, newState: State) : SettingsChangedEventBase<State>(oldState, newState)
}
