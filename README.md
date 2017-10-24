The project **S3 SFTP Server** allows you to connect run your SFTP server backed up by S3 storage.
Currently the application uses in-memory database to log all activities for every user over SFTP (all CRUD operations).

Current stable version: 1.0.0

## How to use

The setup is quite easy and it requires you to only edit application.yaml configuration file.

To directly start the app run:
```maven
mvn spring-boot:run
```

To produce executable jar and run:

```maven
mvn clean install
java -jar target/managed-sftp*.jar
```

### How to configure your app

Example configuration:

```yaml
app:
  sftp:
    port: 8888 # Sftp server port: Default is 22
    users: # Define the list of users
      -
        username: testwithkey
        public-key: ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAq3pyEpghppeIgw9UgkIx/qgSVqMc63vVeVwLV+7U3uqhnSaefJqneL1Z8n2XxbMC+Fl8/r4Wx+fdbRBZojGeyHOAwi63YFQmHtkfKiko8Cnz3Cds09OzShEBv8W8eMY4HpRTYElxb7I5QQAKg5wPPP2Eip6WoChQfz44nzD0SAEbAcj5jKb0tXfxS3ePU3iHRjjbqRTNE4crsuR7xPKwf/RrQEWDlYKNchwgq1atyJETfcuxPJm9KVmbZAiOcVpHpDllQ1+HxVTE33B5qxJGx0gtJenS5Xjgtzw5cxa9CvmPpsq4oHjgLzvgWSsHTg7u7BzoZ6OkTN0yS1UkCfB0lw== rsa-key-20170427
      -
        username: test
        password: pass
    aws:
#      access-key: # Optional: If not provided withing the env
#      secret-key: # Optional: If not provided withing the env
#      assume-role: # Optional: If not provided withing the env
      bucket-name: your.bucket.name # Bucket name
```
