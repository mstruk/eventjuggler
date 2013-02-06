'use strict';

function EventListCtrl($scope, Event, $routeParams) {
    var loadUntilPageIsFull = function(events) {
        $scope.events = events;
        $scope.$watch('events', function() {
            if ($("body").height() < $(window).height()) {
                $scope.events.loadNext(loadUntilPageIsFull);
            }
        });
    };

    $scope.events = Event.getEvents(loadUntilPageIsFull);

    $(window).scroll(function() {
        if ($(window).scrollTop() == $(document).height() - $(window).height()) {
            $scope.events.loadNext();
        }
    });
}

function EventSearchCtrl($scope, Event, $location) {
    $scope.search = function() {
        $location.url("/events?query=" + $scope.query);
        delete $scope.query;
    };
}

function EventMineCtrl($scope, Event) {
    $scope.events = Event.getEventsUser();
}

function EventDetailCtrl($scope, $routeParams, Event) {
    $scope.event = Event.getEvent($routeParams.eventId);
    
    $scope.attend = function() {
        Event.attend($scope.event.id, function() { alert("attending"); });
    };
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

function UserCtrl($scope, User) {
    User.getUser(function(user) {
        $scope.user = user;
    });

    $scope.login = function() {
        User.login($scope.username, $scope.password, function(user) {
            $scope.user = user;
            delete $scope.username;
            delete $scope.password;
        });
    };

    $scope.logout = function() {
        User.logout();
        delete $scope.user;
    };
}

function RegisterCtrl($scope, User) {
    $scope.user = {};
    
    $scope.register = function() {
        User.register($scope.user, function() { alert("registered"); },  function() { alert("failed to register"); });
    };
}