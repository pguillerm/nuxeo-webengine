
package org.nuxeo.ecm.webengine.model.impl;

/**
 * This interface allow to use enum in AbstractResource class for get freemarker
 * template. It's better to list all views ids into an Enumeration.
 *  
 * @author "<a href='mailto:patrickguillerm@gmail.com'>Patrick Guillerm</a>"
 * @since 28 mars 2012
 */
public interface TemplateViewId {

    /**
     * Allow to get freemarker view id
     * @return the view id
     */
    String getViewID();
}
