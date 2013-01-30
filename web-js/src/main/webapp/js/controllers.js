'use strict';

function EventListCtrl($scope, Event, $routeParams) {
    $scope.events = Event.query($routeParams);
}


function EventSearchCtrl($scope, Event, $location) {
    $scope.query = "",
        
    $scope.search = function() {
        $location.url("/events?query=" + $scope.query);
        $scope.query = "";
    };
}

function EventMineCtrl($scope, Event) {
    $scope.events = Event.query({
        paramDefaults : {
            max : 1
        },
        params : $routeParams
    });
}

function EventDetailCtrl($scope, $routeParams, Event) {
    $scope.event = Event.get({
        eventId : $routeParams.eventId
    });
}

function EventCreateCtrl($scope, Event, $location) {
    $scope.event = {
        title : "",
        description : ""
    };

    $scope.save = function() {
        Event.put($scope.event, function() {
            $location.path("#/events");
        });
    };
}