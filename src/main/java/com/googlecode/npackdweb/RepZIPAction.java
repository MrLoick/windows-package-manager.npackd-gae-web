package com.googlecode.npackdweb;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.npackdweb.db.Repository;
import com.googlecode.npackdweb.wlib.Action;
import com.googlecode.npackdweb.wlib.ActionSecurityType;
import com.googlecode.npackdweb.wlib.Page;
import com.googlecode.objectify.Objectify;

/**
 * Creates ZIP for a whole repository definition.
 */
public class RepZIPAction extends Action {
	/**
	 * -
	 */
	public RepZIPAction() {
		super("^/rep/zip$", ActionSecurityType.ANONYMOUS);
	}

	@Override
	public Page perform(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String tag = req.getParameter("tag");

		Objectify ofy = DefaultServlet.getObjectify();
		Repository r = Repository.findByTag(ofy, tag);
		if (r == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Repository " +
					tag + " not found");
			return null;
		} else {
			return new RepZIPPage(tag);
		}
	}
}
