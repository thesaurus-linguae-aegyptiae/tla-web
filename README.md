![java ci](https://github.com/JKatzwinkel/tla-web/workflows/java%20ci/badge.svg)
![LINE](https://img.shields.io/badge/line--coverage-64%25-yellow.svg)
![METHOD](https://img.shields.io/badge/method--coverage-49%25-orange.svg)

TLA web frontend.

## Installation

Specify the following environment variables in a `.env` file, e.g.:

    BOOTSTRAP_VERSION=4.4.1
    FONTAWESOME_VERSION=5.12.1
    JQUERY_VERSION=3.5.0

Add these third-party JS/CSS libraries to your project like this:

    ./gradlew installAssets

Then run the application using the `bootRun` task:

    ./gradlew bootRun


## Misc

You can check for the newest version of package dependencies by running:

    ./gradlew dependencyUpdates

Run the entire thing using Docker Compose:

    docker-compose up -d

