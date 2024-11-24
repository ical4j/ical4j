/*
 *  Copyright (c) 2022, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.validate.schema;

//import com.github.erosb.jsonsKema.*;

import net.fortuna.ical4j.model.property.StructuredData;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.net.URL;

/**
 * XXX: The validator library used in this class currently depends on the URLDecoder.decode(String, Charset)
 * method added in Java 10. As such this will cause an error when used with Java &lt; 10.
 */
public class JsonSchemaValidator implements net.fortuna.ical4j.validate.Validator<StructuredData> {

    private final URL schemaUrl;

    public JsonSchemaValidator(URL schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    @Override
    public ValidationResult validate(StructuredData target) throws ValidationException {
        throw new UnsupportedOperationException("Schema validation not yet implemented");
        /*
        ValidationResult result = new ValidationResult();
        try (final InputStream in = schemaUrl.openStream()) {
            final String schemaJson = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            final JsonValue rawSchema = new JsonParser(schemaJson).parse();

            final Schema schema = new SchemaLoader(rawSchema).load();
            final Validator validator = Validator.forSchema(schema);

            final JsonValue instance = new JsonParser(target.getValue()).parse();
            final ValidationFailure failure = validator.validate(instance);

            if(failure != null) {
                result.getEntries().add(new ValidationEntry(
                        failure.getMessage(),
                        ValidationEntry.Severity.ERROR,
                        target.getName()
                ));
            }
        } catch (IOException e) {
            throw new ValidationException("Unable to retrieve schema");
        }
        return result;
             */
    }
}
