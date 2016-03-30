'use strict';

import EmailConnect from "./manage-widgets/_email-connect";
import SocialConnect from "./manage-widgets/_social-connect";


$(function(){
    SocialConnect.plugin('.js-social-connect');

    EmailConnect
        .plugin('#dlg-email-connect', {
            url: jsRoutes.controllers.core.UserAccounts.handleNewPassword().url
        })
        .on('hmt.emailconnect.success', (e)=>{
            $('.js-email-connect').addClass('show_connected');
            success("You created new email account");
        });

    EmailConnect
        .plugin('#dlg-change-password', {
            url: jsRoutes.controllers.core.UserAccounts.changePassword().url,
        })
        .on('hmt.emailconnect.success', (e)=>{
            let msg = "Your password was successfully updated";
            success(msg);
        });

    EmailConnect
        .plugin('#dlg-change-email', {
            url: jsRoutes.controllers.core.UserAccounts.changeEmail().url,
        })
        .on('hmt.emailconnect.success', (e)=>{
            let msg = "Please check your mailbox and click a confirmation link to complete an email change process";
            success(msg);
        });
});



