'use strict';

function EventListCtrl($scope, Event, $routeParams, $location) {
    $scope.events = Event.getEvents(function loadUntilPageIsFull(events) {
        $scope.events = events;
        $scope.$watch('events', function() {
            if ($("body").height() < $(window).height() && !$scope.events.completed) {
                $scope.events.loadNext(loadUntilPageIsFull);
            }
        });
    });

    $(window).scroll(function() {
        if ($(window).scrollTop() == $(document).height() - $(window).height()) {
            $scope.loading = true;
            $scope.events.loadNext();
        }
    });

    var currentPath = $location.path();
    $scope.$watch(function() {
        return $location.path();
    }, function() {
        if ($location.path() != currentPath) {
            $(window).unbind("scroll");
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

function EventDetailCtrl($scope, $routeParams, Event, User) {
    $scope.event = Event.getEvent($routeParams.eventId);

    $scope.attend = function() {
        Event.attend($scope.event.id, function() {
            $scope.event = Event.getEvent($routeParams.eventId);
        });
    };

    $scope.resign = function() {
        Event.resign($scope.event.id, function() {
            $scope.event = Event.getEvent($routeParams.eventId);
        });
    };
}

function UserCtrl($scope, User) {
    $scope.user = User;

    $scope.login = function() {
        $scope.failed = false;

        User.login(function() {
            $('#loginModal').modal('hide');
        }, function() {
            $scope.failed = true;
        });
    };
}

function RegisterCtrl($scope, User) {
    $scope.user = {
        userName : User.userName,
        password : User.password
    };

    $scope.register = function() {
        $scope.registered = false;
        $scope.failed = false;

        User.register($scope.user, function(response) {
            $scope.registered = true;
        }, function(status) {
            $scope.failed = true;
            $scope.failedMessage = status;
        });
    };
}