/**
 * @author Dimitry Kudrayvtsev
 * @version 2.0
 */

d3.gantt = function(componentId, tasks) {
    var FIT_TIME_DOMAIN_MODE = "fit";
    var FIXED_TIME_DOMAIN_MODE = "fixed";
    var offsetX = 0;
    var offsetY = 0;
    var minWidth = 200;
    
    var unorderedTasks = tasks.slice();    
    
    var margin = {
	top : 20,
	right : 40,
	bottom : 20,
	left : 100
    };
    var timeDomainStart = d3.time.day.offset(new Date(),-3);
    var timeDomainEnd = d3.time.hour.offset(new Date(),+3);
    var timeDomainMode = FIT_TIME_DOMAIN_MODE;// fixed or fit
    var taskTypes = [];
    var taskStatus = [];
    var height = document.body.clientHeight - margin.top - margin.bottom-5;
    var width = document.body.clientWidth - margin.right - margin.left-5;

    var tickFormat = "%H:%M";

    var keyFunction = function(d) {
	return d.startDate + d.taskName + d.endDate;
    };

    var rectTransform = function(d) {
	return "translate(" + x(d.startDate) + "," + y(d.taskName) + ")";
    };

    var x = d3.time.scale().domain([ timeDomainStart, timeDomainEnd ]).range([ 0, width ]).clamp(true);

    var y = d3.scale.ordinal().domain(taskTypes).rangeRoundBands([ 0, height - margin.top - margin.bottom ], .1);
    
    var xAxis = d3.svg.axis().scale(x).orient("bottom").tickFormat(d3.time.format(tickFormat)).tickSubdivide(true)
	    .tickSize(8).tickPadding(8);

    var yAxis = d3.svg.axis().scale(y).orient("left").tickSize(0);

	if (componentId === null || componentId === undefined) { 
		componentId = "body";
	} else {
		componentId = "#"+componentId;
		
		// Define the div for the tooltip
	    var div = d3.select(componentId).append("div")	
	        .attr("class", "with-3d-shadow with-transitions nvtooltip xy-tooltip")				
	        .style("opacity", 0);
	    
		var parent = $(componentId);
		height = parent.height();
	    width = parent.width();
	    
	    if (width < minWidth && parent !== undefined) {
		    
	    	height = 50 + (getUniqueTaskNames(taskNames) * 50);
	    	
	    	// nastavenie width pre firefox pretoze sa stavalo, ze pri prepnuti tabu po pridani novej ziadosti
	    	// sa nestihli vykreslit komponenty podla ktorych sa nastavola velkost grafu.
	    	width = $($(".form-group").get(0)).width();
	    	
	    	if (width === null || width < minWidth) {
		    	var parentsElem = parent.parents();	
		    	for (var i = 0; i < parentsElem.length; i++) {
			    	if (width < minWidth) {
				    	parent = parentsElem.get(i);
				    	width = $(parent).width();
				    }
		    	}
	    	}
	    }
	    
	    x = d3.time.scale().domain([ timeDomainStart, timeDomainEnd ]).range([ 0, width ]).clamp(true);
	    y = d3.scale.ordinal().domain(taskTypes).rangeRoundBands([ 0, height - margin.top - margin.bottom ], .1);
	    xAxis = d3.svg.axis().scale(x).orient("bottom").tickFormat(d3.time.format(tickFormat)).tickSubdivide(true)
	    .tickSize(8).tickPadding(8);
	    yAxis = d3.svg.axis().scale(y).orient("left").tickSize(0);
	}
    
    var initTimeDomain = function() {
	if (timeDomainMode === FIT_TIME_DOMAIN_MODE) {
	    if (tasks === undefined || tasks.length < 1) {
		timeDomainStart = d3.time.day.offset(new Date(), -3);
		timeDomainEnd = d3.time.hour.offset(new Date(), +3);
		return;
	    }
	    tasks.sort(function(a, b) {
		return a.endDate - b.endDate;
	    });
	    timeDomainEnd = tasks[tasks.length - 1].endDate;
	    tasks.sort(function(a, b) {
		return a.startDate - b.startDate;
	    });
	    timeDomainStart = tasks[0].startDate;
	}
    };

    var initAxis = function() {
	x = d3.time.scale().domain([ timeDomainStart, timeDomainEnd ]).range([ 0, width - (margin.left + 10)]).clamp(true);
	y = d3.scale.ordinal().domain(taskTypes).rangeRoundBands([ 0, height - margin.top - margin.bottom ], .1);
	xAxis = d3.svg.axis().scale(x).orient("bottom").tickFormat(d3.time.format(tickFormat)).tickSubdivide(true)
		.tickSize(8).tickPadding(8);

	yAxis = d3.svg.axis().scale(y).orient("left").tickSize(0);
    };
    
    function gantt(tasks, requestClickedCallback) {
	
	initTimeDomain();
	initAxis();
	
	var svg = d3.select(componentId)
	.append("svg")
	.attr("class", "chart")
	.attr("width", width)
	.attr("height", height + margin.top + margin.bottom)
	.attr("style", "overflow: visible;")
	.append("g")
        .attr("class", "gantt-chart")
	.attr("width", width)
	.attr("height", height + margin.top + margin.bottom)
	.attr("transform", "translate(" + margin.left + ", " + margin.top + ")");
	
      svg.selectAll(".chart")
	 .data(unorderedTasks, keyFunction).enter()
	 .append("rect")
	 .attr("rx", 5)
         .attr("ry", 5)
	 .attr("class", function(d){ 
	     if(taskStatus[d.status] == null){ return "bar";}
	     return taskStatus[d.status];
	     }) 
	 .attr("y", 0)
	 .attr("transform", rectTransform)
	 .attr("height", function(d) { return y.rangeBand(); })
	 .attr("width", function(d) { 
	     return (x(d.endDate) - x(d.startDate)); 
	     })
	    .attr("cx", function(d) { return x(d.date); })		 
        .attr("cy", function(d) { return y(d.close); })		
        .on('click', function(d,i){ 			
        	Wicket.Ajax.get({
			'u': requestClickedCallback,
			"ep":[
			      {'name':'id','value': d.id},
			      ],
			});  })
        .on("mouseover", function(d) {		
    		offsetX = $(componentId).offset().left;
    		offsetY = $(componentId).offset().top;
            div.transition()		
                .duration(200)		
                .style("opacity", .9);		
            div	.html("<h3>" + d.type + "</h3><p>"  + d.tooltipText + "</p>")	
                .style("left", (d3.event.pageX - offsetX) + "px")		
                .style("top", (d3.event.pageY - offsetY - 10) + "px");	
            })					
        .on("mouseout", function(d) {		
            div.transition()		
                .duration(500)		
                .style("opacity", 0);	
        });
	 
	 
	 svg.append("g")
	 .attr("class", "x axis")
	 .attr("transform", "translate(0, " + (height - margin.top - margin.bottom) + ")")
	 .transition()
	 .call(xAxis);
	 
	 svg.append("g").attr("class", "y axis").transition().call(yAxis);
	 
	 return gantt;

    };
    
    gantt.redraw = function(tasks) {

	initTimeDomain();
	initAxis();
	
        var svg = d3.select("svg");

        var ganttChartGroup = svg.select(".gantt-chart");
        var rect = ganttChartGroup.selectAll("rect").data(tasks, keyFunction);
        
        rect.enter()
         .insert("rect",":first-child")
         .attr("rx", 5)
         .attr("ry", 5)
	 .attr("class", function(d){ 
	     if(taskStatus[d.status] == null){ return "bar";}
	     return taskStatus[d.status];
	     }) 
	 .transition()
	 .attr("y", 0)
	 .attr("transform", rectTransform)
	 .attr("height", function(d) { return y.rangeBand(); })
	 .attr("width", function(d) { 
	     return (x(d.endDate) - x(d.startDate)); 
	     });

        rect.transition()
          .attr("transform", rectTransform)
	 .attr("height", function(d) { return y.rangeBand(); })
	 .attr("width", function(d) { 
	     return (x(d.endDate) - x(d.startDate)); 
	     });
        
	rect.exit().remove();

	svg.select(".x").transition().call(xAxis);
	svg.select(".y").transition().call(yAxis);
	
	return gantt;
    };

    gantt.margin = function(value) {
	if (!arguments.length)
	    return margin;
	margin = value;
	return gantt;
    };

    gantt.timeDomain = function(value) {
	if (!arguments.length)
	    return [ timeDomainStart, timeDomainEnd ];
	timeDomainStart = +value[0], timeDomainEnd = +value[1];
	return gantt;
    };

    /**
     * @param {string}
     *                vale The value can be "fit" - the domain fits the data or
     *                "fixed" - fixed domain.
     */
    gantt.timeDomainMode = function(value) {
	if (!arguments.length)
	    return timeDomainMode;
        timeDomainMode = value;
        return gantt;

    };

    gantt.taskTypes = function(value) {
	if (!arguments.length)
	    return taskTypes;
	taskTypes = value;
	return gantt;
    };
    
    gantt.taskStatus = function(value) {
	if (!arguments.length)
	    return taskStatus;
	taskStatus = value;
	return gantt;
    };

    gantt.width = function(value) {
	if (!arguments.length)
	    return width;
	width = +value;
	return gantt;
    };

    gantt.height = function(value) {
	if (!arguments.length)
	    return height;
	height = +value;
	return gantt;
    };

    gantt.tickFormat = function(value) {
	if (!arguments.length)
	    return tickFormat;
	tickFormat = value;
	return gantt;
    };

	function getUniqueTaskNames(taskNames) {
		var uniqueTasks = [];
		var unique = true;
		
		for (var i = 0; i < taskNames.length; i++) {
			unique = true;
			for (var j = 0; j < uniqueTasks.length; j++) {
				if (uniqueTasks[j] === taskNames[i]) {
					unique = false;
				}
			}
			if (unique) {
				uniqueTasks.push(taskNames[i])
			}
		}
		
		return uniqueTasks.length;
	}
    
    return gantt;
};