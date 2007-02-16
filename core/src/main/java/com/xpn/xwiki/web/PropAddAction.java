/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @author ludovic
 * @author sdumitriu
 */
package com.xpn.xwiki.web;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.PropertyClass;
import com.xpn.xwiki.objects.meta.MetaClass;
import com.xpn.xwiki.objects.meta.PropertyMetaClass;

public class PropAddAction extends XWikiAction {
	public boolean action(XWikiContext context) throws XWikiException {
        XWiki xwiki = context.getWiki();
        XWikiRequest request = context.getRequest();
        XWikiResponse response = context.getResponse();
        XWikiDocument doc = context.getDoc();
        XWikiForm form = context.getForm();

        XWikiDocument olddoc = (XWikiDocument) doc.clone();
        String propName = ((PropAddForm) form).getPropName();

        if(propName ==null || propName.equals("") || !propName.matches("[\\w\\.\\-\\_]+") ){
            context.put("message","propertynamenotcorrect");
            return true;
        }

        String propType = ((PropAddForm) form).getPropType();
        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(doc.getFullName());
        if (bclass.get(propName)!=null) {
            // TODO: handle the error of the property already existing when we want to add a class property
        } else {
            MetaClass mclass = xwiki.getMetaclass();
            PropertyMetaClass pmclass = (PropertyMetaClass) mclass.get(propType);
            if (pmclass!=null) {
                PropertyClass pclass = (PropertyClass) pmclass.newObject(context);
                pclass.setObject(bclass);
                pclass.setName(propName);
                pclass.setPrettyName(propName);
                bclass.put(propName, pclass);
                String username = context.getUser();
                doc.setAuthor(username);
                if (doc.isNew()) {
                    doc.setCreator(username);
                }
                xwiki.saveDocument(doc, olddoc, context);
            }
        }
        // forward to edit
        String redirect = Utils.getRedirect("edit", context);
        sendRedirect(response, redirect);
        return false;
	}

     public String render(XWikiContext context) throws XWikiException{
         return "exception";
     }
}
