/*
 *  Copyright (c) 2026, Ben Fortuna
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

/**
 * Types in this package are non-null by default; nullable types are annotated with
 * {@link org.jspecify.annotations.Nullable}.
 *
 * <p>Property value accessors (such as {@code getValue()}, {@code getDate()} and {@code getUri()} where the URI is
 * the property's only representation) are declared non-null on the basis that a property always has a value once
 * constructed with one. The no-arg constructors exist to support factories and builders, which set the value before
 * the property is used; a property created with a no-arg constructor (or cleared with a {@code null} value) will
 * return {@code null} from these accessors until a value is set. Accessors for alternative or optional
 * representations (for example {@link net.fortuna.ical4j.model.property.Attach#getUri()} versus
 * {@link net.fortuna.ical4j.model.property.Attach#getBinary()}) are annotated as nullable.</p>
 */
@NullMarked
package net.fortuna.ical4j.model.property;

import org.jspecify.annotations.NullMarked;
