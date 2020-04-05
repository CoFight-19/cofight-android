# CoFight-19 Android client

When a patient is tested positive for Covid-19 authorities need to track recent contact the patient made to warn or quarantine those contacts. CoFight-19 is a volunteering effort to create a complete (basic) stack for setting up a system for tracing contacts.

## How it works (tl;dr)

Each user registers the device phone number and gets back a unique user ID.

Using only Bluetooth, CoFight identifies nearby phones that have the app installed. It records the IDs that come in close proximity and a timestamp. 

When there's a confirmed case, the patient can upload all the data to a central server. Authorities then can then notify the known contacts, thus stopping the spread of the virus.

## Technical

Each device registers a BLE server and client. We define a Service UUID and a Characteristic UUID. We advertise the Service UUID and devices are scanning for that UUID. When a device is found, they connect and read the characteristic that contains the user ID.

Between Android devices, we define a Service Data UUID that we include in the advertisement. Android devices can read the user ID without connecting to the device.

Everything is stored on the device encrypted. Data leave the device only when uploaded by the user. Data older than 21 days are removed on app initialization.

## Caveats

* BLE can record up to 10 meters. But closed contact for the virus spread is considered up to 4 meters.