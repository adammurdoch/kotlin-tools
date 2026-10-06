package net.rubygrapefruit.plugins.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class SourceProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        environment.logger.warn("processing")
        for (file in resolver.getNewFiles()) {
            for (declaration in file.declarations) {
                if (declaration is KSFunctionDeclaration && declaration.simpleName.getShortName() == "main") {
                    environment.logger.warn("found $declaration in $file")
                }
            }
        }
        return emptyList()
    }

    override fun finish() {
        environment.logger.warn("generating")
    }
}
