package org.openapitools.codegen.languages;

import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenType;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;

import org.openapitools.codegen.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

// for any/all of support
import io.swagger.v3.oas.models.media.ComposedSchema;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;

import static org.openapitools.codegen.utils.StringUtils.escape;

public class MarkdownDocumentationCodegen extends DefaultCodegen implements CodegenConfig {
    public static final String PROJECT_NAME = "projectName";

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownDocumentationCodegen.class);

    public CodegenType getTag() {
        return CodegenType.DOCUMENTATION;
    }

    public String getName() {
        return "markdown";
    }

    public String getHelp() {
        return "Generates a markdown documentation.";
    }

    public MarkdownDocumentationCodegen() {
        super();

        generatorMetadata = GeneratorMetadata.newBuilder(generatorMetadata)
                .stability(Stability.BETA)
                .build();

        outputFolder = "generated-code" + File.separator + "markdown";
        modelTemplateFiles.put("model.mustache", ".md");
        apiTemplateFiles.put("api.mustache", ".md");
        embeddedTemplateDir = templateDir = "markdown-documentation";
        apiPackage = File.separator + "Apis";
        modelPackage = "Models";
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
        // TODO: Fill this out.

        // override java data typing
        typeMapping.clear();
        typeMapping.put("array", "array");
        typeMapping.put("map", "map");
        typeMapping.put("List", "array");
        typeMapping.put("boolean", "boolean");
        typeMapping.put("string", "string");
        typeMapping.put("int", "integer");
        typeMapping.put("float", "float");
        typeMapping.put("number", "integer");
        typeMapping.put("date", "string");
        typeMapping.put("date-time", "string");
        typeMapping.put("DateTime", "string");
        typeMapping.put("long", "integer");
        typeMapping.put("short", "integer");
        typeMapping.put("char", "string");
        typeMapping.put("double", "float");
        typeMapping.put("object", "map");
        typeMapping.put("integer", "integer");
        typeMapping.put("ByteArray", "string");
        typeMapping.put("file", "binary");
        typeMapping.put("binary", "binary");
        typeMapping.put("UUID", "string");
        typeMapping.put("URI", "string");



        /**
         * Language Specific Primitives.  These types will not trigger imports by
         * the client generator
         */
        languageSpecificPrimitives.clear();
        languageSpecificPrimitives.add("array");
        languageSpecificPrimitives.add("map");
        languageSpecificPrimitives.add("boolean");
        languageSpecificPrimitives.add("integer");
        languageSpecificPrimitives.add("float");
        languageSpecificPrimitives.add("string");
        languageSpecificPrimitives.add("binary");
        languageSpecificPrimitives.add("date");
        languageSpecificPrimitives.add("date-time");


    }

    @Override
    protected void initializeSpecialCharacterMapping() {
        // escape only those symbols that can mess up markdown
//        specialCharReplacements.put("\\", "\\\\");
//        specialCharReplacements.put("/", "\\/");
//        specialCharReplacements.put("`", "\\`");
//        specialCharReplacements.put("*", "\\*");
//        specialCharReplacements.put("_", "\\_");
//        specialCharReplacements.put("[", "\\[");
//        specialCharReplacements.put("]", "\\]");

        // todo Current markdown api and model mustache templates display properties and parameters in tables. Pipe
        //  symbol in a table can be commonly escaped with a backslash (e.g. GFM supports this). However, in some cases
        //  it may be necessary to choose a different approach.
        specialCharReplacements.put("|", "\\|");

        // add support for docusaurus box callout ":::"
        // docusaurus requires ":::" to be present in the first column of new paragraph (hence \n\n)
        specialCharReplacements.put(":::", System.lineSeparator() + System.lineSeparator() + ":::"+System.lineSeparator());

        // add support for inline `code` blocks
        specialCharReplacements.put("```", System.lineSeparator() + "```"+System.lineSeparator());
    }

    /**
     * Works identically to {@link DefaultCodegen#toParamName(String)} but doesn't camelize.
     *
     * @param name Codegen property object
     * @return the sanitized parameter name
     */
    @Override
    public String toParamName(String name) {
        if (reservedWords.contains(name)) {
            return escapeReservedWord(name);
        } else if (((CharSequence) name).chars().anyMatch(character -> specialCharReplacements.keySet().contains("" + ((char) character)))) {
            return escape(name, specialCharReplacements, null, null);
        }
        return name;
    }

    @Override
    public String escapeQuotationMark(String input) {
        return input;
    }

    /**
     * Override with any special text escaping logic to handle unsafe
     * characters so as to avoid code injection.
     *
     * @param input String to be cleaned up
     * @return string with unsafe characters removed or escaped
     */
    @Override
     public String escapeUnsafeCharacters(String input) {
        // to suppress the warning message
        return input;
    }
    // from typescript/Rust client abstract class
    @Override
    public String toAnyOfName(List<String> names, ComposedSchema composedSchema) {
        List<String> types = getTypesFromSchemas(composedSchema.getAnyOf());

        return String.join(", ", types);
    }

    @Override
    public String toOneOfName(List<String> names, ComposedSchema composedSchema) {
        List<Schema> schemas = ModelUtils.getInterfaces(composedSchema);

        List<String> types = new ArrayList<>();
        for (Schema s : schemas) {
            types.add(getTypeDeclaration(s));
        }
        return "swagger::OneOf" + types.size() + "<" + String.join(",", types) + ">";
    }

    @Override
    public String toAllOfName(List<String> names, ComposedSchema composedSchema) {
        List<String> types = getTypesFromSchemas(composedSchema.getAllOf());

        return String.join(" & ", types);
    }

    @Override
    public String escapeText(String input) {
        if (input == null) {
            return input;
        }

        // chomp tailing newline because it breaks the tables and keep all other sign to show documentation properly
        return StringUtils.chomp(input);
    }

    /**
     * Extracts the list of type names from a list of schemas.
     * Excludes `AnyType` if there are other valid types extracted.
     *
     * @param schemas list of schemas
     * @return list of types
     */
    protected List<String> getTypesFromSchemas(List<Schema> schemas) {
        List<Schema> filteredSchemas = schemas.size() > 1
                ? schemas.stream().filter(schema -> !"AnyType".equals(super.getSchemaType(schema))).collect(Collectors.toList())
                : schemas;

        return filteredSchemas.stream().map(schema -> {
            String schemaType = getSchemaType(schema);
            if (ModelUtils.isArraySchema(schema)) {
                ArraySchema ap = (ArraySchema) schema;
                Schema inner = ap.getItems();
                schemaType = schemaType + "<" + getSchemaType(inner) + ">";
            }
            return schemaType;
        }).distinct().collect(Collectors.toList());
    }


}
