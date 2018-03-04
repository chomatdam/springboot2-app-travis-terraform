
[![Logo][upday-image]][upday-url]

[![Build Status][travis-image]][travis-url-main]

# Test assignment
## Expected
1. Spring Boot Application ([start.spring.io](https://start.spring.io): SB >= 2.0.0)
2. Tests
3. Containerization
4. Infrastructure
5. Deployment

## Details
### Spring Boot Application
* Based on this [JSON](src/main/resources/articles.json) containing a list of articles, set a scheduler parsing and saving to a repository a random article from this list
* Implement a repository for articles and expose 2 endpoints which are:
  * returning all articles available
  * returning articles containing the matching category
* Add unit tests
* Build a simple CI pipeline (recommended: [Travis](https://travis-ci.org), [CircleCI](https://circleci.com), ...)

### Advanced
* Update your scheduler to publish articles on a message queue
* Consume these articles and save them to a repository
    * _if an article is already existing, set the field: updatedAt_
* Create a Dockerfile and a docker-compose.yml
* Add integration tests
* Create an infrastructure on AWS ([AWS Free Tier](https://aws.amazon.com/free/))
* Deploy and update your pipeline consequently

TBC

[upday-image]: https://cdn-images-1.medium.com/max/1200/1*PgHdkXmYtlE4lAdmagLy_w.png
[upday-url]: http://www.upday.com/en/

[travis-image]: https://travis-ci.org/chomatdam/upday-test-assignment.svg?branch=master
[travis-url-main]: https://travis-ci.org/chomatdam/upday-test-assignment
