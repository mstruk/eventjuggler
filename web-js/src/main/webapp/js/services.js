'use strict';

var eventjugglerServices = angular.module('eventjugglerServices', [ 'ngResource' ]);


eventjugglerServices.service('User', function($resource, $cookieStore) {
    var res = $resource('/eventjuggler-rest/user');
    
    var user = { username : null, password : null, loggedIn : false };

    user.login = function(success, error) {
        res.get({ username: user.username, password: user.password }, function(data) {
            if (data.login) {
                user.loggedIn = true;
                
                if (data.name) {
                    user.name = data.name;
                    
                    if (data.lastName) {
                        user.name += " " + data.lastName;
                    }
                } else {
                    user.name = user.username;
                }

                $cookieStore.put("user-username", user.username);
                $cookieStore.put("user-password", user.password);
                
                if (success) {
                    success();
                }
            } else if (error) {
                error();
            }
        }, error); 
    };

    user.logout = function() {
        user.username = null;
        user.password = null;
        user.loggedIn = false;

        $cookieStore.remove("user-username");
        $cookieStore.remove("user-password");
    };
        
    user.register = function(user, success, error) {
        res.save(user, success, error);
    };
    
    if (!user.loggedIn && $cookieStore.get("user-username") && $cookieStore.get("user-password")) {
        user.username = $cookieStore.get("user-username");
        user.password = $cookieStore.get("user-password");
        
        user.login();
    }
    
    return user;
});


eventjugglerServices.factory('Resource', function($resource, User) {
    return function(url) {
        var r = {};
        var addUserCredentials = function(parameters) {
            if (User.loggedIn) {
                if (!parameters) {
                    parameters = {};
                }
                parameters.username = User.username;
                parameters.password = User.password;
            }
            return parameters;
        };
        
        r.res = $resource(url);
        
        r.get = function(parameters, success, error) { return r.res.get(addUserCredentials(parameters), success, error); }; 
        r.query = function(parameters, success, error) { return r.res.query(addUserCredentials(parameters), success, error); };
        r.remove = function(parameters, success, error) { return r.res['delete'](addUserCredentials(parameters), success, error); };
        
        return r;
    };
});


eventjugglerServices.service('Event', function(Resource, User, $http, $routeParams) {
    var eventsRes = Resource('/eventjuggler-rest/event/:eventId');
    var mineRes = Resource('/eventjuggler-rest/event/mine');
    var rsvpRes = Resource('/eventjuggler-rest/event/:eventId/rsvp');
    
    this.getEvents = function(success) {
        var events = [];

        events.loading = false;

        events.parameters = {
            first : 0,
            max : 10,
            sort : "time"
        };

        if ($routeParams.query) {
            events.parameters.query = $routeParams.query;
        }
        
        events.loadNext = function(success) {
            eventsRes.query(events.parameters, function(data) {
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

    this.getEvent = function(eventId, success) {
        var event = {};
        eventsRes.get({ "eventId" : eventId }, function(data) {
            for ( var i in data) {
                event[i] = data[i];
            }

            event.attending = false;

            if (User.loggedIn && event.attendance) {
                for ( var i = 0; i < event.attendance.length; i++) {
                    if (event.attendance[i].login == User.username) {
                        event.attending = true;
                    }
                }
            }

            if (success) {
                success(event);
            }
        });
        return event;
    };

    this.getEventsUser = function() {
        var events = [];

        mineRes.query(null, function(data) {
            var l = events.length;
            for ( var i = 0; i < data.length; i++) {
                events[i + l] = data[i];
            }
        });

        return events;
    };

    this.attend = function(eventId, success) {
        rsvpRes.get({ "eventId" : eventId }, success);
    };
    
    this.resign = function(eventId, success) {
        rsvpRes.remove({ "eventId" : eventId }, success);
    };  
});