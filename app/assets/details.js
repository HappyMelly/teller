$(document).ready( function() {

    // Delete links.
    $('form.delete').submit(function() {
        return confirm('Delete this organisation? You cannot undo this action.');
    });

    // Datatables
    $.extend( $.fn.dataTableExt.oStdClasses, {
        "sWrapper": "dataTables_wrapper form-inline"
    } );
    $('.datatables').each(function() {
        $(this).dataTable( {
            "sPaginationType": "bootstrap",
            "sDom": "<'row'<'span4'l><'span4'f>r>t<'row'<'span4'i><'span4'p>>",
            "iDisplayLength": 100,
            "asStripeClasses":[],
            "aaSorting": [],
            "bFilter": false,
            "bInfo": false,
            "bLengthChange": false,
            "bPaginate": false
        });
    });


});

