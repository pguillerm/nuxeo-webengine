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
 * Contributors:
 *     bstefanescu
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.rendering.fm.adapters;

import java.io.IOException;
import java.util.ArrayList;

import org.nuxeo.ecm.core.api.Blob;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class BlobTemplate extends PropertyWrapper implements TemplateHashModelEx {

    protected Blob blob;
    protected final static String[] keys = new String[] {"name", "data", "length", "mimeType", "encoding", "digest"};

    public BlobTemplate(DocumentObjectWrapper wrapper, Blob blob) {
        super (wrapper);
        this.blob = blob;
    }

    public TemplateCollectionModel keys() throws TemplateModelException {
        return (TemplateCollectionModel)wrapper.wrap(keys);
    }

    public int size() throws TemplateModelException {
        return keys.length;
    }

    public TemplateCollectionModel values() throws TemplateModelException {
        try {
            ArrayList<Object> list = new ArrayList<Object>(keys.length);
            list.add(blob.getFilename());
            list.add(blob.getString());
            list.add(blob.getLength());
            list.add(blob.getMimeType());
            list.add(blob.getEncoding());
            list.add(blob.getDigest());
            return (TemplateCollectionModel)wrapper.wrap(list);
        } catch (Exception  e) {
            throw new TemplateModelException("Failed to adapt complex property values", e);
        }
    }

    public TemplateModel get(String name) throws TemplateModelException {
        try {
            if (keys[0].equals(name)) {
                return new SimpleScalar(blob.getFilename());
            } else if (keys[1].equals(name)) {
                return new SimpleScalar(blob.getString());
            } else if (keys[2].equals(name)) {
                return new SimpleNumber(blob.getLength());
            } else if (keys[3].equals(name)) {
                return new SimpleScalar(blob.getMimeType());
            } else if (keys[4].equals(name)) {
                return new SimpleScalar(blob.getEncoding());
            } else if (keys[5].equals(name)) {
                return new SimpleScalar(blob.getDigest());
            }
            return TemplateModel.NOTHING;
        } catch (IOException e) {
            throw new TemplateModelException(e);
        }
    }

    public boolean isEmpty() throws TemplateModelException {
        return false;
    }

}