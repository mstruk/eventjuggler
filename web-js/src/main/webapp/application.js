var restUrl = location.protocol + "//" + location.host + "/eventjuggler-rest";
var selectedEvents = {};

function loadEvents() {
	$.getJSON(restUrl + "/event", displayEvents);
}

function selectEvent(checkbox) {
	if (checkbox.checked) {
		selectedEvents[checkbox.value]=true;
	} else {
		delete selectedEvents[checkbox.value];
	}
}

function displayEvents(events) {
	var content = "<table class=\"table table-striped\">";
	content += "<tr>";
	content += "<th> </th>";
	content += "<th>ID</th>";
	content += "<th>Title</th>";
	content += "<th>Description</th>";
	content += "</tr>";

	events.sort(function (x, y) { return x.title.localeCompare(y.title); });
	
	for ( var i in events) {
		content += "<tr>";
		content += "<td style='width: 2em;'><input type='checkbox' value='" + events[i].id + "' onClick='selectEvent(this)'/>" + "</td>";
		content += "<td>" + events[i].id + "</td>";
		content += "<td>" + events[i].title + "</td>";
		content += "<td>" + events[i].description + "</td>";
		content += "</tr>";
	}
	content += "</table>";

	$("#events").html(content);
}

function putEvent() {
	var event = JSON.stringify({
		"title" : $("#title").val(),
		"description" : $("#description").val()
	});
	
	$("#title").val(null);
	$("#description").val(null);
	
	$.ajax({
		url : restUrl + "/event",
		type : "PUT",
		contentType : "application/json",
		data : event,
		success : loadEvents
	});
}

function removeSelectedEvents() {
	for (id in selectedEvents) {
		removeEvent(id);
	}
	selectedEvents = {};
}

function removeEvent(id) {
	$.ajax({
		url : restUrl + "/event/" + id,
		type : "DELETE",
		success : loadEvents
	});
}

window.onload=loadEvents;
document.getElementById("createEvent").onsubmit=function() { $('#myModal').modal('hide'); putEvent(); return false; };
document.getElementById("createEventButton").onclick=function() { $('#myModal').modal('hide'); putEvent(); return false; };
