$(document).ready( function() {
    $.extend( $.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    } );
    $('.datatables').each(function() {
        $(this).dataTable( {
            "sPaginationType": "bootstrap",
            "sDom": "<'row'<'span5'l><'span5'f>r>t<'row'<'span5'i><'span5'p>>",
            "iDisplayLength": 25,
            "asStripeClasses":[],
            "aaSorting": [],
            "bLengthChange": false
        });
    });
});
