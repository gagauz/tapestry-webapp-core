Tapestry.MultiTabSelect = function(id, dst, src) {
    var $self = this;
    
    $self.order=src;
    
    $self.updateHidden = function() {
    	$self.values.html('');
    	$self.destination.find('option').each(function(i,e) {
    		$self.values.append($j('<input type="hidden">').attr('name',$self.id).val($j(e).val()));
    	});
    };
    
    $self.findNext = function (s,v) {
    	var i = $self.order.indexOf(v), all = $j(s).find('option').toArray();
    	for(x=0;x<all.length;x++) {
    		lv = $j(all[x]).val();
    		if($self.order.indexOf(lv) > i) {
    			return all[x];
    		}
    	}
    	return false;
    };
	
    $self.moveOption = function (s, d, all) {
    	var from = all ? $j(s).find('option:enabled') : $j(s).find('option:selected');
    	try {
			from.each(function(i,e){
				var v=$j(e).remove().val();
				$j(d).find('option[value="'+v+'"]');
				var n = $self.findNext(d,v);
				if (n) {
					$j(e).insertBefore(n);		
				} else {
					$j(e).appendTo(d);
				}
				$j(e).removeAttr("selected");
			});
		} catch(e) {console.log(e)};
		$self.updateHidden();
	};
	
	$self.sourceClick = function () {
		$self.selectAll.removeAttr('disabled');
		$self.select.removeAttr('disabled');
		$self.deselectAll.attr('disabled','disabled');
		$self.deselect.attr('disabled','disabled');
	};

	$self.destinationClick = function () {
		$self.deselectAll.removeAttr('disabled');
		$self.deselect.removeAttr('disabled');
		$self.selectAll.attr('disabled','disabled');
		$self.select.attr('disabled','disabled');
	};
	
	$self.onSelect = function (event) {
		$self.moveOption($self.source,$self.destination);
		event.preventDefault();
	};
	
	$self.onSelectAll = function (event) {
		$self.moveOption($self.source,$self.destination,true);
		event.preventDefault();
	};
	
	$self.onDeselect = function (event) {
		$self.moveOption($self.destination,$self.source);
		event.preventDefault();
	};
	
	$self.onDeselectAll = function (event) {
		$self.moveOption($self.destination,$self.source,true);
		event.preventDefault();
	};
	
    $self.id=id;
	id = '#'+id;
	$self.source = $j(id + "-source").bind("change", $self.sourceClick);
    $self.destination = $j(id + "-destination").bind("change", $self.destinationClick);
    
    $self.select = $j(id+'-select').click($self.onSelect).attr('disabled','disabled');
    $self.selectAll = $j(id+'-selectall').click($self.onSelectAll);
    $self.deselect = $j(id+'-deselect').click($self.onDeselect).attr('disabled','disabled');
    $self.deselectAll = $j(id+'-deselectall').click($self.onDeselectAll);
    $self.values = $j(id+'-values');
    
    $j(dst).each(function(i,e) {
		$self.values.append($j('<input type="hidden">').attr('name',$self.id).val(e));
	});
}



