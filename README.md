![java ci](https://github.com/JKatzwinkel/tla-web/workflows/java%20ci/badge.svg)
![LINE](https://img.shields.io/badge/line--coverage-48%25-orange.svg)
![METHOD](https://img.shields.io/badge/method--coverage-43%25-orange.svg)

You can check for the newest version of package dependencies by running:

    ./gradlew dependencyUpdates

Download Bootstrap and Font Awesome:

    ./gradlew installAssets

Run the entire thing using Docker Compose:

    docker-compose up -d

The following environment variables must be set (for docker setup): `BACKEND_PORT`, `ES_PORT`, `FRONTEND_POST`,
`FONTAWESOME_VERSION`, `BOOTSTRAP_VERSION` (cf. to [`.env.template`](.env.template))
