/**
 * Copyright (c) 2012, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
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

import net.fortuna.ical4j.model.parameter.Abbrev;
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
import net.fortuna.ical4j.model.parameter.ScheduleAgent;
import net.fortuna.ical4j.model.parameter.ScheduleStatus;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.parameter.Type;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.parameter.Vvenue;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.util.Strings;

/**
 * A factory for creating iCalendar parameters.
 * 
 * $Id $
 *
 * [05-Apr-2004]
 *
 * @author Ben Fortuna
 */
public class ParameterFactoryImpl extends AbstractContentFactory implements ParameterFactory {

    private static final long serialVersionUID = -4034423507432249165L;
    
    private static ParameterFactoryImpl instance = new ParameterFactoryImpl();

    protected ParameterFactoryImpl() {
        registerDefaultFactory(Parameter.ABBREV, new AbbrevFactory());
        registerDefaultFactory(Parameter.ALTREP, new AltRepFactory());
        registerDefaultFactory(Parameter.CN, new CnFactory());
        registerDefaultFactory(Parameter.CUTYPE, new CuTypeFactory());
        registerDefaultFactory(Parameter.DELEGATED_FROM, new DelegatedFromFactory());
        registerDefaultFactory(Parameter.DELEGATED_TO, new DelegatedToFactory());
        registerDefaultFactory(Parameter.DIR, new DirFactory());
        registerDefaultFactory(Parameter.ENCODING, new EncodingFactory());
        registerDefaultFactory(Parameter.FMTTYPE, new FmtTypeFactory());
        registerDefaultFactory(Parameter.FBTYPE, new FbTypeFactory());
        registerDefaultFactory(Parameter.LANGUAGE, new LanguageFactory());
        registerDefaultFactory(Parameter.MEMBER, new MemberFactory());
        registerDefaultFactory(Parameter.PARTSTAT, new PartStatFactory());
        registerDefaultFactory(Parameter.RANGE, new RangeFactory());
        registerDefaultFactory(Parameter.RELATED, new RelatedFactory());
        registerDefaultFactory(Parameter.RELTYPE, new RelTypeFactory());
        registerDefaultFactory(Parameter.ROLE, new RoleFactory());
        registerDefaultFactory(Parameter.RSVP, new RsvpFactory());
        registerDefaultFactory(Parameter.SCHEDULE_AGENT, new ScheduleAgentFactory());
        registerDefaultFactory(Parameter.SCHEDULE_STATUS, new ScheduleStatusFactory());
        registerDefaultFactory(Parameter.SENT_BY, new SentByFactory());
        registerDefaultFactory(Parameter.TYPE, new TypeFactory());
        registerDefaultFactory(Parameter.TZID, new TzIdFactory());
        registerDefaultFactory(Parameter.VALUE, new ValueFactory());
        registerDefaultFactory(Parameter.VVENUE, new VvenueFactory());
    }

    /**
     * @return Returns the instance.
     */
    public static ParameterFactoryImpl getInstance() {
        return instance;
    }

    /**
     * Creates a parameter.
     * @param name name of the parameter
     * @param value a parameter value
     * @return a component
     * @throws URISyntaxException thrown when the specified string is not a valid representation of a URI for selected
     * parameters
     */
    public Parameter createParameter(final String name, final String value)
            throws URISyntaxException {
        final ParameterFactory factory = (ParameterFactory) getFactory(name);
        Parameter parameter = null;
        if (factory != null) {
            parameter = factory.createParameter(name, value);
        }
        else if (isExperimentalName(name)) {
            parameter = new XParameter(name, value);
        }
        else if (allowIllegalNames()) {
            parameter = new XParameter(name, value);
        }
        else {
            throw new IllegalArgumentException("Invalid parameter name: "
                    + name);
        }
        return parameter;
    }

    /**
     * @param name
     * @return
     */
    private boolean isExperimentalName(final String name) {
        return name.startsWith(Parameter.EXPERIMENTAL_PREFIX)
                && name.length() > Parameter.EXPERIMENTAL_PREFIX.length();
    }
    
    private static class AbbrevFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name, final String value) throws URISyntaxException {
            return new Abbrev(value);
        }
    }
    
    private static class AltRepFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name, final String value) throws URISyntaxException {
            return new AltRep(value);
        }
    }
    
    private static class CnFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new Cn(value);
        }
    }
    
    private static class CuTypeFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name, final String value) throws URISyntaxException {
            CuType parameter = new CuType(value);
            if (CuType.INDIVIDUAL.equals(parameter)) {
                parameter = CuType.INDIVIDUAL;
            }
            else if (CuType.GROUP.equals(parameter)) {
                parameter = CuType.GROUP;
            }
            else if (CuType.RESOURCE.equals(parameter)) {
                parameter = CuType.RESOURCE;
            }
            else if (CuType.ROOM.equals(parameter)) {
                parameter = CuType.ROOM;
            }
            else if (CuType.UNKNOWN.equals(parameter)) {
                parameter = CuType.UNKNOWN;
            }
            return parameter;
        }
    }
    
    private static class DelegatedFromFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new DelegatedFrom(value);
        }
    }
    
    private static class DelegatedToFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new DelegatedTo(value);
        }
    }
    
    private static class DirFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new Dir(value);
        }
    }
    
    private static class EncodingFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            Encoding parameter = new Encoding(value);
            if (Encoding.EIGHT_BIT.equals(parameter)) {
                parameter = Encoding.EIGHT_BIT;
            }
            else if (Encoding.BASE64.equals(parameter)) {
                parameter = Encoding.BASE64;
            }
            return parameter;
        }
    }
    
    private static class FmtTypeFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new FmtType(value);
        }
    }
    
    private static class FbTypeFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            FbType parameter = new FbType(value);
            if (FbType.FREE.equals(parameter)) {
                parameter = FbType.FREE;
            }
            else if (FbType.BUSY.equals(parameter)) {
                parameter = FbType.BUSY;
            }
            else if (FbType.BUSY_TENTATIVE.equals(parameter)) {
                parameter = FbType.BUSY_TENTATIVE;
            }
            else if (FbType.BUSY_UNAVAILABLE.equals(parameter)) {
                parameter = FbType.BUSY_UNAVAILABLE;
            }
            return parameter;
        }
    }
    
    private static class LanguageFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new Language(value);
        }
    }
    
    private static class MemberFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new Member(value);
        }
    }
    
    private static class PartStatFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            PartStat parameter = new PartStat(value);
            if (PartStat.NEEDS_ACTION.equals(parameter)) {
                parameter = PartStat.NEEDS_ACTION;
            }
            else if (PartStat.ACCEPTED.equals(parameter)) {
                parameter = PartStat.ACCEPTED;
            }
            else if (PartStat.DECLINED.equals(parameter)) {
                parameter = PartStat.DECLINED;
            }
            else if (PartStat.TENTATIVE.equals(parameter)) {
                parameter = PartStat.TENTATIVE;
            }
            else if (PartStat.DELEGATED.equals(parameter)) {
                parameter = PartStat.DELEGATED;
            }
            else if (PartStat.COMPLETED.equals(parameter)) {
                parameter = PartStat.COMPLETED;
            }
            else if (PartStat.IN_PROCESS.equals(parameter)) {
                parameter = PartStat.IN_PROCESS;
            }
            return parameter;
        }
    }
    
    private static class RangeFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            Range parameter = new Range(value);
            if (Range.THISANDFUTURE.equals(parameter)) {
                parameter = Range.THISANDFUTURE;
            }
            else if (Range.THISANDPRIOR.equals(parameter)) {
                parameter = Range.THISANDPRIOR;
            }
            return parameter;
        }
    }
    
    private static class RelatedFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            Related parameter = new Related(value);
            if (Related.START.equals(parameter)) {
                parameter = Related.START;
            }
            else if (Related.END.equals(parameter)) {
                parameter = Related.END;
            }
            return parameter;
        }
    }
    
    private static class RelTypeFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            RelType parameter = new RelType(value);
            if (RelType.PARENT.equals(parameter)) {
                parameter = RelType.PARENT;
            }
            else if (RelType.CHILD.equals(parameter)) {
                parameter = RelType.CHILD;
            }
            if (RelType.SIBLING.equals(parameter)) {
                parameter = RelType.SIBLING;
            }
            return parameter;
        }
    }
    
    private static class RoleFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            Role parameter = new Role(value);
            if (Role.CHAIR.equals(parameter)) {
                parameter = Role.CHAIR;
            }
            else if (Role.REQ_PARTICIPANT.equals(parameter)) {
                parameter = Role.REQ_PARTICIPANT;
            }
            else if (Role.OPT_PARTICIPANT.equals(parameter)) {
                parameter = Role.OPT_PARTICIPANT;
            }
            else if (Role.NON_PARTICIPANT.equals(parameter)) {
                parameter = Role.NON_PARTICIPANT;
            }
            return parameter;
        }
    }
    
    private static class RsvpFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            Rsvp parameter = new Rsvp(value);
            if (Rsvp.TRUE.equals(parameter)) {
                parameter = Rsvp.TRUE;
            }
            else if (Rsvp.FALSE.equals(parameter)) {
                parameter = Rsvp.FALSE;
            }
            return parameter;
        }
    }

    private static class ScheduleAgentFactory implements ParameterFactory {
        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            final ScheduleAgent parameter = new ScheduleAgent(value);
            if (ScheduleAgent.SERVER.equals(parameter)) {
                return ScheduleAgent.SERVER;
            }
            else if (ScheduleAgent.CLIENT.equals(parameter)) {
                return ScheduleAgent.CLIENT;
            }
            else if (ScheduleAgent.NONE.equals(parameter)) {
                return ScheduleAgent.NONE;
            }
            return parameter;
        }
    }

    private static class ScheduleStatusFactory implements ParameterFactory {
        public Parameter createParameter(final String name, 
        		final String value) throws URISyntaxException {
            return new ScheduleStatus(value);
        }
    }
    
    private static class SentByFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new SentBy(value);
        }
    }
    
    private static class VvenueFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name, final String value)
                throws URISyntaxException {
            return new Vvenue(value);
        }
    }
    
    private static class TypeFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new Type(value);
        }
    }
    
    private static class TzIdFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            return new TzId(Strings.unescape(value));
        }
    }
    
    private static class ValueFactory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Parameter createParameter(final String name,
                final String value) throws URISyntaxException {
            Value parameter = new Value(value);
            if (Value.BINARY.equals(parameter)) {
                parameter = Value.BINARY;
            }
            else if (Value.BOOLEAN.equals(parameter)) {
                parameter = Value.BOOLEAN;
            }
            else if (Value.CAL_ADDRESS.equals(parameter)) {
                parameter = Value.CAL_ADDRESS;
            }
            else if (Value.DATE.equals(parameter)) {
                parameter = Value.DATE;
            }
            else if (Value.DATE_TIME.equals(parameter)) {
                parameter = Value.DATE_TIME;
            }
            else if (Value.DURATION.equals(parameter)) {
                parameter = Value.DURATION;
            }
            else if (Value.FLOAT.equals(parameter)) {
                parameter = Value.FLOAT;
            }
            else if (Value.INTEGER.equals(parameter)) {
                parameter = Value.INTEGER;
            }
            else if (Value.PERIOD.equals(parameter)) {
                parameter = Value.PERIOD;
            }
            else if (Value.RECUR.equals(parameter)) {
                parameter = Value.RECUR;
            }
            else if (Value.TEXT.equals(parameter)) {
                parameter = Value.TEXT;
            }
            else if (Value.TIME.equals(parameter)) {
                parameter = Value.TIME;
            }
            else if (Value.URI.equals(parameter)) {
                parameter = Value.URI;
            }
            else if (Value.UTC_OFFSET.equals(parameter)) {
                parameter = Value.UTC_OFFSET;
            }
            return parameter;
        }
    }
    
}
