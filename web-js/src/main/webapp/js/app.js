'use strict';

angular.module('eventjuggler', [ 'eventjugglerServices' ]).config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/events', {
        templateUrl : 'partials/event-list.html',
        controller : EventListCtrl
    }).when('/events/create', {
        templateUrl : 'partials/event-create.html',
        controller : EventCreateCtrl
    }).when('/events/mine', {
        templateUrl : 'partials/event-mine.html',
        controller : EventMineCtrl
    }).when('/events/:eventId', {
        templateUrl : 'partials/event-detail.html',
        controller : EventDetailCtrl
    }).otherwise({
        redirectTo : '/events'
    });
} ]);
