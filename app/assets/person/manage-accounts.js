$(function(){
    var $socialConnect = $('.js-social-connect');
    if ($socialConnect.length){
        new App.widgets.PersonSocialConnect('.js-social-connect');
    }

    new App.widgets.EmailConnectDlg('#dlg-email-connect', {
        url: jsRoutes.controllers.UserAccounts.handleNewPassword().url,
        success: function(){
            $('.js-email-connect').addClass('show_connected');
            success("You created new email account");
        }
    });

    new App.widgets.EmailConnectDlg('#dlg-change-password', {
        url: jsRoutes.controllers.UserAccounts.changePassword().url,
        success: function() {
            var msg = "Your password was successfully updated";
            success(msg);
        }
    });

    new App.widgets.EmailConnectDlg('#dlg-change-email', {
        url: jsRoutes.controllers.UserAccounts.changeEmail().url,
        success: function() {
            var msg = "Please check your mailbox and click a confirmation link to complete an email change process";
            success(msg);
        }
    });
});