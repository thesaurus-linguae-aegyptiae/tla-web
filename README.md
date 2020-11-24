![build](https://github.com/JKatzwinkel/tla-web/workflows/build/badge.svg)
![deploy](https://github.com/JKatzwinkel/tla-web/workflows/deploy/badge.svg)
![LINE](https://img.shields.io/badge/line--coverage-89%25-brightgreen.svg)
![METHOD](https://img.shields.io/badge/method--coverage-76%25-yellow.svg)

TLA web frontend.


## Installation

There are 2 options.


### 1. Docker

Requirements:

- Docker Compose

Specify a URL where a `.tar.gz` archive containing TLA corpus data can be downloaded via
the environment variable `SAMPLE_URL` (for instance in a `.env` file).
Start docker containers for the entire stack with:

    docker-compose up -d

This will run Elasticsearch, a TLA backend for handling document retrieval, a temporary 
second instance of the backend used for corpus data import, and last not least the TLA
web frontend.

By default, the frontend's web interface can be accessed at port `8080`, the backend
at `8090`, and the backend's Elasticsearch instance at `9200`, but you may configure these
values to your liking via the environment variables `LISTEN_PORT`, `BACKEND_PORT`, and
`ES_PORT`, respectively.


### 2. Gradle

Requirements:

- Java 11
- [tla-es](https://github.com/JKatzwinkel/tla-es)

Install, populate, and run the [backend](https://github.com/JKatzwinkel/tla-es) first.


Run the frontend application using the `bootRun` task from the spring boot gradle plugin
(on windows, you will probably need to use the native wrapper `./gradlew.bat` instead):

    gradle bootrun

On its first run, this will download and install third-party JS/CSS frameworks and libraries such as
[Bootstrap](https://getbootstrap.com/), [Font Awesome](https://fontawesome.com/), and
[JQuery](https://jquery.com/).
In order to override the respective default bundle versions of some of these, you can use the environment variables
shown in [`.env.template`](.env.template), e.g. by defining them in a `.env` file:

    BOOTSTRAP_VERSION=4.4.1
    FONTAWESOME_VERSION=5.12.1
    JQUERY_VERSION=3.5.0

However, this is of course optional as default versions are being defined in the
[build file](build.gradle).


## Misc

You can check for the newest version of package dependencies by running:

    gradle dependencyUpdates

