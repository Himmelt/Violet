# Violet
Violet Library Plugin
# 紫罗兰
紫罗兰 Spigot/Sponge 模板插件

[![TravisCI](https://img.shields.io/travis/Himmelt/Violet/master.svg?label=TravisCI&style=flat-square)](https://travis-ci.org/Himmelt/Violet)
[![CircleCI](https://img.shields.io/circleci/project/github/Himmelt/Violet/master.svg?label=CircleCI&style=flat-square)](https://circleci.com/gh/Himmelt/Violet)
[![Download](https://api.bintray.com/packages/himmelt/Minecraft/Violet/images/download.svg)](https://bintray.com/himmelt/Minecraft/Violet/_latestVersion)

### 简介
这是一个模板插件，可以省去部分重复工作，加速插件开发。

关于名字，起名是个让人纠结的事，所以我一般喜欢用起名的那一瞬间我想到的最近正在接触的事物——紫罗兰永恒花园。
现在想来，这个名字真的很不错啊。

### 特性
1. 语言文件第一次会从jar包内提取到lang文件夹，如果需要自定义化翻译，可直接修改语言文件，然后重载。

### 自律规则
1. 高耦合的类应该合并到一个类中，避免循环依赖造成不必要的麻烦。
2. 在整个运行周期不会修改，或整个服务器运营周期不会更改的配置，不应该用命令配置。
