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

    $scope.popular = Event.getEventsPopular();

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

    $scope.related = Event.getEventsRelated($routeParams.eventId);

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


var oauthPopup;
var loginSuccess;

function sendMainPage() {
    oauthPopup.close();
    loginSuccess();
}

function UserCtrl($scope, User, $location) {
    $scope.user = User;

    $scope.login = function() {
        $scope.failed = false;

        User.login(function() {
            $('#loginModal').modal('hide');
            $location.path('/events');
        }, function() {
            $scope.failed = true;
        });
    };

    $scope.loginFacebook = function () {
        $scope.failed = false;

        loginSuccess = function() {
            User.loginFacebook(function() {
                $('#loginModal').modal('hide');
                $location.path('/events');
            }, function() {
                $scope.failed = true;
            });
        }

        oauthPopup = window.open("/eventjuggler-rest/facebook", "name", "height=768, width=1024");
        oauthPopup.focus();
    };
}

function RegisterCtrl($scope, User) {
    $scope.u = {};

    $scope.register = function() {
        $scope.registered = false;
        $scope.failed = false;

        User.register($scope.u, function(response) {
            $scope.registered = true;
            $scope.u = {};
        }, function(status) {
            $scope.failed = true;
            $scope.failedMessage = status;
        });
    };
}