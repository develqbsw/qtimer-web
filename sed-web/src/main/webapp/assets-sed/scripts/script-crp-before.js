// this script is rendered in the head tag
var overlayCounter=0;
var overlays=[];
//show spinner above table div
function tableBeforeLoad(id) {
	var panel = $("#" + id);
	var table=panel.find(".panel-body");
	panel.closest(".container");
	showOverlayDiv(table, panel, id);
}
// hide spinner
function hideOverlay(id) {
	hideOverlayForId(id + "_overlay");
}
function hideOverlayForId(id) {
	overlayCounter--;
	var elementToHide=$("#" + id);
	elementToHide.hide();
	elementToHide.css("display","none");
	elementToHide.remove();
	var index = overlays.indexOf(id);
	if (index > -1) {
		overlays.splice(index, 1);
	}
	if(overlayCounter===0){

	}
}

window.onload = setupFunc;
var loader = $("#page_loader");
loader.show();

function showOverlay(id){
	var el=$("#"+id);
	var panel=el.parents(".modal-dialog");
	if(panel.length===0){
		panel=el.parents(".portlet.box");
	}
	var parent=panel.parent();
	showOverlayDiv(panel, parent, id);
}

function setupFunc() {
	hideBusysign();
	$(".btn-load").click(function() {
		showBusysign();
	});

	Wicket.Event.subscribe('/ajax/call/complete', function(attributes, jqXHR,
			textStatus) {
		hideBusysign();
	});
}

function hideBusysign() {
	var loader = $("#page_loader");
	loader.hide();
}

function showBusysign() {
	var loader = $("#page_loader");
	loader.show();
}
function tableAfterLoad(id){
	hideOverlay(id);
}

function showOverlayDiv(overlayTo,overlayWhere,overlayId){
	var id=overlayId + "_overlay";
	if($('#'+id).length>0){
		return;
	}
	var overlay = $('<div />').appendTo(overlayWhere);

	if (overlay.offset() !== undefined) {
		var top=overlayTo.offset().top;
		if(overlayWhere.css("position")==="fixed"){
			top=0;
		}
	}
	var oId=overlayId + "_overlay";
	overlay.attr('id', oId);

	overlay.css("width", overlayTo.outerWidth())//
	.css("height", overlayTo.height())//
	.css("top", top)//
	.css("position", "absolute")//
	.css("background-color", "black")//
	.css("z-index", "1001")//
	.css("opacity", "0");
	overlay.html("<div class='spinner_outer'><div class='spinner_middle'><div class='spinner'></div></div></div>");
	overlayCounter++;
	overlays.push(oId);
}
function hideOverlayDivExpecter(idExpecter){
	hideOverlay(idExpecter);
}

function downloadButtonHide(link){
	link.attr('crp-text',link.html());
	link.attr('disabled', 'disabled').html("Načítavam");

	var divParent=link.parent("div.div_wrapper");
	var id=link.attr('id');
	var overlay = $('<div />').appendTo(divParent.parent('div'));
	overlay.attr('id', id + "_overlay");

	var isTable=link.parent('.tableActionButton').first()!==undefined;

	overlay.css("width", link.outerWidth(true))//
	.css("height", link.outerHeight())//
	.css("position", "absolute")//
	.css("background-color", "black")//
	.css("z-index", "1001")//
	.css("opacity", "0.1");
	if(!isTable){
		overlay.css("top", link.offset().top)//
		.css("left", divParent.offset().left);
	}
}

function downloadButtonShow(id){
	var link=$("#"+id);
	link.html(link.attr('crp-text'));
	link.attr('disabled', 'enabled');
	link.removeAttr('crp-text');
	hideOverlay(id);
}

function hideAllOverlays(){
	if(overlays){
		while (overlays.length>0) {
		    var id=overlays[overlays.length-1];
		    hideOverlayForId(id);
		}
	}
}
