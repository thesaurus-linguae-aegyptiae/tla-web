![Java CI](https://github.com/JKatzwinkel/tla-web/workflows/build/badge.svg)
![LINE](https://img.shields.io/badge/line--coverage-46%25-orange.svg)
![METHOD](https://img.shields.io/badge/method--coverage-43%25-orange.svg)

TLA web frontend.

Depends on:

- Spring Boot
- Thymeleaf Layout Dialect
- Lombok
- JSesh
- tla-common
- tla-es


## Usage

First, install third-party JavaScript/CSS resources by running:

    gradle installAssets

This will download and install [Bootstrap](https://getbootstrap.com/), [Font Awesome](https://fontawesome.com/),
[JQuery](https://jquery.com/), and [Headroom.js](https://wicky.nillia.ms/headroom.js/).
In order to override the respective default bundle versions of some of these, you can use the environment variables
shown in [`.env.template`](.env.template), e.g. by defining them in a `.env` file:

    BOOTSTRAP_VERSION=4.4.1
    FONTAWESOME_VERSION=5.12.1
    JQUERY_VERSION=3.5.0

With the necessary third-party resources in place, you can run the web frontend application using the `bootRun` task:

    gradle bootrun



## Misc

You can check for the newest version of package dependencies by running:

    gradle dependencyUpdates

Run the entire thing using Docker Compose:

    docker-compose up -d

