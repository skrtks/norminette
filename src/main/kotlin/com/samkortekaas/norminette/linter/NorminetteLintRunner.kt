package com.samkortekaas.norminette.linter

import com.samkortekaas.norminette.settings.NorminetteSettingsPanel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import org.jetbrains.annotations.NonNls
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

object NorminetteLintRunner {
    private val errorCodes: Map<String, String> = mapOf(
        "SPC_INSTEAD_TAB" to "Spaces at beginning of line",
        "TAB_INSTEAD_SPC" to "Found tab when expecting space",
        "CONSECUTIVE_SPC" to "Two or more consecutives spaces",
        "SPC_BFR_OPERATOR" to "missing space before operator",
        "SPC_AFTER_OPERATOR" to "missing space after operator",
        "NO_SPC_BFR_OPR" to "extra space before operator",
        "NO_SPC_AFR_OPR" to "extra space after operator",
        "SPC_AFTER_PAR" to "Missing space after parenthesis (brace/bracket)",
        "SPC_BFR_PAR" to "Missing space before parenthesis (brace/bracket)",
        "NO_SPC_AFR_PAR" to "Extra space after parenthesis (brace/bracket)",
        "NO_SPC_BFR_PAR" to "Extra space before parenthesis (brace/bracket)",
        "SPC_AFTER_POINTER" to "space after pointer",
        "SPC_BFR_POINTER" to "bad spacing before pointer",
        "SPACE_BEFORE_FUNC" to "space before function name",
        "TOO_MANY_TABS_FUNC" to "extra tabs before function name",
        "MISSING_TAB_FUNC" to "missing tab before function name",
        "MISSING_TAB_VAR" to "missing tab before variable name",
        "TOO_MANY_TAB_VAR" to "extra tab before variable name",
        "LINE_TOO_LONG" to "line too long",
        "EXP_PARENTHESIS" to "Expected parenthesis",
        "MISSING_IDENTIFIER" to "missing type qualifier or identifier in function arguments",
        "FORBIDDEN_CHAR_NAME" to "user defined identifiers should contain only lowercase characters, digits or '_'",
        "TOO_FEW_TAB" to "Missing tabs for indent level",
        "TOO_MANY_TAB" to "Extra tabs for indent level",
        "SPACE_REPLACE_TAB" to "Found space when expecting tab",
        "TAB_REPLACE_SPACE" to "Found tab when expecting space",
        "TOO_MANY_LINES" to "Function has more than 25 lines",
        "SPACE_EMPTY_LINE" to "Space on empty line",
        "SPC_BEFORE_NL" to "Space before newline",
        "TOO_MANY_INSTR" to "Too many instructions on a single line",
        "PREPROC_UKN_STATEMENT" to "Unrecognized preprocessor statement",
        "PREPROC_START_LINE" to "Preprocessor statement not at the beginning of the line",
        "PREPROC_CONSTANT" to "Preprocessor statement must only contain constant defines",
        "PREPROC_EXPECTED_EOL" to "Expected EOL after preprocessor statement",
        "PREPROC_BAD_INDENT" to "Bad preprocessor indentation",
        "USER_DEFINED_TYPEDEF" to "User defined typedef must start with t_",
        "STRUCT_TYPE_NAMING" to "Structure name must start with s_",
        "ENUM_TYPE_NAMING" to "Enum name must start with e_",
        "UNION_TYPE_NAMING" to "Union name must start with u_",
        "GLOBAL_VAR_NAMING" to "Global variable must start with g_",
        "EOL_OPERATOR" to "Logic operator at the end of line",
        "EMPTY_LINE_FUNCTION" to "Empty line in function",
        "EMPTY_LINE_FILE_START" to "Empty line at start of file",
        "EMPTY_LINE_FUNCTION" to "Empty line in function",
        "EMPTY_LINE_EOF" to "Empty line at end of file",
        "WRONG_SCOPE_VAR" to "Variable declared in incorrect scope",
        "IMPLICIT_VAR_TYPE" to "Missing type in variable declaration",
        "VAR_DECL_START_FUNC" to "Variable declaration not at start of function",
        "TOO_MANY_VARS_FUNC" to "Too many variables declarations in a function",
        "TOO_MANY_FUNCS" to "Too many functions in file",
        "BRACE_SHOULD_EOL" to "Expected newline after brace",
        "CONSECUTIVE_NEWLINES" to "Consecutive newlines",
        "NEWLINE_PRECEDES_FUNC" to "Functions must be separated by a newline",
        "NL_AFTER_VAR_DECL" to "Variable declarations must be followed by a newline",
        "MULT_ASSIGN_LINE" to "Multiple assignations on a single line",
        "MULT_DECL_LINE" to "Multiple declarations on a single line",
        "DECL_ASSIGN_LINE" to "Declaration and assignation on a single line",
        "FORBIDDEN_CS" to "Forbidden control structure",
        "SPACE_AFTER_KW" to "Missing space after keyword",
        "RETURN_PARENTHESIS" to "Return value must be in parenthesis",
        "EXP_SEMI_COLON" to "Expected semicolon",
        "EXP_TAB" to "Expected tab",
        "NO_ARGS_VOID" to "Empty function argument requires void",
        "MISALIGNED_VAR_DECL" to "Misaligned variable declaration",
        "MISALIGNED_FUNC_DECL" to "Misaligned function declaration",
        "WRONG_SCOPE_COMMENT" to "Comment is invalid in this scope",
        "MACRO_NAME_CAPITAL" to "Macro name must be capitalized",
        "ASSIGN_IN_CONTROL" to "Assignment in control structure",
        "VLA_FORBIDDEN" to "Variable length array forbidden",
        "TOO_MANY_ARGS" to "Function has more than 4 arguments",
        "INCLUDE_HEADER_ONLY" to ".c file includes are forbidden",
        "INCLUDE_START_FILE" to "Include must be at the start of file",
        "HEADER_PROT_ALL" to "Header protection must include all the instructions",
        "HEADER_PROT_NAME" to "Wrong header protection name",
        "TERNARY_FBIDDEN" to "Ternaries are forbidden",
        "TOO_MANY_VALS" to "Too many values on define",
        "NEWLINE_IN_DECL" to "Newline in declaration",
        "MULT_IN_SINGLE_INSTR" to "Multiple instructions in single line control structure",
        "NEWLINE_DEFINE" to "Newline in define",
        "MISSING_TYPEDEF_ID" to "Missing identifier in typedef declaration",
        "LABEL_FBIDDEN" to "Label statements are forbidden",
        "PREPROC_GLOBAL" to "Preprocessors can only be used in the global scope",
        "WRONG_SCOPE_FCT" to "Function prototype in incorrect scope",
        "WRONG_SCOPE" to "Statement is in incorrect scope",
        "INCORRECT_DEFINE" to "Incorrect values in define",
        "BRACE_NEWLINE" to "Expected newline before brace",
        "EXP_NEWLINE" to "Expected newline after control structure",
        "ARG_TYPE_UKN" to "Unrecognized variable type",
        "COMMENT_ON_INSTR" to "Comment must be on its own line",
        "COMMA_START_LINE" to "Comma at line start",
    )

    fun lint(editor: Editor?): Array<NorminetteWarning> {
        if (NorminetteSettingsPanel.NORMINETTE_PATH_VAL.isEmpty()) NorminetteSettingsPanel.detectAndSetPath()
        val norminettePath = NorminetteSettingsPanel.NORMINETTE_PATH_VAL
        if (!isValidNorminettePath(norminettePath)) return emptyArray()

        val document = editor?.document ?: return emptyArray()
        val fileExtension = FileDocumentManager.getInstance().getFile(document)?.extension ?: return emptyArray()
        val tmpFile = File.createTempFile("norminette", ".$fileExtension")
        createSyncedFile(document, tmpFile.toPath())
        val externalAnnotatorResult = "$norminettePath ${tmpFile.path}".runCommand()
        tmpFile.delete()
        return parse(externalAnnotatorResult)
    }

    private fun isValidNorminettePath(norminettePath: @NonNls String): Boolean {
        if (norminettePath.isEmpty()) return false
        val norminetteExecutable = File(norminettePath)
        if (!norminetteExecutable.exists() || !norminetteExecutable.canExecute()) return false
        return true
    }

    private fun createSyncedFile(doc: Document, tmp: Path) {
        Files.newBufferedWriter(tmp, StandardCharsets.UTF_8).use { out -> out.write(doc.text) }
    }

    private fun parse(res: String?): Array<NorminetteWarning> {
        val errors = res?.split("\n")
        return errors?.mapNotNull { if (it.startsWith("Error: ")) parseError(it) else null }?.toTypedArray()
            ?: emptyArray()
    }

    private fun parseError(error: String): NorminetteWarning? {
        val blocks = error.split("\\s".toRegex()).filter { it != "" }
        val shortCode = blocks[1]
        val line = blocks[blocks.indexOf("(line:") + 1].filter { it.isDigit() }.toIntOrNull() ?: return null
        return errorCodes[shortCode]?.let { NorminetteWarning(line, it) }
    }


    private fun String.runCommand(): String? {
        return try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(60, TimeUnit.MINUTES)
            proc.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}