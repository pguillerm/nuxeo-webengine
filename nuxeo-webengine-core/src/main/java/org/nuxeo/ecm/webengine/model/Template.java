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

package org.nuxeo.ecm.webengine.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.lang.Throwable;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.model.impl.ParameterId;
import org.nuxeo.ecm.webengine.scripting.ScriptFile;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class Template {

    private static final Log log = LogFactory.getLog(Template.class);

    protected final Resource resource;

    protected Map<String, Object> args;

    protected ScriptFile script;

    protected WebContext ctx;

    protected Template(WebContext ctx, Resource resource, ScriptFile script) {
        this.ctx = ctx;
        this.resource = resource;
        this.script = script;
        if (this.ctx == null && this.resource != null) {
            this.ctx = this.resource.getContext();
        }
    }

    public Template(WebContext ctx, String fileName) {
        this(ctx, null, null);
        resolve(fileName);
    }

    public Template(Resource resource, String fileName) {
        this(resource.getContext(), resource, null);
        resolve(fileName);
    }

    public Template(WebContext ctx, ScriptFile script) {
        this(ctx, null, script);
    }

    public Template(Resource resource, ScriptFile script) {
        this(resource.getContext(), resource, script);
    }

    public Template arg(String key, Object value) {
        initializeArgs();
        args.put(key, value);
        return this;
    }
    
    /**
     * Allow to set a parameter in freemarker template context.
     *
     * @param key an enum who implement the ParameterId interface
     * @param value any object
     * @return the current template with new parameter.
     * @see org.nuxeo.ecm.webengine.model.impl.ParameterId
     */
    public Template arg(ParameterId key, Object value){
        initializeArgs();
        args.put(key.getID(), value);
        return this;
    }

    /**
     * Allow to initialize args Map if it's null.
     */
    protected void initializeArgs(){
        if (args == null) {
            args = new HashMap<String, Object>();
        }
    }
    
    public Template args(Map<String, Object> args) {
        this.args = args;
        return this;
    }

    public Map<String, Object> args() {
        return args;
    }

    public Resource resource() {
        return resource;
    }

    protected void resolve(String fileName) {
        if (resource != null) {
            script = resource.getType().getView(ctx.getModule(), fileName);
        } else {
            script = ctx.getModule().getFile(fileName);
        }
    }

    public ScriptFile script() {
        return script;
    }

    public Throwable getRootCause(Throwable e) {
        if (e == null) {
            return null;
        } else if (e.getCause() == null) {
            return e;
        } else {
            return getRootCause(e.getCause());
        }
    }

    public void render(OutputStream out) throws WebException {
        Writer w;
        try {
            w = new OutputStreamWriter(out, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw WebException.wrap("Failed to create output stream: unsupported encoding", e);
        }
        try {
            ctx.render(script(), args, w);
        } catch (Exception e) {
            if (getRootCause(e) instanceof SocketException) {
                log.debug("Output socket closed: failed to write response");
            } else {
                throw WebException.wrap("Failed to write response", e);
            }
        }
        try {
            w.flush();
        } catch (Exception e) {
            if (getRootCause(e) instanceof SocketException) {
                log.debug("Output socket closed: failed to flush response");
            } else {
                throw WebException.wrap("Failed to flush response", e);
            }
        }
    }

    public String render() {
        StringWriter w = new StringWriter();
        try {
            ctx.render(script(), args, w);
        } catch (Exception e) {
            if (getRootCause(e) instanceof SocketException) {
                log.debug("Output socket closed: failed to write response");
            } else {
                throw WebException.wrap("Failed to write response", e);
            }
        }
        try {
            w.flush();
        } catch (Exception e) {
            if (getRootCause(e) instanceof SocketException) {
                log.debug("Output socket closed: failed to flush response");
            } else {
                throw WebException.wrap("Failed to flush response", e);
            }
        }
        return w.getBuffer().toString();
    }

}
