package controllers;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.h2.constant.SysProperties;
import org.joda.time.Period;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.PathParam;
import ninja.utils.NinjaProperties;
import filters.AdminFilter;
import filters.SecureFilter;

import models.MailTransaction;
import models.User;

/**
 * Handles all Actions for the Admin Section
 * 
 * @author Patrick Thum 2012 released under Apache 2.0 License
 */

@FilterWith(
    {
        SecureFilter.class, AdminFilter.class
    })
@Singleton
public class AdminHandler
{
    @Inject
    NinjaProperties ninjaProp;

    @Inject
    Messages msg;

    @Inject
    MailrMessageHandlerFactory mmhf;

    @Inject
    MemCachedSessionHandler mcsh;

    // ---------------------Functions for the Admin-Section ---------------------
    /**
     * Shows a list of all Users in the DB site/admin
     * 
     * @param context
     * @return a list of all Users
     */
    public Result showAdmin(Context context, HttpServletRequest req, String no)
    {
        return Results.html();
    }

    /**
     * Shows a list of all Users in the DB site/admin
     * 
     * @param context
     * @return a list of all Users
     */
    public Result showUsers(Context context, HttpServletRequest req, String no)
    {
        User usr = (User) mcsh.get(req.getSession().getId());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("users", User.all());
        map.put("uid", usr.getId());
        return Results.html().render(map);
    }

    /**
     * Shows a list of all Users in the DB site/admin
     * 
     * @param context
     * @return a list of all Users
     */
    public Result showSumTx(Context context, HttpServletRequest req, String no)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("stats", MailTransaction.getStatusList());
        return Results.html().render(map);
    }

    /**
     * Shows a list of all Users in the DB site/admin
     * 
     * @param context
     * @return a list of all Users
     */
    public Result showMTX(Context context, HttpServletRequest req, @PathParam("no") String no)
    {

        Map<String, Object> map = new HashMap<String, Object>();
        List<MailTransaction> mtxs ;
        int value = Integer.parseInt(no);
        switch(value){
            case(1): //1hour
                //years, months, weeks, days, hours, minutes, secs, millis
                mtxs = MailTransaction.allInPeriod(new Period(0, 0, 0, 0, 1, 0, 0, 0));
                break;
            case(2)://1day
                mtxs = MailTransaction.allInPeriod(new Period(0, 0, 0, 1, 0, 0, 0, 0));
                break;
            case(3)://1week
                mtxs = MailTransaction.allInPeriod(new Period(0, 0, 1, 0, 0, 0, 0, 0));
                break;
            case(4)://1week
                mtxs = MailTransaction.allInPeriod(new Period(0, 1, 0, 0, 0, 0, 0, 0));
                break;
            default://all
                mtxs = MailTransaction.all();        
                break;
        }
        
        // 
        map.put("mtxs", mtxs);
        return Results.html().render(map);
    }

    /**
     * activates or deactivates the User with the given id <br/>
     * POST /admin/activate/{id}
     * 
     * @param id
     *            - id of a user
     * @return the admin-page
     */
    public Result activate(@PathParam("id") Long id, Context context, HttpServletRequest req)
    {
        User usr = (User) mcsh.get(req.getSession().getId());
        if (!(usr.getId() == id))
        { // the user to (de-)activate is not the user who performs this action

            // activate or deactivate the user
            boolean active = User.activate(id);

            // generate the (de-)activation-information mail and send it to the user
            User actusr = User.getById(id);
            String from = ninjaProp.get("mbox.adminaddr");
            String host = ninjaProp.get("mbox.host");

            if (active)
            { // the account is now active
              // generate the message title
                Object[] param = new Object[]
                    {
                        host
                    };
                String subject = msg.get("i18nuser_activate_title", context.getAcceptLanguage(), param);
                // generate the message body
                param = new Object[]
                    {
                        actusr.getForename()
                    };
                String content = msg.get("i18nuser_activate_message", context.getAcceptLanguage(), param);
                // send the mail
                mmhf.sendMail(from, actusr.getMail(), content, subject);
            }
            else
            {// the account is now inactive
             // generate the message title
                Object[] param = new Object[]
                    {
                        host
                    };
                String subject = msg.get("i18nuser_deactivate_title", context.getAcceptLanguage(), param);
                // generate the message body
                param = new Object[]
                    {
                        actusr.getForename()
                    };
                String content = msg.get("i18nuser_deactivate_message", context.getAcceptLanguage(), param);
                // send the mail
                mmhf.sendMail(from, actusr.getMail(), content, subject);

            }

            return Results.redirect("/admin");
        }
        else
        {
            return Results.redirect("/admin");
        }
    }

    /**
     * Pro- or demotes the User with the given id <br/>
     * POST /admin/promote/{id}
     * 
     * @param id
     *            - id of a user
     * @return the admin-page
     */
    public Result promote(@PathParam("id") Long id, Context context, HttpServletRequest req)
    {
        User usr = (User) mcsh.get(req.getSession().getId());
        if (!(usr.getId() == id))
        { // the user to pro-/demote is not the user who performs this action
            User.promote(id);
        }
        return Results.redirect("/admin");

    }

    /**
     * Handles the user delete function <br/>
     * POST /admin/delete/{id}
     * 
     * @param id
     * @return the admin-overview page ( /admin )
     */
    public Result deleteUser(@PathParam("id") Long id, Context context, HttpServletRequest req)
    {
        User usr = (User) mcsh.get(req.getSession().getId());
        if (!(usr.getId() == id))
        { // the user to delete is not the user who performs this action
            User.delete(id);
        }

        return Results.redirect("/admin");
    }

}
