$(function(){
    var $socialConnect = $('.js-social-connect');
    if ($socialConnect.length){
        new App.widgets.PersonSocialConnect('.js-social-connect');
    }

    new App.widgets.EmailConnectDlg('#dlg-email-connect', {
        url: jsRoutes.controllers.UserAccounts.handleNewPassword().url,
        success: function(){
           $('.js-email-connect').addClass('show_connected');
        }
    });

    new App.widgets.EmailConnectDlg('#dlg-change-password', {
        url: jsRoutes.controllers.UserAccounts.changePassword().url
    });

    new App.widgets.EmailConnectDlg('#dlg-change-email', {
        url: jsRoutes.controllers.UserAccounts.changeEmail().url,
    });
});