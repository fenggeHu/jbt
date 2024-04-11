# jbt核心能力
## Sequence
![Sequence.png](sequence.png)
## 引擎
- Engine，回测一段时间内的效果。用于回归策略的有效性
- REngine，最右/最近一个数据的结果。可根据设定的策略实时捕获信号
## 策略类
- Strategy，基本逻辑，可自定义扩展实现更丰富的效果
- Event，信号、事件
- Handler，交易等自定义实现
