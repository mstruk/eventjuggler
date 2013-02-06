'use strict';

angular.module('eventjuggler', [ 'eventjugglerServices', 'ngCookies' ]).config([ '$routeProvider', function($routeProvider) {
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
    }).when('/register', {
        templateUrl : 'partials/register.html',
        controller : RegisterCtrl
    }).otherwise({
        redirectTo : '/events'
    });
} ]);
