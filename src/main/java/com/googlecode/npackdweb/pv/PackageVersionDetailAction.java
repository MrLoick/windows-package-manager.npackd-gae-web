package com.googlecode.npackdweb.pv;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.npackdweb.DefaultServlet;
import com.googlecode.npackdweb.Version;
import com.googlecode.npackdweb.db.PackageVersion;
import com.googlecode.npackdweb.wlib.Action;
import com.googlecode.npackdweb.wlib.ActionSecurityType;
import com.googlecode.npackdweb.wlib.Page;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

/**
 * A package version.
 */
public class PackageVersionDetailAction extends Action {
    /**
     * -
     */
    public PackageVersionDetailAction() {
        super("^/p/([^/]+)/([\\d.]+)$", ActionSecurityType.ANONYMOUS);
    }

    @Override
    public Page perform(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Pattern p = Pattern.compile(getURLRegExp());
        Matcher m = p.matcher(req.getRequestURI());
        m.matches();
        String package_ = m.group(1);
        String version = m.group(2);

        Objectify ofy = DefaultServlet.getObjectify();
        PackageVersion r = ofy.find(new Key<PackageVersion>(
                PackageVersion.class, package_ + "@" + version));
        if (r == null) {
            Version v = Version.parse(version);
            if (!v.toString().equals(version)) {
                resp.sendRedirect("/p/" + package_ + "/" + v.toString());
                return null;
            }
        }

        PackageVersionPage pvp = null;
        if (r == null) {
            User u = UserServiceFactory.getUserService().getCurrentUser();
            if (u != null) {
                r = new PackageVersion(package_, version);
                pvp = new PackageVersionPage(r, true);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            pvp = new PackageVersionPage(r, false);
        }
        return pvp;
    }
}
