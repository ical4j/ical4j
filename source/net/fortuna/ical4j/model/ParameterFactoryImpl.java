/*
 * $Id $
 * 
 * [05-Apr-2004]
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
 * @author Ben Fortuna
 */
public final class ParameterFactoryImpl implements ParameterFactory {

    private static ParameterFactoryImpl instance = new ParameterFactoryImpl();
    
    private Map factories;

    /**
     * Constructor made private to prevent instantiation.
     */
    private ParameterFactoryImpl() {
        factories = new HashMap();
        factories.put(Parameter.ALTREP, createAltRepFactory());
        factories.put(Parameter.CN, createCnFactory());
        factories.put(Parameter.CUTYPE, createCuTypeFactory());
        factories.put(Parameter.DELEGATED_FROM, createDelegatedFromFactory());
        factories.put(Parameter.DELEGATED_TO, createDelegatedToFactory());
        factories.put(Parameter.DIR, createDirFactory());
        factories.put(Parameter.ENCODING, createEncodingFactory());
        factories.put(Parameter.FMTTYPE, createFmtTypeFactory());
        factories.put(Parameter.FBTYPE, createFbTypeFactory());
        factories.put(Parameter.LANGUAGE, createLanguageFactory());
        factories.put(Parameter.MEMBER, createMemberFactory());
        factories.put(Parameter.PARTSTAT, createPartStatFactory());
        factories.put(Parameter.RANGE, createRangeFactory());
        factories.put(Parameter.RELATED, createRelatedFactory());
        factories.put(Parameter.RELTYPE, createRelTypeFactory());
        factories.put(Parameter.ROLE, createRoleFactory());
        factories.put(Parameter.RSVP, createRsvpFactory());
        factories.put(Parameter.SENT_BY, createSentByFactory());
        factories.put(Parameter.TZID, createTzIdFactory());
        factories.put(Parameter.VALUE, createValueFactory());
    }
    
    /**
     * @return
     */
    private ParameterFactory createAltRepFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value) throws URISyntaxException {
                return new AltRep(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createCnFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Cn(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createCuTypeFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                CuType parameter =  new CuType(value);
                if (CuType.INDIVIDUAL.equals(parameter)) {
                    return CuType.INDIVIDUAL;
                }
                else if (CuType.GROUP.equals(parameter)) {
                    return CuType.GROUP;
                }
                else if (CuType.RESOURCE.equals(parameter)) {
                    return CuType.RESOURCE;
                }
                else if (CuType.ROOM.equals(parameter)) {
                    return CuType.ROOM;
                }
                else if (CuType.UNKNOWN.equals(parameter)) {
                    return CuType.UNKNOWN;
                }
                return parameter;
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createDelegatedFromFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new DelegatedFrom(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createDelegatedToFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new DelegatedTo(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createDirFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Dir(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createEncodingFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                Encoding parameter = new Encoding(value);
                if (Encoding.EIGHT_BIT.equals(parameter)) {
                    return Encoding.EIGHT_BIT;
                }
                else if (Encoding.BASE64.equals(parameter)) {
                    return Encoding.BASE64;
                }
                return parameter;
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createFmtTypeFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new FmtType(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createFbTypeFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                FbType parameter = new FbType(value);
                if (FbType.FREE.equals(parameter)) {
                    return FbType.FREE;
                }
                else if (FbType.BUSY.equals(parameter)) {
                    return FbType.BUSY;
                }
                else if (FbType.BUSY_TENTATIVE.equals(parameter)) {
                    return FbType.BUSY_TENTATIVE;
                }
                else if (FbType.BUSY_UNAVAILABLE.equals(parameter)) {
                    return FbType.BUSY_UNAVAILABLE;
                }
                return parameter;
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createLanguageFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Language(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createMemberFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new Member(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createPartStatFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                PartStat parameter = new PartStat(value);
                if (PartStat.NEEDS_ACTION.equals(parameter)) {
                    return PartStat.NEEDS_ACTION;
                }
                else if (PartStat.ACCEPTED.equals(parameter)) {
                    return PartStat.ACCEPTED;
                }
                else if (PartStat.DECLINED.equals(parameter)) {
                    return PartStat.DECLINED;
                }
                else if (PartStat.TENTATIVE.equals(parameter)) {
                    return PartStat.TENTATIVE;
                }
                else if (PartStat.DELEGATED.equals(parameter)) {
                    return PartStat.DELEGATED;
                }
                else if (PartStat.COMPLETED.equals(parameter)) {
                    return PartStat.COMPLETED;
                }
                else if (PartStat.IN_PROCESS.equals(parameter)) {
                    return PartStat.IN_PROCESS;
                }
                return parameter;
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createRangeFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                Range parameter = new Range(value);
                if (Range.THISANDFUTURE.equals(parameter)) {
                    return Range.THISANDFUTURE;
                }
                else if (Range.THISANDPRIOR.equals(parameter)) {
                    return Range.THISANDPRIOR;
                }
                return parameter;
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createRelatedFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                Related parameter = new Related(value);
                if (Related.START.equals(parameter)) {
                    return Related.START;
                }
                else if (Related.END.equals(parameter)) {
                    return Related.END;
                }
                return parameter;
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createRelTypeFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                RelType parameter = new RelType(value);
                if (RelType.PARENT.equals(parameter)) {
                    return RelType.PARENT;
                }
                else if (RelType.CHILD.equals(parameter)) {
                    return RelType.CHILD;
                }
                if (RelType.SIBLING.equals(parameter)) {
                    return RelType.SIBLING;
                }
                return parameter;
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createRoleFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                Role parameter = new Role(value);
                if (Role.CHAIR.equals(parameter)) {
                    return Role.CHAIR;
                }
                else if (Role.REQ_PARTICIPANT.equals(parameter)) {
                    return Role.REQ_PARTICIPANT;
                }
                else if (Role.OPT_PARTICIPANT.equals(parameter)) {
                    return Role.OPT_PARTICIPANT;
                }
                else if (Role.NON_PARTICIPANT.equals(parameter)) {
                    return Role.NON_PARTICIPANT;
                }
                return parameter;
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createRsvpFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                Rsvp parameter = new Rsvp(value);
                if (Rsvp.TRUE.equals(parameter)) {
                    return Rsvp.TRUE;
                }
                else if (Rsvp.FALSE.equals(parameter)) {
                    return Rsvp.FALSE;
                }
                return parameter;
            }
        };
    }

    /**
     * @return
     */
    private ParameterFactory createSentByFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new SentBy(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createTzIdFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                return new TzId(value);
            }
        };
    }
    
    /**
     * @return
     */
    private ParameterFactory createValueFactory() {
        return new ParameterFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.ParameterFactory#createParameter(java.lang.String, java.lang.String)
             */
            public Parameter createParameter(final String name, final String value)
                    throws URISyntaxException {
                Value parameter = new Value(value);
                if (Value.BINARY.equals(parameter)) {
                    return Value.BINARY;
                }
                else if (Value.BOOLEAN.equals(parameter)) {
                    return Value.BOOLEAN;
                }
                else if (Value.CAL_ADDRESS.equals(parameter)) {
                    return Value.CAL_ADDRESS;
                }
                else if (Value.DATE.equals(parameter)) {
                    return Value.DATE;
                }
                else if (Value.DATE_TIME.equals(parameter)) {
                    return Value.DATE_TIME;
                }
                else if (Value.DURATION.equals(parameter)) {
                    return Value.DURATION;
                }
                else if (Value.FLOAT.equals(parameter)) {
                    return Value.FLOAT;
                }
                else if (Value.INTEGER.equals(parameter)) {
                    return Value.INTEGER;
                }
                else if (Value.PERIOD.equals(parameter)) {
                    return Value.PERIOD;
                }
                else if (Value.RECUR.equals(parameter)) {
                    return Value.RECUR;
                }
                else if (Value.TEXT.equals(parameter)) {
                    return Value.TEXT;
                }
                else if (Value.TIME.equals(parameter)) {
                    return Value.TIME;
                }
                else if (Value.URI.equals(parameter)) {
                    return Value.URI;
                }
                else if (Value.UTC_OFFSET.equals(parameter)) {
                    return Value.UTC_OFFSET;
                }
                return parameter;
            }
        };
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
        return name.startsWith(Parameter.EXPERIMENTAL_PREFIX)
                && name.length() > Parameter.EXPERIMENTAL_PREFIX.length();
    }
}
