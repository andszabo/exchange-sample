angular.module('exchange.controllers', [])
    .controller('ExchangeController', ['$scope', 'ExchangeSocket', function ($scope, exchangeSocket) {

        var user = document.getElementById('user').value;

        $scope.newOrder = function () {
            var id = 'xxxxyyyy-yyxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r | 0x8);
                return v.toString(16);
            });
            return {
                'id': id,
                'direction': 'Buy',
                'quantity': null,
                'price': null,
                'user': user
            };
        };

        $scope.reset = function () {
            $scope.errorMessage = '';
            $scope.orders = [];
            $scope.trades = [];
            $scope.instrumentId = '';
            $scope.avgPrice = null;
            $scope.instrumentSelected = false;
            $scope.currentOrder = $scope.newOrder();
            document.getElementById('instrument').focus();
        }

        $scope.placeOrder = function () {
            $scope.currentOrder.instrumentId = $scope.instrumentId;
            exchangeSocket.send("/app/exchange/order", {
                "content-type": "application/json;charset=UTF-8"
            }, JSON.stringify($scope.currentOrder));
            $scope.currentOrder.status = 'Sent';
            $scope.orders.unshift($scope.currentOrder);
            $scope.currentOrder = $scope.newOrder();
        };

        $scope.updateOrders = function (orderUpdates) {
            for (var i = 0; i < orderUpdates.orders.length; i++) {
                var orderUpdate = orderUpdates.orders[i];
                var order = findOrder(orderUpdate.id);
                if (order != null) {
                    order.status = 'Pending';
                    order.creationTime = Date.parse(orderUpdate.creationTime);
                } else if(orderUpdate.instrumentId == $scope.instrumentId) {
                    orderUpdate.status = 'Pending';
                    orderUpdate.creationTime = Date.parse(orderUpdate.creationTime);
                    $scope.orders.unshift(orderUpdate);
                }
            }
        }

        var findOrder = function (id) {
            for (var i = 0; i < $scope.orders.length; i++) {
                var order = $scope.orders[i];
                if (order.id == id) {
                    return order;
                }
            }
            return null;
        };

        $scope.updateTrades = function (tradeUpdates) {
            for (var i = 0; i < tradeUpdates.trades.length; i++) {
                var trade = tradeUpdates.trades[i];
                if(trade.instrumentId == $scope.instrumentId) {
                    if (trade.buyer == user && findTrade(trade.id, "Buy") == null) {
                        addTradeUpdate(trade, "Buy", trade.buyOrderId);
                    }
                    if (trade.seller == user && findTrade(trade.id, "Sell") == null) {
                        addTradeUpdate(trade, "Sell", trade.sellOrderId);
                    }
                }
            }
        }

        var findTrade = function (id, direction) {
            for (var i = 0; i < $scope.trades.length; i++) {
                var trade = $scope.trades[i];
                if (trade.id == id && trade.direction == direction) {
                    return trade;
                }
            }
            return null;
        };

        var addTradeUpdate = function (trade, direction, orderId) {
            var tradeUpdate = {
                'id': trade.id,
                'direction': direction,
                'orderId': orderId,
                'instrumentId': trade.instrumentId,
                'quantity': trade.quantity,
                'price': trade.price,
                'creationTime': Date.parse(trade.creationTime)
            };
            $scope.trades.unshift(tradeUpdate);
            $scope.orders = $scope.orders.filter(function (o) {
                return o.id != orderId;
            });
        };

        $scope.setInstrument = function () {
            exchangeSocket.send("/app/exchange/instrument", {
                "content-type": "application/json;charset=UTF-8"
            }, JSON.stringify({
                'user': user,
                'instrumentId': $scope.instrumentId
            }));
            $scope.instrumentSelected = true;
        }

        $scope.instrumentInfo = function (info) {
            if (info.instrumentId == $scope.instrumentId) {
                $scope.avgPrice = info.averagePrice;
            }
        }

        $scope.dismissError = function () {
            $scope.errorMessage = '';
        }

        var initClient = function () {

            exchangeSocket.init('/exchange');

            exchangeSocket.connect(function (frame) {

                    exchangeSocket.subscribe("/user/queue/order", function (message) {
                        $scope.updateOrders(JSON.parse(message.body));
                    });

                    exchangeSocket.subscribe("/user/queue/instrument", function (message) {
                        $scope.instrumentInfo(JSON.parse(message.body));
                    });

                    exchangeSocket.subscribe("/user/queue/trade", function (message) {
                        $scope.updateTrades(JSON.parse(message.body));
                    });

                    exchangeSocket.subscribe("/user/queue/errors", function (message) {
                        $scope.errorMessage = error(message.body);
                    });
                },
                function (error) {
                    $scope.errorMessage = error;
                });
        };

        $scope.reset();
        initClient();
    }]);