
# Interaction of services when one of them protected by certificate

## How to build
```
mvn clean install
```

## How to run

### How to run both services
```
articles-service/run_via_maven.bat
facade-service/run_via_maven.bat
```

### How to run both services (Facade Service will make UNSECURE calls to Articles Service)
```
articles-service/run_via_maven.bat
facade-service/run_via_maven-unsecure_calls.bat
```

### How to run both services in Docker containers
    docker-compose up

### How to run both services in Docker containers with rebuild service image
    docker-compose up --build --force-recreate --no-deps

### Stop and cleanup started containers
    docker-compose down

## How to make calls

### Make call to protected Article Service using curl (from _cert folder)
```
curl -v \
  --cacert andd3dfx.crt \
  --key andd3dfx.key \
  https://localhost:9082/api/v1/articles
```
Use certs from `issued-for-localhost` folder in case of run via bat-scripts,
or certs from `issued-for-articles-service` folder in case of run via Docker Compose

### Make call to Facade Service using curl
```
curl http://localhost:9081/api/v1/articles
```

## Appendix

### Generating a Keystore (PKCS12 format) with self-signed certificate
Based on https://www.baeldung.com/spring-boot-https-self-signed-certificate

```
keytool -genkeypair -alias andd3dfx -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore andd3dfx.p12 -validity 1825
```

<pre>
Enter keystore password: andreika
Re-enter new password: andreika
What is your first and last name?
[Unknown]:  localhost
What is the name of your organizational unit?
[Unknown]:  
What is the name of your organization?
[Unknown]:  andrei-company
What is the name of your City or Locality?
[Unknown]:  Minsk
What is the name of your State or Province?
[Unknown]:  Minsk
What is the two-letter country code for this unit?
[Unknown]:  BY
Is CN=localhost, OU=andrei-dev, O=andrei-dev, L=Minsk, ST=Minsk, C=BY correct?
[no]:  yes
</pre>

### Extract private key from a P12 file and write it to PEM file
```
openssl pkcs12 -in andd3dfx.p12 -nocerts -out andd3dfx.key
```
(used 'andreika' as a password for all steps)

### Extract the certificate file (the signed public key) from the P12 file
```
openssl pkcs12 -in andd3dfx.p12 -clcerts -nokeys -out andd3dfx.crt
```
(used 'andreika' as a password for all steps)
