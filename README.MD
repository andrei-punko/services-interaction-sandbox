
# Example of services interaction when one of them protected by certificate

## Make call to protected article-service using curl (from _cert folder)
```
curl -v \
  --cacert andd3dfx.crt \
  --key andd3dfx.key \
  https://localhost:9082/api/v1/articles
```

## Make call to facade-service using curl (from _cert folder)
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

## Start application in Docker container (vs Postgres DB in Docker):
    docker-compose up

## Start application in Docker container (vs Postgres DB in Docker) with rebuild service image:
    docker-compose up --build --force-recreate --no-deps
