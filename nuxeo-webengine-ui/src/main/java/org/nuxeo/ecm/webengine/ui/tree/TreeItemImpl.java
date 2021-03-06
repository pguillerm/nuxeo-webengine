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

package org.nuxeo.ecm.webengine.ui.tree;

import org.nuxeo.common.utils.Path;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class TreeItemImpl implements TreeItem {

    private static final long serialVersionUID = 5252830785508229998L;

    public static final int F_CONTAINER = 4;
    public static final int F_EXPANDED = 8;

    public static final TreeItem[] EMPTY_CHILDREN = new TreeItem[0];
    public static final TreeItem[] HAS_CHILDREN = new TreeItem[0];

    protected final ContentProvider provider;
    protected final TreeItem parent;
    protected final Path path;
    protected String label;
    protected String[] facets;
    protected TreeItem[] children = EMPTY_CHILDREN;

    protected final Object obj;

    protected volatile int state = BOTH;

    // TODO: use a map?
    //protected Map<String, TreeItem> childrenMap;

    public TreeItemImpl(TreeItem parent, ContentProvider provider, Object data) {
        this.parent = parent;
        this.provider = provider;
        obj = data;
        String name = provider.getName(obj);
        if (parent != null) {
            path = parent.getPath().append(name);
        } else {
            path = new Path("/");
        }
        if (provider.isContainer(obj)) {
            state |= F_CONTAINER; // set container flag and invalidate children
        }
    }

    public TreeItemImpl(ContentProvider provider, Object data) {
        this(null, provider, data);
    }

    public TreeItemImpl(TreeItem parent, Object data) {
        this(parent, parent.getContentProvider(), data);
    }


    public boolean hasChildren() {
        return children.length > 0;
    }

    public TreeItem[] getChildren() {
        validateChildren();
        return children;
    }

    public Object getObject() {
        return obj;
    }

    public Path getPath() {
        return path;
    }

    public TreeItem getParent() {
        return parent;
    }

    public ContentProvider getContentProvider() {
        return provider;
    }

    public String getName() {
        return path.lastSegment();
    }

    public String getLabel() {
        validateData();
        return label;
    }

    public String[] getFacets() {
        validateData();
        return facets;
    }

    public boolean isContainer() {
        return (state & F_CONTAINER) != 0;
    }

    public TreeItem find(Path path) {
        TreeItem item = this;
        for (int i=0,len=path.segmentCount()-1; i<len; i++) {
            if (!item.hasChildren()) {
                return null;
            }
            item = item.getChild(path.segment(i));
            if (item == null) {
                return null;
            }
        }
        if (!item.hasChildren()) {
            return null;
        }
        return item.getChild(path.lastSegment());
    }

    public TreeItem findAndReveal(Path path) {
        // we expand only parents and not the last segment
        TreeItem item = this;
        int len = path.segmentCount();
        for (int i=0; i<len; i++) {
            item.expand();
            item = item.getChild(path.segment(i));
            if (item == null) {
                return null;
            }
        }
        return item;
    }

    public TreeItem getChild(String name) {
        validateChildren();
        return _getChild(name);
    }

    protected TreeItem _getChild(String name) {
        for (TreeItem child : children) {
            if (name.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }

    public TreeItem[] expand() {
        if (isExpanded()) {
            return children;
        } else {
            if (parent != null && !parent.isExpanded()) {
                parent.expand();
            }
            state |= F_EXPANDED;
            return getChildren();
        }
    }

    protected void loadData() {
        label = provider.getLabel(obj);
        facets = provider.getFacets(obj);
    }

    public void validateData() {
        if ((state & DATA) != 0) {
            loadData();
            state &= ~DATA;
        }
    }

    public void validateChildren() {
        if ((state & CHILDREN) != 0) {
            loadChildren();
            state &= ~CHILDREN;
        }
    }

    protected void loadChildren() {
        if (!isContainer()) {
            return;
        }
        Object[] objects = parent == null ? provider.getElements(obj)
                : provider.getChildren(obj);
        if (objects == null) {
            children = null;
        } else {
            children = new TreeItemImpl[objects.length];
            for (int i = 0; i < objects.length; i++) {
                children[i] = new TreeItemImpl(this, objects[i]);
            }
        }
    }

    public void collapse() {
        state &= ~F_EXPANDED;
    }

    public boolean isExpanded() {
        return (state & F_EXPANDED) != 0;
    }

    /*
     * TODO not completely implemented
     */
    public void refresh(int type) {
        if ((type & DATA) != 0) {
            loadData();
        }
        if ((type & CHILDREN) != 0) {
            loadChildren();
        }
        state &= ~type;
    }

    public void validate() {
        refresh(state);
    }

    public void invalidate(int type) {
        state |= type;
    }

    /*
     * TODO not implemented
     */
    public int getValidationState() {
        return state;
    }

    public Object accept(TreeItemVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "TreeItem: " + obj.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TreeItem) {
            return getObject().equals(((TreeItem) obj).getObject());
        }
        return false;
    }

}
