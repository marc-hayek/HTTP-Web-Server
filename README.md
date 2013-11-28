HTTP-Web-Server
===============

A functioning HTTP web server conforming to HTTP 1.1 specifications written in java

Description
------------

A webserver application written in Java which accepts HTTP1.1 GET requests and responds accordingly.
The application is multi-threaded so it can accept and serve multiple requests at the same time.
The server also responds with the correct responses (200,304,400,401,404).
All of the requests are collected, parsed for info, and stored in a log text file.
The server can return basic webpages as well as dynamic pages, for example, this request will return a mix of paragraphs, links, and images.
/mix?paragraphs=m,links=m,images=n

The project requirements and description is included in the repo.
A details user manual is included in the code and resources folder: UserManual.html