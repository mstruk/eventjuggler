'use strict';

function EventListCtrl($scope, Event) {
    $scope.events = Event.query();
}

function EventMineCtrl($scope, Event) {
    $scope.events = Event.query();
}

function EventDetailCtrl($scope, $routeParams, Event) {
    $scope.event = Event.get({
        eventId : $routeParams.eventId
    });
}

function EventCreateCtrl($scope, $location, Event) {
    $scope.event = { title: "", description: "" };

    $scope.save = function() {
        Event.put($scope.event, function() { $location.path("#/events"); });
    };
}