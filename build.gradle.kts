/*
 * Copyright 2023 Sertan Canpolat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.sc.analysis)

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.safeargs) apply false
}

task("addPrePushGitHook") {
    if (File("$rootDir/.git/hooks/pre-push").exists()) return@task

    println("Copy pre-push hook into .git/hooks folder")
    val from = ".\\.scripts\\pre-push"
    val to = ".\\.git\\hooks\\pre-push"
    exec {
        if (System.getProperty("os.name").lowercase().contains("win")) {
            commandLine("cmd", "/c", "copy", "/Y", from, to)
        } else {
            commandLine("cp", from, to)
        }
    }
}
