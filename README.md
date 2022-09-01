# AWS Lambda to obtain a public key from a host

## Setup

Checkout the repository.

## Build

Run `mvn clean install`.

Add a Java Lambda using the `get-public-key-for-host-1.0-SNAPSHOT.jar` and Handler `org.example.Handler`.

Create a Bucket and extend the Lambda role to be able to write to this Bucket.

## Test

Test the lambda using the Test event JSON (replace `<Region>`, `<Bucket>` and `<File>` accordingly)

```
{
    "input": {
        "host": "https://www.google.com"
    },
    "output": {
        "region": "<Region>",
        "bucket": "<Bucket>",
        "key": "<File>"
    }
}
```

and the Bucket `<Bucket>` in region `<Region>` should contain the file `<File>` containing Google's public key.