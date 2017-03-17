package org.danilopianini.gradle.javadociolinker

import org.gradle.api.Project
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.jvm.tasks.Jar

class JavadocIOLinker implements Plugin<Project> {
    void apply(Project project) {
        project.apply plugin: 'java'
        project.task('downloadJavadocIOPackageLists') << {
            project.configurations.compile.resolvedConfiguration.firstLevelModuleDependencies.each {
                def uripart = "${it.moduleGroup}/${it.moduleName}/${it.moduleVersion}/"
                def url = "http://www.javadoc.io/page/${uripart}"
                try {
                    def packages = "${url}package-list".toURL().getText(requestProperties: ['User-Agent': ""])
                    if (!packages.contains('<')) {
                        def destination = "${project.buildDir}/javadoctmp/${uripart}"
                        new File(destination).mkdirs()
                        new File("${destination}/package-list") << packages
                    } else {
                        println "${url} does not contain a valid javadoc and won't get linked."
                    }
                } catch (IOException e) {
                    println "Could not contact javadoc.io. ${it.moduleGroup}:${it.moduleName}:${it.moduleVersion} will not be linked."
                }
            }
        }
        project.javadoc.dependsOn(project.downloadJavadocIOPackageLists)
        project.javadoc {
            options {
                opt ->
                project.configurations.compile.resolvedConfiguration.firstLevelModuleDependencies.each {
                    def name = "${it.moduleGroup}/${it.moduleName}/${it.moduleVersion}/"
                    try {
                        def uripart = "${it.moduleGroup}/${it.moduleName}/${it.moduleVersion}/"
                        def url = "http://www.javadoc.io/page/${uripart}"
                        def destination = "${project.buildDir}/javadoctmp/${uripart}"
                        def packages = "${url}package-list".toURL().getText(requestProperties: ['User-Agent': ""])
                        if (!packages.contains('<')) {
                            opt.linksOffline(url, destination)
                        }
                    } catch (IOException e) {
                        println "${name} javadoc won't be linked"
                    }
                }
            }
        }
    }
}
