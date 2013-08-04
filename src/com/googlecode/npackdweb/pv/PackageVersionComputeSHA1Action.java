package com.googlecode.npackdweb.pv;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.npackdweb.MessagePage;
import com.googlecode.npackdweb.NWUtils;
import com.googlecode.npackdweb.NWUtils.Info;
import com.googlecode.npackdweb.PackageVersion;
import com.googlecode.npackdweb.wlib.Action;
import com.googlecode.npackdweb.wlib.ActionSecurityType;
import com.googlecode.npackdweb.wlib.Page;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

/**
 * Compute SHA1 for a package version.
 */
public class PackageVersionComputeSHA1Action extends Action {
    /**
     * -
     */
    public PackageVersionComputeSHA1Action() {
        super("^/package-version/compute-sha1$", ActionSecurityType.EDITOR);
    }

    @Override
    public Page perform(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String package_ = req.getParameter("package");
        String version = req.getParameter("version");
        Objectify ofy = NWUtils.getObjectify();
        PackageVersion p = ofy.get(new Key<PackageVersion>(
                PackageVersion.class, package_ + "@" + version));
        Page ret = null;
        Info info = p.check(false);
        if (info != null)
            p.sha1 = NWUtils.byteArrayToHexString(info.sha1);
        NWUtils.savePackageVersion(ofy, p);
        ret = new PackageVersionPage(p, false);
        if (p.downloadCheckError != null)
            ret = new MessagePage("Cannot download the file: "
                    + p.downloadCheckError);
        return ret;
    }
}