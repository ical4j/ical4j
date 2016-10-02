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
package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ValidationException;

/**
 * @author Ben
 *
 */
public final class ComponentValidator {

  private static final String ASSERT_NONE_MESSAGE = "Component [{0}] is not applicable";

  private static final String ASSERT_ONE_OR_LESS_MESSAGE = "Component [{0}] must only be specified once";

  /**
   * Constructor made private to enforce static nature.
   */
  private ComponentValidator() {
  }

  /**
   * @param componentName a component name used in the assertion
   * @param components a list of components
   * @throws ValidationException where the assertion fails
   */
  public static void assertNone(String componentName, ComponentList<?> components) throws ValidationException {
    if (components.getComponent(componentName) != null) {
      throw new ValidationException(ASSERT_NONE_MESSAGE, new Object[]{componentName});
    }
  }

  /**
   * @param componentName a component name used in the assertion
   * @param components a list of components
   * @throws ValidationException where the assertion fails
   */
  public static void assertOneOrLess(String componentName, ComponentList<?> components) throws ValidationException {
    if (components.getComponents(componentName).size() > 1) {
      throw new ValidationException(ASSERT_ONE_OR_LESS_MESSAGE, new Object[]{componentName});
    }
  }
}
