# Violet
Violet Library Plugin
# 紫罗兰
紫罗兰 Spigot/Sponge 模板插件

[![TravisCI](https://img.shields.io/travis/Himmelt/Violet/master.svg?label=TravisCI&logo=travis-ci)](https://travis-ci.org/Himmelt/Violet)
[![CircleCI](https://img.shields.io/circleci/project/github/Himmelt/Violet/master.svg?label=CircleCI&logo=circleci)](https://circleci.com/gh/Himmelt/Violet)
[![License](https://img.shields.io/github/license/Himmelt/Violet.svg?color=important)](https://github.com/Himmelt/Violet/blob/master/LICENSE)

[![Download](https://api.bintray.com/packages/himmelt/Minecraft/Violet/images/download.svg)](https://bintray.com/himmelt/Minecraft/Violet/_latestVersion)
[![Download](https://img.shields.io/badge/Download-release|spigot-success.svg)](https://oss.jfrog.org/artifactory/oss-release-local/org/soraworld/violet-spigot/)
[![Download](https://img.shields.io/badge/Download-release|sponge-success.svg)](https://oss.jfrog.org/artifactory/oss-release-local/org/soraworld/violet-sponge/)
[![Download](https://img.shields.io/badge/Download-snapshot|spigot-success.svg)](https://oss.jfrog.org/artifactory/oss-snapshot-local/org/soraworld/violet-spigot/)
[![Download](https://img.shields.io/badge/Download-snapshot|sponge-success.svg)](https://oss.jfrog.org/artifactory/oss-snapshot-local/org/soraworld/violet-sponge/)

### 简介
这是一个模板插件，可以省去部分重复工作，加速插件开发。

关于名字，起名是个让人纠结的事，所以我一般喜欢用起名的那一瞬间我想到的最近正在接触的事物——紫罗兰永恒花园。
现在想来，这个名字真的很不错啊。

### 特性
1. 语言文件第一次会从jar包内提取到lang文件夹，如果需要自定义化翻译，可直接修改语言文件，然后重载。

### 自律规则
1. 高耦合的类应该合并到一个类中，避免循环依赖造成不必要的麻烦。
2. 在整个运行周期不会修改，或整个服务器运营周期不会更改的配置，不应该用命令配置。
