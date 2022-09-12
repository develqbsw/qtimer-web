var groupClass="form-group";
var groupClassCell="form-group-item";
var SedApp = function() {
	var initSedComponents = function() {
	};
	return {

		// main function to initiate template pages
		init : function() {
			initSedComponents();
		}
	};
}();
function afterError() {
	validate();
	FormElements.init();
}
function validate() {
	$(".input-error").each(function() {
		if($(this).parent().hasClass("error_row")){//special row error when the form row consists of several other rows
			var row=$(this).parent();

			row.addClass( "error" );
			row.find(".select-control").addClass("parsley-error");
			row.closest("."+groupClass+','+'.'+groupClassCell).find(".control-label").addClass("error");

		} else {//all other errors
			var formGroup=$(this).closest("."+groupClass+','+'.'+groupClassCell);

			if(formGroup.hasClass( groupClass)||formGroup.hasClass( groupClassCell)){
				formGroup.addClass( "has-error" );
				formGroup.find(".input_with_error").addClass( "parsley-error" );
			}
		}
	});
}

function parseTime(obj) {

	var val = obj.value;

	if (val == null || val === "") {
		return;
	}
	
	var hours;
	var minutes;

	var separatorIndex = val.indexOf(":");
	if (separatorIndex < 1) {
		if (val.length >= 4) {
			val = val.substring(0, 2) + ':' + val.substring(2, 4);
			separatorIndex = 2;
		} else if (val.length === 3) {
			val = val.substring(0, 2) + ':' + val.substring(2, 3);
			separatorIndex = 2;
		} else if (val.length <= 2) {
			val = val + ":00";
			separatorIndex = 2;
		}
	}

	hours = getValidValue(val.substring(0, separatorIndex), true);
	minutes = getValidValue(val.substring(separatorIndex+1), false);

	obj.value = hours + ":" + minutes;
}

function parseTimeDuration(obj) {

	var val = obj.value;
	
	if (val == null || val === "") {
		return;
	}
	
	var hours;
	var minutes;

	var separatorIndex = val.indexOf(":");
	if (separatorIndex < 1) {

		if (val.length >= 4) {
			val = val.substring(0, 2) + ':' + val.substring(2, 4);
			separatorIndex = 2;
		} else if (val.length === 3) {
			val = val.substring(0, 1) + ':' + val.substring(1, 3);
			separatorIndex = 1;
		} else if (val.length === 2) {
			val = val + ":00";
			separatorIndex = 2;
		} else if (val.length === 1) {
			val = val + ":00";
			separatorIndex = 1;
		}
	}

	hours = formatTo2Digit(parseInt(val.substring(0, separatorIndex)));
	minutes = formatTo2Digit(parseInt(val.substring(separatorIndex+1)));

	obj.value = hours + ":" + minutes;
}

function parseTimeIfEnter(e, obj) {

		var enterKey = 13;
		var charCode = (typeof e.which === "number") ? e.which : e.keyCode;

		if (charCode === enterKey){
			parseTime(obj);
        }
	}

function getValidValue(value, hour) {

	var max;
	var val = parseInt(value);

	if (hour) {
		max = 23;	
	} else {
		max = 59;
	}

	if(val > max) {
		return max;
	} else {
		return formatTo2Digit(val);
	}
}

function formatTo2Digit(value) {

	if (value < 10) {
		return "0" + value;
	} else {
		return value;
	}
}

// panel refresh
function refresh(el) {
     el.block({
         overlayCSS: {
             backgroundColor: '#fff'
         },
         message: '<i class="fa fa-spinner fa-spin"></i>',
         css: {
             border: 'none',
             color: '#333',
             background: 'none'
         }
     });
     window.setTimeout(function() {
         el.unblock();
     }, 500);
}

function readURL(input) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();

		reader.onload = function (e) {
			$('div.form-group > div > img').attr('src', e.target.result);
		}

		reader.readAsDataURL(input.files[0]);
	}
}

function scrollToError() {
	var errorList = $('.has-error');
	if(errorList.length !== 0) {
		$('html, body').animate({
	        scrollTop: $(errorList[0]).offset().top
	    }, 2000);
	}
}
