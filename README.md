# Span省略号处理

> 由于Android TextView最大宽度尾部省略号的场景，在内容含有Span的时候，并未对Span处理，导致达到最大宽度尾部无省略号，甚至Span绘制不完全等异常现象，就该问题进行处理。


#### 看图理解

> 还是没有明白解决啥问题？不多废话，上图!

![](https://raw.githubusercontent.com/Jacksgong/SpanEllipsizeEnd/master/imgs/demo.jpg)

## I. 基本算法

![](https://raw.githubusercontent.com/Jacksgong/SpanEllipsizeEnd/master/imgs/algorithm.jpg)

## II. 使用方法

简单如下:

```
mDemoTv.setText(SpanEllipsizeEndHelper.matchMaxWidth(demoSS, mDemoTv));
```

## III. TODO

- 就目前只支持ImageSpan处理，不断拓展其他Span处理。
- 就目前只支持单行处理，将在以后，考虑多行处理。

## IV. License

```
Copyright 2015 Jacks Blog(blog.dreamtobe.cn).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
