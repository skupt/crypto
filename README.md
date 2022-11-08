# Crypto Investment Recommendation Service

Java position XMCY-ODC - technical task
-----------------
How to Package and Build docker Images
---------------------------
You should have a docker and docker-compose on your machine to proceed. Besides above, pull 1 docker image to be able
to run docker-compose later:
`docker pull amazoncorretto:11`
In system command prompt use maven command inside the root directory of project 'crypto'
`mvn clean package docker:build`. There will be generated JAR package of project.


And Run microservices in Docker Containers
-----------
Continue typing in command prompt being located in root of project
`docker-compose up -d`

Swagger
----------
Swagger is able on url: http://127.0.0.1:8080/swagger-ui/index.html.
There will be information about rest endpoints of this application:

- GET `/api/v1/cryptos/statistic`  Returns the oldest/newest/min/max values for a requested crypto for all available
  data
- GET `/api/v1/cryptos/range` Returns a descending sorted list of all the cryptos, comparing the normalized range (
  i.e. (max-min)/min).
- GET `/api/v1/cryptos/range/highest` Returns the crypto with the highest normalized range for a specific day

Requirements that were set for the recommendation service:
----------

Reads all the prices from the csv files
----------
Application reads prices from files placed in project's `resource/prices` folder. You can set environment variable
`CRYPTO_PRICES_PATH` with full absolute path information and place there production files if you want. When prices are
parsed only part of filename reflecting symbol of cryptocurrency is taking into consideration to recognize name of
currency inside file.

Calculates oldest/newest/min/max for each crypto for the whole month
-------
Taking in consideration this requirement and basing on statement that data in folder prices reflect prices only for
one month (`...you can find one month’s prices...`) and basing on question in 'Things to consider' `Will the
recommendation service be able to handle six months or even a year?` I decided to make this endpoint for 'all time
data'. More precise 'all time' means period in time that starts from `2000-01-01` and have duration of 36500 days
(app. 100 years). Such approach conform current requirements while 'customer' don't change condition about in price
folder should be data for a month. And in such way I made one 'base' method for calculating required 'statistics'
that take 3 params (- crypto's name, - LocalDateTime for point in time for start period, and Duration of period). And
it is reused in other method to perform provided requirements.

Exposes an endpoint that will return a descending sorted list of all the cryptos, comparing the normalized range (i.e.(max-min)/min)
-------
GET `/api/v1/cryptos/range`, see above.

Exposes an endpoint that will return the oldest/newest/min/max values for a requested crypto
-------
GET `/api/v1/cryptos/statistic`, see above.

Exposes an endpoint that will return the crypto with the highest normalized range for a specific day
-------
GET `/api/v1/cryptos/range/highest`

Things to consider:
------
Documentation is our best friend, so it will be good to share one for the endpoints
------
Swagger is able on url: http://127.0.0.1:8080/swagger-ui/index.html

Initially the cryptos are only five, but what if we want to include more? Will the recommendation service be able to scale?
------
Yes, if you include any reasonable amount of cryptos you want it will work. Besides this in a project was implemented
LRU (least recently used) that reduced query execution time by 2 (lightweight request) - 30 (heavyweight requests)
times.
You can adjust it size with environment variable CRYPTO_CACHE_SIZE.

New cryptos pop up every day, so we might need to safeguard recommendations service endpoints from not currently supported cryptos
------
It was done `by default`. When app reads data it creates cryptos map where keys are cryptos symbols. When request comes
to controller handler it validate it in front of available cryptos and send response about wrong currency name back to
client.

For some cryptos it might be safe to invest, by just checking only one month's time frame. However, for some of them it might be more accurate to check six months or even a year. Will the recommendation service be able to handle this?
------
Yes, it could handle any period of time that can fit in LocalDateTime and Duration variables (and in memory of server
instance)

Extra mile for recommendation service (optional):
-------
In XM we run everything on Kubernetes, so containerizing the recommendation service will add great value
-------
I made Dockerfile and docker-compose files and place `io.fabric8:docker-maven-plugin:0.40.1` in pom.xml. It makes able
to build and run project in cluster that contains 1 crypto service in 2 clicks.

Malicious users will always exist, so it will be really beneficial if at least we can rate limit them (based on IP)
-----
