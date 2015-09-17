# Span省略号处理

> 由于Android TextView最大宽度尾部省略号的场景，在内容含有Span的时候，并为对Span处理，导致达到最大宽度尾部无省略号，甚至Span绘制不完全等异常现象，进行处理。


#### 看图理解

> 还是没有明白解决啥问题？不多废话，上图!

![](https://raw.githubusercontent.com/Jacksgong/SpanEllipsizeEnd/master/imgs/demo.jpg)

## 基本算法

![](https://raw.githubusercontent.com/Jacksgong/SpanEllipsizeEnd/master/imgs/algorithm.jpg)

## 使用方法

简单如下:

```
mDemoTv.setText(SpanEllipsizeEndHelper.matchMaxWidth(demoSS, mDemoTv));
```
