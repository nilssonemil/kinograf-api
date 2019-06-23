# kinograf

FIXME

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

### Create Database

Run `psql -U [db-user] -f resources/sql/create-tables.sql [db-name]`.

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2019 FIXME
