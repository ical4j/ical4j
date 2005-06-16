/*
 * $Id$ [05-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import net.fortuna.ical4j.model.parameter.AltRep;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.DelegatedFrom;
import net.fortuna.ical4j.model.parameter.DelegatedTo;
import net.fortuna.ical4j.model.parameter.Dir;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.parameter.FmtType;
import net.fortuna.ical4j.model.parameter.Language;
import net.fortuna.ical4j.model.parameter.Member;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Range;
import net.fortuna.ical4j.model.parameter.RelType;
import net.fortuna.ical4j.model.parameter.Related;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.parameter.XParameter;

/**
 * A factory for creating iCalendar parameters.
 *
 * @author benfortuna
 */
public final class ParameterFactoryImpl implements ParameterFactory {

    private static ParameterFactoryImpl instance = new ParameterFactoryImpl();
    
    private Map factories;

    /**
     * Constructor made private to prevent instantiation.
     */
    private ParameterFactoryImpl() {
        factories = new HashMap();
        factories.put(Parameter.ALTREP, new ParameterFactory() {
            /* (non-Javadoc)
			 * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
			 */
			public final Parameter createParameter(final String name, final String value)
					throws URISyntaxException {
				return new AltRep(value);
			}
        });
        factories.put(Parameter.CN, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Cn(value);
            }
        });
        factories.put(Parameter.CUTYPE, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new CuType(value);
            }
        });
        factories.put(Parameter.DELEGATED_FROM, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new DelegatedFrom(value);
            }
        });
        factories.put(Parameter.DELEGATED_TO, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new DelegatedTo(value);
            }
        });
        factories.put(Parameter.DIR, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Dir(value);
            }
        });
        factories.put(Parameter.ENCODING, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Encoding(value);
            }
        });
        factories.put(Parameter.FMTTYPE, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new FmtType(value);
            }
        });
        factories.put(Parameter.FBTYPE, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new FbType(value);
            }
        });
        factories.put(Parameter.LANGUAGE, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Language(value);
            }
        });
        factories.put(Parameter.MEMBER, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Member(value);
            }
        });
        factories.put(Parameter.PARTSTAT, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new PartStat(value);
            }
        });
        factories.put(Parameter.RANGE, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Range(value);
            }
        });
        factories.put(Parameter.RELATED, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Related(value);
            }
        });
        factories.put(Parameter.RELTYPE, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new RelType(value);
            }
        });
        factories.put(Parameter.ROLE, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Role(value);
            }
        });
        factories.put(Parameter.RSVP, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Rsvp(value);
            }
        });
        factories.put(Parameter.SENT_BY, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new SentBy(value);
            }
        });
        factories.put(Parameter.TZID, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new TzId(value);
            }
        });
        factories.put(Parameter.VALUE, new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public final Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Value(value);
            }
        });
    }

    /**
     * @return Returns the instance.
     */
    public static ParameterFactoryImpl getInstance() {
        return instance;
    }

    /**
     * Creates a parameter.
     *
     * @param name
     *            name of the parameter
     * @param value
     *            a parameter value
     * @return a component
     * @throws URISyntaxException thrown when the specified string
     * is not a valid representation of a URI for selected parameters
     */
    public Parameter createParameter(final String name, final String value)
            throws URISyntaxException {
        ParameterFactory factory = (ParameterFactory) factories.get(name);
        if (factory != null) {
            return factory.createParameter(name, value);
        }
        else if (isExperimentalName(name)) {
            return new XParameter(name, value);
        }
        else {
            throw new IllegalArgumentException("Invalid parameter name: " + name);
        }
    }
    
    /**
     * @param name
     * @return
     */
    private boolean isExperimentalName(final String name) {
        return name.startsWith(Parameter.EXPERIMENTAL_PREFIX);
    }
}