/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <p>
 * o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p>
 * o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
 * o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.ParameterValidator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a COMMENT iCalendar component property.
 *
 * @author benf
 */
public class Comment extends Property implements Escapable {

  private static final long serialVersionUID = 7519125697719626308L;

  private String value;

  /**
   * Default constructor.
   */
  public Comment() {
    super(COMMENT, PropertyFactoryImpl.getInstance());
  }

  /**
   * @param aValue a value string for this component
   */
  public Comment(final String aValue) {
    super(COMMENT, PropertyFactoryImpl.getInstance());
    setValue(aValue);
  }

  /**
   * @param aList  a list of parameters for this component
   * @param aValue a value string for this component
   */
  public Comment(final ParameterList aList, final String aValue) {
    super(COMMENT, aList, PropertyFactoryImpl.getInstance());
    setValue(aValue);
  }

  /**
   * {@inheritDoc}
   */
  public final void validate() throws ValidationException {

        /*
         * ; the following are optional, ; but MUST NOT occur more than once (";" altrepparam) / (";" languageparam) /
         */
    ParameterValidator.getInstance().assertOneOrLess(Parameter.ALTREP,
        getParameters());
    ParameterValidator.getInstance().assertOneOrLess(Parameter.LANGUAGE,
        getParameters());

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */
  }

  /**
   * {@inheritDoc}
   */
  public final void setValue(final String aValue) {
    this.value = aValue;
  }

  /**
   * {@inheritDoc}
   */
  public final String getValue() {
    return value;
  }

  public static class Factory extends Content.Factory implements PropertyFactory {
    private static final long serialVersionUID = 1L;

    public Factory() {
      super(COMMENT);
    }

    public Property createProperty(final ParameterList parameters, final String value)
        throws IOException, URISyntaxException, ParseException {
      return new Comment(parameters, value);
    }

    public Property createProperty() {
      return new Comment();
    }
  }
}
