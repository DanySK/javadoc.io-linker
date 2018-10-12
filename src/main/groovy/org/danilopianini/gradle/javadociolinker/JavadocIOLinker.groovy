package org.danilopianini.gradle.javadociolinker

import org.gradle.api.Project
import org.gradle.api.Plugin

class JavadocIOLinker implements Plugin<Project> {

    void apply(Project project) {

        project.apply plugin: 'java'

        project.task('downloadJavadocIOPackageLists') {
            doLast {
                project.configurations.runtimeClasspath.resolvedConfiguration.firstLevelModuleDependencies.each {
                    def uripart = "${it.moduleGroup}/${it.moduleName}/${it.moduleVersion}/"
                    def url = "https://static.javadoc.io/${uripart}"
                    def destination = "${project.buildDir}/javadoctmp/${uripart}"
                    if (new File("${destination}package-list").canRead()) {
                        return // already downloaded
                    }
                    try {
                        def packages = "${url}package-list".toURL().getText(requestProperties: ['User-Agent': ""])
                        if (!packages.contains('<')) {
                            new File(destination).mkdirs()
                            new File("${destination}/package-list").newWriter().withWriter {
                                it << packages
                            }
                            println "${url} downloaded."
                        } else {
                            println "${url} does not contain a valid javadoc and won't get linked."
                        }
                    } catch (IOException e) {
                        println "Could not contact javadoc.io. ${it.moduleGroup}:${it.moduleName}:${it.moduleVersion} will not be linked."
                    }
                }
            }
        }

        project.javadoc.dependsOn(project.downloadJavadocIOPackageLists)

        project.javadoc {
            doFirst {
                options { opt ->
                    project.configurations.runtimeClasspath.resolvedConfiguration.firstLevelModuleDependencies.each {
                        def uripart = "${it.moduleGroup}/${it.moduleName}/${it.moduleVersion}/"
                        def url = "https://static.javadoc.io/${uripart}"
                        def destination = "${project.buildDir}/javadoctmp/${uripart}"
                        if (new File("${destination}package-list").canRead()) {
                            opt.linksOffline(url, destination)
                        } else {
                            println "${it.moduleGroup}:${it.moduleName}:${it.moduleVersion} javadoc won't be linked."
                        }
                    }
                }
            }
        }
    }
}
