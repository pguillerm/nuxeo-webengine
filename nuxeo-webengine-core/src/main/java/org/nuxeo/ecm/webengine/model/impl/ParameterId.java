/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * $Id$
 */

package org.nuxeo.ecm.webengine.model.impl;

/**
 * This interface allow to use enum with Freemarker template parameters.
 * It's better to list all parameters ids into an Enumeration.

 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 * @since 30 mars 2012
 */
public interface ParameterId {

    /**
     * Allow to get freemarker parameter id.
     *
     * @return the iD
     */
    String getID();
}
