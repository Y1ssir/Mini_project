### Système des gestions des incidents ###

## Setup ##

copie and paste "cloud-config" into "${user.home}"

## Service ##

- Service Company : service of company entity
- Service Config : contain configuration of all other services
- Service Proxy : load balancer and proxy
- Service Register : with Eureka Netflix

## Start ##

run the service with "mvn spring-boot:start"
1 : service-config
2 : service-register
3 : service-proxy
4 : service-company
