'use strict';

function EventListCtrl($scope, Event, $routeParams) {
    var first = 0;
    var max = 10;
    var loading = false;

    $scope.events = [];

    $scope.loadMore = function() {
        if (loading)
            return;

        loading = true;

        var parameters = {
            "first" : first,
            "max" : max,
            "sort" : "time"
        };
        
        if ( $routeParams.query ) {
            parameters.query = $routeParams.query;
        }

        Event.query(parameters, function(data) {
            if (data.length == 0) {
                return;
            }

            $scope.events = $scope.events.concat(data);
            first += max;
            loading = false;

            $scope.$watch('events', function() {
                if ($("body").height() < $(window).height()) {
                    $scope.loadMore();
                }
            });
        });
    };

    $scope.loadMore();

    $(window).scroll(function() {
        if ($(window).scrollTop() == $(document).height() - $(window).height()) {
            $scope.loadMore();
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