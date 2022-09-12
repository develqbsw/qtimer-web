var isDraggable=true;
function initTree(id,getDataCallBack,nodeChangedCallback,nodeSelectedCallback,idNodeToOpen){
	var tree=$('#'+id).jstree({
		'core':{
			'data':{
				'url'	:function(node){return getDataCallBack;},
				'data'	:function(node){return {'id':node.id};},
				'dataType':'json',
			},
			check_callback : function (op, node, par, pos, more) {
				if( (op === "move_node" || op === "copy_node") && more && more.dnd && (more.pos !== "i" && more.pos !== "a")) { // mozne stavy b - before, a - after, i - inside
					return false;
				}
			},
		},
		'dnd' : {
			'is_draggable' : function () {
				return isDraggable;
			}
		},
//		'types':{
//			'node':{
//				'valid_children':['node','leaf'],
//			},
//			'leaf':{
////				'icon':'../assets-sed/img/tree_leaf.png',
//				'valid_children':['node','leaf'],
//			}
//		},
		'plugins':["dnd"],
	});
	tree.on('loaded.jstree', function() {
		tree.jstree('open_all');
	});
	tree.on('loaded.jstree', function() {
		tree.jstree('open_all');
	});

	if(nodeSelectedCallback!==undefined){
		tree.on("select_node.jstree",function (e, data) {
			Wicket.Ajax.get({
				'u': nodeSelectedCallback,
				"ep":[
				      {'name':'id','value':data.node.id},
				      ],
				});
		});
	}

	tree.on("move_node.jstree", function (e, data) {
		var parentNew=data.parent==='#'?null:data.parent;
		var parentOld=data.old_parent==='#'?null:data.old_parent;

		Wicket.Ajax.get({
			'u': nodeChangedCallback,
			"ep":[
			      {'name':'id','value':data.node.id},
			      {'name':'parent_id','value':parentNew},
			      {'name':'parent_old_id','value':parentOld},
			      ],
			});
	});
}
function setDraggable(value){
	isDraggable=value;
}
//rekurzivne otvaranie uzlov
function openNode(tree, idNode){
	var thisNode = tree.jstree("get_node", idNode);
	var idParent=thisNode.parent;
	if(idParent!==undefined&&idParent!=='#'){
		openNode(tree, idParent);
	}
	tree.jstree("open_node",tree.find("#" + idNode));
}
//obnov strom aj s nacitanim uzlov zo serveru
function refreshTree(idComponent,idNode,getDataCallBack,nodeChangedCallback,nodeSelectedCallback){
	var tree=$("#"+idComponent);
	tree.jstree("destroy");
	initTree(idComponent, getDataCallBack, nodeChangedCallback,nodeSelectedCallback,idNode);
}

//zatial sa nepouziva
function moveNode(idComponent,idNode,idFrom,idTo){
	var tree=$("#"+idComponent);
	var node=tree.find("#" + idNode);
	var nodeTo=tree.find("#" + idTo);
	tree.jstree("move_node", node, nodeTo);
	tree.jstree("refresh");
}
