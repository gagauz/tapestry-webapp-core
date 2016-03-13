(function(){
	define(["jquery", "bootstrap/modal"], function($, modal) {
		return {
			showModal: function(id) {
				$('#'+id).modal('show');
			},
			hideModal: function(id) {
				$('#'+id).modal('hide');
			}
		};
	});
}).call(this);