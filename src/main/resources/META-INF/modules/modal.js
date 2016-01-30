(function(){
	define(["jquery", "bootstrap/modal"], function($, modal) {
		var showModal = function(id) {
			$('#'+id).modal('show');
		};
		return {showModal: showModal};
	});
}).call(this);