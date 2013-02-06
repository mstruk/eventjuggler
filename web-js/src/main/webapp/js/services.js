'use strict';

var myModule = angular.module('eventjugglerServices', [ 'ngResource' ]);

myModule.service('Event', function($resource, User) {

    this.getEvents = function(success) {

        var events = [];

        events.loading = false;

        events.parameters = {
            "first" : 0,
            "max" : 10,
            "sort" : "time"
        };

        events.loadNext = function(success) {
            $resource('/eventjuggler-rest/event/:eventId').query(events.parameters, function(data) {
                if (events.loading)
                    return;

                events.loading = true;

                if (data.length == 0) {
                    return;
                }

                var l = events.length;
                for ( var i = 0; i < data.length; i++) {
                    events[i + l] = data[i];
                }

                events.parameters.first += events.parameters.max;
                events.loading = false;

                if (success) {
                    success(events);
                }
            });
        };

        events.loadNext(success);

        return events;
    };

    this.getEvent = function(eventId) {
        var event = {};
        $resource('/eventjuggler-rest/event/:eventId').get({
            "eventId" : eventId,
            isArray : false
        }, function(data) {
            for ( var i in data) {
                event[i] = data[i];
            }
        });
        return event;
    };

    this.getEventsUser = function() {
        var events = [];

        $resource('/eventjuggler-rest/event/mine').query({
            "username" : User.user.login,
            "password" : User.user.password
        }, function(data) {
            var l = events.length;
            for ( var i = 0; i < data.length; i++) {
                events[i + l] = data[i];
            }
        });

        return events;
    };

    this.attend = function(eventId, success) {
        $resource('/eventjuggler-rest/event/:eventId/rsvp').get({
            "eventId" : eventId,
            "username" : User.user.login,
            "password" : User.user.password
        }, success);
    };
});

myModule.service('User', function($http, $resource, $cookieStore) {
    var self = this;
    this.user = undefined;

    this.login = function(username, password, success) {
        $http({
            method : 'GET',
            url : "/eventjuggler-rest/user?username=" + username + "&password=" + password
        }).success(function(data, status, headers, config) {
            if (data) {
                self.user = data;
                $cookieStore.put("username", username);
                $cookieStore.put("password", password);
                if (success) {
                    success(self.user);
                }
            }
        });
    };

    this.logout = function() {
        self.user = undefined;
        $cookieStore.remove("username");
        $cookieStore.remove("password");
    };

    this.getUser = function(success) {
        if (this.user) {
            success(this.user);
        } else if ($cookieStore.get("username")) {
            this.login($cookieStore.get("username"), $cookieStore.get("password"), success);
        }
    };
    
    this.register = function(user, success, error) {
        $resource('/eventjuggler-rest/user').save(user, success, error);
    };
});