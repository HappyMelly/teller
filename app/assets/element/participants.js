/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

var html = `
	<div id="confirmEmail" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="rejectLabel" aria-hidden="true"> 
		<div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		        <h4>Are you sure you want to proceed?</h4>
		      </div>
		      <div class="modal-footer">
		        <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		        <a id="confirmLink" class="btn btn-primary" href="#"> Confirm Resend </a>
		      </div>
		    </div>
	  </div>
	</div>
	`

var dialog = 'resend';
const delay = 25;

/**
 * continue resending emails to participants
 */
function resendEmailsToAll(data) {
	if(data.length > 0){
		$('#confirmLink').data({'ids': data});
	    $('#' + dialog).modal('show');
	    window.setTimeout(function(){
	    	$('#confirmEmail').modal('show');
	    }, delay);
	} else console.log("No participants to resend email.");
}

$(document).ready(function(){

	$('body').append(
		$('<div id="confirmEmail" class="modal fade" tabIndex="-1">').
        attr('id', dialog).attr('role', 'dialog').
        attr('aria-hidden', 'true').append(html));

	$('#confirmLink').on('click', function(e){
		var ids = $.data(this, 'ids');
		var queue = $.Deferred().resolve();
		var pass = 0;
		$.each(ids, function(index, value){
			var evaluationId = value;
			queue = queue.then(function(){
				var url = jsRoutes.controllers.Evaluations.sendConfirmationRequest(evaluationId).url;
				return $.post(url, {}, function(data) {
			        pass = pass + 1;
			        if(pass == ids.length) {
			        	success(JSON.parse(data).message);
			        	window.setTimeout(function(){
				        	$('#' +  dialog).modal('hide');
				        	$('#confirmEmail').modal('hide');
			        	}, delay);
			        }
			    }).fail(function(jqXHR) {
			        var msg = JSON.parse(jqXHR.responseText);
			        error(msg.message)
			    });
			});
		});
	});
});
