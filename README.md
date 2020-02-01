# Violet
Violet Library Plugin
# 紫罗兰
紫罗兰 Spigot/Sponge 模板插件

### 简介
这是一个模板插件，可以省去部分重复工作，加速插件开发。

关于名字，起名是个让人纠结的事，所以我一般喜欢用起名的那一瞬间我想到的最近正在接触的事物——紫罗兰永恒花园。
现在想来，这个名字真的很不错啊。

### 特性
1. 语言文件第一次会从jar包内提取到lang文件夹，如果需要自定义化翻译，可直接修改语言文件，然后重载。

### 自律规则
1. 高耦合的类应该合并到一个类中，避免循环依赖造成不必要的麻烦。
2. 在整个运行周期不会修改，或整个服务器运营周期不会更改的配置，不应该用命令配置。

### 版本声明
自 `2.5.0` 版本开始，将按照 [语义化版本标准](https://semver.org/lang/zh-CN/) 进行版本命名。`2.5.0` 版本不兼容所有旧版本。
