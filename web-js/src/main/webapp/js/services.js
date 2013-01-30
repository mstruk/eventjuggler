'use strict';

angular.module('eventjugglerServices', [ 'ngResource' ]).factory('Event', function($resource) {
    return $resource('/eventjuggler-rest/event/:eventId', {}, {
        'put' : {
            method : 'PUT'
        }
    });
});
