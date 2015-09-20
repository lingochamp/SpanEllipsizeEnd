# Span省略号处理

> 由于Android TextView最大宽度尾部省略号的场景，在内容含有Span的时候，并未对Span处理，导致达到最大宽度尾部无省略号，甚至Span绘制不完全等异常现象，就该问题进行处理。


## 伤心的故事

虽然我发现包括，手Q、脉脉、网易云音乐等在这块处理都有问题，或者是都没有处理吧。。。因此欣然的写了这个算法。抱着造福一方的心态开源。。

最终的解决是猛然回头发现`android.text.TextUtils#ellipsize(...):CharSequence`已经很好的分装了类似方法。

#### 看图理解

> 还是没有明白解决啥问题？不多废话，上图!

![](https://raw.githubusercontent.com/lingochamp/SpanEllipsizeEnd/master/imgs/demo.jpg)


## I. 基本算法

![](https://raw.githubusercontent.com/lingochamp/SpanEllipsizeEnd/master/imgs/algorithm.jpg)

## II. 使用方法

简单如下:

```
mDemoTv.setText(SpanEllipsizeEndHelper.matchMaxWidth(demoSS, mDemoTv));
```

## III. License

```
Copyright (c) 2015 LingoChamp Inc.

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
