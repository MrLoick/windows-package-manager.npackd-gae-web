package com.googlecode.npackdweb.package_;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.npackdweb.DefaultServlet;
import com.googlecode.npackdweb.MessagePage;
import com.googlecode.npackdweb.NWUtils;
import com.googlecode.npackdweb.db.Package;
import com.googlecode.npackdweb.wlib.Action;
import com.googlecode.npackdweb.wlib.ActionSecurityType;
import com.googlecode.npackdweb.wlib.Page;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

/**
 * Delete a package version.
 */
public class PackageDeleteConfirmedAction extends Action {
	/**
	 * -
	 */
	public PackageDeleteConfirmedAction() {
		super("^/package/delete-confirmed$", ActionSecurityType.LOGGED_IN);
	}

	@Override
	public Page perform(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String name = req.getParameter("name");
		Objectify ofy = DefaultServlet.getObjectify();
		Package r = ofy.find(new Key<Package>(Package.class, name));
		Page page;
		if (!r.isCurrentUserPermittedToModify())
			page =
					new MessagePage(
							"You do not have permission to modify this package");
		else {
			UserService us = UserServiceFactory.getUserService();
			User u = us.getCurrentUser();

			String message = req.getParameter("message");
			NWUtils.deletePackage(ofy, name);
			if (message != null) {
				if (!r.createdBy.getEmail().equals(u.getEmail()))
					NWUtils.sendMailTo("The package " + r.title + " (" +
							r.name + ") was deleted by " + u.getNickname() +
							".\n" + "More information: " + message,
							r.createdBy.getEmail());
			}
			resp.sendRedirect("/p");
			page = null;
		}
		return page;
	}
}
