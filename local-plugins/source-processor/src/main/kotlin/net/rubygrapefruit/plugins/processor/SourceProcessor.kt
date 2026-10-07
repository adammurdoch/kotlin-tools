package net.rubygrapefruit.plugins.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import net.rubygrapefruit.plugins.app.metadata.MainFunction
import net.rubygrapefruit.plugins.app.metadata.Names

@OptIn(ExperimentalSerializationApi::class, KspExperimental::class)
class SourceProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val mainFunctions = mutableListOf<MainFunction>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        for (file in resolver.getNewFiles()) {
            for (declaration in file.declarations) {
                if (declaration is KSFunctionDeclaration && declaration.simpleName.getShortName() == "main") {
                    val isUnit = declaration.returnType?.resolve() == resolver.builtIns.unitType
                    if (!isUnit) {
                        continue
                    }
                    val hasParam = if (declaration.parameters.size == 1) {
                        val param = declaration.parameters.first()
                        val paramType = param.type.resolve()
                        val isArray = paramType.declaration.qualifiedName?.asString() == "kotlin.Array"
                        if (!isArray) {
                            continue
                        }
                        val typeParam = paramType.arguments.first()
                        val isString = typeParam.type?.resolve() == resolver.builtIns.stringType
                        if (!isString) {
                            continue
                        }
                        true
                    } else if (declaration.parameters.size > 1) {
                        continue
                    } else {
                        false
                    }
                    val owner = resolver.getOwnerJvmClassName(declaration)!!
                    mainFunctions.add(MainFunction(file.filePath, owner, file.packageName.asString(), hasParam))
                }
            }
        }
        return emptyList()
    }

    override fun finish() {
        environment.logger.warn("main functions:")
        for (function in mainFunctions) {
            environment.logger.warn(function.toString())
        }
        environment.codeGenerator.createNewFile(Dependencies.ALL_FILES, "", Names.metadataFileName, "json").use {
            Json { prettyPrint = true }.encodeToStream(mainFunctions, it)
        }
    }
}
