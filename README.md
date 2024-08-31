# MineLib

MineLib is a library for Minecraft servers plugins development. 
It provides a lot of useful classes and methods to make your plugin development easier.

## Installation

Remember to install MineLib on your server before using it in your plugin.

Replace `{version}` with the latest version number on repository.

### Gradle (Kotlin)

```kotlin
repositories {
    maven("https://repo.nikdekur.tech/releases")
}

dependencies {
    implementation("dev.nikdekur:minelib:{version}")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url "https://repo.nikdekur.tech/releases" }
}

dependencies {
    implementation "dev.nikdekur:minelib:{version}"
}
```

### Maven

```xml

<repository>
    <id>ndkore-repo</id>
    <url>https://repo.nikdekur.tech/releases</url>
</repository>
```

---

```xml

<dependency>
    <groupId>dev.nikdekur</groupId>
    <artifactId>minelib</artifactId>
    <version>{version}</version>
</dependency>
```


## Features

The list of main features provided below. 
For more information, check the documentation (only KDocs for now).

### Commands API
Helps with creating commands in your plugin.
Allow creating command trees, parse arguments, and more.

### Drawing API
Functions to draw shapes with minecraft particles.

### Kotlin Extensions
Nearly 20 classes with extensions for Bukkit API classes, 
which will make your life easier and code cleaner.

### GUIs
Few classes to make creating GUIs easier.
API is small, so for new versions [InvUI](https://github.com/NichtStudioCode/InvUI) is recommended.
But for older versions, MineLib GUIs are still useful.

### I18n (Internationalization)
Full support for localization in your server.
You can create your own messages bundles with your translations 
and get actual messages in runtime for player`s locale.
Almost all features in MineLib support I18n.

### Personal Entities
Create entities that are visible only for specific players.
This is useful when using I18n or temporary entities.

### Holograms 
Holograms are built on top of Personal Entities.
You can create holograms with text and show them to players.

### Movement Detection
Minecraft `PlayerMoveEvent` is not optimized and might be called too often.
MineLib's Movement Detection allows setting the period of time between checking player's movement,
and even process events asynchronously.

### Services System
MineLib urges every plugin to use the Services System.
The Service System forces you to split your plugin into small parts,
which makes your code cleaner and easier to maintain.
MineLib uses [ndkore](https://github.com/NikDeKur/ndkore) Services System.

### Scheduler
Bukkit scheduler always asks you to pass your plugin instance,
haven't you tired of it? MineLib's scheduler already contains your plugin instance
and copies all Bukkit scheduler methods, 
also sorting arguments to support Kotlin lambdas out of parentheses.

### Main Plugin Class
MineLib plugin's starts not from `JavaPlugin` but from `ServerPlugin`!
The main features are:
- Components list, where you can pass your Commands, Listeners and services to be registered.
- Reload support
- Bundled scheduler