Clippefficiency brings back your favorite virtual assistant with an ergonomic focus. You are in charge of your efficiency by setting up suggestions triggered by native key and screen events, which can be anything from finding built-in shortcuts from manual action or suggesting more efficient Vim commands over current key usage. Keep track of your progress over time and get inspiration with community-created and AI suggestions (TBD!)

Great for learning Spring Boot/JavaFX. In haitus but will return sometime!

# Getting Started

Clone the repository and run gradle

## Agent
Handles processing of native events via JNativeHook to app-specific InputEvents

## Core
Core logic / functionality of the entire shortcut engine, with suggestion parsing, event matching, logging, etc.

## Server
Persistent Spring Boot backend with user information.

## Shared
Shared data transfer objects (DTOs) and environment variables 

## UI
Controller and View logic using JavaFX
