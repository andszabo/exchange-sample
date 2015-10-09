angular.module('exchange.services', [])
    .factory('ExchangeSocket', ['$rootScope', function ($rootScope) {
        var stompClient;

        var wrappedSocket = {

            init: function (url) {
                stompClient = Stomp.over(new SockJS(url));
            },
            connect: function (successCallback, errorCallback) {
                var headers = {};
                headers[$("meta[name='_csrf_header']").attr("content")] = $("meta[name='_csrf']").attr("content");
                stompClient.connect(headers, function (frame) {
                    console.log('Connected: ' + frame);
                    $rootScope.$apply(function () {
                        successCallback(frame);
                    });
                }, function (error) {
                    console.log('Connection error: ' + error);
                    $rootScope.$apply(function () {
                        errorCallback(error);
                    });
                });
            },
            disconnect: function () {
                if (stompClient != null) {
                    stompClient.disconnect();
                }
                console.log("Disconnected");
            },
            subscribe: function (destination, callback) {
                stompClient.subscribe(destination, function (message) {
                    $rootScope.$apply(function () {
                        callback(message);
                    });
                });
            },
            send: function (destination, headers, object) {
                stompClient.send(destination, headers, object);
            }
        }

        return wrappedSocket;

    }]);