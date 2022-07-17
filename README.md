# A multithreaded backtesting simulator 

This Java application is designed to backtest trading strategies on longer timeframes. This application optimizes a trading strategy by backtesting thousands of parameters and returning the best result.

The simulator is preloaded with an example of mine. The trading algorithm is an ichimoku based strategy, and the parameter that the simulator is optimizing for is the ratio of leadingA to market price. The simulator is set up to run 1000 simulations on 5 years worth of historical BTC data on 5 minute candles.    